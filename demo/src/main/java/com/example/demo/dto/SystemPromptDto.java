package com.example.demo.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemPromptDto {
  private Long id;
  private String name;
  private String template;
  private Integer version;
  private Boolean active;
  private LocalDateTime createdAt;
  private Long userId;
  private String code;
  private Boolean system;
}
