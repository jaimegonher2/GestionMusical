package com.gestionmusical.model;

public class Venta {

    private int    idVenta;
    private String fechaHora;
    private double total;
    private double descuento;
    private String formaPago;
    private int    idUsuario;
    private int    idCliente;
    private String observaciones;

    public Venta() {
    }

    public Venta(int idVenta, String fechaHora, double total, double descuento, String formaPago, int idUsuario, int idCliente, String observaciones) {
        this.idVenta = idVenta;
        this.fechaHora = fechaHora;
        this.total = total;
        this.descuento = descuento;
        this.formaPago = formaPago;
        this.idUsuario = idUsuario;
        this.idCliente = idCliente;
        this.observaciones = observaciones;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public String getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @Override
    public String toString() {
        return "Venta{" + "idVenta=" + idVenta + ", fechaHora=" + fechaHora + ", total=" + total + ", descuento=" + descuento + ", formaPago=" + formaPago + ", idUsuario=" + idUsuario + ", idCliente=" + idCliente + ", observaciones=" + observaciones + '}';
    }
    
    
}