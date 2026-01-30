package com.example.demo.service.mapper;

import com.example.demo.dto.MessageDto;
import com.example.demo.entity.Message;
import java.util.Collections;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MessageMapper {

  public MessageDto toDto(Message message) {
    if (message == null) {
      return null;
    }

    return MessageDto.builder()
            .id(message.getId())
            .content(message.getContent())
            .conversationId(
                    message.getConversation() != null
                            ? message.getConversation().getId()
                            : null
            )
            .role(message.getRole())
            .createdAt(message.getCreatedAt())
            .build();
  }

  public List<MessageDto> toDtoList(List<Message> messages) {
    if (messages == null) {
      return Collections.emptyList();
    }

    return messages.stream()
            .map(this::toDto)
            .toList();
  }

}
