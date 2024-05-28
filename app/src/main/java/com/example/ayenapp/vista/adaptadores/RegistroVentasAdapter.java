package com.example.ayenapp.vista.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ayenapp.R;
import com.example.ayenapp.modelo.Venta;
import com.example.ayenapp.util.Util;

import java.util.List;

public class RegistroVentasAdapter extends RecyclerView.Adapter<RegistroVentasAdapter.RegistroVentasViewHolder> {

    private List<Venta> dataset;

    public RegistroVentasAdapter(List<Venta> dataset) {
        this.dataset = dataset;
    }

    @NonNull
    @Override
    public RegistroVentasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_registro, parent, false);
        return new RegistroVentasViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RegistroVentasViewHolder holder, int position) {
        Venta venta = dataset.get(position);

        holder.txtFecha.setText(Util.formatearFechaHora(venta.getFecha()));
        holder.txtCodigo.setText(venta.getCodigo());
        holder.txtTotal.setText(Util.formatearDouble(venta.getTotal()) + "€");

    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public static class RegistroVentasViewHolder extends RecyclerView.ViewHolder {

        public TextView txtCodigo, txtFecha, txtTotal;
        public ImageButton btnDetalles;

        public RegistroVentasViewHolder(@NonNull View itemView) {
            super(itemView);

            txtCodigo = itemView.findViewById(R.id.txtCodigoRegistro);
            txtFecha = itemView.findViewById(R.id.txtFechaRegistro);
            txtTotal = itemView.findViewById(R.id.txtTotalRegistro);
            btnDetalles = itemView.findViewById(R.id.btnDetallesRegistro);
        }
    }

    public void setDataset(List<Venta> dataset) {
        this.dataset = dataset;
        notifyDataSetChanged();
    }
}
