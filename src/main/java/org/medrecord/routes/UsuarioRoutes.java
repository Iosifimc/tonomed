package org.medrecord.routes;

import io.javalin.Javalin;
import org.medrecord.controller.UsuarioController;

public class UsuarioRoutes {
    private final UsuarioController usuarioController;

    public UsuarioRoutes(UsuarioController usuarioController) {
        this.usuarioController = usuarioController;
    }

    public void register(Javalin app) {
        // Rutas originales
        app.post("/users", usuarioController::create);
        app.get("/users/{id_usuario}", usuarioController::getById);
        app.put("/users/{id_usuario}", usuarioController::update);
        app.delete("/users/{id_usuario}", usuarioController::deleteById);

        // Ruta de login
        app.post("/login", usuarioController::login);

        // NUEVAS RUTAS DE VERIFICACIÓN
        app.post("/verificar-email", usuarioController::verificarEmail);
        app.get("/verificar-email", usuarioController::verificarEmail); // También GET para links de email
        app.get("/status-verificacion", usuarioController::statusVerificacion);
        app.post("/logout", usuarioController::logout);
    }
}