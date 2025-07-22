package org.medrecord.repository;

import org.medrecord.config.DataBaseConfig;
import org.medrecord.model.Prescripcion;
import org.medrecord.dto.PrescripcionDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class PrescripcionRepository {
    public int savePrescripcion(Prescripcion prescripcion) throws SQLException {
        String query = "INSERT INTO PRESCRIPCION(id_consulta, nombre_medicamento, presentacion_medicamento,dosis, frecuencia, duracion) VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection conn = DataBaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, prescripcion.getIdConsulta());
            stmt.setString(2, prescripcion.getNombreMedicamento());
            stmt.setString(3, prescripcion.getPresentacionMedicamento());
            stmt.setString(4, prescripcion.getDosis());
            stmt.setString(5, prescripcion.getFrecuencia());
            stmt.setString(6, prescripcion.getDuracion());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException("No se pudo agregar la prescripción");
        }
    }

    public void deletePrescripcion(int idPrescripcion) throws SQLException {
        String query = "DELETE FROM PRESCRIPCION WHERE id_prescripcion = ?";
        try (Connection conn = DataBaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idPrescripcion);
            stmt.executeUpdate();
        }
    }

    public int updatePrescripcion(Prescripcion prescripcion) throws SQLException {
        String query = "UPDATE PRESCRIPCION SET nombre_medicamento = ?, presentacion_medicamento = ?, dosis = ?, frecuencia = ?, duracion = ? WHERE id_prescripcion = ?";
        try (Connection conn = DataBaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, prescripcion.getNombreMedicamento());
            stmt.setString(2, prescripcion.getPresentacionMedicamento());
            stmt.setString(3, prescripcion.getDosis());
            stmt.setString(4, prescripcion.getFrecuencia());
            stmt.setString(5, prescripcion.getDuracion());
            stmt.setInt(6, prescripcion.getIdPrescripcion());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return prescripcion.getIdPrescripcion();
            }
            throw new SQLException("No se pudo actualizar la prescripción");
        }
    }


    public List<PrescripcionDTO> findPrescripcionesByConsulta(int idConsulta) throws SQLException {
        List<PrescripcionDTO> prescripcionDTOS = new ArrayList<>();
        String query = "SELECT * FROM PRESCRIPCION WHERE id_consulta = ?";

        try (Connection conn = DataBaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idConsulta);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PrescripcionDTO prescripcionDTO = new PrescripcionDTO();
                    prescripcionDTO.setNombreMedicamento(rs.getString("nombre_medicamento"));
                    prescripcionDTO.setPresentacionMedicamento(rs.getString("presentacion_medicamento"));
                    prescripcionDTO.setDosis(rs.getString("dosis"));
                    prescripcionDTO.setFrecuencia(rs.getString("frecuencia"));
                    prescripcionDTO.setDuracion(rs.getString("duracion"));
                    prescripcionDTOS.add(prescripcionDTO);
                }
            }
        }
        return prescripcionDTOS;
    }



}

