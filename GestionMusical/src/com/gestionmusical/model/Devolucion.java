package com.gestionmusical.model;

public class Devolucion {

    private int    idDevolucion;
    private int    idVenta;
    private int    idProducto;
    private int    cantidad;
    private String motivo;
    private String fechaHora;
    private int    idUsuario;

    public Devolucion() {
    }

    public Devolucion(int idDevolucion, int idVenta, int idProducto, int cantidad, String motivo, String fechaHora, int idUsuario) {
        this.idDevolucion = idDevolucion;
        this.idVenta = idVenta;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.motivo = motivo;
        this.fechaHora = fechaHora;
        this.idUsuario = idUsuario;
    }

    public int getIdDevolucion() {
        return idDevolucion;
    }

    public void setIdDevolucion(int idDevolucion) {
        this.idDevolucion = idDevolucion;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Override
    public String toString() {
        return "Devolucion{" + "idDevolucion=" + idDevolucion + ", idVenta=" + idVenta + ", idProducto=" + idProducto + ", cantidad=" + cantidad + ", motivo=" + motivo + ", fechaHora=" + fechaHora + ", idUsuario=" + idUsuario + '}';
    }
    
    
}