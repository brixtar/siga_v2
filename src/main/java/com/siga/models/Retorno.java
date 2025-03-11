// src/main/java/com/siga/models/Retorno.java

package com.siga.models;

import com.siga.models.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class Retorno extends BaseEntity {
    private String codigo;
    private LocalDateTime fecha;
    private String motivo;
    private String observaciones;
    private Long derivacionId;
    private Long doctorId;

    public void validar() {
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("El motivo del retorno es obligatorio");
        }
        if (derivacionId == null) {
            throw new IllegalArgumentException("La derivación asociada es obligatoria");
        }
        if (doctorId == null) {
            throw new IllegalArgumentException("El doctor es obligatorio");
        }
    }
}