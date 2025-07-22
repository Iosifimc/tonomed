package org.medrecord.service;

import org.medrecord.dto.PerfilUsuarioDTO;
import org.medrecord.model.Usuario;
import org.medrecord.repository.UsuarioRepository;

import java.sql.SQLException;

public class UsuarioService {
    private final UsuarioRepository usuarioRepo;

    public UsuarioService(UsuarioRepository usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }

    public PerfilUsuarioDTO getByIdUser(int idUsuario) throws SQLException {
        return usuarioRepo.findByIdUser(idUsuario);
    }

    public void createUser(Usuario usuario) throws SQLException {
        usuarioRepo.saveUser(usuario);
    }

    public void updateUser(Usuario usuario) throws SQLException {
        usuarioRepo.updateUser(usuario);
    }

    public void deleteByIdUser(int idUsuario) throws SQLException {
        usuarioRepo.deleteUser(idUsuario);
    }

    // LOGIN
    public Usuario login(String email, String password) throws SQLException {
        return usuarioRepo.login(email, password);
    }

    // VERIFICAR EMAIL
    public boolean verificarEmail(String token) throws SQLException {
        return usuarioRepo.verificarEmail(token);
    }

    // OBTENER TOKEN DE VERIFICACIÓN
    public String obtenerTokenVerificacion(String email) throws SQLException {
        return usuarioRepo.obtenerTokenVerificacion(email);
    }

    // BUSCAR USUARIO POR TOKEN
    public Usuario findByTokenVerificacion(String token) throws SQLException {
        return usuarioRepo.findByTokenVerificacion(token);
    }

}