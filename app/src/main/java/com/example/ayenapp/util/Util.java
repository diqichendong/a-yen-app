package com.example.ayenapp.util;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Util {

    public static String formatearDouble(Double d) {
        DecimalFormat formato = new DecimalFormat("0.00");
        return formato.format(d).replace(",", ".");
    }

    public static String crearCodigoVentaCompra(String fechaHora) {
        return fechaHora.replaceAll("[/ :]", "");
    }

    public static String getFechaHoraActual() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return now.format(formatter);
    }
}
