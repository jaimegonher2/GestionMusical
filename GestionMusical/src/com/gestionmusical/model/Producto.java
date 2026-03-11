/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gestionmusical.model;

/**
 *
 * @author jaime
 */
public class Producto {
    
    private int    idProducto;
    private String nombre;
    private String descripcion;
    private String proveedor;
    private double precioCompra;
    private double precioVenta;
    private int    stockActual;
    private int    stockMinimo;
    private int    idCategoria;
    private String fechaAlta;
    private int    activo;

    public Producto() {
    }

    public Producto(int idProducto, String nombre, String descripcion, String proveedor, double precioCompra, double precioVenta, int stockActual, int stockMinimo, int idCategoria, String fechaAlta, int activo) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.proveedor = proveedor;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.idCategoria = idCategoria;
        this.fechaAlta = fechaAlta;
        this.activo = activo;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public double getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(double precioCompra) {
        this.precioCompra = precioCompra;
    }

    public double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public int getStockActual() {
        return stockActual;
    }

    public void setStockActual(int stockActual) {
        this.stockActual = stockActual;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(String fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public int getActivo() {
        return activo;
    }

    public void setActivo(int activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "Producto{" + "idProducto=" + idProducto + ", nombre=" + nombre + ", descripcion=" + descripcion + ", proveedor=" + proveedor + ", precioCompra=" + precioCompra + ", precioVenta=" + precioVenta + ", stockActual=" + stockActual + ", stockMinimo=" + stockMinimo + ", idCategoria=" + idCategoria + ", fechaAlta=" + fechaAlta + ", activo=" + activo + '}';
    }
    
    
    
}
