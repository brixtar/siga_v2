// src/main/java/com/siga/models/Doctor.java

package com.siga.models;

import com.siga.models.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Doctor extends BaseEntity {
    private String dni;
    private String nombre;
    private String apellido;
    private String matricula;
    private String telefono;
    private String email;
    private String direccion;
    private Boolean activo;

    // Constructor
    public Doctor() {
        this.activo = true;
    }

    // Método para obtener nombre completo
    public String getNombreCompleto() {
        return String.format("%s, %s", apellido, nombre);
    }

    // Método para validar el objeto
    public void validar() throws IllegalArgumentException {
        if (dni == null || dni.trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI es obligatorio");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido es obligatorio");
        }
        if (matricula == null || matricula.trim().isEmpty()) {
            throw new IllegalArgumentException("La matrícula es obligatoria");
        }
    }
}