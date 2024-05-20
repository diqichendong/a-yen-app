package com.example.ayenapp.servicio;

import android.view.View;
import android.widget.Toast;

import com.example.ayenapp.R;
import com.example.ayenapp.modelo.Venta;
import com.example.ayenapp.vista.TpvFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

public class VentaService {

    private static String NODO_BASE = "ventas";

    private TpvFragment tpvFragment;

    public VentaService(TpvFragment tpvFragment) {
        this.tpvFragment = tpvFragment;
    }

    /**
     * Guarda la venta en los servicios de Firebase
     *
     * @param venta Venta a guardar
     */
    public void guardarVenta(Venta venta) {
        tpvFragment.setBarraCarga(View.VISIBLE);

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference(NODO_BASE);

        databaseRef.child(venta.getCodigo()).setValue(venta).addOnSuccessListener(unused -> {
            Toast.makeText(tpvFragment.getContext(), tpvFragment.getString(R.string.exitoGuardarVenta), Toast.LENGTH_SHORT).show();
            tpvFragment.initListaLineas();
            tpvFragment.setBarraCarga(View.GONE);
        }).addOnFailureListener(e -> {
            Toast.makeText(tpvFragment.getContext(), tpvFragment.getString(R.string.falloGuardarVenta), Toast.LENGTH_SHORT).show();
            tpvFragment.setBarraCarga(View.GONE);
        });
    }
}
