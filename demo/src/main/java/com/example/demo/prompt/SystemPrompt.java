package com.example.demo.prompt;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SystemPrompt {

  public String getSystemPrompt(String context) {
    return SystemPromptType.UNIVERSITY_ASSISTANT.format(context);
  }
}

