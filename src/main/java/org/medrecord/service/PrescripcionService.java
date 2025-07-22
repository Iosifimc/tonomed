package org.medrecord.service;

import org.medrecord.model.Prescripcion;
import org.medrecord.dto.PrescripcionDTO;
import org.medrecord.repository.PrescripcionRepository;

import java.sql.SQLException;
import java.util.List;

public class PrescripcionService {
    private final PrescripcionRepository prescripcionRepository;

    public PrescripcionService(PrescripcionRepository prescripcionRepository) {
        this.prescripcionRepository = prescripcionRepository;
    }

    public int create(Prescripcion prescripcion) throws SQLException {
        validatePrescripcion(prescripcion);
        return prescripcionRepository.savePrescripcion(prescripcion);
    }

    public void delete(int idPrescripcion) throws SQLException {
        if (idPrescripcion <= 0) {
            throw new IllegalArgumentException("El ID de la prescripción debe ser mayor a 0");
        }
        prescripcionRepository.deletePrescripcion(idPrescripcion);
    }

    public int update(Prescripcion prescripcion) throws SQLException {
        validatePrescripcion(prescripcion);
        if (prescripcion.getIdPrescripcion() <= 0) {
            throw new IllegalArgumentException("El ID de la prescripción debe ser mayor a 0 para actualizar");
        }
        return prescripcionRepository.updatePrescripcion(prescripcion);
    }

    public List<PrescripcionDTO> getPrescripcionesByConsulta(int idConsulta) throws SQLException {
        if (idConsulta <= 0) {
            throw new IllegalArgumentException("El ID de la consulta debe ser mayor a 0");
        }
        return prescripcionRepository.findPrescripcionesByConsulta(idConsulta);
    }

    private void validatePrescripcion(Prescripcion prescripcion) {
        if (prescripcion == null) {
            throw new IllegalArgumentException("La prescripción no puede ser nula");
        }
        if (prescripcion.getIdConsulta() <= 0) {
            throw new IllegalArgumentException("El ID de la consulta debe ser mayor a 0");
        }
        if (prescripcion.getNombreMedicamento() == null || prescripcion.getNombreMedicamento().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del medicamento es obligatorio");
        }
        if (prescripcion.getPresentacionMedicamento() == null || prescripcion.getPresentacionMedicamento().trim().isEmpty()) {
            throw new IllegalArgumentException("La presentación del medicamento es obligatoria");
        }
        if (prescripcion.getDosis() == null || prescripcion.getDosis().trim().isEmpty()) {
            throw new IllegalArgumentException("La dosis es obligatoria");
        }
        if (prescripcion.getFrecuencia() == null || prescripcion.getFrecuencia().trim().isEmpty()) {
            throw new IllegalArgumentException("La frecuencia es obligatoria");
        }
        if (prescripcion.getDuracion() == null || prescripcion.getDuracion().trim().isEmpty()) {
            throw new IllegalArgumentException("La duración es obligatoria");
        }
    }
}