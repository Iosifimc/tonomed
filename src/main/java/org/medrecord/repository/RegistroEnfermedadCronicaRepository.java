package org.medrecord.repository;

import org.medrecord.config.DataBaseConfig;
import org.medrecord.model.RegistroEnfermedadCronica;
import org.medrecord.dto.EnfermedadCronicaDTO;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class RegistroEnfermedadCronicaRepository {
    public int saveRegistroEnfermedadCronica(RegistroEnfermedadCronica registro) throws SQLException {
        String query = "INSERT INTO REGISTRO_ENFERMEDAD_CRONICA(id_usuario, id_enfermedad_cronica, id_enfermedad_personalizada, fecha_diagnostico) VALUES(?, ?, ?, ?)";
        try (Connection conn = DataBaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, registro.getIdUsuario());
            stmt.setInt(2, registro.getIdEnfermedadCronica());
            stmt.setInt(3, registro.getIdEnfermedadPersonalizada());
            stmt.setDate(4, registro.getFechaDiagnostico());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException("No se pudo agregar el registro de enfermedad crónica");
        }
    }

    public void deleteRegistroEnfermedadCronica(int idRegistroEnfermedad) throws SQLException {
        String query = "DELETE FROM REGISTRO_ENFERMEDAD_CRONICA WHERE id_registro_enfermedad = ?";
        try (Connection conn = DataBaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idRegistroEnfermedad);
            stmt.executeUpdate();
        }
    }

    public int updateRegistroEnfermedadCronica(RegistroEnfermedadCronica registro) throws SQLException {
        String query = "UPDATE REGISTRO_ENFERMEDAD_CRONICA SET fecha_diagnostico = ? WHERE id_registro_enfermedad = ?";
        try (Connection conn = DataBaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDate(1, registro.getFechaDiagnostico());
            stmt.setInt(2, registro.getIdRegistroEnfermedad());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return registro.getIdRegistroEnfermedad();
            }
            throw new SQLException("No se pudo actualizar el registro de enfermedad crónica");
        }
    }

    public List<EnfermedadCronicaDTO> findAllEnfermedadesCronicasUsuario(int idUsuario) throws SQLException {
        List<EnfermedadCronicaDTO> enfermedadCronicaDTOS = new ArrayList<>();
        String query = """
        SELECT
            COALESCE(cen.nombre_enfermedad_cronica, ecp.nombre_enfermedad) AS nombre_enfermedad,
            rec.fecha_diagnostico
        FROM REGISTRO_ENFERMEDAD_CRONICA rec
        LEFT JOIN CATALOGO_ENFERMEDAD_CRONICA cen ON rec.id_enfermedad_cronica = cen.id_enfermedad_cronica
        LEFT JOIN ENFERMEDAD_CRONICA_PERSONALIZADA ecp ON rec.id_enfermedad_personalizada = ecp.id_enfermedad_personalizada
        WHERE rec.id_usuario = ?;
    """;
        try (Connection conn = DataBaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idUsuario);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    EnfermedadCronicaDTO enfermedadCronicaDTO = new EnfermedadCronicaDTO();
                    enfermedadCronicaDTO.setNombreEnfermedad(rs.getString("nombre_enfermedad"));
                    enfermedadCronicaDTO.setFechaDiagnostico(rs.getDate("fecha_diagnostico"));
                    enfermedadCronicaDTOS.add(enfermedadCronicaDTO);
                }
            }
        }
        return enfermedadCronicaDTOS;
    }


}
