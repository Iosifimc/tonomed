package org.medrecord.dto;


import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class ConsultaMedicaDTO {
    private String diagnostico;
    private String doctor;
    private String clinica;
    private Date fechaConsulta;

    private List<PrescripcionDTO> prescripcionDTOS = new ArrayList<>();
    private List<MedidasFisiologicasDTO> medidasFisiologicasDTOS = new ArrayList<>();
    private List<ProcedimientoMedicoDTO> procedimientoMedicoDTOS = new ArrayList<>();

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getClinica() {
        return clinica;
    }

    public void setClinica(String clinica) {
        this.clinica = clinica;
    }

    public Date getFechaConsulta() {
        return fechaConsulta;
    }

    public void setFechaConsulta(Date fechaConsulta) {
        this.fechaConsulta = fechaConsulta;
    }

    public List<PrescripcionDTO> getPrescripcionDTOS() {
        return prescripcionDTOS;
    }

    public void setPrescripcionDTOS(List<PrescripcionDTO> prescripcionDTOS) {
        this.prescripcionDTOS = prescripcionDTOS;
    }

    public List<MedidasFisiologicasDTO> getMedidasFisiologicasDTOS() {
        return medidasFisiologicasDTOS;
    }

    public void setMedidasFisiologicasDTOS(List<MedidasFisiologicasDTO> medidasFisiologicasDTOS) {
        this.medidasFisiologicasDTOS = medidasFisiologicasDTOS;
    }

    public List<ProcedimientoMedicoDTO> getProcedimientoMedicoDTOS() {
        return procedimientoMedicoDTOS;
    }

    public void setProcedimientoMedicoDTOS(List<ProcedimientoMedicoDTO> procedimientoMedicoDTOS) {
        this.procedimientoMedicoDTOS = procedimientoMedicoDTOS;
    }
}
