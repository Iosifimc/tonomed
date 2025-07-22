package org.medrecord.service;

import org.medrecord.model.RegistroProcedimiento;
import org.medrecord.dto.ProcedimientoMedicoDTO;
import org.medrecord.repository.RegistroProcedimientoRepository;

import java.sql.SQLException;
import java.util.List;

public class RegistroProcedimientoService {
    private final RegistroProcedimientoRepository registroProcedimientoRepository;

    public RegistroProcedimientoService(RegistroProcedimientoRepository registroProcedimientoRepository) {
        this.registroProcedimientoRepository = registroProcedimientoRepository;
    }

    public int create(RegistroProcedimiento registroProcedimiento) throws SQLException {
        validateRegistroProcedimiento(registroProcedimiento);
        return registroProcedimientoRepository.saveRegistroProcedimiento(registroProcedimiento);
    }

    public void delete(int idRegistroProcedimiento) throws SQLException {
        if (idRegistroProcedimiento <= 0) {
            throw new IllegalArgumentException("El ID del registro de procedimiento debe ser mayor a 0");
        }
        registroProcedimientoRepository.deleteRegistroProcedimiento(idRegistroProcedimiento);
    }

    public int update(RegistroProcedimiento registroProcedimiento) throws SQLException {
        validateRegistroProcedimiento(registroProcedimiento);
        if (registroProcedimiento.getIdRegistroProcedimiento() <= 0) {
            throw new IllegalArgumentException("El ID del registro de procedimiento debe ser mayor a 0 para actualizar");
        }
        return registroProcedimientoRepository.updateRegistroProcedimiento(registroProcedimiento);
    }

    public List<ProcedimientoMedicoDTO> getProcedimientosConsulta(int idConsulta) throws SQLException {
        if (idConsulta <= 0) {
            throw new IllegalArgumentException("El ID de la consulta debe ser mayor a 0");
        }
        return registroProcedimientoRepository.findProcedimientosConsulta(idConsulta);
    }

    private void validateRegistroProcedimiento(RegistroProcedimiento registroProcedimiento) {
        if (registroProcedimiento == null) {
            throw new IllegalArgumentException("El registro de procedimiento no puede ser nulo");
        }
        if (registroProcedimiento.getIdProcedimiento() <= 0) {
            throw new IllegalArgumentException("El ID del procedimiento debe ser mayor a 0");
        }
        if (registroProcedimiento.getIdConsulta() <= 0) {
            throw new IllegalArgumentException("El ID de la consulta debe ser mayor a 0");
        }
    }
}