package org.medrecord.repository;

import org.medrecord.config.DataBaseConfig;
import org.medrecord.model.EnfermedadCronicaPersonalizada;

import java.sql.*;

public class EnfermedadCronicaRepository {
    public int saveEnfermedadCronica(EnfermedadCronicaPersonalizada enfermedadCronicaPersonalizada) throws SQLException {
        String query = "INSERT  INTO ENFERMEDAD_CRONICA_PERSONALIZADA(id_usuario, nombre_enfermedad) VALUES(?, ?)";
        try(Connection conn = DataBaseConfig.getDataSource().getConnection();
        PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, enfermedadCronicaPersonalizada.getIdUsuario());
            stmt.setString(2, enfermedadCronicaPersonalizada.getNombreEnfermedad());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException("No se pudo agregar la enfermedad cronica");
        }
    }

    public void deleteEnfermedadCronica(int idEnfermedadPersonalizada) throws SQLException{
        String query = "DELETE FROM ENFERMEDAD_CRONICA_PERSONALIZADA WHERE id_enfermedad_personalizada = ?";
        try(Connection conn = DataBaseConfig.getDataSource().getConnection();
        PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idEnfermedadPersonalizada);
            stmt.executeUpdate();
        }
    }

    public int updateEnfermedadCronica(EnfermedadCronicaPersonalizada enfermedadCronicaPersonalizada) throws SQLException {
        String query = "UPDATE ENFERMEDAD_CRONICA_PERSONALIZADA SET nombre_enfermedad = ? WHERE id_enfermedad_personalizada = ?";
        try (Connection conn = DataBaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, enfermedadCronicaPersonalizada.getNombreEnfermedad());
            stmt.setInt(2, enfermedadCronicaPersonalizada.getIdEnfermedadPersonalizada());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return enfermedadCronicaPersonalizada.getIdEnfermedadPersonalizada();
            }
            throw new SQLException("No se pudo actualizar la enfermedad crónica");
        }
    }



}
