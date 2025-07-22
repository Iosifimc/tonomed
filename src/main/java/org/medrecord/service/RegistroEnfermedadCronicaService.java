package org.medrecord.service;

import org.medrecord.model.RegistroEnfermedadCronica;
import org.medrecord.dto.EnfermedadCronicaDTO;
import org.medrecord.repository.RegistroEnfermedadCronicaRepository;

import java.sql.SQLException;
import java.util.List;

public class RegistroEnfermedadCronicaService {
    private final RegistroEnfermedadCronicaRepository registroEnfermedadCronicaRepository;

    public RegistroEnfermedadCronicaService(RegistroEnfermedadCronicaRepository registroEnfermedadCronicaRepository) {
        this.registroEnfermedadCronicaRepository = registroEnfermedadCronicaRepository;
    }

    public int create(RegistroEnfermedadCronica registro) throws SQLException {
        validateRegistroEnfermedadCronica(registro);
        return registroEnfermedadCronicaRepository.saveRegistroEnfermedadCronica(registro);
    }

    public void delete(int idRegistroEnfermedad) throws SQLException {
        if (idRegistroEnfermedad <= 0) {
            throw new IllegalArgumentException("El ID del registro de enfermedad debe ser mayor a 0");
        }
        registroEnfermedadCronicaRepository.deleteRegistroEnfermedadCronica(idRegistroEnfermedad);
    }

    public int update(RegistroEnfermedadCronica registro) throws SQLException {
        if (registro == null) {
            throw new IllegalArgumentException("El registro de enfermedad no puede ser nulo");
        }
        if (registro.getIdRegistroEnfermedad() <= 0) {
            throw new IllegalArgumentException("El ID del registro debe ser mayor a 0 para actualizar");
        }
        if (registro.getFechaDiagnostico() == null) {
            throw new IllegalArgumentException("La fecha de diagnóstico es obligatoria");
        }
        return registroEnfermedadCronicaRepository.updateRegistroEnfermedadCronica(registro);
    }

    public List<EnfermedadCronicaDTO> getEnfermedadesCronicasByUsuario(int idUsuario) throws SQLException {
        if (idUsuario <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser mayor a 0");
        }
        return registroEnfermedadCronicaRepository.findAllEnfermedadesCronicasUsuario(idUsuario);
    }

    private void validateRegistroEnfermedadCronica(RegistroEnfermedadCronica registro) {
        if (registro == null) {
            throw new IllegalArgumentException("El registro de enfermedad crónica no puede ser nulo");
        }
        if (registro.getIdUsuario() <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser mayor a 0");
        }
        if (registro.getFechaDiagnostico() == null) {
            throw new IllegalArgumentException("La fecha de diagnóstico es obligatoria");
        }
        // Validar que tenga al menos un tipo de enfermedad (catálogo o personalizada)
        if (registro.getIdEnfermedadCronica() <= 0 && registro.getIdEnfermedadPersonalizada() <= 0) {
            throw new IllegalArgumentException("Debe especificar un tipo de enfermedad (catálogo o personalizada)");
        }
    }
}