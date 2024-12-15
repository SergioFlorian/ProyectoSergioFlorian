package org.example.proyectoinstituto.dto;

public class TrabajoTabla {
    private int idTrabajo;
    private String titulo;
    private String descripcion;
    private String fechaEntrega;

    public TrabajoTabla(int idTrabajo, String titulo, String descripcion, String fechaEntrega) {
        this.idTrabajo = idTrabajo;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaEntrega = fechaEntrega;
    }

    public int getIdTrabajo() {
        return idTrabajo;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getFechaEntrega() {
        return fechaEntrega;
    }
}
