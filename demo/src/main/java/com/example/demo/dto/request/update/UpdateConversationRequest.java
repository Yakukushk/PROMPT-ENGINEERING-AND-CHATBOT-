package com.example.demo.dto.request.update;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateConversationRequest {
  @Schema(description = "user id")
  private Long userId;
  @Schema(description = "conversation title")
  private String title;
  private Integer initialMessageCount;
  private Long systemPromptId;
}
