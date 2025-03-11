// src/main/java/com/siga/models/Alumno.java

package com.siga.models;

import com.siga.models.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Alumno extends BaseEntity {
    private String dni;
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
    private String direccion;

    public void validar() {
        if (dni == null || dni.trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI del alumno es obligatorio");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del alumno es obligatorio");
        }
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido del alumno es obligatorio");
        }
    }
}