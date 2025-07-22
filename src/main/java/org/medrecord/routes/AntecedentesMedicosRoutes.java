package org.medrecord.routes;

import io.javalin.Javalin;
import org.medrecord.controller.AntecedentesMedicosController;

public class AntecedentesMedicosRoutes {
    private final AntecedentesMedicosController antecedentesMedicosController;

    public AntecedentesMedicosRoutes(AntecedentesMedicosController antecedentesMedicosController) {
        this.antecedentesMedicosController = antecedentesMedicosController;
    }

    public void register(Javalin app) {
        // Rutas principales
        app.post("/antecedentes-medicos", antecedentesMedicosController::createAntecedentesMedicos);
        app.get("/antecedentes-medicos/usuario/{id}", antecedentesMedicosController::getAntecedentesMedicos);

        // Rutas para actualizar elementos específicos
        app.put("/alergias/{id}", antecedentesMedicosController::updateAlergia);
        app.put("/medicamentos-cronicos/{id}", antecedentesMedicosController::updateMedicamentoCronico);
        app.put("/cirugias/{id}", antecedentesMedicosController::updateRegistroCirugia);
        app.put("/enfermedades-cronicas/{id}", antecedentesMedicosController::updateRegistroEnfermedadCronica);

        // Rutas para eliminar elementos específicos
        app.delete("/alergias/{id}", antecedentesMedicosController::deleteAlergia);
        app.delete("/medicamentos-cronicos/{id}", antecedentesMedicosController::deleteMedicamentoCronico);
        app.delete("/cirugias/{id}", antecedentesMedicosController::deleteRegistroCirugia);
        app.delete("/enfermedades-cronicas/{id}", antecedentesMedicosController::deleteRegistroEnfermedadCronica);
    }
}