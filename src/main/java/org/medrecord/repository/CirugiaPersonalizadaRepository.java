package org.medrecord.repository;

import org.medrecord.config.DataBaseConfig;
import org.medrecord.model.CirugiaPersonalizada;

import java.sql.*;

public class CirugiaPersonalizadaRepository {
    public int saveCirugiaPersonalizada(CirugiaPersonalizada cirugiaPersonalizada) throws SQLException{
        String query = "INSERT INTO CIRUGIA_PERSONALIZADA(id_usuario, nombre_cirugia) VALUES(?, ?)";
        try(Connection conn = DataBaseConfig.getDataSource().getConnection();
        PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, cirugiaPersonalizada.getIdCirugiaPersonalizada());
            stmt.setString(2, cirugiaPersonalizada.getNombreCirugia());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return cirugiaPersonalizada.getIdCirugiaPersonalizada();
            }
            throw new SQLException("No se pudo agregar la cirugia personalizada");
        }
    }

    public void deleteCirugiaPersonalizada(int idCirugiaPersonalizada) throws SQLException{
        String query = "DELETE FROM CIRUGIA_PERSONALIZADA WHERE id_cirugia_personalizada = ?";
        try(Connection conn = DataBaseConfig.getDataSource().getConnection();
        PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idCirugiaPersonalizada);
            stmt.executeUpdate();
        }
    }

    public int updateCirugiaPersonalizada(CirugiaPersonalizada cirugiaPersonalizada) throws SQLException {
        String query = "UPDATE CIRUGIA_PERSONALIZADA SET nombre_cirugia = ? WHERE id_cirugia_personalizada = ?";
        try (Connection conn = DataBaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, cirugiaPersonalizada.getNombreCirugia());
            stmt.setInt(2, cirugiaPersonalizada.getIdCirugiaPersonalizada());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return cirugiaPersonalizada.getIdCirugiaPersonalizada();
            }
            throw new SQLException("No se pudo actualizar la cirugía personalizada");
        }
    }

}
