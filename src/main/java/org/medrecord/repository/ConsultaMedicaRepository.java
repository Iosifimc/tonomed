package org.medrecord.repository;

import org.medrecord.config.DataBaseConfig;
import org.medrecord.model.ConsultaMedica;
import org.medrecord.dto.ConsultaMedicaDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConsultaMedicaRepository {

    public int saveConsultaMedica(ConsultaMedica consultaMedica) throws SQLException{
        String query = "INSERT INTO CONSULTA_MEDICA (id_usuario, diagnostico, doctor, clinica, fecha_consulta) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DataBaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, consultaMedica.getIdUsuario());
            stmt.setString(2, consultaMedica.getDiagnostico());
            stmt.setString(3, consultaMedica.getDoctor());
            stmt.setString(4, consultaMedica.getClinica());
            stmt.setDate(5, consultaMedica.getFechaConsulta());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if(rs.next()){
                return rs.getInt(1);
            }
            throw new SQLException("No se pudo agregar la consulta medica");
        }
    }

    public void deleteConsultaMedica(int idConsulta) throws SQLException {
        String query = "DELETE FROM CONSULTA_MEDICA WHERE id_consulta = ?";
        try (Connection conn = DataBaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idConsulta);
            stmt.executeUpdate();
        }
    }

    public int updateConsultaMedica(ConsultaMedica consultaMedica) throws SQLException {
        String query = "UPDATE CONSULTA_MEDICA SET diagnostico = ?, doctor = ?, clinica = ?, fecha_consulta = ? WHERE id_consulta = ?";
        try (Connection conn = DataBaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, consultaMedica.getDiagnostico());
            stmt.setString(2, consultaMedica.getDoctor());
            stmt.setString(3, consultaMedica.getClinica());
            stmt.setDate(4, consultaMedica.getFechaConsulta());
            stmt.setInt(5, consultaMedica.getIdConsulta());
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                return consultaMedica.getIdConsulta();
            }
            throw new SQLException("No se pudo actualizar la consulta medica");
        }
    }


    public List<ConsultaMedicaDTO> findConsultasByUsuario(int idUsuario) throws SQLException {
        List<ConsultaMedicaDTO> consultaMedicaDTOS = new ArrayList<>();
        String query = "SELECT diagnostico, doctor, clinica, fecha_consulta FROM CONSULTA_MEDICA WHERE id_usuario = ? ORDER BY fecha_consulta DESC";

        try (Connection conn = DataBaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ConsultaMedicaDTO consultaMedicaDTO = new ConsultaMedicaDTO();
                    consultaMedicaDTO.setDiagnostico(rs.getString("diagnostico"));
                    consultaMedicaDTO.setDoctor(rs.getString("doctor"));
                    consultaMedicaDTO.setClinica(rs.getString("clinica"));
                    consultaMedicaDTO.setFechaConsulta(rs.getDate("fecha_consulta"));
                    consultaMedicaDTOS.add(consultaMedicaDTO);
                }
            }
        }
        return consultaMedicaDTOS;
    }


}
