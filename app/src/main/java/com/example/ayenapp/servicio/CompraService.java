package com.example.ayenapp.servicio;

import android.view.View;
import android.widget.Toast;

import com.example.ayenapp.R;
import com.example.ayenapp.modelo.Compra;
import com.example.ayenapp.vista.CompraFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CompraService {

    private static final String NODO_BASE = "compras";

    private CompraFragment compraFragment;

    public CompraService(CompraFragment compraFragment) {
        this.compraFragment = compraFragment;
    }

    /**
     * Guarda la compra en los servicios de Firebase
     *
     * @param compra Venta a guardar
     */
    public void guardarCompra(Compra compra) {
        compraFragment.setBarraCarga(View.VISIBLE);

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference(NODO_BASE);

        databaseRef.child(compra.getCodigo()).setValue(compra).addOnSuccessListener(unused -> {
            Toast.makeText(compraFragment.getContext(), compraFragment.getString(R.string.exitoGuardarCompra), Toast.LENGTH_SHORT).show();
            compraFragment.initListaLineas();
            compraFragment.setBarraCarga(View.GONE);
        }).addOnFailureListener(e -> {
            Toast.makeText(compraFragment.getContext(), compraFragment.getString(R.string.falloGuardarCompra), Toast.LENGTH_SHORT).show();
            compraFragment.setBarraCarga(View.GONE);
        });
    }
}
