package com.example.ayenapp.vista;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ayenapp.R;
import com.example.ayenapp.modelo.Producto;
import com.example.ayenapp.servicio.EscanerService;
import com.example.ayenapp.vista.adaptadores.BuscarAdapter;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class BuscarActivity extends AppCompatActivity {

    private EditText txtBuscar;
    private ImageButton btnEscaner;
    private RecyclerView rv;

    private EscanerService escanerService;

    private List<Producto> productos;
    private BuscarAdapter buscarAdapter;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_buscar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();
    }

    /**
     * Inicializar vista de la activity
     */
    private void init() {
        productos = (List<Producto>) getIntent().getSerializableExtra("productos");

        txtBuscar = findViewById(R.id.txtBuscarVenta);
        btnEscaner = findViewById(R.id.btnCodigoBarrasBuscarVenta);
        rv = findViewById(R.id.rvBuscarVenta);

        escanerService = new EscanerService(this);

        initToolbar();
        initListaProductos();
        initBuscador();
        initEscaner();
    }

    /**
     * Inicializa el toolbar
     */
    private void initToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbarBuscar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    /**
     * Inicializar el botón del escáner de códigos de barras
     */
    private void initEscaner() {
        btnEscaner.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(new String[]{Manifest.permission.CAMERA});
            } else {
                escanerService.abrirEscaner();
            }
        });
    }

    /**
     * Inicializar la lista de productos
     */
    private void initListaProductos() {
        rv.setLayoutManager(new LinearLayoutManager(this));
        buscarAdapter = new BuscarAdapter(this, productos);
        rv.setAdapter(buscarAdapter);
    }

    /**
     * Inicializar el buscador
     */
    private void initBuscador() {
        txtBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filtrar(s.toString());
            }
        });
    }

    /**
     * Filtra la lista de producto
     *
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

        buscarAdapter.setDatalist(listaFiltrada);
    }

    /**
     * Comprar si cumple el filtro por nombre y código
     *
     * @param producto Producto a comprobar
     * @param filtro   Texto del filtro
     * @return true si cumple el filtro, false si no
     */
    private boolean cumpleFiltro(Producto producto, String filtro) {
        return producto.getCodigo().toLowerCase().contains(filtro.toLowerCase())
                || producto.getNombre().toLowerCase().contains(filtro.toLowerCase());
    }

    /**
     * Añade el producto a la una línea de venta
     *
     * @param producto Producto a añadir
     */
    public void addProducto(Producto producto) {
        Intent i = new Intent();
        i.putExtra("producto", producto);
        setResult(Activity.RESULT_OK, i);
        finish();
    }

    public void setCodigoBarras(String codigo) {
        txtBuscar.setText(codigo);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}