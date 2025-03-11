package com.siga.repository.impl;

import com.siga.models.Hemograma;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HemogramaRepositoryImpl extends BaseRepositoryImpl<Hemograma, Long> {
    private static final Logger log = LoggerFactory.getLogger(HemogramaRepositoryImpl.class);
    private final Map<Long, Hemograma> cache = new ConcurrentHashMap<>();

    // Constructor
    public HemogramaRepositoryImpl(Connection connection) {
        super(connection);
    }

    @Override
    public Hemograma save(Hemograma hemograma) {
        log.info("Guardando hemograma: {}", hemograma);
        validarHemograma(hemograma);
        
        String sql = "INSERT INTO hemograma (consulta_id, hematocrito, hemoglobina, leucocitos, " +
                    "eritrocitos, observaciones) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setHemogramaParameters(stmt, hemograma);
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    hemograma.setId(rs.getLong(1));
                    cache.put(hemograma.getId(), hemograma);
                }
            }
            
            return hemograma;
        } catch (SQLException e) {
            log.error("Error al guardar hemograma: {}", e.getMessage());
            throw new RuntimeException("Error al guardar hemograma", e);
        }
    }

    @Override
    public Optional<Hemograma> findById(Long id) {
        log.info("Buscando hemograma con ID: {}", id);
        
        // Primero intentamos obtener del caché
        Hemograma cachedHemograma = cache.get(id);
        if (cachedHemograma != null) {
            return Optional.of(cachedHemograma);
        }

        String sql = "SELECT * FROM hemograma WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Hemograma hemograma = mapResultSetToHemograma(rs);
                    cache.put(id, hemograma);
                    return Optional.of(hemograma);
                }
            }
        } catch (SQLException e) {
            log.error("Error al buscar hemograma por ID: {}", e.getMessage());
            throw new RuntimeException("Error al buscar hemograma", e);
        }
        
        return Optional.empty();
    }

    @Override
    public void update(Hemograma hemograma) {
        log.info("Actualizando hemograma: {}", hemograma);
        validarHemograma(hemograma);
        
        String sql = "UPDATE hemograma SET hematocrito = ?, hemoglobina = ?, leucocitos = ?, " +
                    "eritrocitos = ?, observaciones = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setHemogramaParameters(stmt, hemograma);
            stmt.setLong(6, hemograma.getId());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                cache.put(hemograma.getId(), hemograma);
            }
        } catch (SQLException e) {
            log.error("Error al actualizar hemograma: {}", e.getMessage());
            throw new RuntimeException("Error al actualizar hemograma", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        log.info("Eliminando hemograma con ID: {}", id);
        
        String sql = "DELETE FROM hemograma WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                cache.remove(id);
            }
        } catch (SQLException e) {
            log.error("Error al eliminar hemograma: {}", e.getMessage());
            throw new RuntimeException("Error al eliminar hemograma", e);
        }
    }

    // Métodos de búsqueda específicos
    public List<Hemograma> findByRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        log.info("Buscando hemogramas entre fechas {} y {}", fechaInicio, fechaFin);
        
        String sql = "SELECT h.* FROM hemograma h " +
                    "INNER JOIN consulta c ON h.consulta_id = c.id " +
                    "WHERE DATE(c.fecha) BETWEEN ? AND ?";
        
        List<Hemograma> resultados = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(fechaInicio));
            stmt.setDate(2, Date.valueOf(fechaFin));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    resultados.add(mapResultSetToHemograma(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Error al buscar hemogramas por rango de fechas: {}", e.getMessage());
            throw new RuntimeException("Error al buscar hemogramas", e);
        }
        
        return resultados;
    }

    // Métodos de estadísticas
    public double calcularPromedioHematocrito() {
        log.info("Calculando promedio de hematocrito");
        
        String sql = "SELECT AVG(hematocrito) AS promedio_hematocrito FROM hemograma";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getDouble("promedio_hematocrito");
            }
        } catch (SQLException e) {
            log.error("Error al calcular promedio de hematocrito: {}", e.getMessage());
            throw new RuntimeException("Error al calcular estadísticas", e);
        }
        
        return 0.0;
    }

    // Métodos privados de utilidad
    private void validarHemograma(Hemograma hemograma) {
        if (hemograma == null) {
            throw new IllegalArgumentException("El hemograma no puede ser null");
        }
        if (hemograma.getHematocrito() < 20 || hemograma.getHematocrito() > 60) {
            throw new IllegalArgumentException("El hematocrito debe estar entre 20 y 60");
        }
        if (hemograma.getHemoglobina() < 5 || hemograma.getHemoglobina() > 20) {
            throw new IllegalArgumentException("La hemoglobina debe estar entre 5 y 20");
        }
        // Agregar más validaciones según sea necesario
    }

    private void setHemogramaParameters(PreparedStatement stmt, Hemograma hemograma) throws SQLException {
        stmt.setLong(1, hemograma.getConsultaId());
        stmt.setDouble(2, hemograma.getHematocrito());
        stmt.setDouble(3, hemograma.getHemoglobina());
        stmt.setDouble(4, hemograma.getLeucocitos());
        stmt.setDouble(5, hemograma.getEritrocitos());
        stmt.setString(6, hemograma.getObservaciones());
    }

    private Hemograma mapResultSetToHemograma(ResultSet rs) throws SQLException {
        Hemograma hemograma = new Hemograma();
        hemograma.setId(rs.getLong("id"));
        hemograma.setConsultaId(rs.getLong("consulta_id"));
        hemograma.setHematocrito(rs.getDouble("hematocrito"));
        hemograma.setHemoglobina(rs.getDouble("hemoglobina"));
        hemograma.setLeucocitos(rs.getDouble("leucocitos"));
        hemograma.setEritrocitos(rs.getDouble("eritrocitos"));
        hemograma.setObservaciones(rs.getString("observaciones"));
        return hemograma;
    }
}