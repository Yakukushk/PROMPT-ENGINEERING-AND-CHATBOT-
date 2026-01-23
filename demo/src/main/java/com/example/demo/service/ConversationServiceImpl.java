package com.example.demo.service;

import com.example.demo.dto.request.ConversationRequest;
import com.example.demo.entity.Conversation;
import com.example.demo.entity.Message;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ConversationRepository;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.interfaces.ConversationService;
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

  @Override
  public List<Conversation> findAll() {
    return conversationRepository.findAll();
  }

  @Override
  public Optional<Conversation> findById(Long id) {
    return conversationRepository.findById(id);
  }

  @Override
  public List<Conversation> findAllById(Iterable<Long> ids) {
    return conversationRepository.findAllById(ids);
  }

  @Override
  @Transactional
  public Conversation save(ConversationRequest request) {

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
            .messageCount(request.getInitialMessageCount() != null ?
                    request.getInitialMessageCount() : 0)
            .build();

    return conversationRepository.save(conversation);
  }

  @Override
  @Transactional
  public Conversation update(Long id, ConversationRequest request) {
    Conversation conversation = findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", id));

    User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

    conversation.setUser(user);
    conversation.setTitle(request.getTitle());
    conversation.setUpdatedAt(LocalDateTime.now());

    if (request.getInitialMessageCount() != null) {
      conversation.setMessageCount(request.getInitialMessageCount());
    }

    return conversationRepository.save(conversation);
  }

  @Override
  @Transactional
  public void delete(Long conversationId) {
    List<Message> messages = messageRepository.findByConversationId(conversationId);
    if (!messages.isEmpty()) {
      messageRepository.deleteAll(messages);

      Conversation conversation = conversationRepository.findById(conversationId)
              .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId));

      conversationRepository.delete(conversation);
      conversationRepository.save(conversation);
    }
  }

  @Override
  @Transactional
  public Conversation addMessageCount(Long conversationId) {
    Conversation conversation = conversationRepository.findById(conversationId)
            .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", conversationId));

    conversation.setMessageCount(conversation.getMessageCount() + 1);
    conversation.setUpdatedAt(LocalDateTime.now());

    return conversationRepository.save(conversation);
  }

  @Transactional
  public void incrementMessageCount(Long conversationId) {
    conversationRepository.incrementMessageCount(conversationId);
  }
}

