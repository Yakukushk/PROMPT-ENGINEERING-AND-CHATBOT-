package com.example.demo.dto.request.update;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSystemPromptRequest {
  private String name;
  private String template;
  private Integer version;
  private Boolean active;
  private Long userId;
}
