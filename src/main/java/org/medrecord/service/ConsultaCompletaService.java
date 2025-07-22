package org.medrecord.service;

import org.medrecord.model.ConsultaMedica;
import org.medrecord.model.Prescripcion;
import org.medrecord.model.RegistroMedida;
import org.medrecord.model.RegistroProcedimiento;
import org.medrecord.dto.ConsultaMedicaDTO;
import org.medrecord.dto.PrescripcionDTO;
import org.medrecord.dto.MedidasFisiologicasDTO;
import org.medrecord.dto.ProcedimientoMedicoDTO;
import org.medrecord.config.DataBaseConfig;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class ConsultaCompletaService {
    private final ConsultaMedicaService consultaMedicaService;
    private final PrescripcionService prescripcionService;
    private final RegistroMedidaService registroMedidaService;
    private final RegistroProcedimientoService registroProcedimientoService;

    public ConsultaCompletaService(
            ConsultaMedicaService consultaMedicaService,
            PrescripcionService prescripcionService,
            RegistroMedidaService registroMedidaService,
            RegistroProcedimientoService registroProcedimientoService) {
        this.consultaMedicaService = consultaMedicaService;
        this.prescripcionService = prescripcionService;
        this.registroMedidaService = registroMedidaService;
        this.registroProcedimientoService = registroProcedimientoService;
    }

    /**
     * Crea una consulta médica completa con todas sus prescripciones, medidas y procedimientos
     * Todo se ejecuta en una sola transacción
     */
    public int createConsultaCompleta(
            ConsultaMedica consultaMedica,
            List<Prescripcion> prescripciones,
            List<RegistroMedida> medidasConsulta,
            List<RegistroProcedimiento> procedimientos) throws SQLException {

        Connection conn = null;
        try {
            conn = DataBaseConfig.getDataSource().getConnection();
            conn.setAutoCommit(false);

            // 1. Crear la consulta médica
            int idConsulta = consultaMedicaService.create(consultaMedica);

            // 2. Asignar el ID de consulta a todas las prescripciones y crearlas
            if (prescripciones != null && !prescripciones.isEmpty()) {
                for (Prescripcion prescripcion : prescripciones) {
                    prescripcion.setIdConsulta(idConsulta);
                    prescripcionService.create(prescripcion);
                }
            }

            // 3. Asignar el ID de consulta a todas las medidas y crearlas (opcional)
            if (medidasConsulta != null && !medidasConsulta.isEmpty()) {
                for (RegistroMedida medida : medidasConsulta) {
                    medida.setIdConsulta(idConsulta);
                    medida.setIdUsuario(null); // Asegurar que es null para medidas de consulta
                    registroMedidaService.createMedidaConsulta(medida);
                }
            }

            // 4. Asignar el ID de consulta a todos los procedimientos y crearlos (opcional)
            if (procedimientos != null && !procedimientos.isEmpty()) {
                for (RegistroProcedimiento procedimiento : procedimientos) {
                    procedimiento.setIdConsulta(idConsulta);
                    registroProcedimientoService.create(procedimiento);
                }
            }

            conn.commit();
            return idConsulta;

        } catch (Exception e) {
            if (conn != null) {
                conn.rollback();
            }
            throw new SQLException("Error al crear la consulta completa: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    /**
     * Actualiza una consulta médica completa con todas sus prescripciones, medidas y procedimientos
     * Todo se ejecuta en una sola transacción
     */
    public int updateConsultaCompleta(
            ConsultaMedica consultaMedica,
            List<Prescripcion> prescripciones,
            List<RegistroMedida> medidasConsulta,
            List<RegistroProcedimiento> procedimientos) throws SQLException {

        Connection conn = null;
        try {
            conn = DataBaseConfig.getDataSource().getConnection();
            conn.setAutoCommit(false);

            int idConsulta = consultaMedica.getIdConsulta();

            // 1. Actualizar la consulta médica
            consultaMedicaService.update(consultaMedica);

            // 2. Actualizar prescripciones
            if (prescripciones != null && !prescripciones.isEmpty()) {
                for (Prescripcion prescripcion : prescripciones) {
                    prescripcion.setIdConsulta(idConsulta);
                    if (prescripcion.getIdPrescripcion() > 0) {
                        prescripcionService.update(prescripcion);
                    } else {
                        prescripcionService.create(prescripcion);
                    }
                }
            }

            // 3. Actualizar medidas de consulta (opcional)
            if (medidasConsulta != null && !medidasConsulta.isEmpty()) {
                for (RegistroMedida medida : medidasConsulta) {
                    medida.setIdConsulta(idConsulta);
                    medida.setIdUsuario(null);
                    if (medida.getIdRegistroMedida() > 0) {
                        registroMedidaService.update(medida);
                    } else {
                        registroMedidaService.createMedidaConsulta(medida);
                    }
                }
            }

            // 4. Actualizar procedimientos (opcional)
            if (procedimientos != null && !procedimientos.isEmpty()) {
                for (RegistroProcedimiento procedimiento : procedimientos) {
                    procedimiento.setIdConsulta(idConsulta);
                    if (procedimiento.getIdRegistroProcedimiento() > 0) {
                        registroProcedimientoService.update(procedimiento);
                    } else {
                        registroProcedimientoService.create(procedimiento);
                    }
                }
            }

            conn.commit();
            return idConsulta;

        } catch (Exception e) {
            if (conn != null) {
                conn.rollback();
            }
            throw new SQLException("Error al actualizar la consulta completa: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    /**
     * Obtiene una consulta médica completa con todas sus prescripciones, medidas y procedimientos
     */
    public ConsultaMedicaDTO getConsultaCompleta(int idConsulta) throws SQLException {
        try {
            // Obtener prescripciones
            List<PrescripcionDTO> prescripciones = prescripcionService.getPrescripcionesByConsulta(idConsulta);

            // Obtener medidas (opcional)
            List<MedidasFisiologicasDTO> medidas = registroMedidaService.getMedidasConsulta(idConsulta);
            if (medidas == null) {
                medidas = new ArrayList<>();
            }

            // Obtener procedimientos (opcional)
            List<ProcedimientoMedicoDTO> procedimientos = registroProcedimientoService.getProcedimientosConsulta(idConsulta);
            if (procedimientos == null) {
                procedimientos = new ArrayList<>();
            }

            // Crear el DTO completo
            ConsultaMedicaDTO consultaCompleta = new ConsultaMedicaDTO();
            consultaCompleta.setPrescripcionDTOS(prescripciones);
            consultaCompleta.setMedidasFisiologicasDTOS(medidas);
            consultaCompleta.setProcedimientoMedicoDTOS(procedimientos);

            return consultaCompleta;

        } catch (Exception e) {
            throw new SQLException("Error al obtener la consulta completa: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene todas las consultas de un usuario con información básica
     */
    public List<ConsultaMedicaDTO> getConsultasByUsuario(int idUsuario) throws SQLException {
        return consultaMedicaService.getConsultasByUsuario(idUsuario);
    }

    // Métodos individuales para eliminaciones por separado
    public void deleteConsulta(int idConsulta) throws SQLException {
        consultaMedicaService.delete(idConsulta);
    }

    public void deletePrescripcion(int idPrescripcion) throws SQLException {
        prescripcionService.delete(idPrescripcion);
    }

    public void deleteRegistroMedida(int idRegistroMedida) throws SQLException {
        registroMedidaService.delete(idRegistroMedida);
    }

    public void deleteRegistroProcedimiento(int idRegistroProcedimiento) throws SQLException {
        registroProcedimientoService.delete(idRegistroProcedimiento);
    }
}