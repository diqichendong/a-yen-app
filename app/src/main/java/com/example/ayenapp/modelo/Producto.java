package com.example.ayenapp.modelo;

import java.io.Serializable;
import java.util.Objects;

public class Producto implements Serializable {

    private String codigo;
    private String nombre;
    private Double precio;
    private Double coste;
    private Integer stock;
    private Integer umbralCompra;
    private String foto;

    public Producto(String codigo, String nombre, Double precio, Double coste, Integer stock, Integer umbralCompra, String foto) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.coste = coste;
        this.stock = stock;
        this.umbralCompra = umbralCompra;
        this.foto = foto;
    }

    public Producto() {}

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Double getCoste() {
        return coste;
    }

    public void setCoste(Double coste) {
        this.coste = coste;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getUmbralCompra() {
        return umbralCompra;
    }

    public void setUmbralCompra(Integer umbralCompra) {
        this.umbralCompra = umbralCompra;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Producto producto = (Producto) o;
        return Objects.equals(codigo, producto.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre + " - " + precio + "€";
    }
}
