// src/main/java/com/siga/repositories/base/CrudRepository.java

package com.siga.repositories.base;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<T, ID> {
    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    void delete(ID id);
    boolean exists(ID id);
}