package com.example.demo.service.interfaces;

import com.example.demo.dto.request.ConversationRequest;
import com.example.demo.entity.Conversation;
import com.example.demo.service.interfaces.base.CrudService;

public interface ConversationService extends CrudService<Conversation, Long, ConversationRequest> {
  Conversation addMessageCount(Long conversationId);
}
