package com.example.demo.service.interfaces;

import com.example.demo.dto.MessageDto;
import com.example.demo.dto.request.MessageRequest;
import com.example.demo.dto.request.update.UpdateMessageRequest;
import com.example.demo.service.interfaces.base.CrudService;

public interface MessageService extends CrudService<MessageDto, Long, MessageRequest, UpdateMessageRequest> { }
