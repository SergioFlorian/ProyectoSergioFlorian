package org.example.proyectoinstituto.dto;

public class Trabajos {

    private int id_trabajo;
    private String titulo;
    private String descripcion;
    private String fecha_entrega;
    private int id_asignatura;
    private int id_profesor;

    public Trabajos(int id_trabajo, String titulo, String descripcion, String fecha_entrega, int id_asignatura, int id_profesor) {
        this.id_trabajo = id_trabajo;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fecha_entrega = fecha_entrega;
        this.id_asignatura = id_asignatura;
        this.id_profesor = id_profesor;
    }

    public Trabajos(String titulo, String descripcion, String fecha_entrega, int id_asignatura) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fecha_entrega = fecha_entrega;
        this.id_asignatura = id_asignatura;
    }

    public Trabajos(int id_trabajo, String titulo, String descripcion, String fecha_entrega) {
        this.id_trabajo = id_trabajo;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fecha_entrega = fecha_entrega;
    }

    public int getId_trabajo() {
        return id_trabajo;
    }

    public void setId_trabajo(int id_trabajo) {
        this.id_trabajo = id_trabajo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFecha_entrega() {
        return fecha_entrega;
    }

    public void setFecha_entrega(String fecha_entrega) {
        this.fecha_entrega = fecha_entrega;
    }

    public int getId_asignatura() {
        return id_asignatura;
    }

    public void setId_asignatura(int id_asignatura) {
        this.id_asignatura = id_asignatura;
    }

    public int getId_profesor() {
        return id_profesor;
    }

    public void setId_profesor(int id_profesor) {
        this.id_profesor = id_profesor;
    }

}
