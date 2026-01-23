package com.example.demo.entity;

import com.example.demo.entity.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "username", nullable = false)
  private String username;

  @Column(name = "password_hash", nullable = false, length = 255)
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String password;

  @Column(name = "full_name", length = 150)
  private String fullName;

  @Column(name = "user_role", length = 20)
  @Enumerated(EnumType.STRING)
  private UserRole role;

  @Column(name = "is_active")
  private Boolean isActive = true;

  @Column(name = "created_at")
  private LocalDateTime createdAt = LocalDateTime.now();

  @Column(name = "last_login")
  private LocalDateTime lastLogin;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    if (isActive == null) {
      isActive = true;
    }
  }
}
