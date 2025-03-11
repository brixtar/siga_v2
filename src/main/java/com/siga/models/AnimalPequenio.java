// src/main/java/com/siga/models/AnimalPequenio.java

package com.siga.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;
import java.time.Period;

@Data
@EqualsAndHashCode(callSuper = true)
public class AnimalPequenio extends Animal {
    private Boolean esterilizado;
    private String microchip;

    @Override
    public int calcularEdad() {
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    @Override
    public void validar() {
        validarBase();
        // Validaciones específicas para animales pequeños
        if (esterilizado == null) {
            throw new IllegalArgumentException("Debe especificar si está esterilizado");
        }
    }
}