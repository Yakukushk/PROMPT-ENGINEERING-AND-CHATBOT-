package com.example.demo.service;

import com.example.demo.dto.SystemPromptDto;
import com.example.demo.dto.request.SystemPromptRequest;
import com.example.demo.dto.request.update.UpdateSystemPromptRequest;
import com.example.demo.entity.Conversation;
import com.example.demo.entity.SystemPrompt;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ConversationRepository;
import com.example.demo.repository.SystemPromptRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.interfaces.SystemPromptService;
import com.example.demo.service.mapper.SystemPromptMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class SystemPromptServiceImpl implements SystemPromptService {
  private final SystemPromptMapper systemPromptMapper;
  private final SystemPromptRepository systemPromptRepository;
  private final UserRepository userRepository;
  private final ConversationRepository conversationRepository;

//  @Override
//  public List<SystemPromptDto> getSystemPromptsByUser(Long userId) {
//    User user = userRepository.findById(userId)
//            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
//
//    return systemPromptRepository.findSystemPromptByUser(user)
//            .stream()
//            .map(systemPromptMapper::toDto)
//            .toList();
//  }

  @Override
  public List<SystemPromptDto> findAll() {
    return systemPromptRepository.findAll()
            .stream()
            .map(systemPromptMapper::toDto)
            .toList();
  }

  @Override
  public Optional<SystemPromptDto> findById(Long id) {
    return systemPromptRepository.findById(id)
            .map(systemPromptMapper::toDto);

  }

  @Override
  public List<SystemPromptDto> findAllById(Iterable<Long> ids) {
    return systemPromptRepository.findAllById(ids)
            .stream().map(systemPromptMapper::toDto)
            .toList();
  }

  @Override
  @Transactional
  public SystemPromptDto save(SystemPromptRequest request) {
    if(request == null) {
      throw new IllegalArgumentException("System Prompt required");
    }

    User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

    SystemPrompt systemPrompt = SystemPrompt.builder()
            .user(user)
            .active(true)
            .createdAt(LocalDateTime.now())
            .template(request.getTemplate())
            .version(request.getVersion())
            .name(request.getName())
            .code("USER_" + user.getId() + "_" + UUID.randomUUID())
            .system(false)
            .build();

    SystemPrompt saveSystemPrompt = systemPromptRepository.save(systemPrompt);
    return systemPromptMapper.toDto(saveSystemPrompt);
  }

  @Override
  @Transactional
  public SystemPromptDto update(Long id, UpdateSystemPromptRequest request) {
    SystemPrompt systemPrompt = systemPromptRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("System Prompt", "id", id));

    User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

    systemPrompt.setUser(user);
    systemPrompt.setTemplate(request.getTemplate());
    systemPrompt.setName(request.getName());
    systemPrompt.setActive(request.getActive());

    SystemPrompt updatedSystemPrompt = systemPromptRepository.save(systemPrompt);
    return systemPromptMapper.toDto(updatedSystemPrompt);
  }

  @Override
  @Transactional
  public void delete(Long id) {
    SystemPrompt systemPrompt = systemPromptRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("System Prompt", "id", id));

   systemPromptRepository.delete(systemPrompt);
  }

  @Override
  public String getPromptForConversation(Long conversationId, String context) {
    Conversation conversation = conversationRepository.findById(conversationId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Conversation", "id", conversationId)
            );

    SystemPrompt prompt = conversation.getSystemPrompt();

    if (prompt == null || !prompt.getActive()) {
      throw new IllegalStateException("No active system prompt for conversation");
    }

    return prompt.getTemplate()
            .replace("{{context}}", context == null ? "" : context);
  }
}
