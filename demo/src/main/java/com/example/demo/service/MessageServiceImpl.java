package com.example.demo.service;

import com.example.demo.dto.MessageDto;
import com.example.demo.dto.request.MessageRequest;
import com.example.demo.dto.request.update.UpdateMessageRequest;
import com.example.demo.entity.Conversation;
import com.example.demo.entity.Message;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ConversationRepository;
import com.example.demo.repository.MessageRepository;
import com.example.demo.service.interfaces.MessageService;
import com.example.demo.service.mapper.MessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageServiceImpl implements MessageService {

  private final MessageRepository messageRepository;
  private final ConversationRepository conversationRepository;
  private final MessageMapper messageMapper;

  @Override
  public List<MessageDto> findAll() {
    log.debug("Finding all messages");
    return messageRepository.findAll().stream()
            .map(messageMapper::toDto)
            .toList();
  }

  @Override
  public Optional<MessageDto> findById(Long id) {
    return messageRepository.findById(id)
            .map(messageMapper::toDto);
  }

  @Override
  public List<MessageDto> findAllById(Iterable<Long> ids) {
    return messageRepository.findAllById(ids).stream()
            .map(messageMapper::toDto)
            .toList();
  }

  @Override
  public List<MessageDto> findByConversationId(Long conversationId) {
    return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId).stream()
            .map(messageMapper::toDto)
            .toList();
  }

  @Override
  @Transactional
  public MessageDto save(MessageRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("Message request cannot be null");
    }
    Conversation conversation = conversationRepository.findById(request.getConversationId())
            .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", request.getConversationId()));

    Message message = Message.builder()
            .conversation(conversation)
            .content(request.getContent())
            .createdAt(LocalDateTime.now())
            .role(request.getRole())
            .build();
    Message savedMessage = messageRepository.save(message);

    conversation.setMessageCount(conversation.getMessageCount() + 1);
    conversation.setUpdatedAt(LocalDateTime.now());
    conversationRepository.save(conversation);

    return messageMapper.toDto(savedMessage);
  }

  @Override
  public MessageDto update(Long id, UpdateMessageRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("Message request cannot be null");
    }

    Message message = messageRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Message", "id", id));

    if (!message.getConversation().getId().equals(request.getConversationId())) {
      Conversation newConversation = conversationRepository.findById(request.getConversationId())
              .orElseThrow(() -> new ResourceNotFoundException("Conversation", "id", request.getConversationId()));
      message.setConversation(newConversation);
    }

    message.setContent(request.getContent());
    Message updatedMessage = messageRepository.save(message);
    return messageMapper.toDto(updatedMessage);
  }

  @Override
  @Transactional
  public void delete(Long id) {
    Message message = messageRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Message", "id", id));

    Conversation conversation = message.getConversation();
    if (conversation.getMessageCount() > 0) {
      conversation.setMessageCount(conversation.getMessageCount() - 1);
      conversation.setUpdatedAt(LocalDateTime.now());
      conversationRepository.save(conversation);
    }

    messageRepository.delete(message);
  }
}
