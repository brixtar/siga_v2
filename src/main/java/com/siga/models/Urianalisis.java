// src/main/java/com/siga/models/Urianalisis.java

package com.siga.models;

import com.siga.models.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Urianalisis extends BaseEntity {
    private Long consultaId;
    private Double densidad;
    private Double pH;
    private String proteinas;
    private String glucosa;
    private String observaciones;

    public void validar() {
        if (consultaId == null) {
            throw new IllegalArgumentException("La consulta asociada es obligatoria");
        }
        if (densidad == null || densidad < 0) {
            throw new IllegalArgumentException("La densidad debe ser un valor positivo");
        }
        if (pH == null || pH < 0 || pH > 14) {
            throw new IllegalArgumentException("El pH debe estar entre 0 y 14");
        }
    }
}