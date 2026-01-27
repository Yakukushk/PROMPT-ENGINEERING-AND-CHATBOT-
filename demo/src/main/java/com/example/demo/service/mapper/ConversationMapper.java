package com.example.demo.service.mapper;

import com.example.demo.dto.ConversationDto;
import com.example.demo.entity.Conversation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConversationMapper {
  private final MessageMapper messageMapper;
  private final DocumentMapper documentMapper;

  public ConversationDto toDto(Conversation conversation) {
    if (conversation == null) {
      return null;
    }

    return ConversationDto.builder()
            .conversationId(conversation.getId())
            .title(conversation.getTitle())
            .userId(
                    conversation.getUser() != null
                            ? conversation.getUser().getId()
                            : null
            )
            .messageCount(conversation.getMessageCount())
            .createdAt(conversation.getCreatedAt())
            .updatedAt(conversation.getUpdatedAt())
            .documents(documentMapper.toDtoList(conversation.getDocuments()))
            .messages(messageMapper.toDtoList(conversation.getMessages()))
            .build();
  }
}
