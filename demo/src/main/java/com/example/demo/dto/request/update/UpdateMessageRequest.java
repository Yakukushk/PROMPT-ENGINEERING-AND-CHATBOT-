package com.example.demo.dto.request.update;

import com.example.demo.entity.enums.MessageRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateMessageRequest {
  private Long conversationId;
  private String content;
  private MessageRole role;
}
