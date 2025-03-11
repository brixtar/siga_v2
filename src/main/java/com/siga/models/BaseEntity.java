// src/main/java/com/siga/models/base/BaseEntity.java

package com.siga.models.base;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public abstract class BaseEntity {
    protected Long id;
    protected Boolean estado;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
}