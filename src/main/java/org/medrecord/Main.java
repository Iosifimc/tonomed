package org.medrecord;

import io.javalin.Javalin;
import io.javalin.plugin.bundled.CorsPluginConfig;
import org.medrecord.di.AppModule;

public class Main {
    public static void main(String[] args) {

        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(CorsPluginConfig.CorsRule::anyHost);
            });
        }).start(7000);

        // Rutas generales
        app.get("/", ctx -> ctx.result("Bienvenido a MedRecord"));
        AppModule.initUser().register(app);
        AppModule.initConsultaCompleta().register(app);
        AppModule.initMedidasFisiologicas().register(app);
        AppModule.initAntecedentesMedicos().register(app);
    }
}