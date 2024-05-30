package com.example.ayenapp.vista.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ayenapp.R;
import com.example.ayenapp.modelo.Linea;
import com.example.ayenapp.servicio.ProductoService;
import com.example.ayenapp.util.Util;
import com.example.ayenapp.vista.RegistroActivity;

import java.util.List;

public class RegistroAdapter extends RecyclerView.Adapter<RegistroAdapter.RegistroViewHolder> {

    private List<Linea> dataset;
    private RegistroActivity registroActivity;
    private ProductoService productoService;

    public RegistroAdapter(RegistroActivity registroActivity, List<Linea> dataset) {
        this.dataset = dataset;
        this.registroActivity = registroActivity;
        this.productoService = new ProductoService(registroActivity);
    }

    @NonNull
    @Override
    public RegistroAdapter.RegistroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_registro_detalles, parent, false);
        return new RegistroViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RegistroAdapter.RegistroViewHolder holder, int position) {
        Linea linea = dataset.get(position);

        holder.txtCantidad.setText(linea.getCantidad() + "x");
        holder.txtNombre.setText(linea.getProducto().getNombre());
        holder.txtTotal.setText(Util.formatearDouble(linea.getPrecio()) + "€");
        productoService.loadFoto(linea.getProducto(), holder.imgProducto);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public static class RegistroViewHolder extends RecyclerView.ViewHolder {

        public TextView txtCantidad, txtNombre, txtTotal;
        public ImageView imgProducto;

        public RegistroViewHolder(@NonNull View itemView) {
            super(itemView);

            txtCantidad = itemView.findViewById(R.id.txtCantidadLineaRegistro);
            txtNombre = itemView.findViewById(R.id.txtNombreLineaRegistro);
            txtTotal = itemView.findViewById(R.id.txtTotalLineaRegistro);
            imgProducto = itemView.findViewById(R.id.imgProductoLineaRegistro);
        }
    }
}
