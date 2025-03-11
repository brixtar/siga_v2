// src/main/java/com/siga/models/Consulta.java

package com.siga.models;

import com.siga.models.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class Consulta extends BaseEntity {
    private String codigo;
    private LocalDateTime fecha;
    private String motivo;
    private String diagnostico;
    private String tratamiento;
    private String observaciones;
    private Long animalId;
    private Long doctorId;
    private Long alumnoId;
    private String tipo; // "PEQUENIO" o "GRANDE"
    private Boolean finalizada;

    // Constructor
    public Consulta() {
        this.fecha = LocalDateTime.now();
        this.finalizada = false;
    }

    public void validar() {
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("El motivo es obligatorio");
        }
        if (animalId == null) {
            throw new IllegalArgumentException("El animal es obligatorio");
        }
        if (doctorId == null) {
            throw new IllegalArgumentException("El doctor es obligatorio");
        }
        if (tipo == null || (!tipo.equals("PEQUENIO") && !tipo.equals("GRANDE"))) {
            throw new IllegalArgumentException("El tipo de consulta debe ser PEQUENIO o GRANDE");
        }
    }
}