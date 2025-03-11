package com.siga.repository.impl;

import com.siga.models.Retorno;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RetornoRepositoryImpl extends BaseRepositoryImpl<Retorno, Long> {
    private static final Logger log = LoggerFactory.getLogger(RetornoRepositoryImpl.class);
    private final Map<Long, Retorno> cache = new ConcurrentHashMap<>();

    public RetornoRepositoryImpl(Connection connection) {
        super(connection);
    }

    @Override
    public Retorno save(Retorno retorno) {
        log.info("Guardando retorno: {}", retorno);
        validarRetorno(retorno);
        
        String sql = "INSERT INTO retorno (derivacion_id, fecha_retorno, diagnostico, " +
                    "tratamiento, observaciones) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setRetornoParameters(stmt, retorno);
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    retorno.setId(rs.getLong(1));
                    cache.put(retorno.getId(), retorno);
                }
            }
            
            return retorno;
        } catch (SQLException e) {
            log.error("Error al guardar retorno: {}", e.getMessage());
            throw new RuntimeException("Error al guardar retorno", e);
        }
    }

    @Override
    public Optional<Retorno> findById(Long id) {
        log.info("Buscando retorno con ID: {}", id);
        
        Retorno cachedRetorno = cache.get(id);
        if (cachedRetorno != null) {
            return Optional.of(cachedRetorno);
        }

        String sql = "SELECT * FROM retorno WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Retorno retorno = mapResultSetToRetorno(rs);
                    cache.put(id, retorno);
                    return Optional.of(retorno);
                }
            }
        } catch (SQLException e) {
            log.error("Error al buscar retorno por ID: {}", e.getMessage());
            throw new RuntimeException("Error al buscar retorno", e);
        }
        
        return Optional.empty();
    }

    @Override
    public void update(Retorno retorno) {
        log.info("Actualizando retorno: {}", retorno);
        validarRetorno(retorno);
        
        String sql = "UPDATE retorno SET fecha_retorno = ?, diagnostico = ?, " +
                    "tratamiento = ?, observaciones = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setRetornoUpdateParameters(stmt, retorno);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                cache.put(retorno.getId(), retorno);
            }
        } catch (SQLException e) {
            log.error("Error al actualizar retorno: {}", e.getMessage());
            throw new RuntimeException("Error al actualizar retorno", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        log.info("Eliminando retorno con ID: {}", id);
        
        String sql = "DELETE FROM retorno WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                cache.remove(id);
            }
        } catch (SQLException e) {
            log.error("Error al eliminar retorno: {}", e.getMessage());
            throw new RuntimeException("Error al eliminar retorno", e);
        }
    }

    // Métodos de búsqueda específicos
    public List<Retorno> findByDerivacion(Long derivacionId) {
        log.info("Buscando retornos por derivación ID: {}", derivacionId);
        
        String sql = "SELECT * FROM retorno WHERE derivacion_id = ?";
        
        List<Retorno> resultados = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, derivacionId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    resultados.add(mapResultSetToRetorno(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Error al buscar retornos por derivación: {}", e.getMessage());
            throw new RuntimeException("Error al buscar retornos", e);
        }
        
        return resultados;
    }

    // Métodos privados de utilidad
    private void validarRetorno(Retorno retorno) {
        if (retorno == null) {
            throw new IllegalArgumentException("El retorno no puede ser null");
        }
        if (retorno.getDerivacionId() == null) {
            throw new IllegalArgumentException("El ID de derivación es requerido");
        }
        if (retorno.getFechaRetorno() == null) {
            throw new IllegalArgumentException("La fecha de retorno es requerida");
        }
        // Agregar más validaciones según sea necesario
    }

    private void setRetornoParameters(PreparedStatement stmt, Retorno retorno) throws SQLException {
        stmt.setLong(1, retorno.getDerivacionId());
        stmt.setDate(2, Date.valueOf(retorno.getFechaRetorno()));
        stmt.setString(3, retorno.getDiagnostico());
        stmt.setString(4, retorno.getTratamiento());
        stmt.setString(5, retorno.getObservaciones());
    }

    private void setRetornoUpdateParameters(PreparedStatement stmt, Retorno retorno) throws SQLException {
        stmt.setDate(1, Date.valueOf(retorno.getFechaRetorno()));
        stmt.setString(2, retorno.getDiagnostico());
        stmt.setString(3, retorno.getTratamiento());
        stmt.setString(4, retorno.getObservaciones());
        stmt.setLong(5, retorno.getId());
    }

    private Retorno mapResultSetToRetorno(ResultSet rs) throws SQLException {
        Retorno retorno = new Retorno();
        retorno.setId(rs.getLong("id"));
        retorno.setDerivacionId(rs.getLong("derivacion_id"));
        retorno.setFechaRetorno(rs.getDate("fecha_retorno").toLocalDate());
        retorno.setDiagnostico(rs.getString("diagnostico"));
        retorno.setTratamiento(rs.getString("tratamiento"));
        retorno.setObservaciones(rs.getString("observaciones"));
        return retorno;
    }
}