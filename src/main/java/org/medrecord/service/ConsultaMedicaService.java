package org.medrecord.service;

import org.medrecord.model.ConsultaMedica;
import org.medrecord.dto.ConsultaMedicaDTO;
import org.medrecord.repository.ConsultaMedicaRepository;

import java.sql.SQLException;
import java.util.List;

public class ConsultaMedicaService {
    private final ConsultaMedicaRepository consultaMedicaRepository;

    public ConsultaMedicaService(ConsultaMedicaRepository consultaMedicaRepository) {
        this.consultaMedicaRepository = consultaMedicaRepository;
    }

    public int create(ConsultaMedica consultaMedica) throws SQLException {
        validateConsultaMedica(consultaMedica);
        return consultaMedicaRepository.saveConsultaMedica(consultaMedica);
    }

    public void delete(int idConsulta) throws SQLException {
        if (idConsulta <= 0) {
            throw new IllegalArgumentException("El ID de la consulta debe ser mayor a 0");
        }
        consultaMedicaRepository.deleteConsultaMedica(idConsulta);
    }

    public int update(ConsultaMedica consultaMedica) throws SQLException {
        validateConsultaMedica(consultaMedica);
        if (consultaMedica.getIdConsulta() <= 0) {
            throw new IllegalArgumentException("El ID de la consulta debe ser mayor a 0 para actualizar");
        }
        return consultaMedicaRepository.updateConsultaMedica(consultaMedica);
    }

    public List<ConsultaMedicaDTO> getConsultasByUsuario(int idUsuario) throws SQLException {
        if (idUsuario <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser mayor a 0");
        }
        return consultaMedicaRepository.findConsultasByUsuario(idUsuario);
    }

    private void validateConsultaMedica(ConsultaMedica consultaMedica) {
        if (consultaMedica == null) {
            throw new IllegalArgumentException("La consulta médica no puede ser nula");
        }
        if (consultaMedica.getIdUsuario() <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser mayor a 0");
        }
        if (consultaMedica.getDiagnostico() == null || consultaMedica.getDiagnostico().trim().isEmpty()) {
            throw new IllegalArgumentException("El diagnóstico es obligatorio");
        }
        if (consultaMedica.getDoctor() == null || consultaMedica.getDoctor().trim().isEmpty()) {
            throw new IllegalArgumentException("El doctor es obligatorio");
        }
        if (consultaMedica.getClinica() == null || consultaMedica.getClinica().trim().isEmpty()) {
            throw new IllegalArgumentException("La clínica es obligatoria");
        }
        if (consultaMedica.getFechaConsulta() == null) {
            throw new IllegalArgumentException("La fecha de consulta es obligatoria");
        }
    }
}
