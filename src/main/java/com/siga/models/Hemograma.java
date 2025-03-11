// src/main/java/com/siga/models/Hemograma.java

package com.siga.models;

import com.siga.models.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Hemograma extends BaseEntity {
    private Long consultaId;
    private Double hematocrito;
    private Double hemoglobina;
    private Double leucocitos;
    private Double eritrocitos;
    private String observaciones;

    public void validar() {
        if (consultaId == null) {
            throw new IllegalArgumentException("La consulta asociada es obligatoria");
        }
        if (hematocrito == null || hematocrito < 0) {
            throw new IllegalArgumentException("El hematocrito debe ser un valor positivo");
        }
        if (hemoglobina == null || hemoglobina < 0) {
            throw new IllegalArgumentException("La hemoglobina debe ser un valor positivo");
        }
    }
}