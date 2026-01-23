package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.service.interfaces.UserService;

import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {
  @Override
  public List<User> findAll() {
    return List.of();
  }

  @Override
  public Optional<User> findById(Long id) {
    return Optional.empty();
  }

  @Override
  public List<User> findAllById(Iterable<Long> ids) {
    return List.of();
  }

  @Override
  public User save(User entity) {
    return null;
  }

  @Override
  public User update(Long id, User entity) {
    return null;
  }

  @Override
  public void delete(User entity) {

  }
}
