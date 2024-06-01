package com.example.ayenapp.servicio;

import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.ayenapp.R;
import com.example.ayenapp.modelo.Compra;
import com.example.ayenapp.modelo.Venta;
import com.example.ayenapp.util.Util;
import com.example.ayenapp.vista.CompraFragment;
import com.example.ayenapp.vista.GuardarProductoActivity;
import com.example.ayenapp.vista.RegistroComprasFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CompraService {

    private static final String NODO_BASE = "compras";

    private CompraFragment compraFragment;
    private RegistroComprasFragment registroComprasFragment;
    private GuardarProductoActivity guardarProductoActivity;

    public CompraService(CompraFragment compraFragment) {
        this.compraFragment = compraFragment;
    }

    public CompraService(RegistroComprasFragment registroComprasFragment) {
        this.registroComprasFragment = registroComprasFragment;
    }

    public CompraService(GuardarProductoActivity guardarProductoActivity) {
        this.guardarProductoActivity = guardarProductoActivity;
    }

    /**
     * Guarda la compra en los servicios de Firebase
     *
     * @param compra Venta a guardar
     */
    public void guardarCompra(Compra compra) {
        if (compraFragment != null) {
            compraFragment.setBarraCarga(View.VISIBLE);
        }

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference(NODO_BASE);

        databaseRef.child(compra.getCodigo()).setValue(compra).addOnSuccessListener(unused -> {
            if (compraFragment != null) {
                Toast.makeText(compraFragment.getContext(), compraFragment.getString(R.string.exitoGuardarCompra), Toast.LENGTH_SHORT).show();
                compraFragment.initListaLineas();
                compraFragment.setBarraCarga(View.GONE);
            }
        }).addOnFailureListener(e -> {
            if (compraFragment != null) {
                Toast.makeText(compraFragment.getContext(), compraFragment.getString(R.string.falloGuardarCompra), Toast.LENGTH_SHORT).show();
                compraFragment.setBarraCarga(View.GONE);
            }
            if (guardarProductoActivity != null) {
                Toast.makeText(guardarProductoActivity, guardarProductoActivity.getString(R.string.falloGuardarCompra), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Obtener las compras entre un rango de fecha
     *
     * @param min Fecha mínima
     * @param max Fecha máxima
     */
    public void getCompras(LocalDateTime min, LocalDateTime max) {
        registroComprasFragment.setBarraCarga(View.VISIBLE);

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference(NODO_BASE);

        Query query = databaseRef.orderByChild("fecha")
                .startAt(Util.formatearFechaHora(min))
                .endAt(Util.formatearFechaHora(max));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Compra> compras = new ArrayList<>();

                for (DataSnapshot ventaSnapshot : snapshot.getChildren()) {
                    Compra compra = ventaSnapshot.getValue(Compra.class);
                    compras.add(compra);
                }

                registroComprasFragment.setCompras(compras);
                registroComprasFragment.setBarraCarga(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(registroComprasFragment.getContext(),
                        registroComprasFragment.getString(R.string.falloObtenerCompras),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
