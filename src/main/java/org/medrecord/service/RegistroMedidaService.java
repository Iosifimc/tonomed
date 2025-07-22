package org.medrecord.service;

import org.medrecord.model.RegistroMedida;
import org.medrecord.dto.MedidasFisiologicasDTO;
import org.medrecord.repository.RegistroMedidaRepository;

import java.sql.SQLException;
import java.util.List;

public class RegistroMedidaService {
    private final RegistroMedidaRepository registroMedidaRepository;

    public RegistroMedidaService(RegistroMedidaRepository registroMedidaRepository) {
        this.registroMedidaRepository = registroMedidaRepository;
    }

    public int createMedidaPersonal(RegistroMedida registroMedida) throws SQLException {
        validateMedidaPersonal(registroMedida);
        return registroMedidaRepository.saveRegistroMedidaPersonal(registroMedida);
    }

    public int createMedidaConsulta(RegistroMedida registroMedida) throws SQLException {
        validateMedidaConsulta(registroMedida);
        return registroMedidaRepository.saveRegistroMedidaConsulta(registroMedida);
    }

    public void delete(int idRegistroMedida) throws SQLException {
        if (idRegistroMedida <= 0) {
            throw new IllegalArgumentException("El ID del registro de medida debe ser mayor a 0");
        }
        registroMedidaRepository.deleteRegistroMedida(idRegistroMedida);
    }

    public int update(RegistroMedida registroMedida) throws SQLException {
        validateRegistroMedida(registroMedida);
        if (registroMedida.getIdRegistroMedida() <= 0) {
            throw new IllegalArgumentException("El ID del registro de medida debe ser mayor a 0 para actualizar");
        }
        return registroMedidaRepository.updateRegistroMedida(registroMedida);
    }

    public List<MedidasFisiologicasDTO> getAllMedidasUsuario(int idUsuario) throws SQLException {
        if (idUsuario <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser mayor a 0");
        }
        return registroMedidaRepository.findAllMedidasUsuario(idUsuario);
    }

    public List<MedidasFisiologicasDTO> getMedidasConsulta(int idConsulta) throws SQLException {
        if (idConsulta <= 0) {
            throw new IllegalArgumentException("El ID de la consulta debe ser mayor a 0");
        }
        return registroMedidaRepository.findMedidaConsulta(idConsulta);
    }

    private void validateRegistroMedida(RegistroMedida registroMedida) {
        if (registroMedida == null) {
            throw new IllegalArgumentException("El registro de medida no puede ser nulo");
        }
        if (registroMedida.getIdMedida() <= 0) {
            throw new IllegalArgumentException("El ID de la medida debe ser mayor a 0");
        }
        if (registroMedida.getFechaRegistro() == null) {
            throw new IllegalArgumentException("La fecha de registro es obligatoria");
        }
        if (registroMedida.getValorMedida() < 0) {
            throw new IllegalArgumentException("El valor de la medida no puede ser negativo");
        }
    }

    private void validateMedidaPersonal(RegistroMedida registroMedida) {
        validateRegistroMedida(registroMedida);
        if (registroMedida.getIdUsuario() == null || registroMedida.getIdUsuario() <= 0) {
            throw new IllegalArgumentException("El ID del usuario es obligatorio para una medida personal");
        }
        if (registroMedida.getIdConsulta() != null) {
            throw new IllegalArgumentException("El ID de consulta debe ser nulo para una medida personal");
        }
    }

    private void validateMedidaConsulta(RegistroMedida registroMedida) {
        validateRegistroMedida(registroMedida);
        if (registroMedida.getIdConsulta() == null || registroMedida.getIdConsulta() <= 0) {
            throw new IllegalArgumentException("El ID de la consulta es obligatorio para una medida de consulta");
        }
        if (registroMedida.getIdUsuario() != null) {
            throw new IllegalArgumentException("El ID de usuario debe ser nulo para una medida de consulta");
        }
    }
}