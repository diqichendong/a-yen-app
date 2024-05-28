package com.example.ayenapp.util;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Util {

    public static String formatearDouble(Double d) {
        DecimalFormat formato = new DecimalFormat("0.00");
        return formato.format(d).replace(",", ".");
    }

    public static String crearCodigoVentaCompra(LocalDateTime fecha) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return fecha.format(formatter);
    }

    public static String formatearFechaHora(LocalDateTime fecha) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        return fecha.format(formatter);
    }

    public static String formatearFechaHora(String fechaHora) {
        DateTimeFormatter formatoAntes = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        DateTimeFormatter formatoDespues = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime fecha = LocalDateTime.parse(fechaHora, formatoAntes);
        return formatoDespues.format(fecha);
    }

    public static String formatearFecha(LocalDateTime fecha) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return fecha.format(formatter);
    }

}
