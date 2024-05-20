package com.example.ayenapp.vista.adaptadores;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ayenapp.R;
import com.example.ayenapp.modelo.Linea;
import com.example.ayenapp.util.Util;
import com.example.ayenapp.vista.TpvFragment;

import java.util.List;

public class TpvAdapter extends RecyclerView.Adapter<TpvAdapter.TpvViewHolder> {

    private List<Linea> datalist;
    private TpvFragment tpvFragment;

    public TpvAdapter(TpvFragment tpvFragment, List<Linea> datalist) {
        this.tpvFragment = tpvFragment;
        this.datalist = datalist;
    }

    @NonNull
    @Override
    public TpvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_linea_venta, parent, false);
        return new TpvAdapter.TpvViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TpvViewHolder holder, int position) {
        Linea linea = datalist.get(position);

        holder.txtNombre.setText(linea.getProducto().getNombre());
        holder.txtPrecio.setText(Util.formatearDouble(linea.getPrecio()) + "€");
        holder.txtCantidad.setText(linea.getCantidad().toString());
        holder.btnBorrar.setOnClickListener(v -> borrarLinea(linea));
        holder.btnMenos.setOnClickListener(v -> botonMenos(linea));
        holder.btnMas.setOnClickListener(v -> botonMas(linea));

        holder.txtCantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().trim().isEmpty()) {
                    if (s.toString().trim().equals("0")) {
                        borrarLinea(linea);
                    } else {
                        Integer cantidad = new Integer(s.toString());
                        linea.setCantidad(cantidad);
                        linea.setPrecio(cantidad * linea.getProducto().getPrecio());
                        holder.txtPrecio.setText(Util.formatearDouble(linea.getPrecio()) + "€");
                    }
                } else {
                    holder.txtPrecio.setText("");
                }

                tpvFragment.actualizarTotal();
            }
        });
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public class TpvViewHolder extends RecyclerView.ViewHolder {

        public TextView txtNombre, txtPrecio;
        public EditText txtCantidad;
        public ImageButton btnBorrar, btnMenos, btnMas;

        public TpvViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombreLineaVenta);
            txtPrecio = itemView.findViewById(R.id.txtPrecioLineaVenta);
            txtCantidad = itemView.findViewById(R.id.txtCantidadLineaVenta);
            btnBorrar = itemView.findViewById(R.id.btnBorrarLineaVenta);
            btnMenos = itemView.findViewById(R.id.btnMenosLineaVenta);
            btnMas = itemView.findViewById(R.id.btnMasLineaVenta);
        }
    }

    public List<Linea> getDatalist() {
        return this.datalist;
    }

    public void setDatalist(List<Linea> datalist) {
        this.datalist = datalist;
        tpvFragment.actualizarTotal();
        notifyDataSetChanged();
    }

    /**
     * Borrar una linea de venta
     *
     * @param linea Linea de venta
     */
    private void borrarLinea(Linea linea) {
        this.datalist.remove(linea);
        tpvFragment.actualizarTotal();
        notifyDataSetChanged();
    }

    /**
     * Disminuye en 1 la cantidad de la linea o la borra si es menor o igual a 0
     *
     * @param linea Linea de venta
     */
    private void botonMenos(Linea linea) {
        Integer nuevaCantidad = linea.getCantidad() - 1;
        if (nuevaCantidad <= 0) {
            borrarLinea(linea);
        } else {
            linea.setCantidad(nuevaCantidad);
            linea.setPrecio(nuevaCantidad * linea.getProducto().getPrecio());
            tpvFragment.actualizarTotal();
            notifyDataSetChanged();
        }
    }

    /**
     * Aumenta en 1 la cantidad de la linea
     *
     * @param linea Linea de venta
     */
    private void botonMas(Linea linea) {
        Integer nuevaCantidad = linea.getCantidad() + 1;
        linea.setCantidad(nuevaCantidad);
        linea.setPrecio(nuevaCantidad * linea.getProducto().getPrecio());
        tpvFragment.actualizarTotal();
        notifyDataSetChanged();
    }

}
