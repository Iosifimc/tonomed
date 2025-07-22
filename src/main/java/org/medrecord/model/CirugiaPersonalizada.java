package org.medrecord.model;

public class CirugiaPersonalizada {
    private int idCirugiaPersonalizada;
    private int idUsuario;
    private String nombreCirugia;

    public int getIdCirugiaPersonalizada() {
        return idCirugiaPersonalizada;
    }

    public void setIdCirugiaPersonalizada(int idCirugiaPersonalizada) {
        this.idCirugiaPersonalizada = idCirugiaPersonalizada;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreCirugia() {
        return nombreCirugia;
    }

    public void setNombreCirugia(String nombreCirugia) {
        this.nombreCirugia = nombreCirugia;
    }
}
