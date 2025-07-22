package org.medrecord.controller;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.medrecord.service.AntecedentesMedicosService;
import org.medrecord.model.Alergia;
import org.medrecord.model.MedicamentoCronico;
import org.medrecord.model.RegistroCirugia;
import org.medrecord.model.RegistroEnfermedadCronica;
import org.medrecord.service.AntecedentesMedicosService.AntecedentesMedicosCompletos;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class AntecedentesMedicosController {
    private final AntecedentesMedicosService antecedentesMedicosService;

    public AntecedentesMedicosController(AntecedentesMedicosService antecedentesMedicosService) {
        this.antecedentesMedicosService = antecedentesMedicosService;
    }

    /**
     * Crea antecedentes médicos completos
     */
    public void createAntecedentesMedicos(Context ctx) {
        try {
            Map<String, Object> requestBody = ctx.bodyAsClass(Map.class);

            // Extraer alergias (opcional)
            List<Alergia> alergias = null;
            if (requestBody.containsKey("alergias") && requestBody.get("alergias") != null) {
                List<Map<String, Object>> alergiasMap = (List<Map<String, Object>>) requestBody.get("alergias");
                if (!alergiasMap.isEmpty()) {
                    alergias = mapToAlergias(alergiasMap);
                }
            }

            // Extraer medicamentos crónicos (opcional)
            List<MedicamentoCronico> medicamentosCronicos = null;
            if (requestBody.containsKey("medicamentosCronicos") && requestBody.get("medicamentosCronicos") != null) {
                List<Map<String, Object>> medicamentosMap = (List<Map<String, Object>>) requestBody.get("medicamentosCronicos");
                if (!medicamentosMap.isEmpty()) {
                    medicamentosCronicos = mapToMedicamentosCronicos(medicamentosMap);
                }
            }

            // Extraer cirugías (opcional)
            List<RegistroCirugia> cirugias = null;
            if (requestBody.containsKey("cirugias") && requestBody.get("cirugias") != null) {
                List<Map<String, Object>> cirugiasMap = (List<Map<String, Object>>) requestBody.get("cirugias");
                if (!cirugiasMap.isEmpty()) {
                    cirugias = mapToRegistroCirugias(cirugiasMap);
                }
            }

            // Extraer enfermedades crónicas (opcional)
            List<RegistroEnfermedadCronica> enfermedadesCronicas = null;
            if (requestBody.containsKey("enfermedadesCronicas") && requestBody.get("enfermedadesCronicas") != null) {
                List<Map<String, Object>> enfermedadesMap = (List<Map<String, Object>>) requestBody.get("enfermedadesCronicas");
                if (!enfermedadesMap.isEmpty()) {
                    enfermedadesCronicas = mapToRegistroEnfermedadesCronicas(enfermedadesMap);
                }
            }

            antecedentesMedicosService.createAntecedentesMedicos(
                    alergias, medicamentosCronicos, cirugias, enfermedadesCronicas);

            ctx.status(HttpStatus.CREATED).result("Antecedentes médicos creados exitosamente");
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error al crear antecedentes médicos");
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Error en el formato de los datos enviados");
        }
    }

    /**
     * Obtiene todos los antecedentes médicos de un usuario
     */
    public void getAntecedentesMedicos(Context ctx) {
        try {
            int idUsuario = Integer.parseInt(ctx.pathParam("id"));
            AntecedentesMedicosCompletos antecedentes = antecedentesMedicosService.getAntecedentesMedicos(idUsuario);
            ctx.json(antecedentes);
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("ID de usuario inválido");
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error al obtener antecedentes médicos");
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error interno del servidor");
        }
    }

    /**
     * Actualiza una alergia específica
     */
    public void updateAlergia(Context ctx) {
        try {
            int idAlergia = Integer.parseInt(ctx.pathParam("id"));
            Map<String, Object> requestBody = ctx.bodyAsClass(Map.class);

            Alergia alergia = new Alergia();
            alergia.setIdAlergia(idAlergia);
            alergia.setIdUsuario((Integer) requestBody.get("idUsuario"));
            alergia.setNombreAlergia((String) requestBody.get("nombreAlergia"));

            int idActualizado = antecedentesMedicosService.updateAlergia(alergia);
            ctx.status(HttpStatus.OK).result("Alergia actualizada exitosamente con ID: " + idActualizado);
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("ID de alergia inválido");
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error al actualizar la alergia");
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Error en el formato de los datos enviados");
        }
    }

    /**
     * Actualiza un medicamento crónico específico
     */
    public void updateMedicamentoCronico(Context ctx) {
        try {
            int idMedicamento = Integer.parseInt(ctx.pathParam("id"));
            Map<String, Object> requestBody = ctx.bodyAsClass(Map.class);

            MedicamentoCronico medicamento = new MedicamentoCronico();
            medicamento.setIdMedicamentoCronico(idMedicamento);
            medicamento.setIdUsuario((Integer) requestBody.get("idUsuario"));
            medicamento.setNombreMedicamento((String) requestBody.get("nombreMedicamento"));

            int idActualizado = antecedentesMedicosService.updateMedicamentoCronico(medicamento);
            ctx.status(HttpStatus.OK).result("Medicamento crónico actualizado exitosamente con ID: " + idActualizado);
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("ID de medicamento inválido");
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error al actualizar el medicamento");
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Error en el formato de los datos enviados");
        }
    }

    /**
     * Actualiza un registro de cirugía específico
     */
    public void updateRegistroCirugia(Context ctx) {
        try {
            int idRegistroCirugia = Integer.parseInt(ctx.pathParam("id"));
            Map<String, Object> requestBody = ctx.bodyAsClass(Map.class);

            RegistroCirugia cirugia = new RegistroCirugia();
            cirugia.setIdRegistroCirugia(idRegistroCirugia);
            cirugia.setFechaCirugia(java.sql.Date.valueOf((String) requestBody.get("fechaCirugia")));

            int idActualizado = antecedentesMedicosService.updateRegistroCirugia(cirugia);
            ctx.status(HttpStatus.OK).result("Registro de cirugía actualizado exitosamente con ID: " + idActualizado);
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("ID de registro de cirugía inválido");
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error al actualizar el registro de cirugía");
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Error en el formato de los datos enviados");
        }
    }

    /**
     * Actualiza un registro de enfermedad crónica específico
     */
    public void updateRegistroEnfermedadCronica(Context ctx) {
        try {
            int idRegistroEnfermedad = Integer.parseInt(ctx.pathParam("id"));
            Map<String, Object> requestBody = ctx.bodyAsClass(Map.class);

            RegistroEnfermedadCronica enfermedad = new RegistroEnfermedadCronica();
            enfermedad.setIdRegistroEnfermedad(idRegistroEnfermedad);
            enfermedad.setFechaDiagnostico(java.sql.Date.valueOf((String) requestBody.get("fechaDiagnostico")));

            int idActualizado = antecedentesMedicosService.updateRegistroEnfermedadCronica(enfermedad);
            ctx.status(HttpStatus.OK).result("Registro de enfermedad crónica actualizado exitosamente con ID: " + idActualizado);
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("ID de registro de enfermedad inválido");
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error al actualizar el registro de enfermedad");
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Error en el formato de los datos enviados");
        }
    }

    /**
     * Elimina una alergia específica
     */
    public void deleteAlergia(Context ctx) {
        try {
            int idAlergia = Integer.parseInt(ctx.pathParam("id"));
            antecedentesMedicosService.deleteAlergia(idAlergia);
            ctx.status(HttpStatus.OK).result("Alergia eliminada exitosamente");
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("ID de alergia inválido");
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error al eliminar la alergia");
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error interno del servidor");
        }
    }

    /**
     * Elimina un medicamento crónico específico
     */
    public void deleteMedicamentoCronico(Context ctx) {
        try {
            int idMedicamento = Integer.parseInt(ctx.pathParam("id"));
            antecedentesMedicosService.deleteMedicamentoCronico(idMedicamento);
            ctx.status(HttpStatus.OK).result("Medicamento crónico eliminado exitosamente");
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("ID de medicamento inválido");
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error al eliminar el medicamento");
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error interno del servidor");
        }
    }

    /**
     * Elimina un registro de cirugía específico
     */
    public void deleteRegistroCirugia(Context ctx) {
        try {
            int idRegistroCirugia = Integer.parseInt(ctx.pathParam("id"));
            antecedentesMedicosService.deleteRegistroCirugia(idRegistroCirugia);
            ctx.status(HttpStatus.OK).result("Registro de cirugía eliminado exitosamente");
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("ID de registro de cirugía inválido");
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error al eliminar el registro de cirugía");
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error interno del servidor");
        }
    }

    /**
     * Elimina un registro de enfermedad crónica específico
     */
    public void deleteRegistroEnfermedadCronica(Context ctx) {
        try {
            int idRegistroEnfermedad = Integer.parseInt(ctx.pathParam("id"));
            antecedentesMedicosService.deleteRegistroEnfermedadCronica(idRegistroEnfermedad);
            ctx.status(HttpStatus.OK).result("Registro de enfermedad crónica eliminado exitosamente");
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("ID de registro de enfermedad inválido");
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
        } catch (SQLException e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error al eliminar el registro de enfermedad");
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error interno del servidor");
        }
    }

    // Métodos helper para mapear datos
    private List<Alergia> mapToAlergias(List<Map<String, Object>> mapList) {
        List<Alergia> alergias = new ArrayList<>();
        for (Map<String, Object> map : mapList) {
            Alergia alergia = new Alergia();
            alergia.setIdUsuario((Integer) map.get("idUsuario"));
            alergia.setNombreAlergia((String) map.get("nombreAlergia"));
            alergias.add(alergia);
        }
        return alergias;
    }

    private List<MedicamentoCronico> mapToMedicamentosCronicos(List<Map<String, Object>> mapList) {
        List<MedicamentoCronico> medicamentos = new ArrayList<>();
        for (Map<String, Object> map : mapList) {
            MedicamentoCronico medicamento = new MedicamentoCronico();
            medicamento.setIdUsuario((Integer) map.get("idUsuario"));
            medicamento.setNombreMedicamento((String) map.get("nombreMedicamento"));
            medicamentos.add(medicamento);
        }
        return medicamentos;
    }

    private List<RegistroCirugia> mapToRegistroCirugias(List<Map<String, Object>> mapList) {
        List<RegistroCirugia> cirugias = new ArrayList<>();
        for (Map<String, Object> map : mapList) {
            RegistroCirugia cirugia = new RegistroCirugia();
            cirugia.setIdUsuario((Integer) map.get("idUsuario"));

            // Manejar idCirugia (puede ser null)
            if (map.containsKey("idCirugia") && map.get("idCirugia") != null) {
                cirugia.setIdCirugia((Integer) map.get("idCirugia"));
            }

            // Manejar idCirugiaPersonalizada (puede ser null)
            if (map.containsKey("idCirugiaPersonalizada") && map.get("idCirugiaPersonalizada") != null) {
                cirugia.setIdCirugiaPersonalizada((Integer) map.get("idCirugiaPersonalizada"));
            }

            cirugia.setFechaCirugia(java.sql.Date.valueOf((String) map.get("fechaCirugia")));
            cirugias.add(cirugia);
        }
        return cirugias;
    }

    private List<RegistroEnfermedadCronica> mapToRegistroEnfermedadesCronicas(List<Map<String, Object>> mapList) {
        List<RegistroEnfermedadCronica> enfermedades = new ArrayList<>();
        for (Map<String, Object> map : mapList) {
            RegistroEnfermedadCronica enfermedad = new RegistroEnfermedadCronica();
            enfermedad.setIdUsuario((Integer) map.get("idUsuario"));

            // Manejar idEnfermedadCronica (puede ser null)
            if (map.containsKey("idEnfermedadCronica") && map.get("idEnfermedadCronica") != null) {
                enfermedad.setIdEnfermedadCronica((Integer) map.get("idEnfermedadCronica"));
            }

            // Manejar idEnfermedadPersonalizada (puede ser null)
            if (map.containsKey("idEnfermedadPersonalizada") && map.get("idEnfermedadPersonalizada") != null) {
                enfermedad.setIdEnfermedadPersonalizada((Integer) map.get("idEnfermedadPersonalizada"));
            }

            enfermedad.setFechaDiagnostico(java.sql.Date.valueOf((String) map.get("fechaDiagnostico")));
            enfermedades.add(enfermedad);
        }
        return enfermedades;
    }
}