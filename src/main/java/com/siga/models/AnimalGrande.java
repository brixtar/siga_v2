// src/main/java/com/siga/models/AnimalGrande.java

package com.siga.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;
import java.time.Period;

@Data
@EqualsAndHashCode(callSuper = true)
public class AnimalGrande extends Animal {
    private String numeroRegistro;
    private String proposito; // Ejemplo: "Reproducción", "Trabajo", "Deporte"
    private Double altura;

    @Override
    public int calcularEdad() {
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    @Override
    public void validar() {
        validarBase();
        // Validaciones específicas para animales grandes
        if (numeroRegistro == null || numeroRegistro.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de registro es obligatorio");
        }
        if (proposito == null || proposito.trim().isEmpty()) {
            throw new IllegalArgumentException("El propósito es obligatorio");
        }
    }
}