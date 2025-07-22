package org.medrecord.routes;

import io.javalin.Javalin;
import org.medrecord.controller.ConsultaCompletaController;

public class ConsultaCompletaRoutes {
    private final ConsultaCompletaController consultaCompletaController;

    public ConsultaCompletaRoutes(ConsultaCompletaController consultaCompletaController) {
        this.consultaCompletaController = consultaCompletaController;
    }

    public void register(Javalin app) {
        // Rutas para consultas médicas
        app.get("/consultas/usuario/{id}", consultaCompletaController::getConsultasByUsuario);
        app.get("/consultas/{id}", consultaCompletaController::getConsultaCompleta);
        app.post("/consultas", consultaCompletaController::createConsultaCompleta);
        app.put("/consultas/{id}", consultaCompletaController::updateConsultaCompleta);
        app.delete("/consultas/{id}", consultaCompletaController::deleteConsulta);

        // Rutas para eliminar elementos específicos
        app.delete("/prescripciones/{id}", consultaCompletaController::deletePrescripcion);
        app.delete("/medidas/{id}", consultaCompletaController::deleteRegistroMedida);
        app.delete("/procedimientos/{id}", consultaCompletaController::deleteRegistroProcedimiento);
    }
}
