// src/main/java/com/siga/models/Animal.java

package com.siga.models;

import com.siga.models.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class Animal extends BaseEntity {
    protected String codigo;
    protected String nombre;
    protected LocalDate fechaNacimiento;
    protected String sexo;
    protected Double peso;
    protected String color;
    protected String observaciones;
    protected Long especieId;
    protected Long razaId;
    protected Long duenioId;
    protected Boolean activo;

    // Constructor
    public Animal() {
        this.activo = true;
    }

    // Método abstracto para calcular la edad
    public abstract int calcularEdad();

    // Método para validar campos básicos
    protected void validarBase() {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (fechaNacimiento == null) {
            throw new IllegalArgumentException("La fecha de nacimiento es obligatoria");
        }
        if (duenioId == null) {
            throw new IllegalArgumentException("El dueño es obligatorio");
        }
        if (especieId == null) {
            throw new IllegalArgumentException("La especie es obligatoria");
        }
        if (razaId == null) {
            throw new IllegalArgumentException("La raza es obligatoria");
        }
    }
}