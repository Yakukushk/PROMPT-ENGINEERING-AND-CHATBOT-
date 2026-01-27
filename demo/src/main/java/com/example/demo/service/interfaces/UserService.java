package com.example.demo.service.interfaces;

import com.example.demo.dto.UserDto;
import com.example.demo.dto.request.UserRequest;
import com.example.demo.dto.request.update.UpdateUserRequest;
import com.example.demo.service.interfaces.base.CrudService;

public interface UserService extends CrudService<UserDto, Long, UserRequest, UpdateUserRequest> {
   String authenticateUser(UserRequest request);
   UserDto findByUsername(String username);
}
