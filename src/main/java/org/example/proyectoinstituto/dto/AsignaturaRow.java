package org.example.proyectoinstituto.dto;

public class AsignaturaRow {
    private final String nombreAsignatura;
    private final String descripcion;
    private final String profesor;
    private final Double calificacion;
    private final Integer faltas;

    public AsignaturaRow(String nombreAsignatura, String descripcion, String profesor, Double calificacion, Integer faltas) {
        this.nombreAsignatura = nombreAsignatura;
        this.descripcion = descripcion;
        this.profesor = profesor;
        this.calificacion = calificacion;
        this.faltas = faltas;
    }

    public String getNombreAsignatura() {
        return nombreAsignatura;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getProfesor() {
        return profesor;
    }

    public Double getCalificacion() {
        return calificacion;
    }

    public Integer getFaltas() {
        return faltas;
    }
}

