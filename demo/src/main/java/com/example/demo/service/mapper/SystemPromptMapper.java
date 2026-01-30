package com.example.demo.service.mapper;

import com.example.demo.dto.SystemPromptDto;
import com.example.demo.entity.SystemPrompt;
import org.springframework.stereotype.Component;

@Component
public class SystemPromptMapper {
  public SystemPromptDto toDto(SystemPrompt systemPrompt) {
    if (systemPrompt == null) {
      return null;
    }

    return SystemPromptDto.builder()
            .id(systemPrompt.getId())
            .active(systemPrompt.getActive())
            .name(systemPrompt.getName())
            .template(systemPrompt.getTemplate())
            .version(systemPrompt.getVersion())
            .code(systemPrompt.getCode())
            .system(systemPrompt.getSystem())
            .createdAt(systemPrompt.getCreatedAt())
            .userId(systemPrompt.getUser() != null
                    ? systemPrompt.getUser().getId() : null)
            .build();
  }
}
