// src/main/java/com/siga/models/QuimicaClinica.java

package com.siga.models;

import com.siga.models.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class QuimicaClinica extends BaseEntity {
    private Long consultaId;
    private Double glucosa;
    private Double urea;
    private Double creatinina;
    private Double colesterol;
    private String observaciones;

    public void validar() {
        if (consultaId == null) {
            throw new IllegalArgumentException("La consulta asociada es obligatoria");
        }
        if (glucosa == null || glucosa < 0) {
            throw new IllegalArgumentException("La glucosa debe ser un valor positivo");
        }
        if (urea == null || urea < 0) {
            throw new IllegalArgumentException("La urea debe ser un valor positivo");
        }
    }
}