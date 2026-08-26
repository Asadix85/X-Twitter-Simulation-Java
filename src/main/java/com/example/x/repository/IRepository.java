package com.example.x.repository;

import java.util.List;

public interface IRepository<T> {
    boolean add(T entity);
    boolean delete(String id);
    boolean update(String id, T entity);
    List<T> findAll();
    T findById(String id);
}