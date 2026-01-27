package com.example.demo.dto.request;

import com.example.demo.entity.enums.MessageRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageRequest {
  private Long conversationId;
  private String content;
  private MessageRole role;
}