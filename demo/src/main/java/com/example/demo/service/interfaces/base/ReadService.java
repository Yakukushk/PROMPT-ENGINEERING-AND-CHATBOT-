package com.example.demo.service.interfaces.base;

import java.util.List;
import java.util.Optional;

public interface ReadService<T, ID> {
  List<T> findAll();
  Optional<T> findById(ID id);
  List<T> findAllById(Iterable<ID> ids);
}
