package com.example.ayenapp.modelo;

import com.example.ayenapp.util.Util;

import java.io.Serializable;
import java.util.Objects;

public class Linea implements Serializable {

    private Producto producto;
    private Integer cantidad;
    private Double precio;

    public Linea() {}

    public Linea(Producto producto, Integer cantidad, Double precio) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precio = precio;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Linea linea = (Linea) o;
        return Objects.equals(producto, linea.producto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(producto);
    }

    @Override
    public String toString() {
        return cantidad + "x " + producto.getNombre() + " - " + Util.formatearDouble(precio) + "€" ;
    }
}
