package org.medrecord.model;

public class RegistroProcedimiento {
    private int idRegistroProcedimiento;
    private int idProcedimiento;
    private int idConsulta;
    private String notaAdicional;

    public int getIdRegistroProcedimiento() {
        return idRegistroProcedimiento;
    }

    public void setIdRegistroProcedimiento(int idRegistroProcedimiento) {
        this.idRegistroProcedimiento = idRegistroProcedimiento;
    }

    public int getIdProcedimiento() {
        return idProcedimiento;
    }

    public void setIdProcedimiento(int idProcedimiento) {
        this.idProcedimiento = idProcedimiento;
    }

    public int getIdConsulta() {
        return idConsulta;
    }

    public void setIdConsulta(int idConsulta) {
        this.idConsulta = idConsulta;
    }

    public String getNotaAdicional() {
        return notaAdicional;
    }

    public void setNotaAdicional(String notaAdicional) {
        this.notaAdicional = notaAdicional;
    }
}
