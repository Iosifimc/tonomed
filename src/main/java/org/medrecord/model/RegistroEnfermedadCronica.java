package org.medrecord.model;

import java.sql.Date;

public class RegistroEnfermedadCronica {
    private int idRegistroEnfermedad;
    private int idUsuario;
    private int idEnfermedadCronica;
    private int idEnfermedadPersonalizada;
    private Date fechaDiagnostico;
    private String nombreEnfermedad;

    public int getIdRegistroEnfermedad() {
        return idRegistroEnfermedad;
    }

    public void setIdRegistroEnfermedad(int idRegistroEnfermedad) {
        this.idRegistroEnfermedad = idRegistroEnfermedad;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdEnfermedadCronica() {
        return idEnfermedadCronica;
    }

    public void setIdEnfermedadCronica(int idEnfermedadCronica) {
        this.idEnfermedadCronica = idEnfermedadCronica;
    }

    public int getIdEnfermedadPersonalizada() {
        return idEnfermedadPersonalizada;
    }

    public void setIdEnfermedadPersonalizada(int idEnfermedadPersonalizada) {
        this.idEnfermedadPersonalizada = idEnfermedadPersonalizada;
    }

    public Date getFechaDiagnostico() {
        return fechaDiagnostico;
    }

    public void setFechaDiagnostico(Date fechaDiagnostico) {
        this.fechaDiagnostico = fechaDiagnostico;
    }

    public String getNombreEnfermedad() {
        return nombreEnfermedad;
    }

    public void setNombreEnfermedad(String nombreEnfermedad) {
        this.nombreEnfermedad = nombreEnfermedad;
    }
}
