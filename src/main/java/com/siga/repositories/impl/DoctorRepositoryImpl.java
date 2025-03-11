// src/main/java/com/siga/repository/impl/DoctorRepositoryImpl.java

package com.siga.repository.impl;

import com.siga.models.Doctor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DoctorRepositoryImpl extends BaseRepositoryImpl<Doctor, Long> {
    
    @Override
    public Doctor save(Doctor doctor) {
        String sql = "INSERT INTO doctor (dni, nombre, apellido, matricula, telefono, email, direccion, activo) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, doctor.getDni());
            stmt.setString(2, doctor.getNombre());
            stmt.setString(3, doctor.getApellido());
            stmt.setString(4, doctor.getMatricula());
            stmt.setString(5, doctor.getTelefono());
            stmt.setString(6, doctor.getEmail());
            stmt.setString(7, doctor.getDireccion());
            stmt.setBoolean(8, doctor.getActivo());
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    doctor.setId(rs.getLong(1));
                }
            }
            
            return doctor;
        } catch (SQLException e) {
            handleSQLException(e, "save doctor");
            return null;
        }
    }

    @Override
    public Optional<Doctor> findById(Long id) {
        String sql = "SELECT * FROM doctor WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToDoctor(rs));
                }
            }
        } catch (SQLException e) {
            handleSQLException(e, "find doctor by id");
        }
        
        return Optional.empty();
    }

    @Override
    public List<Doctor> findAll() {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctor WHERE activo = true";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                doctors.add(mapResultSetToDoctor(rs));
            }
        } catch (SQLException e) {
            handleSQLException(e, "find all doctors");
        }
        
        return doctors;
    }

    @Override
    public void delete(Long id) {
        String sql = "UPDATE doctor SET activo = false WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            handleSQLException(e, "delete doctor");
        }
    }

    @Override
    public boolean exists(Long id) {
        String sql = "SELECT COUNT(*) FROM doctor WHERE id = ? AND activo = true";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            handleSQLException(e, "check doctor exists");
        }
        
        return false;
    }

    @Override
    public void update(Doctor doctor) {
        String sql = "UPDATE doctor SET dni = ?, nombre = ?, apellido = ?, matricula = ?, " +
                    "telefono = ?, email = ?, direccion = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, doctor.getDni());
            stmt.setString(2, doctor.getNombre());
            stmt.setString(3, doctor.getApellido());
            stmt.setString(4, doctor.getMatricula());
            stmt.setString(5, doctor.getTelefono());
            stmt.setString(6, doctor.getEmail());
            stmt.setString(7, doctor.getDireccion());
            stmt.setLong(8, doctor.getId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            handleSQLException(e, "update doctor");
        }
    }

    private Doctor mapResultSetToDoctor(ResultSet rs) throws SQLException {
        Doctor doctor = new Doctor();
        doctor.setId(rs.getLong("id"));
        doctor.setDni(rs.getString("dni"));
        doctor.setNombre(rs.getString("nombre"));
        doctor.setApellido(rs.getString("apellido"));
        doctor.setMatricula(rs.getString("matricula"));
        doctor.setTelefono(rs.getString("telefono"));
        doctor.setEmail(rs.getString("email"));
        doctor.setDireccion(rs.getString("direccion"));
        doctor.setActivo(rs.getBoolean("activo"));
        return doctor;
    }
}