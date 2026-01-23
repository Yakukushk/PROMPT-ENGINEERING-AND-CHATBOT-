package com.example.demo.mapper;

import com.example.demo.dto.ConversationDto;
import com.example.demo.entity.Conversation;
import org.mapstruct.Mapper;

@Mapper
public interface ConversationMapper {
  ConversationDto toDto(Conversation conversation);
  Conversation toEntity(ConversationDto conversationDto);
}
