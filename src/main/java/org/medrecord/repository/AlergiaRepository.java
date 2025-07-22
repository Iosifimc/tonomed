package org.medrecord.repository;

import org.medrecord.config.DataBaseConfig;
import org.medrecord.model.Alergia;
import org.medrecord.dto.AlergiasDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlergiaRepository {
    public int saveAlergia(Alergia alergia) throws SQLException{
        String query = "INSERT INTO ALERGIAS(id_usuario, nombre_alergia) VALUES(?, ?)";
        try(Connection conn = DataBaseConfig.getDataSource().getConnection();
        PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);){
            stmt.setInt(1, alergia.getIdUsuario());
            stmt.setString(2, alergia.getNombreAlergia());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if(rs.next()){
                return rs.getInt(1);
            }
            throw new SQLException("No se pudo agregar la alergia");
        }
    }

    public void deleteAlergia(int idAlergia) throws SQLException{
        String query = "DELETE FROM ALERGIAS WHERE id_alergia = ?";
        try(Connection conn = DataBaseConfig.getDataSource().getConnection();
        PreparedStatement stmt = conn.prepareStatement(query);){
            stmt.setInt(1, idAlergia);
            stmt.executeUpdate();
        }
    }

    public int updateAlergia(Alergia alergia) throws SQLException {
        String query = "UPDATE ALERGIAS SET nombre_alergia = ? WHERE id_alergia = ?";
        try (Connection conn = DataBaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, alergia.getNombreAlergia());
            stmt.setInt(2, alergia.getIdAlergia());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return alergia.getIdAlergia();
            }
            throw new SQLException("No se pudo actualizar la alergia");
        }
    }

    public List<AlergiasDTO> findAllAlergiaUsuario(int idUsuario) throws SQLException {
        List<AlergiasDTO> alergiasDTOS = new ArrayList<>();
        String query = "SELECT nombre_alergia FROM ALERGIAS WHERE id_usuario = ?";

        try (Connection conn = DataBaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AlergiasDTO alergiasDTO = new AlergiasDTO();
                    alergiasDTO.setNombreAlergia(rs.getString("nombre_alergia"));
                    alergiasDTOS.add(alergiasDTO);
                }
            }
        }
        return alergiasDTOS;
    }



}
