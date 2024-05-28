package com.example.ayenapp.vista;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ayenapp.R;
import com.example.ayenapp.modelo.Venta;
import com.example.ayenapp.servicio.VentaService;
import com.example.ayenapp.util.Util;
import com.example.ayenapp.vista.adaptadores.RegistroVentasAdapter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RegistroVentasFragment extends Fragment {

    private MainActivity mainActivity;

    private View view;
    private RecyclerView rv;
    private EditText fechaInicio, fechaFin;
    private TextView txtNoHayVentas;

    private VentaService ventaService;

    private List<Venta> ventas;
    private RegistroVentasAdapter registroVentasAdapter;
    private LocalDateTime fechaInicioSeleccionada;
    private LocalDateTime fechaFinSeleccionada;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_registro_ventas, container, false);

        init();

        return view;
    }

    /**
     * Inicializar componentes de la vista
     */
    private void init() {
        mainActivity = (MainActivity) getActivity();

        rv = view.findViewById(R.id.rvRegistroVentas);
        fechaInicio = view.findViewById(R.id.fechaInicioVentas);
        fechaFin = view.findViewById(R.id.fechaFinVentas);
        txtNoHayVentas = view.findViewById(R.id.txtNoHayVentas);

        ventaService = new VentaService(this);

        fechaInicioSeleccionada = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        fechaFinSeleccionada = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        ventaService.getVentas(fechaInicioSeleccionada, fechaFinSeleccionada);

        initFechaInicio();
        initFechaFin();
        initListaVentas();
    }

    /**
     * Inicializar lista de ventas
     */
    private void initListaVentas() {
        ventas = new ArrayList<>();
        registroVentasAdapter = new RegistroVentasAdapter(ventas);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(registroVentasAdapter);
    }

    /**
     * Inicializar la fecha de inicio
     */
    private void initFechaInicio() {
        fechaInicio.setText(Util.formatearFecha(fechaInicioSeleccionada));

        fechaInicio.setOnClickListener(v -> {
            int anyo = fechaInicioSeleccionada.getYear();
            int mes = fechaInicioSeleccionada.getMonthValue() - 1;
            int dia = fechaInicioSeleccionada.getDayOfMonth();

            new DatePickerDialog(getContext(), (datepicker, year, month, day) -> {
                fechaInicioSeleccionada = LocalDateTime.of(year, month + 1, day, 0, 0, 0);
                if (fechaInicioSeleccionada.compareTo(fechaFinSeleccionada) > 0) {
                    fechaFinSeleccionada = fechaInicioSeleccionada;
                    fechaFin.setText(Util.formatearFecha(fechaFinSeleccionada));
                }
                fechaInicio.setText(Util.formatearFecha(fechaInicioSeleccionada));
                ventaService.getVentas(fechaInicioSeleccionada, fechaFinSeleccionada);
            }, anyo, mes, dia).show();
        });
    }

    /**
     * Inicializar la fecha de fin
     */
    private void initFechaFin() {
        fechaFin.setText(Util.formatearFecha(fechaFinSeleccionada));

        fechaFin.setOnClickListener(v -> {
            int anyo = fechaFinSeleccionada.getYear();
            int mes = fechaFinSeleccionada.getMonthValue() - 1;
            int dia = fechaFinSeleccionada.getDayOfMonth();

            new DatePickerDialog(getContext(), (datepicker, year, month, day) -> {
                fechaFinSeleccionada = LocalDateTime.of(year, month + 1, day, 23, 59, 59);
                if (fechaFinSeleccionada.compareTo(fechaInicioSeleccionada) < 0) {
                    fechaInicioSeleccionada = fechaFinSeleccionada;
                    fechaInicio.setText(Util.formatearFecha(fechaInicioSeleccionada));
                }
                fechaFin.setText(Util.formatearFecha(fechaFinSeleccionada));
                ventaService.getVentas(fechaInicioSeleccionada, fechaFinSeleccionada);
            }, anyo, mes, dia).show();
        });
    }

    /**
     * Establecer el estado de la barra de carga
     *
     * @param estado Estado de la barra View.VISIBLE o View.GONE
     */
    public void setBarraCarga(int estado) {
        mainActivity.setBarraCarga(estado);
    }

    /**
     * Establece la nuevo registro de ventas
     *
     * @param ventas Lista de registro de ventas
     */
    public void setVentas(List<Venta> ventas) {
        this.ventas = ventas;
        registroVentasAdapter.setDataset(ventas);

        if (ventas.isEmpty()) {
            txtNoHayVentas.setVisibility(View.VISIBLE);
        } else {
            txtNoHayVentas.setVisibility(View.GONE);
        }
    }
}