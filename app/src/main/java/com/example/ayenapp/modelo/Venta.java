package com.example.ayenapp.modelo;

import com.example.ayenapp.util.Util;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Venta implements Serializable {

    private String codigo;
    private String fecha;
    private List<Linea> lineasVenta;
    private Double total;

    public Venta() {}

    public Venta(String codigo, String fecha, List<Linea> lineasVenta, Double total) {
        this.codigo = codigo;
        this.fecha = fecha;
        this.lineasVenta = lineasVenta;
        this.total = total;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public List<Linea> getLineasVenta() {
        return lineasVenta;
    }

    public void setLineasVenta(List<Linea> lineasVenta) {
        this.lineasVenta = lineasVenta;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Venta venta = (Venta) o;
        return Objects.equals(codigo, venta.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return fecha + " - " + codigo + " - " + Util.formatearDouble(total) + "€";
    }
}
