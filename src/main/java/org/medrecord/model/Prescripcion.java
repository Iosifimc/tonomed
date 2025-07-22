package org.medrecord.model;

public class Prescripcion {
    private int idPrescripcion;
    private int idConsulta;
    private String nombreMedicamento;
    private String presentacionMedicamento;
    private String dosis;
    private String frecuencia;
    private String duracion;

    public int getIdPrescripcion() {
        return idPrescripcion;
    }

    public void setIdPrescripcion(int idPrescripcion) {
        this.idPrescripcion = idPrescripcion;
    }

    public int getIdConsulta() {
        return idConsulta;
    }

    public void setIdConsulta(int idConsulta) {
        this.idConsulta = idConsulta;
    }

    public String getPresentacionMedicamento() {
        return presentacionMedicamento;
    }

    public void setPresentacionMedicamento(String presentacionMedicamento) {
        this.presentacionMedicamento = presentacionMedicamento;
    }

    public String getNombreMedicamento() {
        return nombreMedicamento;
    }

    public void setNombreMedicamento(String nombreMedicamento) {
        this.nombreMedicamento = nombreMedicamento;
    }

    public String getDosis() {
        return dosis;
    }

    public void setDosis(String dosis) {
        this.dosis = dosis;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }
}
