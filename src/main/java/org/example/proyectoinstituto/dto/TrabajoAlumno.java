package org.example.proyectoinstituto.dto;

public class TrabajoAlumno {
    private String nombreTrabajo;
    private double calificacion;

    public TrabajoAlumno(String nombreTrabajo, double calificacion) {
        this.nombreTrabajo = nombreTrabajo;
        this.calificacion = calificacion;
    }


    public String getNombreTrabajo() {
        return nombreTrabajo;
    }

    public void setNombreTrabajo(String nombreTrabajo) {
        this.nombreTrabajo = nombreTrabajo;
    }

    public double getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(double calificacion) {
        this.calificacion = calificacion;
    }
}

