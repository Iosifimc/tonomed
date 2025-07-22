package org.medrecord.di;

import org.medrecord.controller.*;
import org.medrecord.repository.*;
import org.medrecord.routes.*;
import org.medrecord.service.*;


public class AppModule {

    public static UsuarioRoutes initUser() {
        UsuarioRepository usuarioRepo = new UsuarioRepository();
        UsuarioService usuarioService = new UsuarioService(usuarioRepo);
        UsuarioController usuarioController = new UsuarioController(usuarioService);
        return new UsuarioRoutes(usuarioController);
    }

    public static ConsultaCompletaRoutes initConsultaCompleta() {
        ConsultaMedicaRepository consultaMedicaRepo = new ConsultaMedicaRepository();
        PrescripcionRepository prescripcionRepo = new PrescripcionRepository();
        RegistroMedidaRepository registroMedidaRepo = new RegistroMedidaRepository();
        RegistroProcedimientoRepository registroProcedimientoRepo = new RegistroProcedimientoRepository();
        ConsultaMedicaService consultaMedicaService = new ConsultaMedicaService(consultaMedicaRepo);
        PrescripcionService prescripcionService = new PrescripcionService(prescripcionRepo);
        RegistroMedidaService registroMedidaService = new RegistroMedidaService(registroMedidaRepo);
        RegistroProcedimientoService registroProcedimientoService = new RegistroProcedimientoService(registroProcedimientoRepo);
        ConsultaCompletaService consultaCompletaService = new ConsultaCompletaService(
                consultaMedicaService,
                prescripcionService,
                registroMedidaService,
                registroProcedimientoService
        );
        ConsultaCompletaController consultaCompletaController = new ConsultaCompletaController(consultaCompletaService);
        return new ConsultaCompletaRoutes(consultaCompletaController);
    }

    public static MedidasFisiologicasRoutes initMedidasFisiologicas() {
        RegistroMedidaRepository registroMedidaRepo = new RegistroMedidaRepository();
        MedidasFisiologicasService medidasFisiologicasService = new MedidasFisiologicasService(registroMedidaRepo);
        MedidasFisiologicasController medidasFisiologicasController = new MedidasFisiologicasController(medidasFisiologicasService);
        return new MedidasFisiologicasRoutes(medidasFisiologicasController);
    }

    public static AntecedentesMedicosRoutes initAntecedentesMedicos() {
        // Inicializar repositories
        AlergiaRepository alergiaRepo = new AlergiaRepository();
        MedicamentoCronicoRepository medicamentoCronicoRepo = new MedicamentoCronicoRepository();
        CirugiaPersonalizadaRepository cirugiaPersonalizadaRepo = new CirugiaPersonalizadaRepository();
        EnfermedadCronicaRepository enfermedadCronicaRepo = new EnfermedadCronicaRepository();
        RegistroCirugiaRepository registroCirugiaRepo = new RegistroCirugiaRepository();
        RegistroEnfermedadCronicaRepository registroEnfermedadCronicaRepo = new RegistroEnfermedadCronicaRepository();
        AlergiaService alergiaService = new AlergiaService(alergiaRepo);
        MedicamentoCronicoService medicamentoCronicoService = new MedicamentoCronicoService(medicamentoCronicoRepo);
        RegistroCirugiaService registroCirugiaService = new RegistroCirugiaService(registroCirugiaRepo);
        RegistroEnfermedadCronicaService registroEnfermedadCronicaService = new RegistroEnfermedadCronicaService(registroEnfermedadCronicaRepo);
        AntecedentesMedicosService antecedentesMedicosService = new AntecedentesMedicosService(
                alergiaService,
                medicamentoCronicoService,
                registroCirugiaService,
                registroEnfermedadCronicaService
        );
        AntecedentesMedicosController antecedentesMedicosController = new AntecedentesMedicosController(antecedentesMedicosService);
        return new AntecedentesMedicosRoutes(antecedentesMedicosController);
    }
}


