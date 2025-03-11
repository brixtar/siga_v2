// src/main/java/com/siga/repository/impl/AnimalRepositoryImpl.java

package com.siga.repository.impl;

import com.siga.models.Animal;
import com.siga.models.AnimalPequenio;
import com.siga.models.AnimalGrande;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AnimalRepositoryImpl extends BaseRepositoryImpl<Animal, Long> {
    
    @Override
    public Animal save(Animal animal) {
        connection.setAutoCommit(false);
        try {
            // Primero guardamos los datos comunes
            String sqlComun = "INSERT INTO animal (codigo, nombre, fecha_nacimiento, sexo, peso, " +
                            "color, observaciones, especie_id, raza_id, duenio_id, activo, tipo) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement stmt = connection.prepareStatement(sqlComun, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, animal.getCodigo());
                stmt.setString(2, animal.getNombre());
                stmt.setDate(3, Date.valueOf(animal.getFechaNacimiento()));
                stmt.setString(4, animal.getSexo());
                stmt.setDouble(5, animal.getPeso());
                stmt.setString(6, animal.getColor());
                stmt.setString(7, animal.getObservaciones());
                stmt.setLong(8, animal.getEspecieId());
                stmt.setLong(9, animal.getRazaId());
                stmt.setLong(10, animal.getDuenioId());
                stmt.setBoolean(11, animal.getActivo());
                stmt.setString(12, animal instanceof AnimalPequenio ? "P" : "G");
                
                stmt.executeUpdate();
                
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        animal.setId(rs.getLong(1));
                    }
                }
            }
            
            // Luego guardamos los datos específicos según el tipo
            if (animal instanceof AnimalPequenio) {
                saveAnimalPequenio((AnimalPequenio) animal);
            } else if (animal instanceof AnimalGrande) {
                saveAnimalGrande((AnimalGrande) animal);
            }
            
            connection.commit();
            return animal;
            
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                handleSQLException(ex, "rollback save animal");
            }
            handleSQLException(e, "save animal");
            return null;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                handleSQLException(e, "reset autocommit");
            }
        }
    }

    private void saveAnimalPequenio(AnimalPequenio animal) throws SQLException {
        String sql = "INSERT INTO animal_pequenio (animal_id, esterilizado, microchip) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, animal.getId());
            stmt.setBoolean(2, animal.getEsterilizado());
            stmt.setString(3, animal.getMicrochip());
            stmt.executeUpdate();
        }
    }

    private void saveAnimalGrande(AnimalGrande animal) throws SQLException {
        String sql = "INSERT INTO animal_grande (animal_id, numero_registro, proposito, altura) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, animal.getId());
            stmt.setString(2, animal.getNumeroRegistro());
            stmt.setString(3, animal.getProposito());
            stmt.setDouble(4, animal.getAltura());
            stmt.executeUpdate();
        }
    }

    // ... Implementar resto de métodos (findById, findAll, delete, exists, update)
}