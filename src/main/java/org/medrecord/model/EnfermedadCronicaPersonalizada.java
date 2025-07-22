package org.medrecord.model;

public class EnfermedadCronicaPersonalizada {
    private int idEnfermedadPersonalizada;
    private int idUsuario;
    private String nombreEnfermedad;

    public int getIdEnfermedadPersonalizada() {
        return idEnfermedadPersonalizada;
    }

    public void setIdEnfermedadPersonalizada(int idEnfermedadPersonalizada) {
        this.idEnfermedadPersonalizada = idEnfermedadPersonalizada;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreEnfermedad() {
        return nombreEnfermedad;
    }

    public void setNombreEnfermedad(String nombreEnfermedad) {
        this.nombreEnfermedad = nombreEnfermedad;
    }
}
