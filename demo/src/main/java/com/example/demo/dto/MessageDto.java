package com.example.demo.dto;

import com.example.demo.entity.enums.MessageRole;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDto {
  private Long id;
  private Long conversationId;
  private String content;
  private MessageRole role;
  private LocalDateTime createdAt;
}
