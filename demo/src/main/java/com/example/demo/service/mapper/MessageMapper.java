package com.example.demo.service.mapper;

import com.example.demo.dto.MessageDto;
import com.example.demo.entity.Message;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MessageMapper {

  public MessageDto toDto(Message message) {
    if (message == null) {
      return null;
    }

    return MessageDto.builder()
            .content(message.getContent())
            .conversationId(
                    message.getConversation() != null
                            ? message.getConversation().getId()
                            : null
            )
            .createdAt(message.getCreatedAt())
            .build();
  }

  public List<MessageDto> toDtoList(List<Message> messages) {
    if (messages == null) {
      return null;
    }

    return messages.stream()
            .map(this::toDto)
            .toList();
  }

}
