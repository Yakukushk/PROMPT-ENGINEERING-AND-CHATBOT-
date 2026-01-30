package com.example.demo.dto;

import com.example.demo.entity.Message;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationDto {
  private Long conversationId;
  private Long userId;
  private String title;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Integer messageCount;
  private List<MessageDto> messages;
  private List<DocumentDto> documents;
  private Long systemPromptId;
}
