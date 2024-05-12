package com.example.ayenapp.servicio;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.ayenapp.R;
import com.example.ayenapp.modelo.Producto;
import com.example.ayenapp.vista.CrearProductoActivity;
import com.example.ayenapp.vista.ProductosFragment;
import com.example.ayenapp.vista.adaptadores.ProductosAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ProductoService {

    private Context context;
    private ProductosFragment productosFragment;

    public ProductoService() {
    }

    public ProductoService(Context context) {
        this.context = context;
    }

    public ProductoService(ProductosFragment fragment) {
        this.productosFragment = fragment;
        this.context = fragment.getContext();
    }

    /**
     * Guarda el producto en los servicios de Firebase
     *
     * @param producto Producto a guardar
     * @param uri      Foto del producto a guardar
     */
    public void guardarProducto(Producto producto, Uri uri) {
        CrearProductoActivity activity = (CrearProductoActivity) context;
        activity.setBarraCarga(View.VISIBLE);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child(producto.getFoto());
        storageRef.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference ref = database.getReference("productos");
                    ref.child(producto.getCodigo()).setValue(producto)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(context, context.getString(R.string.exitoCrearProducto), Toast.LENGTH_SHORT).show();
                                activity.setBarraCarga(View.GONE);
                                activity.finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, context.getString(R.string.falloCrearProducto), Toast.LENGTH_SHORT).show();
                                activity.setBarraCarga(View.GONE);
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, context.getString(R.string.falloCrearProducto), Toast.LENGTH_SHORT).show();
                    activity.setBarraCarga(View.GONE);
                });
    }

    /**
     * Cargar la imagen del producto
     * @param producto Producto del que queremos la imagen
     * @param imageView ImageView que actualizaremos
     */
    public void loadFoto(Producto producto, ImageView imageView) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child(producto.getFoto());
        storageRef.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Glide.with(imageView).load(uri).into(imageView);
                })
                .addOnFailureListener(exception -> {
                    Toast.makeText(context, context.getString(R.string.falloCargarImagen), Toast.LENGTH_SHORT).show();
                });
    }

    public void getProductos() {
        List<Producto> productos = new ArrayList<>();

        productosFragment.setBarraCarga(View.VISIBLE);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference productosRef = database.getReference().child("productos");
        productosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productos.clear();

                for (DataSnapshot productoSnapshot : dataSnapshot.getChildren()) {
                    Producto producto = productoSnapshot.getValue(Producto.class);
                    productos.add(producto);
                }

                productosFragment.setProductos(productos);
                productosFragment.setBarraCarga(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, context.getString(R.string.falloCargarProductos), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
