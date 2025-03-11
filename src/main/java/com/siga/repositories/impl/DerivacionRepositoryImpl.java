package com.siga.repository.impl;

import com.siga.models.Derivacion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DerivacionRepositoryImpl extends BaseRepositoryImpl<Derivacion, Long> {
    private static final Logger log = LoggerFactory.getLogger(DerivacionRepositoryImpl.class);
    private final Map<Long, Derivacion> cache = new ConcurrentHashMap<>();

    public DerivacionRepositoryImpl(Connection connection) {
        super(connection);
    }

    @Override
    public Derivacion save(Derivacion derivacion) {
        log.info("Guardando derivación: {}", derivacion);
        validarDerivacion(derivacion);
        
        String sql = "INSERT INTO derivacion (consulta_id, doctor_id, fecha_derivacion, " +
                    "motivo, observaciones) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setDerivacionParameters(stmt, derivacion);
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    derivacion.setId(rs.getLong(1));
                    cache.put(derivacion.getId(), derivacion);
                }
            }
            
            return derivacion;
        } catch (SQLException e) {
            log.error("Error al guardar derivación: {}", e.getMessage());
            throw new RuntimeException("Error al guardar derivación", e);
        }
    }

    @Override
    public Optional<Derivacion> findById(Long id) {
        log.info("Buscando derivación con ID: {}", id);
        
        Derivacion cachedDerivacion = cache.get(id);
        if (cachedDerivacion != null) {
            return Optional.of(cachedDerivacion);
        }

        String sql = "SELECT * FROM derivacion WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Derivacion derivacion = mapResultSetToDerivacion(rs);
                    cache.put(id, derivacion);
                    return Optional.of(derivacion);
                }
            }
        } catch (SQLException e) {
            log.error("Error al buscar derivación por ID: {}", e.getMessage());
            throw new RuntimeException("Error al buscar derivación", e);
        }
        
        return Optional.empty();
    }

    @Override
    public void update(Derivacion derivacion) {
        log.info("Actualizando derivación: {}", derivacion);
        validarDerivacion(derivacion);
        
        String sql = "UPDATE derivacion SET doctor_id = ?, fecha_derivacion = ?, " +
                    "motivo = ?, observaciones = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setDerivacionUpdateParameters(stmt, derivacion);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                cache.put(derivacion.getId(), derivacion);
            }
        } catch (SQLException e) {
            log.error("Error al actualizar derivación: {}", e.getMessage());
            throw new RuntimeException("Error al actualizar derivación", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        log.info("Eliminando derivación con ID: {}", id);
        
        String sql = "DELETE FROM derivacion WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                cache.remove(id);
            }
        } catch (SQLException e) {
            log.error("Error al eliminar derivación: {}", e.getMessage());
            throw new RuntimeException("Error al eliminar derivación", e);
        }
    }

    // Métodos de búsqueda específicos
    public List<Derivacion> findByDoctor(Long doctorId) {
        log.info("Buscando derivaciones por doctor ID: {}", doctorId);
        
        String sql = "SELECT * FROM derivacion WHERE doctor_id = ?";
        
        List<Derivacion> resultados = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, doctorId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    resultados.add(mapResultSetToDerivacion(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Error al buscar derivaciones por doctor: {}", e.getMessage());
            throw new RuntimeException("Error al buscar derivaciones", e);
        }
        
        return resultados;
    }

    // Métodos privados de utilidad
    private void validarDerivacion(Derivacion derivacion) {
        if (derivacion == null) {
            throw new IllegalArgumentException("La derivación no puede ser null");
        }
        if (derivacion.getDoctorId() == null) {
            throw new IllegalArgumentException("El ID del doctor es requerido");
        }
        if (derivacion.getFechaDerivacion() == null) {
            throw new IllegalArgumentException("La fecha de derivación es requerida");
        }
        // Agregar más validaciones según sea necesario
    }

    private void setDerivacionParameters(PreparedStatement stmt, Derivacion derivacion) throws SQLException {
        stmt.setLong(1, derivacion.getConsultaId());
        stmt.setLong(2, derivacion.getDoctorId());
        stmt.setDate(3, Date.valueOf(derivacion.getFechaDerivacion()));
        stmt.setString(4, derivacion.getMotivo());
        stmt.setString(5, derivacion.getObservaciones());
    }

    private void setDerivacionUpdateParameters(PreparedStatement stmt, Derivacion derivacion) throws SQLException {
        stmt.setLong(1, derivacion.getDoctorId());
        stmt.setDate(2, Date.valueOf(derivacion.getFechaDerivacion()));
        stmt.setString(3, derivacion.getMotivo());
        stmt.setString(4, derivacion.getObservaciones());
        stmt.setLong(5, derivacion.getId());
    }

    private Derivacion mapResultSetToDerivacion(ResultSet rs) throws SQLException {
        Derivacion derivacion = new Derivacion();
        derivacion.setId(rs.getLong("id"));
        derivacion.setConsultaId(rs.getLong("consulta_id"));
        derivacion.setDoctorId(rs.getLong("doctor_id"));
        derivacion.setFechaDerivacion(rs.getDate("fecha_derivacion").toLocalDate());
        derivacion.setMotivo(rs.getString("motivo"));
        derivacion.setObservaciones(rs.getString("observaciones"));
        return derivacion;
    }
}