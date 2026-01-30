package com.example.demo.service.interfaces;

import com.example.demo.dto.SystemPromptDto;
import com.example.demo.dto.request.SystemPromptRequest;
import com.example.demo.dto.request.update.UpdateSystemPromptRequest;
import com.example.demo.service.interfaces.base.CrudService;

import java.util.List;

public interface SystemPromptService extends CrudService<SystemPromptDto, Long, SystemPromptRequest, UpdateSystemPromptRequest> {
   String getPromptForConversation(Long conversationId, String context);
}
