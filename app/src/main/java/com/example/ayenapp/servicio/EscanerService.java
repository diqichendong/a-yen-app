package com.example.ayenapp.servicio;

import androidx.activity.result.ActivityResultLauncher;

import com.example.ayenapp.R;
import com.example.ayenapp.vista.CrearProductoActivity;
import com.example.ayenapp.vista.ProductosFragment;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class EscanerService {

    private CrearProductoActivity crearProductoActivity;
    private ProductosFragment productosFragment;
    private ActivityResultLauncher<ScanOptions> scannerActivityLauncher;

    public EscanerService(CrearProductoActivity activity) {
        this.crearProductoActivity = activity;
        scannerActivityLauncher = activity.registerForActivityResult(
                new ScanContract(),
                result -> {
                    if (result.getContents() != null) {
                        activity.setTxtCodigo(result.getContents());
                    }
                }
        );
    }

    public EscanerService(ProductosFragment fragment) {
        this.productosFragment = fragment;
        scannerActivityLauncher = fragment.registerForActivityResult(
                new ScanContract(),
                result -> {
                    if (result.getContents() != null) {
                        fragment.setTxtBuscar(result.getContents());
                    }
                }
        );
    }

    /**
     * Abrir el activity del escáner
     */
    public void abrirEscaner() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.PRODUCT_CODE_TYPES);
        int btnEscanerCodigo = R.string.btnEscanerCodigo;
        if (crearProductoActivity != null) {
            options.setPrompt(crearProductoActivity.getString(btnEscanerCodigo));
        } else if (productosFragment != null) {
            options.setPrompt(productosFragment.getString(btnEscanerCodigo));
        }
        options.setCameraId(0);
        options.setBeepEnabled(false);
        options.setBarcodeImageEnabled(true);
        options.setOrientationLocked(false);
        scannerActivityLauncher.launch(options);
    }
}
