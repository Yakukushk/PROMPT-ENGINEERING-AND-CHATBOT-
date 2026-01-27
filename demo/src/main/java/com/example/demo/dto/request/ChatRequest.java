package com.example.demo.dto.request;

import com.example.demo.entity.enums.MessageRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest
{
  private Long conversationId;
  private String message;
  private MessageRole role;
}
