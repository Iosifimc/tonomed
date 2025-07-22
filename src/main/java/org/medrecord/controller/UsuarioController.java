package org.medrecord.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.medrecord.dto.LoginRequest;
import org.medrecord.dto.PerfilUsuarioDTO;
import org.medrecord.model.Usuario;
import org.medrecord.service.UsuarioService;
import java.sql.SQLIntegrityConstraintViolationException;

public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    public void getById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id_usuario"));
            PerfilUsuarioDTO perfilUsuarioDTO = usuarioService.getByIdUser(id);
            if (perfilUsuarioDTO != null) {
                ctx.json(perfilUsuarioDTO);
            } else {
                ctx.status(HttpStatus.NOT_FOUND).result("Usuario no encontrado");
            }
        } catch (Exception e) {
            ctx.status(404).result("Error al encontrar al usuario" + e.getMessage());
        }
    }

    public void create(Context ctx) {
        try {
            Usuario usuario = ctx.bodyAsClass(Usuario.class);
            usuarioService.createUser(usuario);

            // Obtener el token de verificación generado
            String token = usuarioService.obtenerTokenVerificacion(usuario.getCorreo());

            // Establecer cookie con el token de verificación (24 horas)
            ctx.cookie("verification_token", token, 86400);

            ctx.status(201).json(java.util.Map.of(
                    "mensaje", "Usuario creado. Verifica tu correo electrónico.",
                    "tokenVerificacion", token,
                    "instrucciones", "Usa el endpoint /verificar-email con el token para verificar tu cuenta"
            ));
        }catch (SQLIntegrityConstraintViolationException e) {
            ctx.status(409).result("Ya existe un usuario con ese correo electrónico.");
        }catch (Exception e) {
            ctx.status(400).result("Error al crear usuario: " + e.getMessage());
        }
    }

    public void update(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id_usuario"));
            Usuario usuario = ctx.bodyAsClass(Usuario.class);
            usuario.setIdUsuario(id);
            usuarioService.updateUser(usuario);
            ctx.status(200).result("Usuario actualizado");
        } catch (SQLIntegrityConstraintViolationException e) {
            ctx.status(409).result("Ya existe un usuario con ese correo electrónico.");
        } catch (Exception e) {
            ctx.status(400).result("Error al actualizar usuario: " + e.getMessage());
        }
    }

    public void deleteById(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id_usuario"));
            usuarioService.deleteByIdUser(id);
            ctx.status(200).result("Usuario eliminado");
        } catch (Exception e) {
            ctx.status(400).result("Error al eliminar usuario: " + e.getMessage());
        }
    }

    // LOGIN - Solo usuarios verificados
    public void login(Context ctx) {
        try {
            LoginRequest loginRequest = ctx.bodyAsClass(LoginRequest.class);
            Usuario usuario = usuarioService.login(loginRequest.getCorreo(), loginRequest.getContrasena());

            if (usuario != null) {
                // Establecer cookie de sesión (1 hora)
                ctx.cookie("session_user_id", String.valueOf(usuario.getIdUsuario()), 3600);
                ctx.status(200).json(usuario);
            } else {
                ctx.status(401).result("Credenciales incorrectas o cuenta no verificada");
            }
        } catch (Exception e) {
            ctx.status(400).result("Error al iniciar sesión: " + e.getMessage());
        }
    }

    // VERIFICAR EMAIL usando token de cookie o parámetro
    public void verificarEmail(Context ctx) {
        try {
            // Intentar obtener token de cookie primero, luego de query param
            String token = ctx.cookie("verification_token");
            if (token == null) {
                token = ctx.queryParam("token");
            }

            if (token == null) {
                ctx.status(400).result("Token de verificación requerido");
                return;
            }

            boolean verificado = usuarioService.verificarEmail(token);

            if (verificado) {
                // Limpiar cookie de verificación
                ctx.removeCookie("verification_token");
                ctx.status(200).result("Correo electrónico verificado exitosamente. Ya puedes iniciar sesión.");
            } else {
                ctx.status(400).result("Token de verificación inválido o expirado");
            }
        } catch (Exception e) {
            ctx.status(400).result("Error al verificar email: " + e.getMessage());
        }
    }

    // OBTENER STATUS DE VERIFICACIÓN
    public void statusVerificacion(Context ctx) {
        try {
            String token = ctx.cookie("verification_token");

            if (token == null) {
                ctx.status(200).json(java.util.Map.of(
                        "verificado", false,
                        "mensaje", "No hay token de verificación"
                ));
                return;
            }

            Usuario usuario = usuarioService.findByTokenVerificacion(token);

            if (usuario != null) {
                ctx.status(200).json(java.util.Map.of(
                        "verificado", false,
                        "correo", usuario.getCorreo(),
                        "mensaje", "Cuenta pendiente de verificación",
                        "token", token
                ));
            } else {
                ctx.status(200).json(java.util.Map.of(
                        "verificado", false,
                        "mensaje", "Token inválido o expirado"
                ));
            }
        } catch (Exception e) {
            ctx.status(400).result("Error al verificar status: " + e.getMessage());
        }
    }

    // LOGOUT
    public void logout(Context ctx) {
        try {
            ctx.removeCookie("session_user_id");
            ctx.removeCookie("verification_token");
            ctx.status(200).result("Sesión cerrada exitosamente");
        } catch (Exception e) {
            ctx.status(400).result("Error al cerrar sesión: " + e.getMessage());
        }
    }

}