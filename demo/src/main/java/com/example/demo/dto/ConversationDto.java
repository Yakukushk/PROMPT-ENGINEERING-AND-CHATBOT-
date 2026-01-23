package com.example.demo.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationDto {
  private Long userId;
  private String title;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Integer messageCount;
}
