package org.medrecord.repository;

import org.medrecord.config.DataBaseConfig;
import org.medrecord.model.Usuario;
import org.medrecord.dto.PerfilUsuarioDTO;
import org.medrecord.util.Hash;
import org.medrecord.util.TokenUtil;

import java.sql.*;


public class UsuarioRepository {
    private final Hash hash = new Hash();

    public PerfilUsuarioDTO findByIdUser(int idUsuario) throws SQLException {
        PerfilUsuarioDTO perfilUsuarioDTO = null;
        String query = "SELECT * FROM USUARIO WHERE id_usuario = ?";

        try (Connection conn = DataBaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idUsuario);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    perfilUsuarioDTO = new PerfilUsuarioDTO();
                    perfilUsuarioDTO.setNombre(rs.getString("nombre"));
                    perfilUsuarioDTO.setApellidoPaterno(rs.getString("apellido_paterno"));
                    perfilUsuarioDTO.setApellidoMaterno(rs.getString("apellido_materno"));
                    perfilUsuarioDTO.setCorreo(rs.getString("email"));
                    perfilUsuarioDTO.setFechaNacimiento(rs.getDate("fecha_nacimiento"));
                    perfilUsuarioDTO.setSexo(rs.getString("sexo"));
                }
            }
        }

        return perfilUsuarioDTO;
    }

    public void saveUser(Usuario usuario) throws SQLException {
        // Hashear la contraseña antes de guardar
        String hashedPassword = hash.sha256(usuario.getContrasena());

        // Generar token de verificación
        String token = TokenUtil.generarToken();

        String query = "INSERT INTO USUARIO (nombre, apellido_paterno, apellido_materno, email, contrasena, fecha_nacimiento, sexo, email_verificado, token_verificacion, token_expiracion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DataBaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getApellidoPaterno());
            stmt.setString(3, usuario.getApellidoMaterno());
            stmt.setString(4, usuario.getCorreo());
            stmt.setString(5, hashedPassword);
            stmt.setDate(6, usuario.getFechaNacimiento());
            stmt.setString(7, usuario.getSexo());
            stmt.setBoolean(8, false); // Email no verificado inicialmente
            stmt.setString(9, token);
            stmt.setTimestamp(10, TokenUtil.generarExpiracion());
            stmt.executeUpdate();
        }
    }

    public void updateUser(Usuario usuario) throws SQLException {
        String query = "UPDATE USUARIO SET nombre = ?, apellido_paterno = ?, apellido_materno = ?, email = ?, fecha_nacimiento = ?, sexo = ? WHERE id_usuario = ?";
        try (Connection conn = DataBaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getApellidoPaterno());
            stmt.setString(3, usuario.getApellidoMaterno());
            stmt.setString(4, usuario.getCorreo());
            stmt.setDate(5, usuario.getFechaNacimiento());
            stmt.setString(6, usuario.getSexo());
            stmt.setInt(7, usuario.getIdUsuario());
            stmt.executeUpdate();
        }
    }

    public void deleteUser(int idUsuario) throws SQLException {
        String query = "DELETE FROM USUARIO WHERE id_usuario = ?";
        try (Connection conn = DataBaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idUsuario);
            stmt.executeUpdate();
        }
    }

    // LOGIN - Solo permite usuarios verificados
    public Usuario login(String email, String password) throws SQLException {
        Usuario usuario = null;
        String query = "SELECT * FROM USUARIO WHERE email = ? AND email_verificado = TRUE";

        try (Connection conn = DataBaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String hashedPasswordDB = rs.getString("contrasena");

                    if (hash.verificarPassword(password, hashedPasswordDB)) {
                        usuario = mapearUsuario(rs);
                        // NO regresamos la contraseña ni tokens por seguridad
                        usuario.setContrasena(null);
                        usuario.setTokenVerificacion(null);
                    }
                }
            }
        }

        return usuario;
    }

    // BUSCAR USUARIO POR TOKEN DE VERIFICACIÓN
    public Usuario findByTokenVerificacion(String token) throws SQLException {
        Usuario usuario = null;
        String query = "SELECT * FROM USUARIO WHERE token_verificacion = ? AND token_expiracion > NOW()";

        try (Connection conn = DataBaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, token);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    usuario = mapearUsuario(rs);
                }
            }
        }

        return usuario;
    }

    // VERIFICAR EMAIL
    public boolean verificarEmail(String token) throws SQLException {
        String query = "UPDATE USUARIO SET email_verificado = TRUE, token_verificacion = NULL, token_expiracion = NULL WHERE token_verificacion = ? AND token_expiracion > NOW()";

        try (Connection conn = DataBaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, token);
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
        }
    }

    // OBTENER TOKEN DE VERIFICACIÓN POR EMAIL
    public String obtenerTokenVerificacion(String email) throws SQLException {
        String token = null;
        String query = "SELECT token_verificacion FROM USUARIO WHERE email = ? AND email_verificado = FALSE";

        try (Connection conn = DataBaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    token = rs.getString("token_verificacion");
                }
            }
        }

        return token;
    }

    // Método auxiliar para mapear ResultSet a Usuario
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getInt("id_usuario"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setApellidoPaterno(rs.getString("apellido_paterno"));
        usuario.setApellidoMaterno(rs.getString("apellido_materno"));
        usuario.setCorreo(rs.getString("email"));
        usuario.setContrasena(rs.getString("contrasena"));
        usuario.setFechaNacimiento(rs.getDate("fecha_nacimiento"));
        usuario.setSexo(rs.getString("sexo"));
        usuario.setFechaCreacion(rs.getTimestamp("fecha_creacion"));

        // Campos de verificación (pueden ser null si no existen)
        try {
            usuario.setEmailVerificado(rs.getBoolean("email_verificado"));
            usuario.setTokenVerificacion(rs.getString("token_verificacion"));
            usuario.setTokenExpiracion(rs.getTimestamp("token_expiracion"));
        } catch (SQLException e) {
            // Si las columnas no existen, usar valores por defecto
            usuario.setEmailVerificado(true); // Para retrocompatibilidad
            usuario.setTokenVerificacion(null);
            usuario.setTokenExpiracion(null);
        }

        return usuario;
    }

}

