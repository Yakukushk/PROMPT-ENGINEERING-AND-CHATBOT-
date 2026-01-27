package com.example.demo.service.interfaces;

import com.example.demo.dto.ConversationDto;
import com.example.demo.dto.request.ConversationRequest;
import com.example.demo.dto.request.update.UpdateConversationRequest;
import com.example.demo.service.interfaces.base.CrudService;

import java.util.List;

public interface ConversationService extends CrudService<ConversationDto, Long, ConversationRequest, UpdateConversationRequest> {
  List<ConversationDto> findConversationByUsername(String username);
}
