package org.medrecord.model;

import java.sql.Date;

public class RegistroMedida {
    private int idRegistroMedida;
    private int idMedida;
    private Integer idUsuario;
    private Integer idConsulta;
    private Date fechaRegistro;
    private double valorMedida;
    private String notaAdicional;
    private String tipoMedida;
    private String unidadMedida;

    public int getIdRegistroMedida() {
        return idRegistroMedida;
    }

    public void setIdRegistroMedida(int idRegistroMedida) {
        this.idRegistroMedida = idRegistroMedida;
    }

    public int getIdMedida() {
        return idMedida;
    }

    public void setIdMedida(int idMedida) {
        this.idMedida = idMedida;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdConsulta() {
        return idConsulta;
    }

    public void setIdConsulta(Integer idConsulta) {
        this.idConsulta = idConsulta;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public double getValorMedida() {
        return valorMedida;
    }

    public void setValorMedida(double valorMedida) {
        this.valorMedida = valorMedida;
    }

    public String getNotaAdicional() {
        return notaAdicional;
    }

    public void setNotaAdicional(String notaAdicional) {
        this.notaAdicional = notaAdicional;
    }

    public String getTipoMedida() {
        return tipoMedida;
    }

    public void setTipoMedida(String tipoMedida) {
        this.tipoMedida = tipoMedida;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }
}
