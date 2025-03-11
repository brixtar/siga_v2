// src/main/java/com/siga/models/Derivacion.java

package com.siga.models;

import com.siga.models.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class Derivacion extends BaseEntity {
    private String codigo;
    private LocalDateTime fecha;
    private String motivo;
    private String observaciones;
    private Long animalId;
    private Long doctorId;
    private Long alumnoId;

    public void validar() {
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("El motivo de la derivación es obligatorio");
        }
        if (animalId == null) {
            throw new IllegalArgumentException("El animal es obligatorio");
        }
        if (doctorId == null) {
            throw new IllegalArgumentException("El doctor es obligatorio");
        }
    }
}