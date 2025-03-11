// src/main/java/com/siga/models/Especie.java

package com.siga.models;

import com.siga.models.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Especie extends BaseEntity {
    private String nombre;
    private String descripcion;

    public void validar() {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la especie es obligatorio");
        }
    }
}