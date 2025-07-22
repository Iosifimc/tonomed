package org.medrecord.repository;

import org.medrecord.config.DataBaseConfig;
import org.medrecord.model.RegistroMedida;
import org.medrecord.dto.MedidasFisiologicasDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RegistroMedidaRepository {

    public int saveRegistroMedidaPersonal(RegistroMedida registroMedida) throws SQLException {
        if (registroMedida.getIdUsuario() == null) {
            throw new IllegalArgumentException("El idUsuario no puede ser nulo para una medida personal.");
        }
        if (registroMedida.getIdConsulta() != null) {
            throw new IllegalArgumentException("El idConsulta debe ser nulo para una medida personal.");
        }

        String query = "INSERT INTO REGISTRO_MEDIDA(id_medida, id_usuario, id_consulta, fecha_registro, valor_medida, nota_adicional) VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection conn = DataBaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, registroMedida.getIdMedida());
            stmt.setInt(2, registroMedida.getIdUsuario());
            stmt.setNull(3, Types.INTEGER); // id_consulta null
            stmt.setDate(4, registroMedida.getFechaRegistro());
            stmt.setDouble(5, registroMedida.getValorMedida());
            stmt.setString(6, registroMedida.getNotaAdicional());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new SQLException("No se pudo agregar el registro de medida personal");
            }
        }
    }

    public int saveRegistroMedidaConsulta(RegistroMedida registroMedida) throws SQLException {
        if (registroMedida.getIdConsulta() == null) {
            throw new IllegalArgumentException("El idConsulta no puede ser nulo para una medida de consulta.");
        }
        if (registroMedida.getIdUsuario() != null) {
            throw new IllegalArgumentException("El idUsuario debe ser nulo para una medida de consulta.");
        }

        String query = "INSERT INTO REGISTRO_MEDIDA(id_medida, id_usuario, id_consulta, fecha_registro, valor_medida, nota_adicional) VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection conn = DataBaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, registroMedida.getIdMedida());
            stmt.setNull(2, Types.INTEGER); // id_usuario null
            stmt.setInt(3, registroMedida.getIdConsulta());
            stmt.setDate(4, registroMedida.getFechaRegistro());
            stmt.setDouble(5, registroMedida.getValorMedida());
            stmt.setString(6, registroMedida.getNotaAdicional());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new SQLException("No se pudo agregar el registro de medida de consulta");
            }
        }
    }


    public void deleteRegistroMedida(int idRegistroMedida) throws SQLException {
        String query = "DELETE FROM REGISTRO_MEDIDA WHERE id_registro_medida = ?";
        try (Connection conn = DataBaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idRegistroMedida);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("No se encontró el registro de medida a eliminar");
            }
        }
    }

    public int updateRegistroMedida(RegistroMedida registroMedida) throws SQLException {
        String query = "UPDATE REGISTRO_MEDIDA SET fecha_registro = ?, valor_medida = ?, nota_adicional = ? WHERE id_registro_medida = ?";
        try (Connection conn = DataBaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDate(1, registroMedida.getFechaRegistro());
            stmt.setDouble(2, registroMedida.getValorMedida());
            stmt.setString(3, registroMedida.getNotaAdicional());
            stmt.setInt(4, registroMedida.getIdRegistroMedida());
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                return registroMedida.getIdRegistroMedida();
            }
            throw new SQLException("No se pudo actualizar el registro de medida");
        }
    }

    public List<MedidasFisiologicasDTO> findAllMedidasUsuario(int idUsuario) throws SQLException {
        List<MedidasFisiologicasDTO> medidasFisiologicasDTOS = new ArrayList<>();
        String query = """
    SELECT rm.fecha_registro, rm.valor_medida, rm.nota_adicional,
           cmf.tipo_medida, cmf.unidad_medida
    FROM REGISTRO_MEDIDA rm
    INNER JOIN CATALOGO_MEDIDA_FISIOLOGICA cmf ON rm.id_medida = cmf.id_medida
    WHERE rm.id_usuario = ?;
    """;

        try (
                Connection conn = DataBaseConfig.getDataSource().getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
        ) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MedidasFisiologicasDTO medidasFisiologicasDTO = new MedidasFisiologicasDTO();
                    medidasFisiologicasDTO.setFechaRegistro(rs.getDate("fecha_registro"));
                    medidasFisiologicasDTO.setValorMedida(rs.getDouble("valor_medida"));
                    medidasFisiologicasDTO.setNotaAdicional(rs.getString("nota_adicional"));
                    medidasFisiologicasDTO.setTipoMedida(rs.getString("tipo_medida"));
                    medidasFisiologicasDTO.setUnidadMedida(rs.getString("unidad_medida"));
                    medidasFisiologicasDTOS.add(medidasFisiologicasDTO);
                }
            }
        }
        return medidasFisiologicasDTOS;
    }


    public List<MedidasFisiologicasDTO> findMedidaConsulta(int idConsulta) throws SQLException {
        List<MedidasFisiologicasDTO> medidasFisiologicasDTOS = new ArrayList<>();
        String query = """
    SELECT rm.fecha_registro,rm.valor_medida, rm.nota_adicional,
           cmf.tipo_medida, cmf.unidad_medida
    FROM REGISTRO_MEDIDA rm
    INNER JOIN CATALOGO_MEDIDA_FISIOLOGICA cmf ON rm.id_medida = cmf.id_medida
    WHERE rm.id_consulta = ?;
    """;
        try (
                Connection conn = DataBaseConfig.getDataSource().getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
        ) {
            stmt.setInt(1, idConsulta);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MedidasFisiologicasDTO medidasFisiologicasDTO = new MedidasFisiologicasDTO();
                    medidasFisiologicasDTO.setFechaRegistro(rs.getDate("fecha_registro"));
                    medidasFisiologicasDTO.setValorMedida(rs.getDouble("valor_medida"));
                    medidasFisiologicasDTO.setNotaAdicional(rs.getString("nota_adicional"));
                    medidasFisiologicasDTO.setTipoMedida(rs.getString("tipo_medida"));
                    medidasFisiologicasDTO.setUnidadMedida(rs.getString("unidad_medida"));
                    medidasFisiologicasDTOS.add(medidasFisiologicasDTO);
                }
            }
        }
        return medidasFisiologicasDTOS;
    }


}
