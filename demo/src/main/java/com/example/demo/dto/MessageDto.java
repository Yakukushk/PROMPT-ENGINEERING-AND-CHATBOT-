package com.example.demo.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDto {
  private Long conversationId;
  private String content;
  private LocalDateTime createdAt;
}
