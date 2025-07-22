package org.medrecord.service;

import org.medrecord.model.MedicamentoCronico;
import org.medrecord.dto.MedicamentoCronicoDTO;
import org.medrecord.repository.MedicamentoCronicoRepository;

import java.sql.SQLException;
import java.util.List;

public class MedicamentoCronicoService {
    private final MedicamentoCronicoRepository medicamentoCronicoRepository;

    public MedicamentoCronicoService(MedicamentoCronicoRepository medicamentoCronicoRepository) {
        this.medicamentoCronicoRepository = medicamentoCronicoRepository;
    }

    public int create(MedicamentoCronico medicamento) throws SQLException {
        validateMedicamentoCronico(medicamento);
        return medicamentoCronicoRepository.saveMedicamentoCronico(medicamento);
    }

    public void delete(int idMedicamentoCronico) throws SQLException {
        if (idMedicamentoCronico <= 0) {
            throw new IllegalArgumentException("El ID del medicamento crónico debe ser mayor a 0");
        }
        medicamentoCronicoRepository.deleteMedicamentoCronico(idMedicamentoCronico);
    }

    public int update(MedicamentoCronico medicamento) throws SQLException {
        validateMedicamentoCronico(medicamento);
        if (medicamento.getIdMedicamentoCronico() <= 0) {
            throw new IllegalArgumentException("El ID del medicamento debe ser mayor a 0 para actualizar");
        }
        return medicamentoCronicoRepository.updateMedicamentoCronico(medicamento);
    }

    public List<MedicamentoCronicoDTO> getAllMedicamentosByUsuario(int idUsuario) throws SQLException {
        if (idUsuario <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser mayor a 0");
        }
        return medicamentoCronicoRepository.findAllMedicamentoCronicoUsuario(idUsuario);
    }

    private void validateMedicamentoCronico(MedicamentoCronico medicamento) {
        if (medicamento == null) {
            throw new IllegalArgumentException("El medicamento crónico no puede ser nulo");
        }
        if (medicamento.getIdUsuario() <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser mayor a 0");
        }
        if (medicamento.getNombreMedicamento() == null || medicamento.getNombreMedicamento().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del medicamento no puede estar vacío");
        }
    }
}