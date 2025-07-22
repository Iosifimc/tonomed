package org.medrecord.service;

import org.medrecord.model.Alergia;
import org.medrecord.dto.AlergiasDTO;
import org.medrecord.repository.AlergiaRepository;

import java.sql.SQLException;
import java.util.List;

public class AlergiaService {
    private final AlergiaRepository alergiaRepository;

    public AlergiaService(AlergiaRepository alergiaRepository) {
        this.alergiaRepository = alergiaRepository;
    }

    public int create(Alergia alergia) throws SQLException {
        validateAlergia(alergia);
        return alergiaRepository.saveAlergia(alergia);
    }

    public void delete(int idAlergia) throws SQLException {
        if (idAlergia <= 0) {
            throw new IllegalArgumentException("El ID de la alergia debe ser mayor a 0");
        }
        alergiaRepository.deleteAlergia(idAlergia);
    }

    public int update(Alergia alergia) throws SQLException {
        validateAlergia(alergia);
        if (alergia.getIdAlergia() <= 0) {
            throw new IllegalArgumentException("El ID de la alergia debe ser mayor a 0 para actualizar");
        }
        return alergiaRepository.updateAlergia(alergia);
    }

    public List<AlergiasDTO> getAllAlergiasByUsuario(int idUsuario) throws SQLException {
        if (idUsuario <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser mayor a 0");
        }
        return alergiaRepository.findAllAlergiaUsuario(idUsuario);
    }

    private void validateAlergia(Alergia alergia) {
        if (alergia == null) {
            throw new IllegalArgumentException("La alergia no puede ser nula");
        }
        if (alergia.getIdUsuario() <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser mayor a 0");
        }
        if (alergia.getNombreAlergia() == null || alergia.getNombreAlergia().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la alergia no puede estar vacío");
        }
    }
}
