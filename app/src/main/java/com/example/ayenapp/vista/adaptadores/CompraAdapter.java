package com.example.ayenapp.vista.adaptadores;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ayenapp.R;
import com.example.ayenapp.modelo.Linea;
import com.example.ayenapp.servicio.ProductoService;
import com.example.ayenapp.util.Util;
import com.example.ayenapp.vista.CompraFragment;

import java.util.List;

public class CompraAdapter extends RecyclerView.Adapter<CompraAdapter.CompraViewHolder> {

    private List<Linea> datalist;
    private CompraFragment compraFragment;
    private ProductoService productoService;

    public CompraAdapter(CompraFragment compraFragment, List<Linea> datalist) {
        this.compraFragment = compraFragment;
        this.datalist = datalist;
        productoService = new ProductoService(compraFragment);
    }

    @NonNull
    @Override
    public CompraViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_linea_compra, parent, false);
        return new CompraAdapter.CompraViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CompraViewHolder holder, int position) {
        Linea linea = datalist.get(position);

        holder.txtNombre.setText(linea.getProducto().getNombre());
        holder.txtPrecio.setText(Util.formatearDouble(linea.getPrecio()) + "€");
        holder.txtCantidad.setText(linea.getCantidad().toString());
        holder.txtCoste.setText(Util.formatearDouble(linea.getProducto().getCoste()));
        holder.btnBorrar.setOnClickListener(v -> borrarLinea(linea));
        holder.btnMenos.setOnClickListener(v -> holder.txtCantidad.setText("" + (linea.getCantidad() - 1)));
        holder.btnMas.setOnClickListener(v -> holder.txtCantidad.setText("" + (linea.getCantidad() + 1)));
        productoService.loadFoto(linea.getProducto(), holder.imgProducto);

        initCantidadListener(holder, linea);
        initCosteListener(holder, linea);
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public class CompraViewHolder extends RecyclerView.ViewHolder {

        public TextView txtNombre, txtPrecio;
        public EditText txtCantidad, txtCoste;
        public ImageButton btnBorrar, btnMenos, btnMas;
        public ImageView imgProducto;

        public CompraViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombreLineaCompra);
            txtPrecio = itemView.findViewById(R.id.txtPrecioLineaCompra);
            txtCantidad = itemView.findViewById(R.id.txtCantidadLineaCompra);
            txtCoste = itemView.findViewById(R.id.txtCosteLineaCompra);
            btnBorrar = itemView.findViewById(R.id.btnBorrarLineaCompra);
            btnMenos = itemView.findViewById(R.id.btnMenosLineaCompra);
            btnMas = itemView.findViewById(R.id.btnMasLineaCompra);
            imgProducto = itemView.findViewById(R.id.imgProductoCompra);
        }
    }

    public void setDatalist(List<Linea> datalist) {
        this.datalist = datalist;
        compraFragment.actualizarTotal();
        notifyDataSetChanged();
    }

    /**
     * Borrar una linea de venta
     *
     * @param linea Linea de venta
     */
    private void borrarLinea(Linea linea) {
        this.datalist.remove(linea);
        compraFragment.actualizarTotal();
        notifyDataSetChanged();
    }

    /**
     * Inicializa el listener de cambio de texto del campo cantidad
     *
     * @param holder Holder de los elementos
     * @param linea  Linea de compra
     */
    private void initCantidadListener(CompraViewHolder holder, Linea linea) {
        holder.txtCantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().trim().isEmpty()) {
                    Integer cantidad = new Integer(s.toString());
                    linea.setCantidad(cantidad);
                    linea.setPrecio(cantidad * linea.getProducto().getCoste());
                    holder.txtPrecio.setText(Util.formatearDouble(linea.getPrecio()) + "€");
                } else {
                    holder.txtCantidad.setText("0");
                    holder.txtCantidad.selectAll();
                    linea.setCantidad(0);
                    linea.setPrecio(.0);
                    holder.txtPrecio.setText(Util.formatearDouble(0.0) + "€");
                }

                compraFragment.actualizarTotal();
            }
        });
    }

    /**
     * Inicializa el listener de cambio de texto del campo coste
     *
     * @param holder Holder de los elementos
     * @param linea  Linea de compra
     */
    private void initCosteListener(CompraViewHolder holder, Linea linea) {
        holder.txtCoste.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().trim().isEmpty()) {
                    Double coste = Double.valueOf(s.toString());
                    linea.getProducto().setCoste(coste);
                    linea.setPrecio(linea.getCantidad() * linea.getProducto().getCoste());
                    holder.txtPrecio.setText(Util.formatearDouble(linea.getPrecio()) + "€");
                } else {
                    holder.txtCoste.setText("0");
                    holder.txtCoste.selectAll();
                    linea.getProducto().setCoste(.0);
                    linea.setPrecio(.0);
                    holder.txtPrecio.setText(Util.formatearDouble(0.0) + "€");
                }

                compraFragment.actualizarTotal();
            }
        });
    }
}
