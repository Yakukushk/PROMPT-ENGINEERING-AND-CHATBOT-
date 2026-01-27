package com.example.demo.controller;

import com.example.demo.dto.UserDto;
import com.example.demo.dto.request.UserRequest;
import com.example.demo.entity.User;
import com.example.demo.entity.enums.UserRole;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserService userService;
  private final JwtUtil jwtUtil;

  @PostMapping("/signin")
  public String authenticateUser(@RequestBody UserRequest request) {
    return userService.authenticateUser(request);
  }

  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@RequestBody UserRequest userDto) {
    userService.save(userDto);
    return ResponseEntity.ok("User was created");
  }
}
