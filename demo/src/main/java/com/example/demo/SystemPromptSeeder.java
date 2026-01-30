package com.example.demo;

import com.example.demo.entity.SystemPrompt;
import com.example.demo.prompt.DefaultSystemPrompt;
import com.example.demo.repository.SystemPromptRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SystemPromptSeeder implements CommandLineRunner {

  private final SystemPromptRepository repository;

  @Override
  public void run(String... args) {
    for (DefaultSystemPrompt prompt : DefaultSystemPrompt.values()) {

      repository.findByCode(prompt.getCode())
              .ifPresentOrElse(
                      existing -> {
                        if (existing.getVersion() < prompt.getVersion()) {
                          log.info("Updating system prompt {}", prompt.getCode());
                          existing.setTemplate(prompt.getTemplate());
                          existing.setVersion(prompt.getVersion());
                          repository.save(existing);
                        }
                      },
                      () -> {
                        log.info("Seeding system prompt {}", prompt.getCode());
                        repository.save(
                                SystemPrompt.builder()
                                        .code(prompt.getCode())
                                        .name(prompt.getName())
                                        .template(prompt.getTemplate())
                                        .version(prompt.getVersion())
                                        .active(true)
                                        .system(true)
                                        .build()
                        );
                      }
              );
    }
  }
}

