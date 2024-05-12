package com.example.ayenapp.util;

import java.text.DecimalFormat;

public class Util {

    public static String formatearDouble(Double d) {
        DecimalFormat formato = new DecimalFormat("0.00");
        return formato.format(d);
    }

}
