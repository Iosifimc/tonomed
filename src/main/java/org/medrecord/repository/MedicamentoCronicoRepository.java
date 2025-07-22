package org.medrecord.repository;

import org.medrecord.config.DataBaseConfig;
import org.medrecord.model.MedicamentoCronico;
import org.medrecord.dto.MedicamentoCronicoDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicamentoCronicoRepository {

    public int saveMedicamentoCronico(MedicamentoCronico medicamentoCronico) throws SQLException {
        String query = "INSERT INTO MEDICAMENTO_CRONICO(id_usuario, nombre_medicamento) VALUES(?, ?)";
        try (Connection conn = DataBaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, medicamentoCronico.getIdUsuario());
            stmt.setString(2, medicamentoCronico.getNombreMedicamento());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException("No se pudo agregar el medicamento crónico");
        }
    }

    public void deleteMedicamentoCronico(int idMedicamentoCronico) throws SQLException {
        String query = "DELETE FROM MEDICAMENTO_CRONICO WHERE id_medicamento_cronico = ?";
        try (Connection conn = DataBaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idMedicamentoCronico);
            stmt.executeUpdate();
        }
    }

    public int updateMedicamentoCronico(MedicamentoCronico medicamentoCronico) throws SQLException {
        String query = "UPDATE MEDICAMENTO_CRONICO SET nombre_medicamento = ? WHERE id_medicamento_cronico = ?";
        try (Connection conn = DataBaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, medicamentoCronico.getNombreMedicamento());
            stmt.setInt(2, medicamentoCronico.getIdMedicamentoCronico());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return medicamentoCronico.getIdMedicamentoCronico();
            }
            throw new SQLException("No se pudo actualizar el medicamento crónico");
        }
    }

    public List<MedicamentoCronicoDTO> findAllMedicamentoCronicoUsuario(int idUsuario) throws SQLException {
        List<MedicamentoCronicoDTO> medicamentosCronicos = new ArrayList<>();
        String query = "SELECT nombre_medicamento FROM MEDICAMENTO_CRONICO WHERE id_usuario = ?";

        try (Connection conn = DataBaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MedicamentoCronicoDTO medicamentoCronicoDTO = new MedicamentoCronicoDTO();
                    medicamentoCronicoDTO.setNombreMedicamento(rs.getString("nombre_medicamento"));
                    medicamentosCronicos.add(medicamentoCronicoDTO);
                }
            }
        }
        return medicamentosCronicos;
    }
}
