package com.example.ayenapp.servicio;

import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.ayenapp.R;
import com.example.ayenapp.modelo.Venta;
import com.example.ayenapp.util.Util;
import com.example.ayenapp.vista.RegistroVentasFragment;
import com.example.ayenapp.vista.TpvFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VentaService {

    private static String NODO_BASE = "ventas";

    private TpvFragment tpvFragment;
    private RegistroVentasFragment registroVentasFragment;

    public VentaService(TpvFragment tpvFragment) {
        this.tpvFragment = tpvFragment;
    }

    public VentaService(RegistroVentasFragment registroVentasFragment) {
        this.registroVentasFragment = registroVentasFragment;
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

    /**
     * Obtener las ventas entre un rango de fecha
     *
     * @param min Fecha mínima
     * @param max Fecha máxima
     */
    public void getVentas(LocalDateTime min, LocalDateTime max) {
        registroVentasFragment.setBarraCarga(View.VISIBLE);

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference(NODO_BASE);

        Query query = databaseRef.orderByChild("fecha")
                .startAt(Util.formatearFechaHora(min))
                .endAt(Util.formatearFechaHora(max));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Venta> ventas = new ArrayList<>();

                for (DataSnapshot ventaSnapshot : snapshot.getChildren()) {
                    Venta venta = ventaSnapshot.getValue(Venta.class);
                    ventas.add(venta);
                }

                registroVentasFragment.setVentas(ventas);
                registroVentasFragment.setBarraCarga(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(registroVentasFragment.getContext(),
                        registroVentasFragment.getString(R.string.falloObtenerVentas),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
