package com.example.ayenapp.vista;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ayenapp.R;
import com.example.ayenapp.modelo.Linea;
import com.example.ayenapp.modelo.Producto;
import com.example.ayenapp.modelo.Venta;
import com.example.ayenapp.servicio.EscanerService;
import com.example.ayenapp.servicio.ProductoService;
import com.example.ayenapp.servicio.VentaService;
import com.example.ayenapp.util.Util;
import com.example.ayenapp.vista.adaptadores.TpvAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TpvFragment extends Fragment {

    private MainActivity mainActivity;

    private EscanerService escanerService;
    private ProductoService productoService;

    private View view;
    private Button btnFinalizar, btnCancelar, btnBuscar, btnEscanear;
    private RecyclerView rv;
    private TextView txtTotal;

    private VentaService ventaService;

    private TpvAdapter tpvAdapter;
    private List<Linea> lineasVenta;
    private List<Producto> productos;
    private Toast toast;
    private ActivityResultLauncher<Intent> startForResult;

    // Launcher para pedir permisos
    ActivityResultLauncher<String[]> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            permissions -> {
                if (permissions.get(Manifest.permission.CAMERA)) {
                    escanerService.abrirEscaner();
                }
            }
    );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tpv, container, false);

        init();

        return view;
    }

    /**
     * Inicializar los componentes de la vista
     */
    private void init() {
        mainActivity = (MainActivity) getActivity();

        btnFinalizar = view.findViewById(R.id.btnFinalizarVenta);
        btnCancelar = view.findViewById(R.id.btnCancelarVenta);
        btnEscanear = view.findViewById(R.id.btnEscanearVenta);
        btnBuscar = view.findViewById(R.id.btnBuscarVenta);
        rv = view.findViewById(R.id.rvVenta);
        txtTotal = view.findViewById(R.id.txtTotalVenta);

        escanerService = new EscanerService(this);
        productoService = new ProductoService(this);
        ventaService = new VentaService(this);

        productoService.getProductos();

        initListaLineas();
        initEscaner();
        initCancelar();
        initBuscar();
        initFinalizar();
    }

    /**
     * Inicializar el botón de finalizar venta
     */
    private void initFinalizar() {
        btnFinalizar.setOnClickListener(v -> {
            if (lineasVenta.size() > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.dialogFinalizarVentaTitle)
                        .setMessage(R.string.dialogFinalizarVentaMensaje)
                        .setCancelable(false)
                        .setNegativeButton(R.string.no, null)
                        .setPositiveButton(R.string.si, (dialog, which) -> finalizarVenta())
                        .create()
                        .show();
            } else {
                if (toast != null){
                    toast.cancel();
                }
                toast = Toast.makeText(getContext(), getString(R.string.mensajeVentaVacia), Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    /**
     * Crear la venta y guardarla
     */
    private void finalizarVenta() {
        String fecha = Util.getFechaHoraActual();
        Venta venta = new Venta(
                Util.crearCodigoVentaCompra(fecha),
                fecha,
                lineasVenta,
                lineasVenta.stream().mapToDouble(Linea::getPrecio).sum()
        );
        ventaService.guardarVenta(venta);
        productoService.actualizarStock(lineasVenta);
    }

    /**
     * Inicializar el botón buscar
     */
    private void initBuscar() {
        startForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Producto producto = (Producto) data.getSerializableExtra("producto");
                            if (producto != null) {
                                addLineaVenta(producto);
                            }
                        }
                    }
                }
        );
        btnBuscar.setOnClickListener(v -> {
            mainActivity.setBarraCarga(View.VISIBLE);

            Intent i = new Intent(getContext(), BuscarActivity.class);
            i.putExtra("productos", (Serializable) productos);
            startForResult.launch(i);

            mainActivity.setBarraCarga(View.GONE);
        });
    }

    /**
     * Inicializar botón cancelar venta
     */
    private void initCancelar() {
        btnCancelar.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.dialogCancelarVentaTitulo)
                    .setMessage(R.string.dialogCancelarVentaMensaje)
                    .setCancelable(false)
                    .setPositiveButton(R.string.si, (dialog, which) -> initListaLineas())
                    .setNegativeButton(R.string.no, null)
                    .create()
                    .show();
        });
    }

    /**
     * Inicializar botón del escáner
     */
    private void initEscaner() {
        btnEscanear.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(new String[]{Manifest.permission.CAMERA});
            } else {
                escanerService.abrirEscaner();
            }
        });
    }

    /**
     * Inicializar lista de lineas de venta
     */
    public void initListaLineas() {
        lineasVenta = new ArrayList<>();
        tpvAdapter = new TpvAdapter(this, lineasVenta);
        rv.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rv.setAdapter(tpvAdapter);
        actualizarTotal();
    }

    /**
     * Manejar el código de barras escaneado
     *
     * @param codigoBarras Código de barras
     */
    public void manejarCodigoBarras(String codigoBarras) {
        Producto coincidencia = productos
                .stream()
                .filter(producto -> producto.getCodigo().equals(codigoBarras))
                .findFirst()
                .orElse(null);

        addLineaVenta(coincidencia);
    }

    /**
     * Añade una nueva linea de venta o aumenta en 1 su cantidad si ya existe
     *
     * @param producto Producto de la linea de venta
     */
    private void addLineaVenta(Producto producto) {
        if (producto != null) {
            Linea nuevaLinea = new Linea(producto, 1, producto.getPrecio());
            if (lineasVenta.contains(nuevaLinea)) {
                Linea lineaAntigua = lineasVenta
                        .stream()
                        .filter(linea -> linea.getProducto().equals(producto))
                        .findFirst()
                        .orElse(null);

                Integer nuevaCantidad = lineaAntigua.getCantidad() + 1;
                lineaAntigua.setCantidad(nuevaCantidad);
                lineaAntigua.setPrecio(nuevaCantidad * lineaAntigua.getProducto().getPrecio());
            } else {
                lineasVenta.add(nuevaLinea);
            }

            tpvAdapter.setDatalist(lineasVenta);
        } else {
            if (toast != null) {
                toast.cancel();
            }
            toast = Toast.makeText(getContext(), getString(R.string.mensajeProductoNoExiste), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
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
     * Actualiza el total de la venta
     */
    public void actualizarTotal() {
        Double total = lineasVenta.stream()
                .mapToDouble(Linea::getPrecio)
                .sum();

        txtTotal.setText(Util.formatearDouble(total) + "€");
    }
}