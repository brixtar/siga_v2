// src/main/java/com/siga/repository/BaseRepository.java

package com.siga.repository;

import java.util.List;
import java.util.Optional;

public interface BaseRepository<T, ID> {
    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    void delete(ID id);
    boolean exists(ID id);
    void update(T entity);
}