package com.example.ayenapp.vista;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ayenapp.R;
import com.example.ayenapp.modelo.Compra;
import com.example.ayenapp.modelo.Linea;
import com.example.ayenapp.modelo.Venta;
import com.example.ayenapp.util.Util;
import com.example.ayenapp.vista.adaptadores.RegistroAdapter;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class RegistroActivity extends AppCompatActivity {

    private TextView txtCodigo, txtFecha, txtTotal;
    private RecyclerView rv;
    private Button btnCerrar;
    private MaterialToolbar toolbar;

    private Venta venta;
    private Compra compra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();
    }

    /**
     * Inicializar componentes de la vista
     */
    private void init() {
        txtCodigo = findViewById(R.id.txtCodigoRegistroDetalles);
        txtFecha = findViewById(R.id.txtFechaRegistroDetalles);
        txtTotal = findViewById(R.id.txtTotalRegistroDetalles);
        rv = findViewById(R.id.rvRegistro);
        btnCerrar = findViewById(R.id.btnCerrarRegistro);
        toolbar = findViewById(R.id.toolbarRegistro);

        venta = (Venta) getIntent().getSerializableExtra("venta");
        compra = (Compra) getIntent().getSerializableExtra("compra");

        initToolbar();
        initVenta();
        initCompra();
        btnCerrar.setOnClickListener(v -> finish());
    }

    /**
     * Inicializar registro de compra
     */
    private void initCompra() {
        if (compra != null) {
            txtCodigo.setText(compra.getCodigo());
            txtFecha.setText(Util.formatearFechaHora(compra.getFecha()));
            txtTotal.setText(Util.formatearDouble(compra.getTotal()) + "€");
            initLista(compra.getLineasCompra());
        }
    }

    /**
     * Inicializar registros de venta
     */
    private void initVenta() {
        if (venta != null) {
            txtCodigo.setText(venta.getCodigo());
            txtFecha.setText(Util.formatearFechaHora(venta.getFecha()));
            txtTotal.setText(Util.formatearDouble(venta.getTotal()) + "€");
            initLista(venta.getLineasVenta());
        }
    }

    /**
     * Inicializar lista de lineas de registro
     * @param lista Lista de lineas de registro
     */
    private void initLista(List<Linea> lista) {
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new RegistroAdapter(this, lista));
    }

    /**
     * Inicializa el toolbar
     */
    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}