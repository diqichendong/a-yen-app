package com.example.ayenapp.vista;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.Manifest;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.ayenapp.R;
import com.example.ayenapp.modelo.Producto;
import com.example.ayenapp.servicio.CamaraService;
import com.example.ayenapp.servicio.EscanerService;
import com.example.ayenapp.servicio.GaleriaService;
import com.example.ayenapp.servicio.ProductoService;
import com.example.ayenapp.util.Util;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class GuardarProductoActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 101;
    private static final int REQUEST_SCANNER_CAMERA_PERMISSION = 102;
    private static final String IMAGE_STORAGE_BASE = "productos/";

    private ImageView imgProducto;
    private EditText txtCodigo, txtNombre, txtPrecio, txtCoste, txtStock, txtUmbral;
    private SwitchMaterial switchAddCompra;
    private ImageButton btnCodigoBarras;
    private ProgressBar barraCarga;
    private Toast toast;

    private Uri fotoUri;
    private Producto producto;
    private Producto productoAntiguo;

    private ProductoService productoService;
    private CamaraService camaraService;
    private GaleriaService galeriaService;
    private EscanerService escanerService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_guardar_producto);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();
    }

    /**
     * Inicializa los elementos de la pantalla
     */
    private void init() {
        productoAntiguo = (Producto) getIntent().getSerializableExtra(getString(R.string.keyProductoAntiguo));

        productoService = new ProductoService(this);
        camaraService = new CamaraService(this);
        galeriaService = new GaleriaService(this);
        escanerService = new EscanerService(this);

        txtCodigo = findViewById(R.id.txtCodigo);
        txtNombre = findViewById(R.id.txtNombre);
        txtPrecio = findViewById(R.id.txtPrecio);
        txtCoste = findViewById(R.id.txtCoste);
        txtUmbral = findViewById(R.id.txtUmbral);
        txtStock = findViewById(R.id.txtStock);
        imgProducto = findViewById(R.id.imgProducto);
        switchAddCompra = findViewById(R.id.switchAddCompra);
        btnCodigoBarras = findViewById(R.id.btnCodigoBarras);

        initToolbar();
        initHacerFoto();
        initSubirFoto();
        initCodigoBarras();
        initProductoAntiguo();
        initCrear();
        initBarraCarga();

        findViewById(R.id.btnCancelar).setOnClickListener(v -> finish());
    }

    private void initProductoAntiguo() {
        if (productoAntiguo != null) {
            txtCodigo.setText(productoAntiguo.getCodigo());
            txtNombre.setText(productoAntiguo.getNombre());
            txtPrecio.setText(Util.formatearDouble(productoAntiguo.getPrecio()));
            txtCoste.setText(Util.formatearDouble(productoAntiguo.getCoste()));
            txtStock.setText(productoAntiguo.getStock().toString());
            txtUmbral.setText(productoAntiguo.getUmbralCompra().toString());
            productoService.loadFoto(productoAntiguo, imgProducto);
            switchAddCompra.setVisibility(View.GONE);
            txtCodigo.setFocusable(false);
            txtCodigo.setFocusableInTouchMode(false);
            txtCodigo.setOnClickListener(v -> mostraMensajeCodigoNoEditable());
            btnCodigoBarras.setOnClickListener(v -> mostraMensajeCodigoNoEditable());
        }
    }

    /**
     * Inicializar el botón "Hacer foto"
     */
    private void initHacerFoto() {
        Button btnHacerFoto = findViewById(R.id.btnHacerFoto);
        btnHacerFoto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            } else {
                camaraService.abrirCamara();
            }
        });
    }

    /**
     * Inicializar el botón "Subir foto"
     */
    private void initSubirFoto() {
        Button btnSubirFoto = findViewById(R.id.btnSubirFoto);
        btnSubirFoto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE_PERMISSION);
            } else {
                galeriaService.abrirGaleria();
            }
        });
    }

    /**
     * Inicializar el botón del escaner de códigos de barras
     */
    private void initCodigoBarras() {
        btnCodigoBarras.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_SCANNER_CAMERA_PERMISSION);
            } else {
                escanerService.abrirEscaner();
            }
        });
    }

    /**
     * Inicializa el botón de crear
     */
    private void initCrear() {
        Button btnCrear = findViewById(R.id.btnCrear);
        btnCrear.setOnClickListener(v -> guardarProducto());
    }

    /**
     * Inicializar la barra de carga
     */
    private void initBarraCarga() {
        barraCarga = findViewById(R.id.barraCarga);
        barraCarga.setIndeterminate(true);
        barraCarga.setVisibility(View.GONE);
    }

    /**
     * Inicializa el toolbar
     */
    private void initToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    /**
     * Crea el producto
     */
    private void guardarProducto() {
        if (isDatosValidos()) {
            producto = new Producto(
                    txtCodigo.getText().toString().trim(),
                    txtNombre.getText().toString().trim(),
                    Double.parseDouble(txtPrecio.getText().toString()),
                    Double.parseDouble(txtCoste.getText().toString()),
                    Integer.parseInt(txtStock.getText().toString()),
                    Integer.parseInt(txtUmbral.getText().toString()),
                    IMAGE_STORAGE_BASE + txtCodigo.getText().toString().trim() + ".jpg"
            );

            productoService.guardarProducto(producto, fotoUri);
        }
    }

    /**
     * Comprueba los datos introducidos
     *
     * @return true si son válidos, false si no
     */
    private boolean isDatosValidos() {
        boolean precioValido = !txtPrecio.getText().toString().isEmpty()
                && Double.parseDouble(txtPrecio.getText().toString()) > 0;
        boolean costeValido = !txtCoste.getText().toString().isEmpty()
                && Double.parseDouble(txtCoste.getText().toString()) > 0;
        boolean stockValido = !txtStock.getText().toString().isEmpty()
                && Integer.parseInt(txtStock.getText().toString()) >= 0;
        boolean umbralValido = !txtUmbral.getText().toString().isEmpty()
                && Integer.parseInt(txtUmbral.getText().toString()) >= 0;

        if (!precioValido) {
            Toast.makeText(this, getString(R.string.precioInvalido), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!costeValido) {
            Toast.makeText(this, getString(R.string.costeInvalido), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!stockValido) {
            Toast.makeText(this, getString(R.string.stockInvalido), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!umbralValido) {
            Toast.makeText(this, getString(R.string.umbralInvalido), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (fotoUri == null && productoAntiguo == null) {
            Toast.makeText(this, getString(R.string.sinFoto), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (txtCodigo.getText().toString().trim().toString().isEmpty()
                || txtNombre.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.camposVacios), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * Establece la imagen del producto
     *
     * @param fotoUri URI de la imagen
     */
    public void setImgProducto(Uri fotoUri) {
        this.fotoUri = fotoUri;
        imgProducto.setImageURI(fotoUri);
    }

    /**
     * Establece el código en el campo
     *
     * @param codigo Código a establecer
     */
    public void setTxtCodigo(String codigo) {
        txtCodigo.setText(codigo);
    }

    /**
     * Establecer el estado de la barra de carga
     *
     * @param estado Estado de la barra View.VISIBLE o View.GONE
     */
    public void setBarraCarga(int estado) {
        barraCarga.setVisibility(estado);
        if (estado == View.VISIBLE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    private void mostraMensajeCodigoNoEditable() {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, getString(R.string.mensajeCodigoNoEditable), Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                camaraService.abrirCamara();
            }
        }

        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                galeriaService.abrirGaleria();
            }
        }

        if (requestCode == REQUEST_SCANNER_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                escanerService.abrirEscaner();
            }
        }
    }
}