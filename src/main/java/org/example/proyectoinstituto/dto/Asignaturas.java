package org.example.proyectoinstituto.dto;

public class Asignaturas {

    private int id_asignatura;
    private String nombre_asignatura;
    private String descripcion;
    private int id_profesor;

    public Asignaturas(int id_asignatura, String nombre_asignatura, String descripcion, int id_profesor) {
        this.id_asignatura = id_asignatura;
        this.nombre_asignatura = nombre_asignatura;
        this.descripcion = descripcion;
        this.id_profesor = id_profesor;
    }

    public int getId_asignatura() {
        return id_asignatura;
    }

    public void setId_asignatura(int id_asignatura) {
        this.id_asignatura = id_asignatura;
    }

    public String getNombre_asignatura() {
        return nombre_asignatura;
    }

    public void setNombre_asignatura(String nombre_asignatura) {
        this.nombre_asignatura = nombre_asignatura;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getId_profesor() {
        return id_profesor;
    }

    public void setId_profesor(int id_profesor) {
        this.id_profesor = id_profesor;
    }
}
