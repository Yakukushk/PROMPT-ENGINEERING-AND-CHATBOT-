package com.example.demo.service;

import com.example.demo.dto.ConversationDto;
import com.example.demo.dto.request.ConversationRequest;
import com.example.demo.dto.request.update.UpdateConversationRequest;
import com.example.demo.entity.Conversation;
import com.example.demo.entity.Document;
import com.example.demo.entity.Message;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ConversationRepository;
import com.example.demo.repository.DocumentRepository;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.interfaces.ConversationService;
import com.example.demo.service.mapper.ConversationMapper;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConversationServiceImpl implements ConversationService {
  private final ConversationRepository conversationRepository;
  private final UserRepository userRepository;
  private final MessageRepository messageRepository;
  private final ConversationMapper conversationMapper;
  private final DocumentRepository documentRepository;

  @Override
  public List<ConversationDto> findAll() {
    return conversationRepository.findAll().stream()
            .map(conversationMapper::toDto)
            .toList();
  }

  @Override
  public Optional<ConversationDto> findById(Long id) {
    return conversationRepository.findById(id)
            .map(conversationMapper::toDto);
  }

  @Override
  public List<ConversationDto> findAllById(Iterable<Long> ids) {
    return conversationRepository.findAllById(ids)
            .stream().map(conversationMapper::toDto)
            .toList();
  }

  @Override
  @Transactional
  public ConversationDto save(ConversationRequest request) {

    if (request == null) {
      throw new IllegalArgumentException("Conversation request cannot be null");
    }

    User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

    Conversation conversation = Conversation.builder()
            .user(user)
            .title(request.getTitle())
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .messageCount(0)
            .build();

    Conversation saveConversation = conversationRepository.save(conversation);
    return conversationMapper.toDto(saveConversation);
  }

  @Override
  @Transactional
  public ConversationDto update(Long id, UpdateConversationRequest request) {

    Conversation conversation = conversationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", id));

    User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

    conversation.setUser(user);
    conversation.setTitle(request.getTitle());
    conversation.setUpdatedAt(LocalDateTime.now());

    if (request.getInitialMessageCount() != null) {
      conversation.setMessageCount(request.getInitialMessageCount());
    }

    Conversation updated = conversationRepository.save(conversation);
    return conversationMapper.toDto(updated);
  }

  @Override
  @Transactional
  public void delete(Long conversationId) {
    log.debug("Deleting conversation with id: {}", conversationId);

    Conversation conversation = conversationRepository.findById(conversationId)
            .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId));
    
    conversationRepository.delete(conversation);

    log.info("Conversation and all related messages/documents deleted with id: {}", conversationId);
  }

  @Override
  public List<ConversationDto> findConversationByUsername(String username) {
    User user = userRepository.findByUsername(username);
    return conversationRepository.findConversationByUser(user)
            .stream().map(conversationMapper::toDto).toList();
  }

  @Transactional
  public void incrementMessageCount(Long conversationId) {
    conversationRepository.incrementMessageCount(conversationId);
  }
}

