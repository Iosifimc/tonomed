package org.medrecord.service;

import org.medrecord.model.Alergia;
import org.medrecord.model.MedicamentoCronico;
import org.medrecord.model.RegistroCirugia;
import org.medrecord.model.RegistroEnfermedadCronica;
import org.medrecord.dto.AlergiasDTO;
import org.medrecord.dto.MedicamentoCronicoDTO;
import org.medrecord.dto.CirugiaDTO;
import org.medrecord.dto.EnfermedadCronicaDTO;
import org.medrecord.config.DataBaseConfig;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class AntecedentesMedicosService {
    private final AlergiaService alergiaService;
    private final MedicamentoCronicoService medicamentoCronicoService;
    private final RegistroCirugiaService registroCirugiaService;
    private final RegistroEnfermedadCronicaService registroEnfermedadCronicaService;

    public AntecedentesMedicosService(
            AlergiaService alergiaService,
            MedicamentoCronicoService medicamentoCronicoService,
            RegistroCirugiaService registroCirugiaService,
            RegistroEnfermedadCronicaService registroEnfermedadCronicaService) {
        this.alergiaService = alergiaService;
        this.medicamentoCronicoService = medicamentoCronicoService;
        this.registroCirugiaService = registroCirugiaService;
        this.registroEnfermedadCronicaService = registroEnfermedadCronicaService;
    }

    /**
     * Crea antecedentes médicos completos en una sola transacción
     * Todos los parámetros son opcionales
     */
    public void createAntecedentesMedicos(
            List<Alergia> alergias,
            List<MedicamentoCronico> medicamentosCronicos,
            List<RegistroCirugia> cirugias,
            List<RegistroEnfermedadCronica> enfermedadesCronicas) throws SQLException {

        Connection conn = null;
        try {
            conn = DataBaseConfig.getDataSource().getConnection();
            conn.setAutoCommit(false);

            // 1. Crear alergias (opcional)
            if (alergias != null && !alergias.isEmpty()) {
                for (Alergia alergia : alergias) {
                    alergiaService.create(alergia);
                }
            }

            // 2. Crear medicamentos crónicos (opcional)
            if (medicamentosCronicos != null && !medicamentosCronicos.isEmpty()) {
                for (MedicamentoCronico medicamento : medicamentosCronicos) {
                    medicamentoCronicoService.create(medicamento);
                }
            }

            // 3. Crear cirugías (opcional)
            if (cirugias != null && !cirugias.isEmpty()) {
                for (RegistroCirugia cirugia : cirugias) {
                    registroCirugiaService.create(cirugia);
                }
            }

            // 4. Crear enfermedades crónicas (opcional)
            if (enfermedadesCronicas != null && !enfermedadesCronicas.isEmpty()) {
                for (RegistroEnfermedadCronica enfermedad : enfermedadesCronicas) {
                    registroEnfermedadCronicaService.create(enfermedad);
                }
            }

            conn.commit();

        } catch (Exception e) {
            if (conn != null) {
                conn.rollback();
            }
            throw new SQLException("Error al crear antecedentes médicos: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    /**
     * Obtiene todos los antecedentes médicos de un usuario
     */
    public AntecedentesMedicosCompletos getAntecedentesMedicos(int idUsuario) throws SQLException {
        if (idUsuario <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser mayor a 0");
        }

        AntecedentesMedicosCompletos antecedentes = new AntecedentesMedicosCompletos();

        // Obtener cada tipo de antecedente
        antecedentes.setAlergias(alergiaService.getAllAlergiasByUsuario(idUsuario));
        antecedentes.setMedicamentosCronicos(medicamentoCronicoService.getAllMedicamentosByUsuario(idUsuario));
        antecedentes.setCirugias(registroCirugiaService.getCirugiasByUsuario(idUsuario));
        antecedentes.setEnfermedadesCronicas(registroEnfermedadCronicaService.getEnfermedadesCronicasByUsuario(idUsuario));

        return antecedentes;
    }

    // Métodos de eliminación individual - delegan a services específicos
    public void deleteAlergia(int idAlergia) throws SQLException {
        alergiaService.delete(idAlergia);
    }

    public void deleteMedicamentoCronico(int idMedicamentoCronico) throws SQLException {
        medicamentoCronicoService.delete(idMedicamentoCronico);
    }

    public void deleteRegistroCirugia(int idRegistroCirugia) throws SQLException {
        registroCirugiaService.delete(idRegistroCirugia);
    }

    public void deleteRegistroEnfermedadCronica(int idRegistroEnfermedad) throws SQLException {
        registroEnfermedadCronicaService.delete(idRegistroEnfermedad);
    }

    // Métodos de actualización individual - delegan a services específicos
    public int updateAlergia(Alergia alergia) throws SQLException {
        return alergiaService.update(alergia);
    }

    public int updateMedicamentoCronico(MedicamentoCronico medicamento) throws SQLException {
        return medicamentoCronicoService.update(medicamento);
    }

    public int updateRegistroCirugia(RegistroCirugia cirugia) throws SQLException {
        return registroCirugiaService.update(cirugia);
    }

    public int updateRegistroEnfermedadCronica(RegistroEnfermedadCronica enfermedad) throws SQLException {
        return registroEnfermedadCronicaService.update(enfermedad);
    }

    // Clase DTO para respuesta completa
    public static class AntecedentesMedicosCompletos {
        private List<AlergiasDTO> alergias = new ArrayList<>();
        private List<MedicamentoCronicoDTO> medicamentosCronicos = new ArrayList<>();
        private List<CirugiaDTO> cirugias = new ArrayList<>();
        private List<EnfermedadCronicaDTO> enfermedadesCronicas = new ArrayList<>();

        public List<AlergiasDTO> getAlergias() { return alergias; }
        public void setAlergias(List<AlergiasDTO> alergias) {
            this.alergias = alergias != null ? alergias : new ArrayList<>();
        }

        public List<MedicamentoCronicoDTO> getMedicamentosCronicos() { return medicamentosCronicos; }
        public void setMedicamentosCronicos(List<MedicamentoCronicoDTO> medicamentosCronicos) {
            this.medicamentosCronicos = medicamentosCronicos != null ? medicamentosCronicos : new ArrayList<>();
        }

        public List<CirugiaDTO> getCirugias() { return cirugias; }
        public void setCirugias(List<CirugiaDTO> cirugias) {
            this.cirugias = cirugias != null ? cirugias : new ArrayList<>();
        }

        public List<EnfermedadCronicaDTO> getEnfermedadesCronicas() { return enfermedadesCronicas; }
        public void setEnfermedadesCronicas(List<EnfermedadCronicaDTO> enfermedadesCronicas) {
            this.enfermedadesCronicas = enfermedadesCronicas != null ? enfermedadesCronicas : new ArrayList<>();
        }
    }
}
