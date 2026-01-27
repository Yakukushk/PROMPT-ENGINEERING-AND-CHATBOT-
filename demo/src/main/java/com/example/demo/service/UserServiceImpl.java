package com.example.demo.service;

import com.example.demo.dto.UserDto;
import com.example.demo.dto.request.UserRequest;
import com.example.demo.dto.request.update.UpdateUserRequest;
import com.example.demo.entity.Conversation;
import com.example.demo.entity.User;
import com.example.demo.entity.enums.UserRole;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ConversationRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.interfaces.ConversationService;
import com.example.demo.service.interfaces.UserService;
import com.example.demo.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;
  private final ConversationRepository conversationRepository;

  @Override
  public List<UserDto> findAll() {
    log.debug("Finding all users");
    return userRepository.findAll()
            .stream()
            .map(userMapper::toDto)
            .toList();
  }

  @Override
  public Optional<UserDto> findById(Long id) {
    log.debug("Finding user by id={}", id);
    return userRepository.findById(id)
            .map(userMapper::toDto);
  }

  @Override
  public List<UserDto> findAllById(Iterable<Long> ids) {
    return userRepository.findAllById(ids)
            .stream()
            .map(userMapper::toDto)
            .toList();
  }

  @Override
  @Transactional
  public String authenticateUser(UserRequest request) {
    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
            )
    );
    final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    log.info("Login username from token: {}", userDetails.getUsername());
    User user = userRepository.findByUsername(userDetails.getUsername());
    if (user != null) {
      user.setLastLogin(LocalDateTime.now());
      userRepository.save(user);
    }

    return jwtUtil.generateToken(userDetails.getUsername());
  }

  @Override
  public UserDto findByUsername(String username) {
    User user = userRepository.findByUsername(username);

    return userMapper.toDto(user);
  }


  @Override
  @Transactional
  public UserDto save(UserRequest request) {
    if (userRepository.existsByUsername(request.getUsername())) {
      throw new IllegalArgumentException("User exists");
    }

    User user = User.builder()
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))
            .fullName(request.getFullName())
            .role(UserRole.ROLE_USER)
            .isActive(true)
            .createdAt(LocalDateTime.now())
            .build();

    User savedUser = userRepository.save(user);
    log.info("User created with id={}", savedUser.getId());

    return userMapper.toDto(savedUser);
  }

  @Override
  @Transactional
  public UserDto update(Long id, UpdateUserRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("User request cannot be null");
    }

    User user = userRepository.findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException("User", "id", id)
            );

    user.setUsername(request.getUsername());
    user.setFullName(request.getFullName());
    user.setRole(request.getRole());

    if (request.getPassword() != null && !request.getPassword().isBlank()) {
      user.setPassword(request.getPassword()); // ⚠️ hash позже
    }

    User updatedUser = userRepository.save(user);
    log.info("User updated with id={}", updatedUser.getId());

    return userMapper.toDto(updatedUser);
  }

  @Override
  @Transactional
  public void delete(Long id) {
    User user = userRepository.findById(id)
            .orElseThrow(() ->
                    new ResourceNotFoundException("User", "id", id)
            );

    conversationRepository.deleteByUser(user);
    userRepository.delete(user);

    log.info("User deleted with id={}", id);
  }
}