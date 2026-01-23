package com.example.demo.service;

import com.example.demo.entity.Document;
import com.example.demo.service.interfaces.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentServiceImpl implements DocumentService {


  @Override
  public List findAll() {
    return List.of();
  }

  @Override
  public Optional findById(Object o) {
    return Optional.empty();
  }

  @Override
  public List findAllById(Iterable iterable) {
    return List.of();
  }

  @Override
  public Object save(Object request) {
    return null;
  }

  @Override
  public Object update(Object o, Object request) {
    return null;
  }

  @Override
  public void delete(Object o) {

  }
}
