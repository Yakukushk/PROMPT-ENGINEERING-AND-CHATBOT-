package com.example.demo.mapper;

import com.example.demo.dto.DocumentDto;
import com.example.demo.entity.Document;
import org.mapstruct.Mapper;

@Mapper
public interface DocumentMapper {
  Document toDto(DocumentDto documentDto);
  DocumentDto toEntity(Document document);
}
