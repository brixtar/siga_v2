package com.siga.repository.impl;

import com.siga.models.QuimicaClinica;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class QuimicaClinicaRepositoryImpl extends BaseRepositoryImpl<QuimicaClinica, Long> {
    private static final Logger log = LoggerFactory.getLogger(QuimicaClinicaRepositoryImpl.class);
    private final Map<Long, QuimicaClinica> cache = new ConcurrentHashMap<>();

    public QuimicaClinicaRepositoryImpl(Connection connection) {
        super(connection);
    }

    @Override
    public QuimicaClinica save(QuimicaClinica quimicaClinica) {
        log.info("Guardando química clínica: {}", quimicaClinica);
        validarQuimicaClinica(quimicaClinica);
        
        String sql = "INSERT INTO quimica_clinica (consulta_id, glucosa, urea, creatinina, " +
                    "colesterol, trigliceridos, alt, ast, observaciones) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setQuimicaClinicaParameters(stmt, quimicaClinica);
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    quimicaClinica.setId(rs.getLong(1));
                    cache.put(quimicaClinica.getId(), quimicaClinica);
                }
            }
            
            return quimicaClinica;
        } catch (SQLException e) {
            log.error("Error al guardar química clínica: {}", e.getMessage());
            throw new RuntimeException("Error al guardar química clínica", e);
        }
    }

    @Override
    public Optional<QuimicaClinica> findById(Long id) {
        log.info("Buscando química clínica con ID: {}", id);
        
        QuimicaClinica cachedQuimicaClinica = cache.get(id);
        if (cachedQuimicaClinica != null) {
            return Optional.of(cachedQuimicaClinica);
        }

        String sql = "SELECT * FROM quimica_clinica WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    QuimicaClinica quimicaClinica = mapResultSetToQuimicaClinica(rs);
                    cache.put(id, quimicaClinica);
                    return Optional.of(quimicaClinica);
                }
            }
        } catch (SQLException e) {
            log.error("Error al buscar química clínica por ID: {}", e.getMessage());
            throw new RuntimeException("Error al buscar química clínica", e);
        }
        
        return Optional.empty();
    }

    @Override
    public void update(QuimicaClinica quimicaClinica) {
        log.info("Actualizando química clínica: {}", quimicaClinica);
        validarQuimicaClinica(quimicaClinica);
        
        String sql = "UPDATE quimica_clinica SET glucosa = ?, urea = ?, creatinina = ?, " +
                    "colesterol = ?, trigliceridos = ?, alt = ?, ast = ?, observaciones = ? " +
                    "WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setQuimicaClinicaUpdateParameters(stmt, quimicaClinica);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                cache.put(quimicaClinica.getId(), quimicaClinica);
            }
        } catch (SQLException e) {
            log.error("Error al actualizar química clínica: {}", e.getMessage());
            throw new RuntimeException("Error al actualizar química clínica", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        log.info("Eliminando química clínica con ID: {}", id);
        
        String sql = "DELETE FROM quimica_clinica WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                cache.remove(id);
            }
        } catch (SQLException e) {
            log.error("Error al eliminar química clínica: {}", e.getMessage());
            throw new RuntimeException("Error al eliminar química clínica", e);
        }
    }

    // Métodos de búsqueda específicos
    public List<QuimicaClinica> findByRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        log.info("Buscando química clínica entre fechas {} y {}", fechaInicio, fechaFin);
        
        String sql = "SELECT qc.* FROM quimica_clinica qc " +
                    "INNER JOIN consulta c ON qc.consulta_id = c.id " +
                    "WHERE DATE(c.fecha) BETWEEN ? AND ?";
        
        List<QuimicaClinica> resultados = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(fechaInicio));
            stmt.setDate(2, Date.valueOf(fechaFin));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    resultados.add(mapResultSetToQuimicaClinica(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Error al buscar química clínica por rango de fechas: {}", e.getMessage());
            throw new RuntimeException("Error al buscar química clínica", e);
        }
        
        return resultados;
    }

    // Métodos privados de utilidad
    private void validarQuimicaClinica(QuimicaClinica quimicaClinica) {
        if (quimicaClinica == null) {
            throw new IllegalArgumentException("La química clínica no puede ser null");
        }
        if (quimicaClinica.getGlucosa() < 0) {
            throw new IllegalArgumentException("La glucosa no puede ser negativa");
        }
        if (quimicaClinica.getUrea() < 0) {
            throw new IllegalArgumentException("La urea no puede ser negativa");
        }
        // Agregar más validaciones según sea necesario
    }

    private void setQuimicaClinicaParameters(PreparedStatement stmt, QuimicaClinica quimicaClinica) throws SQLException {
        stmt.setLong(1, quimicaClinica.getConsultaId());
        stmt.setDouble(2, quimicaClinica.getGlucosa());
        stmt.setDouble(3, quimicaClinica.getUrea());
        stmt.setDouble(4, quimicaClinica.getCreatinina());
        stmt.setDouble(5, quimicaClinica.getColesterol());
        stmt.setDouble(6, quimicaClinica.getTrigliceridos());
        stmt.setDouble(7, quimicaClinica.getAlt());
        stmt.setDouble(8, quimicaClinica.getAst());
        stmt.setString(9, quimicaClinica.getObservaciones());
    }

    private void setQuimicaClinicaUpdateParameters(PreparedStatement stmt, QuimicaClinica quimicaClinica) throws SQLException {
        setQuimicaClinicaParameters(stmt, quimicaClinica);
        stmt.setLong(10, quimicaClinica.getId());
    }

    private QuimicaClinica mapResultSetToQuimicaClinica(ResultSet rs) throws SQLException {
        QuimicaClinica quimicaClinica = new QuimicaClinica();
        quimicaClinica.setId(rs.getLong("id"));
        quimicaClinica.setConsultaId(rs.getLong("consulta_id"));
        quimicaClinica.setGlucosa(rs.getDouble("glucosa"));
        quimicaClinica.setUrea(rs.getDouble("urea"));
        quimicaClinica.setCreatinina(rs.getDouble("creatinina"));
        quimicaClinica.setColesterol(rs.getDouble("colesterol"));
        quimicaClinica.setTrigliceridos(rs.getDouble("trigliceridos"));
        quimicaClinica.setAlt(rs.getDouble("alt"));
        quimicaClinica.setAst(rs.getDouble("ast"));
        quimicaClinica.setObservaciones(rs.getString("observaciones"));
        return quimicaClinica;
    }
}