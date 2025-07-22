package org.medrecord.dto;

public class PrescripcionDTO {
    private String nombreMedicamento;
    private String presentacionMedicamento;
    private String dosis;
    private String frecuencia;
    private String duracion;

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

    public String getPresentacionMedicamento() {
        return presentacionMedicamento;
    }

    public void setPresentacionMedicamento(String presentacionMedicamento) {
        this.presentacionMedicamento = presentacionMedicamento;
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
