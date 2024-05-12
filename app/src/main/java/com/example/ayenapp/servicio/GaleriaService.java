package com.example.ayenapp.servicio;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.ayenapp.vista.CrearProductoActivity;

public class GaleriaService {

    private CrearProductoActivity crearProductoActivity;
    private ActivityResultLauncher<Intent> galleryActivityLauncher;

    private Uri fotoUri;

    public GaleriaService(CrearProductoActivity activity) {
        this.crearProductoActivity = activity;
        galleryActivityLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        fotoUri = result.getData().getData();
                        Log.d("aaaaaaaaaaa", fotoUri.toString());
                        activity.setImgProducto(fotoUri);
                    }
                });
    }

    /**
     * Abrir el intent de la galería
     */
    public void abrirGaleria() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryActivityLauncher.launch(galleryIntent);
    }

}
