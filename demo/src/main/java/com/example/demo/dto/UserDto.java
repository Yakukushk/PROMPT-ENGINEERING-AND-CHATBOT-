package com.example.demo.dto;

import com.example.demo.entity.enums.UserRole;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
  private String username;
  private String password;
  private String fullName;
  private UserRole role;
  private Boolean isActive = true;
  private LocalDateTime createdAt = LocalDateTime.now();
  private LocalDateTime lastLogin;
}
