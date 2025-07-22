package org.medrecord.repository;

import org.medrecord.config.DataBaseConfig;
import org.medrecord.model.RegistroCirugia;
import org.medrecord.dto.CirugiaDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RegistroCirugiaRepository {
    public int saveRegistroCirugia(RegistroCirugia registroCirugia) throws SQLException {
        String query = "INSERT INTO REGISTRO_CIRUGIA(id_usuario, id_cirugia, id_cirugia_personalizada, fecha_cirugia) VALUES(?, ?, ?, ?)";
        try (Connection conn = DataBaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, registroCirugia.getIdUsuario());
            stmt.setInt(2, registroCirugia.getIdCirugia());
            stmt.setInt(3, registroCirugia.getIdCirugiaPersonalizada());
            stmt.setDate(4, registroCirugia.getFechaCirugia());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException("No se pudo agregar el registro de cirugía");
        }
    }

    public void deleteRegistroCirugia(int idRegistroCirugia) throws SQLException {
        String query = "DELETE FROM REGISTRO_CIRUGIA WHERE id_registro_cirugia = ?";
        try (Connection conn = DataBaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idRegistroCirugia);
            stmt.executeUpdate();
        }
    }

    public int updateRegistroCirugia(RegistroCirugia registroCirugia) throws SQLException {
        String query = "UPDATE REGISTRO_CIRUGIA SET fecha_cirugia = ? WHERE id_registro_cirugia = ?";
        try (Connection conn = DataBaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDate(1, registroCirugia.getFechaCirugia());
            stmt.setInt(2, registroCirugia.getIdRegistroCirugia());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return registroCirugia.getIdRegistroCirugia();
            }
            throw new SQLException("No se pudo actualizar el registro de cirugía");
        }
    }

    public List<CirugiaDTO> findCirugiasUsuario(int idUsuario) throws SQLException {
        List<CirugiaDTO> cirugiaDTOS = new ArrayList<>();
        String query = """
        SELECT
            COALESCE(cc.nombre_cirugia, cp.nombre_cirugia) AS nombre_cirugia,
            rc.fecha_cirugia
        FROM REGISTRO_CIRUGIA rc
        LEFT JOIN CATALOGO_CIRUGIA cc ON rc.id_cirugia = cc.id_cirugia
        LEFT JOIN CIRUGIA_PERSONALIZADA cp ON rc.id_cirugia_personalizada = cp.id_cirugia_personalizada
        WHERE rc.id_usuario = ?;
    """;
        try (Connection conn = DataBaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idUsuario);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CirugiaDTO cirugiaDTO = new CirugiaDTO();
                    cirugiaDTO.setNombreCirugia(rs.getString("nombre_cirugia"));
                    cirugiaDTO.setFechaCirugia(rs.getDate("fecha_cirugia"));
                    cirugiaDTOS.add(cirugiaDTO);
                }
            }
        }
        return cirugiaDTOS;
    }


}

