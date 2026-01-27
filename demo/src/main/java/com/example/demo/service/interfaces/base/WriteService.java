package com.example.demo.service.interfaces.base;

public interface WriteService<T, ID, RQ, RU> {
  T save(RQ request);
  T update(ID id, RU request);
  void delete(ID id);
}
