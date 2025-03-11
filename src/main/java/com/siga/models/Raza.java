// src/main/java/com/siga/models/Raza.java

package com.siga.models;

import com.siga.models.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Raza extends BaseEntity {
    private String nombre;
    private String descripcion;
    private Long especieId;

    public void validar() {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la raza es obligatorio");
        }
        if (especieId == null) {
            throw new IllegalArgumentException("La especie asociada es obligatoria");
        }
    }
}