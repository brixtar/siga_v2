package com.siga.repository.impl;

import com.siga.models.Urianalisis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UrianalisisRepositoryImpl extends BaseRepositoryImpl<Urianalisis, Long> {
    private static final Logger log = LoggerFactory.getLogger(UrianalisisRepositoryImpl.class);
    private final Map<Long, Urianalisis> cache = new ConcurrentHashMap<>();

    // Constructor
    public UrianalisisRepositoryImpl(Connection connection) {
        super(connection);
    }

    @Override
    public Urianalisis save(Urianalisis urianalisis) {
        log.info("Guardando urianálisis: {}", urianalisis);
        validarUrianalisis(urianalisis);
        
        String sql = "INSERT INTO urianalisis (consulta_id, densidad, ph, proteinas, glucosa, " +
                    "cetonas, sangre, nitritos, leucocitos, color, aspecto, observaciones) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setUrianalisisParameters(stmt, urianalisis);
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    urianalisis.setId(rs.getLong(1));
                    cache.put(urianalisis.getId(), urianalisis);
                }
            }
            
            return urianalisis;
        } catch (SQLException e) {
            log.error("Error al guardar urianálisis: {}", e.getMessage());
            throw new RuntimeException("Error al guardar urianálisis", e);
        }
    }

    @Override
    public Optional<Urianalisis> findById(Long id) {
        log.info("Buscando urianálisis con ID: {}", id);
        
        // Primero intentamos obtener del caché
        Urianalisis cachedUrianalisis = cache.get(id);
        if (cachedUrianalisis != null) {
            return Optional.of(cachedUrianalisis);
        }

        String sql = "SELECT * FROM urianalisis WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Urianalisis urianalisis = mapResultSetToUrianalisis(rs);
                    cache.put(id, urianalisis);
                    return Optional.of(urianalisis);
                }
            }
        } catch (SQLException e) {
            log.error("Error al buscar urianálisis por ID: {}", e.getMessage());
            throw new RuntimeException("Error al buscar urianálisis", e);
        }
        
        return Optional.empty();
    }

    @Override
    public void update(Urianalisis urianalisis) {
        log.info("Actualizando urianálisis: {}", urianalisis);
        validarUrianalisis(urianalisis);
        
        String sql = "UPDATE urianalisis SET densidad = ?, ph = ?, proteinas = ?, glucosa = ?, " +
                    "cetonas = ?, sangre = ?, nitritos = ?, leucocitos = ?, color = ?, " +
                    "aspecto = ?, observaciones = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setUrianalisisUpdateParameters(stmt, urianalisis);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                cache.put(urianalisis.getId(), urianalisis);
            }
        } catch (SQLException e) {
            log.error("Error al actualizar urianálisis: {}", e.getMessage());
            throw new RuntimeException("Error al actualizar urianálisis", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        log.info("Eliminando urianálisis con ID: {}", id);
        
        String sql = "DELETE FROM urianalisis WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                cache.remove(id);
            }
        } catch (SQLException e) {
            log.error("Error al eliminar urianálisis: {}", e.getMessage());
            throw new RuntimeException("Error al eliminar urianálisis", e);
        }
    }

    // Métodos de búsqueda específicos
    public List<Urianalisis> findByRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        log.info("Buscando urianálisis entre fechas {} y {}", fechaInicio, fechaFin);
        
        String sql = "SELECT u.* FROM urianalisis u " +
                    "INNER JOIN consulta c ON u.consulta_id = c.id " +
                    "WHERE DATE(c.fecha) BETWEEN ? AND ?";
        
        List<Urianalisis> resultados = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(fechaInicio));
            stmt.setDate(2, Date.valueOf(fechaFin));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    resultados.add(mapResultSetToUrianalisis(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Error al buscar urianálisis por rango de fechas: {}", e.getMessage());
            throw new RuntimeException("Error al buscar urianálisis", e);
        }
        
        return resultados;
    }

    // Métodos de estadísticas
    public double calcularPromedioPH() {
        log.info("Calculando promedio de pH");
        
        String sql = "SELECT AVG(ph) AS promedio_ph FROM urianalisis";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getDouble("promedio_ph");
            }
        } catch (SQLException e) {
            log.error("Error al calcular promedio de pH: {}", e.getMessage());
            throw new RuntimeException("Error al calcular estadísticas", e);
        }
        
        return 0.0;
    }

    // Métodos privados de utilidad
    private void validarUrianalisis(Urianalisis urianalisis) {
        if (urianalisis == null) {
            throw new IllegalArgumentException("El urianálisis no puede ser null");
        }
        if (urianalisis.getDensidad() < 1.000 || urianalisis.getDensidad() > 1.050) {
            throw new IllegalArgumentException("La densidad debe estar entre 1.000 y 1.050");
        }
        if (urianalisis.getPH() < 4.5 || urianalisis.getPH() > 8.0) {
            throw new IllegalArgumentException("El pH debe estar entre 4.5 y 8.0");
        }
        // Agregar más validaciones según sea necesario
    }

    private void setUrianalisisParameters(PreparedStatement stmt, Urianalisis urianalisis) throws SQLException {
        stmt.setLong(1, urianalisis.getConsultaId());
        stmt.setDouble(2, urianalisis.getDensidad());
        stmt.setDouble(3, urianalisis.getPH());
        stmt.setString(4, urianalisis.getProteinas());
        stmt.setString(5, urianalisis.getGlucosa());
        stmt.setString(6, urianalisis.getCetonas());
        stmt.setString(7, urianalisis.getSangre());
        stmt.setString(8, urianalisis.getNitritos());
        stmt.setString(9, urianalisis.getLeucocitos());
        stmt.setString(10, urianalisis.getColor());
        stmt.setString(11, urianalisis.getAspecto());
        stmt.setString(12, urianalisis.getObservaciones());
    }

    private void setUrianalisisUpdateParameters(PreparedStatement stmt, Urianalisis urianalisis) throws SQLException {
        setUrianalisisParameters(stmt, urianalisis);
        stmt.setLong(13, urianalisis.getId());
    }

    private Urianalisis mapResultSetToUrianalisis(ResultSet rs) throws SQLException {
        Urianalisis urianalisis = new Urianalisis();
        urianalisis.setId(rs.getLong("id"));
        urianalisis.setConsultaId(rs.getLong("consulta_id"));
        urianalisis.setDensidad(rs.getDouble("densidad"));
        urianalisis.setPH(rs.getDouble("ph"));
        urianalisis.setProteinas(rs.getString("proteinas"));
        urianalisis.setGlucosa(rs.getString("glucosa"));
        urianalisis.setCetonas(rs.getString("cetonas"));
        urianalisis.setSangre(rs.getString("sangre"));
        urianalisis.setNitritos(rs.getString("nitritos"));
        urianalisis.setLeucocitos(rs.getString("leucocitos"));
        urianalisis.setColor(rs.getString("color"));
        urianalisis.setAspecto(rs.getString("aspecto"));
        urianalisis.setObservaciones(rs.getString("observaciones"));
        return urianalisis;
    }
}