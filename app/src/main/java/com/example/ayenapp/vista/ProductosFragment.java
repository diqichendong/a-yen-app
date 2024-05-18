package com.example.ayenapp.vista;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.ayenapp.R;
import com.example.ayenapp.modelo.Producto;
import com.example.ayenapp.servicio.EscanerService;
import com.example.ayenapp.servicio.ProductoService;
import com.example.ayenapp.vista.adaptadores.ProductosAdapter;

import java.util.ArrayList;
import java.util.List;

public class ProductosFragment extends Fragment {

    private MainActivity mainActivity;

    private View view;
    private RecyclerView rv;
    private EditText txtBuscar;

    private EscanerService escanerService;
    private ProductoService productoService;

    private List<Producto> productos;
    private ProductosAdapter productosAdapter;

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
        view = inflater.inflate(R.layout.fragment_productos, container, false);

        init();

        return view;
    }

    /**
     * Inicializa los componentes de la vista
     */
    private void init() {
        mainActivity = (MainActivity) getActivity();

        escanerService = new EscanerService(this);
        productoService = new ProductoService(this);

        rv = view.findViewById(R.id.rvProductos);
        txtBuscar = view.findViewById(R.id.txtBuscar);

        initCrearProducto();
        initListaProductos();
        initBuscador();
        initCodigoBarras();
    }

    /**
     * Inicializar buscador
     */
    private void initBuscador() {
        txtBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filtrar(s.toString());
            }
        });
    }

    /**
     * Inicializar lista de productos
     */
    private void initListaProductos() {
        rv.setLayoutManager(new LinearLayoutManager(view.getContext()));
        productos = new ArrayList<>();
        productosAdapter = new ProductosAdapter(this, productos);
        rv.setAdapter(productosAdapter);
        productoService.getProductos();
    }

    /**
     * Inicializa el botón de crear producto
     */
    private void initCrearProducto() {
        Button btnCrearProducto = view.findViewById(R.id.btnCrearProducto);
        btnCrearProducto.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), GuardarProductoActivity.class);
            startActivity(i);
        });
    }

    /**
     * Inicializar el botón del escaner de códigos de barras
     */
    private void initCodigoBarras() {
        ImageButton btnCodigoBarras = view.findViewById(R.id.btnCodigoBarrasProductos);
        btnCodigoBarras.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(new String[]{Manifest.permission.CAMERA});
            } else {
                escanerService.abrirEscaner();
            }
        });
    }

    /**
     * Establece el texto en el campo de búsqueda
     * @param cadena Texto de búsqueda
     */
    public void setTxtBuscar(String cadena) {
        txtBuscar.setText(cadena);
    }

    /**
     * Establecer el estado de la barra de carga
     * @param estado Estado de la barra View.VISIBLE o View.GONE
     */
    public void setBarraCarga(int estado) {
        mainActivity.setBarraCarga(estado);
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
        productosAdapter.setDatalist(productos);
    }

    /**
     * Filtra la lista de producto
     * @param filtro Texto del filtro
     */
    private void filtrar(String filtro) {
        List<Producto> listaFiltrada = new ArrayList<>();

        if (filtro.trim().isEmpty()) {
            listaFiltrada = productos;
        } else {
            for (Producto producto : productos) {
                if (cumpleFiltro(producto, filtro)) {
                    listaFiltrada.add(producto);
                }
            }
        }

        productosAdapter.setDatalist(listaFiltrada);
    }

    /**
     * Comprar si cumple el filtro por nombre y código
     * @param producto Producto a comprobar
     * @param filtro Texto del filtro
     * @return true si cumple el filtro, false si no
     */
    private boolean cumpleFiltro(Producto producto, String filtro) {
        return producto.getCodigo().toLowerCase().contains(filtro.toLowerCase())
                || producto.getNombre().toLowerCase().contains(filtro.toLowerCase());
    }
}