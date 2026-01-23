package com.example.demo.mapper;

import com.example.demo.dto.MessageDto;
import com.example.demo.entity.Message;
import org.mapstruct.Mapper;

@Mapper
public interface MessageMapper {
  MessageDto toDto(Message message);
  Message toEntity(MessageDto messageDto);
}
