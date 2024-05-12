package com.example.ayenapp.vista.adaptadores;

import android.annotation.SuppressLint;
import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;

public class ProductosAdapter extends RecyclerView.Adapter<ProductosAdapter.ProductoViewHolder> {

    private List<Producto> datalist;
    private ProductoService productoService;

    public ProductosAdapter(List<Producto> datalist) {
        this.datalist = datalist;
        this.productoService = new ProductoService();
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_producto, parent, false);
        return new ProductoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Producto producto = datalist.get(position);

        holder.txtCodigo.setText(producto.getCodigo());
        holder.txtNombre.setText(producto.getNombre());
        holder.txtPrecio.setText(Util.formatearDouble(producto.getPrecio()) + "€");
        holder.txtCoste.setText(Util.formatearDouble(producto.getCoste()) + "€");
        holder.txtStock.setText(producto.getStock().toString());
        holder.txtUmbral.setText(producto.getUmbralCompra().toString());
        productoService.loadFoto(producto, holder.imgProducto);
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    /**
     * Establece una nueva lista
     * @param datalist Lista de productos
     */
    public void setDatalist(List<Producto> datalist) {
        this.datalist = datalist;
        notifyDataSetChanged();
    }

    public class ProductoViewHolder extends RecyclerView.ViewHolder {

        public TextView txtCodigo, txtNombre, txtPrecio, txtCoste, txtStock, txtUmbral;
        public Button btnModificar, btnEliminar;
        public ImageView imgProducto;
        public Context context;
        public View view;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            view = itemView;
            txtCodigo = itemView.findViewById(R.id.txtCodigoCard);
            txtNombre = itemView.findViewById(R.id.txtNombreCard);
            txtPrecio = itemView.findViewById(R.id.txtPrecioCard);
            txtCoste = itemView.findViewById(R.id.txtCosteCard);
            txtStock = itemView.findViewById(R.id.txtStockCard);
            txtUmbral = itemView.findViewById(R.id.txtUmbralCard);
            btnModificar = itemView.findViewById(R.id.btnModificarCard);
            btnEliminar = itemView.findViewById(R.id.btnEliminarCard);
            imgProducto = itemView.findViewById(R.id.imgProductoCard);
        }
    }
}
