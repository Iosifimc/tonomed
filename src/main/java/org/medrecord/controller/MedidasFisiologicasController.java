package org.medrecord.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.medrecord.service.MedidasFisiologicasService;
import org.medrecord.model.RegistroMedida;
import org.medrecord.dto.MedidasFisiologicasDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class MedidasFisiologicasController {
    private final MedidasFisiologicasService medidasFisiologicasService;

    public MedidasFisiologicasController(MedidasFisiologicasService medidasFisiologicasService) {
        this.medidasFisiologicasService = medidasFisiologicasService;
    }

    public void createMedidaPersonal(Context ctx) {
        try {
            RegistroMedida registroMedida = ctx.bodyAsClass(RegistroMedida.class);
            int idRegistro = medidasFisiologicasService.createMedidaPersonal(registroMedida);
            ctx.status(HttpStatus.CREATED).result("Medida personal creada exitosamente con ID: " + idRegistro);
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error al crear la medida personal");
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Error en el formato de los datos enviados");
        }
    }

    public void getAllMedidasUsuario(Context ctx) {
        try {
            int idUsuario = Integer.parseInt(ctx.pathParam("id"));
            List<MedidasFisiologicasDTO> medidas = medidasFisiologicasService.getAllMedidasUsuario(idUsuario);
            ctx.json(medidas);
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("ID de usuario inválido");
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error al obtener las medidas del usuario");
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error interno del servidor");
        }
    }

    public void updateRegistroMedida(Context ctx) {
        try {
            int idRegistroMedida = Integer.parseInt(ctx.pathParam("id"));
            Map<String, Object> requestBody = ctx.bodyAsClass(Map.class);

            RegistroMedida registroMedida = mapToRegistroMedida(requestBody);
            registroMedida.setIdRegistroMedida(idRegistroMedida);

            int idActualizado = medidasFisiologicasService.updateRegistroMedida(registroMedida);
            ctx.status(HttpStatus.OK).result("Registro de medida actualizado exitosamente con ID: " + idActualizado);
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("ID de registro de medida inválido");
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error al actualizar el registro de medida");
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Error en el formato de los datos enviados");
        }
    }

    public void deleteRegistroMedida(Context ctx) {
        try {
            int idRegistroMedida = Integer.parseInt(ctx.pathParam("id"));
            medidasFisiologicasService.deleteRegistroMedida(idRegistroMedida);
            ctx.status(HttpStatus.OK).result("Registro de medida eliminado exitosamente");
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("ID de registro de medida inválido");
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error al eliminar el registro de medida");
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error interno del servidor");
        }
    }

    private RegistroMedida mapToRegistroMedida(Map<String, Object> map) {
        RegistroMedida medida = new RegistroMedida();
        medida.setFechaRegistro(java.sql.Date.valueOf((String) map.get("fechaRegistro")));
        medida.setValorMedida((Double) map.get("valorMedida"));
        medida.setNotaAdicional((String) map.get("notaAdicional"));
        return medida;
    }
}
