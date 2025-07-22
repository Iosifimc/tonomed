package org.medrecord.model;

public class MedicamentoCronico {
    private int idMedicamentoCronico;
    private int idUsuario;
    private String nombreMedicamento;

    public int getIdMedicamentoCronico() {
        return idMedicamentoCronico;
    }

    public void setIdMedicamentoCronico(int idMedicamentoCronico) {
        this.idMedicamentoCronico = idMedicamentoCronico;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreMedicamento() {
        return nombreMedicamento;
    }

    public void setNombreMedicamento(String nombreMedicamento) {
        this.nombreMedicamento = nombreMedicamento;
    }
}
