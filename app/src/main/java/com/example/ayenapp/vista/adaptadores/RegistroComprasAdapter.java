package com.example.ayenapp.vista.adaptadores;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ayenapp.R;
import com.example.ayenapp.modelo.Compra;
import com.example.ayenapp.modelo.Venta;
import com.example.ayenapp.util.Util;
import com.example.ayenapp.vista.RegistroActivity;
import com.example.ayenapp.vista.RegistroComprasFragment;

import java.util.List;

public class RegistroComprasAdapter extends RecyclerView.Adapter<RegistroComprasAdapter.RegistroComprasViewHolder> {

    private List<Compra> dataset;
    private RegistroComprasFragment registroComprasFragment;

    public RegistroComprasAdapter(RegistroComprasFragment registroComprasFragment, List<Compra> dataset) {
        this.dataset = dataset;
        this.registroComprasFragment = registroComprasFragment;
    }

    @NonNull
    @Override
    public RegistroComprasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_registro, parent, false);
        return new RegistroComprasViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RegistroComprasViewHolder holder, int position) {
        Compra compra = dataset.get(position);

        holder.txtFecha.setText(Util.formatearFechaHora(compra.getFecha()));
        holder.txtCodigo.setText(compra.getCodigo());
        holder.txtTotal.setText(Util.formatearDouble(compra.getTotal()) + "€");

        holder.btnDetalles.setOnClickListener(v -> {
            Intent i = new Intent(registroComprasFragment.getContext(), RegistroActivity.class);
            i.putExtra("compra", compra);
            registroComprasFragment.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public static class RegistroComprasViewHolder extends RecyclerView.ViewHolder {

        public TextView txtCodigo, txtFecha, txtTotal;
        public ImageButton btnDetalles;

        public RegistroComprasViewHolder(@NonNull View itemView) {
            super(itemView);

            txtCodigo = itemView.findViewById(R.id.txtCodigoRegistro);
            txtFecha = itemView.findViewById(R.id.txtFechaRegistro);
            txtTotal = itemView.findViewById(R.id.txtTotalRegistro);
            btnDetalles = itemView.findViewById(R.id.btnDetallesRegistro);
        }
    }

    public void setDataset(List<Compra> dataset) {
        this.dataset = dataset;
        notifyDataSetChanged();
    }
}
