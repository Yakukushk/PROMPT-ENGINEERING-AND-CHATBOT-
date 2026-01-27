package com.example.demo.dto.request.update;

import com.example.demo.entity.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
  private String username;
  private String password;
  private String fullName;
  private UserRole role;
}
