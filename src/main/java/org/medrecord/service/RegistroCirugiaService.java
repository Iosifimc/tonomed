package org.medrecord.service;

import org.medrecord.model.RegistroCirugia;
import org.medrecord.dto.CirugiaDTO;
import org.medrecord.repository.RegistroCirugiaRepository;

import java.sql.SQLException;
import java.util.List;

public class RegistroCirugiaService {
    private final RegistroCirugiaRepository registroCirugiaRepository;

    public RegistroCirugiaService(RegistroCirugiaRepository registroCirugiaRepository) {
        this.registroCirugiaRepository = registroCirugiaRepository;
    }

    public int create(RegistroCirugia registro) throws SQLException {
        validateRegistroCirugia(registro);
        return registroCirugiaRepository.saveRegistroCirugia(registro);
    }

    public void delete(int idRegistroCirugia) throws SQLException {
        if (idRegistroCirugia <= 0) {
            throw new IllegalArgumentException("El ID del registro de cirugía debe ser mayor a 0");
        }
        registroCirugiaRepository.deleteRegistroCirugia(idRegistroCirugia);
    }

    public int update(RegistroCirugia registro) throws SQLException {
        if (registro == null) {
            throw new IllegalArgumentException("El registro de cirugía no puede ser nulo");
        }
        if (registro.getIdRegistroCirugia() <= 0) {
            throw new IllegalArgumentException("El ID del registro debe ser mayor a 0 para actualizar");
        }
        if (registro.getFechaCirugia() == null) {
            throw new IllegalArgumentException("La fecha de cirugía es obligatoria");
        }
        return registroCirugiaRepository.updateRegistroCirugia(registro);
    }

    public List<CirugiaDTO> getCirugiasByUsuario(int idUsuario) throws SQLException {
        if (idUsuario <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser mayor a 0");
        }
        return registroCirugiaRepository.findCirugiasUsuario(idUsuario);
    }

    private void validateRegistroCirugia(RegistroCirugia registro) {
        if (registro == null) {
            throw new IllegalArgumentException("El registro de cirugía no puede ser nulo");
        }
        if (registro.getIdUsuario() <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser mayor a 0");
        }
        if (registro.getFechaCirugia() == null) {
            throw new IllegalArgumentException("La fecha de cirugía es obligatoria");
        }
        // Validar que tenga al menos un tipo de cirugía (catálogo o personalizada)
        if (registro.getIdCirugia() <= 0 && registro.getIdCirugiaPersonalizada() <= 0) {
            throw new IllegalArgumentException("Debe especificar un tipo de cirugía (catálogo o personalizada)");
        }
    }
}