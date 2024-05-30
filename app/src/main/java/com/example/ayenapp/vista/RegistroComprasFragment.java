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
import com.example.ayenapp.modelo.Compra;
import com.example.ayenapp.modelo.Venta;
import com.example.ayenapp.servicio.CompraService;
import com.example.ayenapp.servicio.VentaService;
import com.example.ayenapp.util.Util;
import com.example.ayenapp.vista.adaptadores.RegistroComprasAdapter;
import com.example.ayenapp.vista.adaptadores.RegistroVentasAdapter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RegistroComprasFragment extends Fragment {

    private MainActivity mainActivity;

    private View view;
    private RecyclerView rv;
    private EditText fechaInicio, fechaFin;
    private TextView txtNoHayCompras;

    private CompraService compraService;

    private List<Compra> compras;
    private RegistroComprasAdapter registroComprasAdapter;
    private LocalDateTime fechaInicioSeleccionada;
    private LocalDateTime fechaFinSeleccionada;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_registro_compras, container, false);

        init();

        return view;
    }

    /**
     * Inicializar componentes de la vista
     */
    private void init() {
        mainActivity = (MainActivity) getActivity();

        rv = view.findViewById(R.id.rvRegistroCompras);
        fechaInicio = view.findViewById(R.id.fechaInicioCompras);
        fechaFin = view.findViewById(R.id.fechaFinCompras);
        txtNoHayCompras = view.findViewById(R.id.txtNoHayCompras);

        compraService = new CompraService(this);

        fechaInicioSeleccionada = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        fechaFinSeleccionada = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        compraService.getCompras(fechaInicioSeleccionada, fechaFinSeleccionada);

        initFechaInicio();
        initFechaFin();
        initListaVentas();
    }

    /**
     * Inicializar lista de compras
     */
    private void initListaVentas() {
        compras = new ArrayList<>();
        registroComprasAdapter = new RegistroComprasAdapter(this, compras);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(registroComprasAdapter);
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
                compraService.getCompras(fechaInicioSeleccionada, fechaFinSeleccionada);
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
                compraService.getCompras(fechaInicioSeleccionada, fechaFinSeleccionada);
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
     * Establece la nuevo registro de compras
     *
     * @param compras Lista de registro de compras
     */
    public void setCompras(List<Compra> compras) {
        this.compras = compras;
        registroComprasAdapter.setDataset(compras);

        if (compras.isEmpty()) {
            txtNoHayCompras.setVisibility(View.VISIBLE);
        } else {
            txtNoHayCompras.setVisibility(View.GONE);
        }
    }
}