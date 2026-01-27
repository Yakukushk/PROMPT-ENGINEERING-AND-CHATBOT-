package com.example.demo.service.mapper;

import com.example.demo.dto.UserDto;
import com.example.demo.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  public UserDto toDto(User user) {
    if (user == null) {
      return null;
    }

    return UserDto.builder()
            .userId(user.getId())
            .username(user.getUsername())
            .fullName(user.getFullName())
            .password(user.getPassword())
            .role(user.getRole())
            .isActive(user.getIsActive())
            .createdAt(user.getCreatedAt())
            .lastLogin(user.getLastLogin())
            .build();
  }
}
