package com.example.ayenapp.vista.adaptadores;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ayenapp.R;
import com.example.ayenapp.modelo.Producto;
import com.example.ayenapp.servicio.ProductoService;
import com.example.ayenapp.util.Util;
import com.example.ayenapp.vista.BuscarActivity;
import com.example.ayenapp.vista.TpvFragment;

import java.util.List;

public class BuscarAdapter extends RecyclerView.Adapter<BuscarAdapter.TpvBuscarViewHolder> {

    private List<Producto> datalist;
    private ProductoService productoService;
    private BuscarActivity buscarActivity;

    public BuscarAdapter(BuscarActivity buscarActivity, List<Producto> datalist) {
        this.datalist = datalist;
        this.buscarActivity = buscarActivity;
        this.productoService = new ProductoService(buscarActivity);
    }

    @NonNull
    @Override
    public BuscarAdapter.TpvBuscarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_buscar_producto_venta, parent, false);
        return new TpvBuscarViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BuscarAdapter.TpvBuscarViewHolder holder, int position) {
        Producto producto = datalist.get(position);

        holder.txtNombre.setText(producto.getNombre());
        holder.txtCodigo.setText(producto.getCodigo());
        holder.txtPrecio.setText(Util.formatearDouble(producto.getPrecio()) + "€");
        holder.txtStock.setText(producto.getStock().toString());
        productoService.loadFoto(producto, holder.imgProducto);

        holder.btnAdd.setOnClickListener(v -> buscarActivity.addProducto(producto));
    }

    @Override
    public int getItemCount() {
        return this.datalist.size();
    }

    public class TpvBuscarViewHolder extends RecyclerView.ViewHolder {

        public TextView txtCodigo, txtNombre, txtPrecio, txtStock;
        public ImageView imgProducto;
        public Button btnAdd;

        public TpvBuscarViewHolder(@NonNull View itemView) {
            super(itemView);

            txtCodigo = itemView.findViewById(R.id.txtCodigoBuscar);
            txtNombre = itemView.findViewById(R.id.txtNombreBuscar);
            txtPrecio = itemView.findViewById(R.id.txtPrecioBuscar);
            txtStock = itemView.findViewById(R.id.txtStockBuscar);
            imgProducto = itemView.findViewById(R.id.imgProductoBuscar);
            btnAdd = itemView.findViewById(R.id.btnAddBuscar);
        }
    }

    public void setDatalist(List<Producto> datalist) {
        this.datalist = datalist;
        notifyDataSetChanged();
    }
}
