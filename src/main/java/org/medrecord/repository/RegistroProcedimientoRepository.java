package org.medrecord.repository;

import org.medrecord.config.DataBaseConfig;
import org.medrecord.model.RegistroProcedimiento;
import org.medrecord.dto.ProcedimientoMedicoDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RegistroProcedimientoRepository {
    public int saveRegistroProcedimiento(RegistroProcedimiento registro) throws SQLException {
        String query = "INSERT INTO REGISTRO_PROCEDIMIENTO(id_procedimiento, id_consulta, nota_adicional) VALUES(?, ?, ?)";
        try (Connection conn = DataBaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, registro.getIdProcedimiento());
            stmt.setInt(2, registro.getIdConsulta());
            stmt.setString(3, registro.getNotaAdicional());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException("No se pudo agregar el registro de procedimiento");
        }
    }

    public void deleteRegistroProcedimiento(int idRegistroProcedimiento) throws SQLException {
        String query = "DELETE FROM REGISTRO_PROCEDIMIENTO WHERE id_registro_procedimiento = ?";
        try (Connection conn = DataBaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idRegistroProcedimiento);
            stmt.executeUpdate();
        }
    }

    public int updateRegistroProcedimiento(RegistroProcedimiento registro) throws SQLException {
        String query = "UPDATE REGISTRO_PROCEDIMIENTO SET nota_adicional = ? WHERE id_registro_procedimiento = ?";
        try (Connection conn = DataBaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, registro.getNotaAdicional());
            stmt.setInt(2, registro.getIdRegistroProcedimiento());
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                return registro.getIdRegistroProcedimiento();
            }
            throw new SQLException("No se encontró el registro a actualizar");
        }
    }

    public List<ProcedimientoMedicoDTO> findProcedimientosConsulta(int idConsulta) throws SQLException {
        List<ProcedimientoMedicoDTO> procedimientos = new ArrayList<>();
        String query = """
        SELECT cpm.tipo_procedimiento, rp.nota_adicional
        FROM REGISTRO_PROCEDIMIENTO rp
        INNER JOIN CATALOGO_PROCEDIMIENTO_MEDICO cpm ON rp.id_procedimiento = cpm.id_procedimiento
        WHERE rp.id_consulta = ?;
        """;

        try (Connection conn = DataBaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idConsulta);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ProcedimientoMedicoDTO procedimientoMedicoDTO = new ProcedimientoMedicoDTO();
                    procedimientoMedicoDTO.setTipoProcedimiento(rs.getString("tipo_procedimiento"));
                    procedimientoMedicoDTO.setNotaAdicional(rs.getString("nota_adicional"));
                    procedimientos.add(procedimientoMedicoDTO);
                }
            }
        }
        return procedimientos;
    }



}

