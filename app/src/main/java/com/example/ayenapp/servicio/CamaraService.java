package com.example.ayenapp.servicio;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;

import com.example.ayenapp.vista.CrearProductoActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CamaraService {

    private CrearProductoActivity crearProductoActivity;
    private ActivityResultLauncher<Intent> cameraActivityLauncher;

    private Uri fotoUri;

    public CamaraService(CrearProductoActivity activity) {
        this.crearProductoActivity = activity;
        cameraActivityLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        crearProductoActivity.setImgProducto(fotoUri);
                    }
                }
        );
    }

    /**
     * Abrir el intent de la camara
     */
    public void abrirCamara() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 500 * 500);
        if (cameraIntent.resolveActivity(crearProductoActivity.getPackageManager()) != null) {
            File fotoFile = null;
            try {
                fotoFile = createImageFile();
            } catch (IOException ex) {
                Log.e("Camara", ex.getMessage());
            }
            if (fotoFile != null) {
                fotoUri = FileProvider.getUriForFile(crearProductoActivity, "com.example.ayenapp.fileprovider", fotoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
                cameraActivityLauncher.launch(cameraIntent);
            }
        }
    }

    /**
     * Crea un fichero temporal para la foto realizada
     *
     * @return Fichero de la foto
     */
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = crearProductoActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

}
