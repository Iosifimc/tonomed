package org.medrecord.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.medrecord.service.ConsultaCompletaService;
import org.medrecord.model.ConsultaMedica;
import org.medrecord.model.Prescripcion;
import org.medrecord.model.RegistroMedida;
import org.medrecord.model.RegistroProcedimiento;
import org.medrecord.dto.ConsultaMedicaDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class ConsultaCompletaController {
    private final ConsultaCompletaService consultaCompletaService;

    public ConsultaCompletaController(ConsultaCompletaService consultaCompletaService) {
        this.consultaCompletaService = consultaCompletaService;
    }
    public void getConsultasByUsuario(Context ctx) {
        try {
            int idUsuario = Integer.parseInt(ctx.pathParam("id"));
            List<ConsultaMedicaDTO> consultas = consultaCompletaService.getConsultasByUsuario(idUsuario);
            ctx.json(consultas);
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("ID de usuario inválido");
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error al obtener consultas del usuario");
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error interno del servidor");
        }
    }

    public void getConsultaCompleta(Context ctx) {
        try {
            int idConsulta = Integer.parseInt(ctx.pathParam("id"));
            ConsultaMedicaDTO consultaCompleta = consultaCompletaService.getConsultaCompleta(idConsulta);
            if (consultaCompleta != null) {
                ctx.json(consultaCompleta);
            } else {
                ctx.status(HttpStatus.NOT_FOUND).result("Consulta no encontrada");
            }
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("ID de consulta inválido");
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error al obtener la consulta");
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error interno del servidor");
        }
    }

    public void createConsultaCompleta(Context ctx) {
        try {
            Map<String, Object> requestBody = ctx.bodyAsClass(Map.class);

            // Extraer consulta médica
            Map<String, Object> consultaMap = (Map<String, Object>) requestBody.get("consultaMedica");
            ConsultaMedica consultaMedica = mapToConsultaMedica(consultaMap);

            // Extraer prescripciones (obligatorias)
            List<Map<String, Object>> prescripcionesMap = (List<Map<String, Object>>) requestBody.get("prescripciones");
            List<Prescripcion> prescripciones = mapToPrescripciones(prescripcionesMap);

            // Extraer medidas (opcionales)
            List<RegistroMedida> medidasConsulta = null;
            if (requestBody.containsKey("medidasConsulta")) {
                List<Map<String, Object>> medidasMap = (List<Map<String, Object>>) requestBody.get("medidasConsulta");
                medidasConsulta = mapToRegistroMedidas(medidasMap);
            }

            // Extraer procedimientos (opcionales)
            List<RegistroProcedimiento> procedimientos = null;
            if (requestBody.containsKey("procedimientos")) {
                List<Map<String, Object>> procedimientosMap = (List<Map<String, Object>>) requestBody.get("procedimientos");
                procedimientos = mapToRegistroProcedimientos(procedimientosMap);
            }

            int idConsulta = consultaCompletaService.createConsultaCompleta(
                    consultaMedica, prescripciones, medidasConsulta, procedimientos);

            ctx.status(HttpStatus.CREATED).result("Consulta creada exitosamente con ID: " + idConsulta);
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error al crear la consulta");
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Error en el formato de los datos enviados");
        }
    }

    public void updateConsultaCompleta(Context ctx) {
        try {
            int idConsulta = Integer.parseInt(ctx.pathParam("id"));
            Map<String, Object> requestBody = ctx.bodyAsClass(Map.class);

            // Extraer consulta médica
            Map<String, Object> consultaMap = (Map<String, Object>) requestBody.get("consultaMedica");
            ConsultaMedica consultaMedica = mapToConsultaMedica(consultaMap);
            consultaMedica.setIdConsulta(idConsulta);

            // Extraer prescripciones (obligatorias)
            List<Map<String, Object>> prescripcionesMap = (List<Map<String, Object>>) requestBody.get("prescripciones");
            List<Prescripcion> prescripciones = mapToPrescripciones(prescripcionesMap);

            // Extraer medidas (opcionales)
            List<RegistroMedida> medidasConsulta = null;
            if (requestBody.containsKey("medidasConsulta")) {
                List<Map<String, Object>> medidasMap = (List<Map<String, Object>>) requestBody.get("medidasConsulta");
                medidasConsulta = mapToRegistroMedidas(medidasMap);
            }

            // Extraer procedimientos (opcionales)
            List<RegistroProcedimiento> procedimientos = null;
            if (requestBody.containsKey("procedimientos")) {
                List<Map<String, Object>> procedimientosMap = (List<Map<String, Object>>) requestBody.get("procedimientos");
                procedimientos = mapToRegistroProcedimientos(procedimientosMap);
            }

            int idConsultaActualizada = consultaCompletaService.updateConsultaCompleta(
                    consultaMedica, prescripciones, medidasConsulta, procedimientos);

            ctx.status(HttpStatus.OK).result("Consulta actualizada exitosamente con ID: " + idConsultaActualizada);
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("ID de consulta inválido");
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error al actualizar la consulta");
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Error en el formato de los datos enviados");
        }
    }

    public void deleteConsulta(Context ctx) {
        try {
            int idConsulta = Integer.parseInt(ctx.pathParam("id"));
            consultaCompletaService.deleteConsulta(idConsulta);
            ctx.status(HttpStatus.OK).result("Consulta eliminada exitosamente");
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("ID de consulta inválido");
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error al eliminar la consulta");
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error interno del servidor");
        }
    }

    public void deletePrescripcion(Context ctx) {
        try {
            int idPrescripcion = Integer.parseInt(ctx.pathParam("id"));
            consultaCompletaService.deletePrescripcion(idPrescripcion);
            ctx.status(HttpStatus.OK).result("Prescripción eliminada exitosamente");
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("ID de prescripción inválido");
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error al eliminar la prescripción");
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error interno del servidor");
        }
    }

    public void deleteRegistroMedida(Context ctx) {
        try {
            int idRegistroMedida = Integer.parseInt(ctx.pathParam("id"));
            consultaCompletaService.deleteRegistroMedida(idRegistroMedida);
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

    public void deleteRegistroProcedimiento(Context ctx) {
        try {
            int idRegistroProcedimiento = Integer.parseInt(ctx.pathParam("id"));
            consultaCompletaService.deleteRegistroProcedimiento(idRegistroProcedimiento);
            ctx.status(HttpStatus.OK).result("Registro de procedimiento eliminado exitosamente");
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("ID de registro de procedimiento inválido");
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error al eliminar el registro de procedimiento");
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error interno del servidor");
        }
    }

    // Métodos helper para mapear datos
    private ConsultaMedica mapToConsultaMedica(Map<String, Object> map) {
        ConsultaMedica consulta = new ConsultaMedica();
        consulta.setIdUsuario((Integer) map.get("idUsuario"));
        consulta.setDiagnostico((String) map.get("diagnostico"));
        consulta.setDoctor((String) map.get("doctor"));
        consulta.setClinica((String) map.get("clinica"));
        consulta.setFechaConsulta(java.sql.Date.valueOf((String) map.get("fechaConsulta")));
        return consulta;
    }

    private List<Prescripcion> mapToPrescripciones(List<Map<String, Object>> mapList) {
        if (mapList == null) return null;

        List<Prescripcion> prescripciones = new ArrayList<>();
        for (Map<String, Object> map : mapList) {
            Prescripcion prescripcion = new Prescripcion();
            if (map.containsKey("idPrescripcion") && map.get("idPrescripcion") != null) {
                prescripcion.setIdPrescripcion((Integer) map.get("idPrescripcion"));
            }
            prescripcion.setNombreMedicamento((String) map.get("nombreMedicamento"));
            prescripcion.setPresentacionMedicamento((String) map.get("presentacionMedicamento"));
            prescripcion.setDosis((String) map.get("dosis"));
            prescripcion.setFrecuencia((String) map.get("frecuencia"));
            prescripcion.setDuracion((String) map.get("duracion"));
            prescripciones.add(prescripcion);
        }
        return prescripciones;
    }

    private List<RegistroMedida> mapToRegistroMedidas(List<Map<String, Object>> mapList) {
        if (mapList == null) return null;

        List<RegistroMedida> medidas = new ArrayList<>();
        for (Map<String, Object> map : mapList) {
            RegistroMedida medida = new RegistroMedida();
            if (map.containsKey("idRegistroMedida") && map.get("idRegistroMedida") != null) {
                medida.setIdRegistroMedida((Integer) map.get("idRegistroMedida"));
            }
            medida.setIdMedida((Integer) map.get("idMedida"));
            medida.setFechaRegistro(java.sql.Date.valueOf((String) map.get("fechaRegistro")));
            medida.setValorMedida((Double) map.get("valorMedida"));
            medida.setNotaAdicional((String) map.get("notaAdicional"));
            medidas.add(medida);
        }
        return medidas;
    }

    private List<RegistroProcedimiento> mapToRegistroProcedimientos(List<Map<String, Object>> mapList) {
        if (mapList == null) return null;

        List<RegistroProcedimiento> procedimientos = new ArrayList<>();
        for (Map<String, Object> map : mapList) {
            RegistroProcedimiento procedimiento = new RegistroProcedimiento();
            if (map.containsKey("idRegistroProcedimiento") && map.get("idRegistroProcedimiento") != null) {
                procedimiento.setIdRegistroProcedimiento((Integer) map.get("idRegistroProcedimiento"));
            }
            procedimiento.setIdProcedimiento((Integer) map.get("idProcedimiento"));
            procedimiento.setNotaAdicional((String) map.get("notaAdicional"));
            procedimientos.add(procedimiento);
        }
        return procedimientos;
    }
}