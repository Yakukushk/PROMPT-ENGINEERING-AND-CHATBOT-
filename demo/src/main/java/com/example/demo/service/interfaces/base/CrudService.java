package com.example.demo.service.interfaces.base;

public interface CrudService<T, ID, RQ, RU> extends ReadService<T, ID>, WriteService<T, ID, RQ, RU> { }