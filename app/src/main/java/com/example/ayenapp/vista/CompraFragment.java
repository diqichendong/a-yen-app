package com.example.ayenapp.vista;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ayenapp.R;
import com.example.ayenapp.modelo.Compra;
import com.example.ayenapp.modelo.Linea;
import com.example.ayenapp.modelo.Producto;
import com.example.ayenapp.modelo.Venta;
import com.example.ayenapp.servicio.CompraService;
import com.example.ayenapp.servicio.EscanerService;
import com.example.ayenapp.servicio.ProductoService;
import com.example.ayenapp.servicio.VentaService;
import com.example.ayenapp.util.Util;
import com.example.ayenapp.vista.adaptadores.CompraAdapter;
import com.example.ayenapp.vista.adaptadores.TpvAdapter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CompraFragment extends Fragment {

    private MainActivity mainActivity;

    private ProductoService productoService;

    private View view;
    private Button btnFinalizar, btnReiniciar, btnAdd;
    private RecyclerView rv;
    private TextView txtTotal, txtListaVacia;

    private CompraService compraService;

    private CompraAdapter compraAdapter;
    private List<Linea> lineasCompra;
    private List<Producto> productos;
    private Toast toast;
    private ActivityResultLauncher<Intent> startForResult;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_compra, container, false);

        init();

        return view;
    }

    /**
     * Inicializa los componentes de la vista
     */
    private void init() {
        this.mainActivity = (MainActivity) getActivity();

        btnFinalizar = view.findViewById(R.id.btnFinalizarCompra);
        btnReiniciar = view.findViewById(R.id.btnReiniciarCompra);
        btnAdd = view.findViewById(R.id.btnAddCompra);
        rv = view.findViewById(R.id.rvCompra);
        txtTotal = view.findViewById(R.id.txtTotalCompra);
        txtListaVacia = view.findViewById(R.id.txtListaCompraVacia);

        productoService = new ProductoService(this);
        compraService = new CompraService(this);

        productoService.getProductos();

        initReiniciar();
        initAdd();
        initFinalizar();
    }

    /**
     * Inicializar el botón de finalizar compra
     */
    private void initFinalizar() {
        btnFinalizar.setOnClickListener(v -> {
            double total = lineasCompra.stream().mapToDouble(Linea::getPrecio).sum();
            if (total > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.dialogFinalizarCompraTitle)
                        .setMessage(R.string.dialogFinalizarCompraMensaje)
                        .setCancelable(false)
                        .setNegativeButton(R.string.no, null)
                        .setPositiveButton(R.string.si, (dialog, which) -> finalizarCompra())
                        .create()
                        .show();
            } else {
                if (toast != null) {
                    toast.cancel();
                }
                toast = Toast.makeText(getContext(), getString(R.string.mensajeCompraVacia), Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    /**
     * Crear la compra y guardarla
     */
    private void finalizarCompra() {
        LocalDateTime fechaActual = LocalDateTime.now();
        Compra compra = new Compra(
                "C-" + Util.crearCodigoVentaCompra(fechaActual),
                Util.formatearFechaHora(fechaActual),
                lineasCompra,
                lineasCompra.stream().mapToDouble(Linea::getPrecio).sum()
        );
        compraService.guardarCompra(compra);
        productoService.actualizarStock(lineasCompra);
        productoService.actualizarCoste(lineasCompra);
    }

    /**
     * Inicializar el botón de añadir
     */
    private void initAdd() {
        startForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Producto producto = (Producto) data.getSerializableExtra("producto");
                            if (producto != null) {
                                addLineaCompra(producto);
                            }
                        }
                    }
                }
        );
        btnAdd.setOnClickListener(v -> {
            mainActivity.setBarraCarga(View.VISIBLE);

            Intent i = new Intent(getContext(), BuscarActivity.class);
            i.putExtra("productos", (Serializable) productos);
            startForResult.launch(i);

            mainActivity.setBarraCarga(View.GONE);
        });
    }

    /**
     * Inicializar botón reiniciar compra
     */
    private void initReiniciar() {
        btnReiniciar.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.dialogReiniciarCompraTitulo)
                    .setMessage(R.string.dialogReiniciarCompraMensaje)
                    .setCancelable(false)
                    .setPositiveButton(R.string.si, (dialog, which) -> initListaLineas())
                    .setNegativeButton(R.string.no, null)
                    .create()
                    .show();
        });
    }

    /**
     * Inicializar lista de lineas de compra
     */
    public void initListaLineas() {
        lineasCompra = productos.stream()
                .filter(p -> p.getStock() <= p.getUmbralCompra())
                .map(p -> new Linea(p, 0, 0.0))
                .collect(Collectors.toList());

        compraAdapter = new CompraAdapter(this, lineasCompra);
        rv.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rv.setAdapter(compraAdapter);
        actualizarTotal();
    }

    /**
     * Añade una nueva linea de compra si no existe
     *
     * @param producto Producto de la linea de compra
     */
    private void addLineaCompra(Producto producto) {
        if (producto != null) {
            Linea nuevaLinea = new Linea(producto, 0, 0.0);
            if (!lineasCompra.contains(nuevaLinea)) {
                lineasCompra.add(nuevaLinea);
            }

            compraAdapter.notifyDataSetChanged();
            comprobarListaVacia(lineasCompra);
        }
    }

    public void setProductos(List<Producto> productos) {
        if (this.productos == null) {
            this.productos = productos;
            initListaLineas();
        } else {
            this.productos = productos;
        }
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
     * Actualiza el total de la compra
     */
    public void actualizarTotal() {
        Double total = lineasCompra.stream()
                .mapToDouble(Linea::getPrecio)
                .sum();

        txtTotal.setText(Util.formatearDouble(total) + "€");
        comprobarListaVacia(lineasCompra);
    }

    /**
     * Comprobar el tamaño de la lista para mostrar mensaje
     */
    private void comprobarListaVacia(List<Linea> lista) {
        if (lista.isEmpty()) {
            txtListaVacia.setVisibility(View.VISIBLE);
        } else {
            txtListaVacia.setVisibility(View.GONE);
        }
    }
}