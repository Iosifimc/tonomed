package org.medrecord.model;

import java.sql.Date;

public class RegistroCirugia {
    private int idRegistroCirugia;
    private int idUsuario;
    private int idCirugia;
    private int idCirugiaPersonalizada;
    private Date fechaCirugia;
    private String nombreCirugia;

    public int getIdRegistroCirugia() {
        return idRegistroCirugia;
    }

    public void setIdRegistroCirugia(int idRegistroCirugia) {
        this.idRegistroCirugia = idRegistroCirugia;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdCirugia() {
        return idCirugia;
    }

    public void setIdCirugia(int idCirugia) {
        this.idCirugia = idCirugia;
    }

    public int getIdCirugiaPersonalizada() {
        return idCirugiaPersonalizada;
    }

    public void setIdCirugiaPersonalizada(int idCirugiaPersonalizada) {
        this.idCirugiaPersonalizada = idCirugiaPersonalizada;
    }

    public Date getFechaCirugia() {
        return fechaCirugia;
    }

    public void setFechaCirugia(Date fechaCirugia) {
        this.fechaCirugia = fechaCirugia;
    }

    public String getNombreCirugia() {
        return nombreCirugia;
    }

    public void setNombreCirugia(String nombreCirugia) {
        this.nombreCirugia = nombreCirugia;
    }
}
