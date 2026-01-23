package com.example.demo.service.interfaces.base;

public interface WriteService<T, ID, RQ> {
  T save(RQ request);
  T update(ID id, RQ request);
  void delete(ID id);
}
