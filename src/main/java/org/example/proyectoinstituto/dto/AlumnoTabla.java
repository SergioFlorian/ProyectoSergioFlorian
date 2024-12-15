package org.example.proyectoinstituto.dto;

public class AlumnoTabla {
    private int id_alumno;
    private String nombre;
    private String apellidos;
    private double calificacion;
    private int faltas;

    public AlumnoTabla(int id_alumno, String nombre, String apellidos, double calificacion, int faltas) {
        this.id_alumno = id_alumno;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.calificacion = calificacion;
        this.faltas = faltas;
    }

    public AlumnoTabla(String nombre, String apellidos, double calificacion, int faltas) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.calificacion = calificacion;
        this.faltas = faltas;
    }

    public int getId_alumno() {
        return id_alumno;
    }

    public void setId_alumno(int id_alumno) {
        this.id_alumno = id_alumno;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public double getCalificacion() {
        return calificacion;
    }

    public int getFaltas() {
        return faltas;
    }
}

