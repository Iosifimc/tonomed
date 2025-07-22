package org.medrecord.routes;

import io.javalin.Javalin;
import org.medrecord.controller.MedidasFisiologicasController;

public class MedidasFisiologicasRoutes {
    private final MedidasFisiologicasController medidasFisiologicasController;

    public MedidasFisiologicasRoutes(MedidasFisiologicasController medidasFisiologicasController) {
        this.medidasFisiologicasController = medidasFisiologicasController;
    }

    public void register(Javalin app) {
        // Rutas para medidas fisiológicas personales
        app.post("/medidas-personales", medidasFisiologicasController::createMedidaPersonal);
        app.get("/medidas/usuario/{id}", medidasFisiologicasController::getAllMedidasUsuario);
        app.put("/registro-medidas/{id}", medidasFisiologicasController::updateRegistroMedida);
        app.delete("/registro-medidas/{id}", medidasFisiologicasController::deleteRegistroMedida);
    }
}
