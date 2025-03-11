// src/main/java/com/siga/repository/impl/ConsultaRepositoryImpl.java

package com.siga.repository.impl;

import com.siga.models.Consulta;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConsultaRepositoryImpl extends BaseRepositoryImpl<Consulta, Long> {
    
    @Override
    public Consulta save(Consulta consulta) {
        String sql = "INSERT INTO consulta (codigo, fecha, motivo, diagnostico, tratamiento, " +
                    "observaciones, animal_id, doctor_id, alumno_id, tipo, finalizada) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, consulta.getCodigo());
            stmt.setTimestamp(2, Timestamp.valueOf(consulta.getFecha()));
            stmt.setString(3, consulta.getMotivo());
            stmt.setString(4, consulta.getDiagnostico());
            stmt.setString(5, consulta.getTratamiento());
            stmt.setString(6, consulta.getObservaciones());
            stmt.setLong(7, consulta.getAnimalId());
            stmt.setLong(8, consulta.getDoctorId());
            stmt.setObject(9, consulta.getAlumnoId()); // Puede ser null
            stmt.setString(10, consulta.getTipo());
            stmt.setBoolean(11, consulta.getFinalizada());
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    consulta.setId(rs.getLong(1));
                }
            }
            
            return consulta;
        } catch (SQLException e) {
            handleSQLException(e, "save consulta");
            return null;
        }
    }

    // ... Implementar resto de métodos (findById, findAll, delete, exists, update)
}