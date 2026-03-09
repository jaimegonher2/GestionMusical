package com.gestionmusical.model;

public class Backup {

    private int    idBackup;
    private String rutaArchivo;
    private String fechaCreacion;
    private int    idUsuario;
    private String descripcion;

    public Backup() {
    }

    public Backup(int idBackup, String rutaArchivo, String fechaCreacion, int idUsuario, String descripcion) {
        this.idBackup = idBackup;
        this.rutaArchivo = rutaArchivo;
        this.fechaCreacion = fechaCreacion;
        this.idUsuario = idUsuario;
        this.descripcion = descripcion;
    }

    public int getIdBackup() {
        return idBackup;
    }

    public void setIdBackup(int idBackup) {
        this.idBackup = idBackup;
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    @Override
    public String toString() {
        return "Backup [" + fechaCreacion + "] → " + rutaArchivo;
    }
}
