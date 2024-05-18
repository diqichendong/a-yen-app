package com.example.ayenapp.servicio;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.ayenapp.R;
import com.example.ayenapp.modelo.Producto;
import com.example.ayenapp.vista.GuardarProductoActivity;
import com.example.ayenapp.vista.ProductosFragment;
import com.example.ayenapp.vista.TpvFragment;
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

    private static String NODO_BASE = "productos";
    private static final String LOCAL_URI_PREFIX = "content://";

    private Context context;
    private GuardarProductoActivity guardarProductoActivity;
    private ProductosFragment productosFragment;
    private TpvFragment tpvFragment;
    private ProductosAdapter productosAdapter;

    public ProductoService(GuardarProductoActivity activity) {
        this.guardarProductoActivity = activity;
        this.context = activity;
    }

    public ProductoService(ProductosFragment fragment) {
        this.productosFragment = fragment;
        this.context = fragment.getContext();
    }

    public ProductoService(TpvFragment tpvFragment) {
        this.tpvFragment = tpvFragment;
        this.context = tpvFragment.getContext();
    }

    public ProductoService(ProductosAdapter adapter) {
        this.productosAdapter = adapter;
    }

    /**
     * Guarda el producto en los servicios de Firebase
     *
     * @param producto Producto a guardar
     * @param uri      Foto del producto a guardar
     */
    public void guardarProducto(Producto producto, Uri uri) {
        guardarProductoActivity.setBarraCarga(View.VISIBLE);
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference(NODO_BASE);
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        if (uri != null) {  // Si hay cambio de foto
            // Guardar foto producto
            storageRef.child(producto.getFoto()).putFile(uri).addOnSuccessListener(taskSnapshot -> {
                // Guardar producto
                databaseRef.child(producto.getCodigo()).setValue(producto).addOnSuccessListener(unused -> {
                    exitoGuardar();
                }).addOnFailureListener(e -> {
                    falloGuardar();
                });
            }).addOnFailureListener(e -> {
                falloGuardar();
            });
        } else {    // No hay cambio de foto
            // Guardar producto
            databaseRef.child(producto.getCodigo()).setValue(producto).addOnSuccessListener(unused -> {
                exitoGuardar();
            }).addOnFailureListener(e -> {
                falloGuardar();
            });
        }
    }

    /**
     * Procedimiento al guardar con éxito un producto
     */
    private void exitoGuardar() {
        Toast.makeText(guardarProductoActivity, guardarProductoActivity.getString(R.string.exitoGuardarProducto), Toast.LENGTH_SHORT).show();
        guardarProductoActivity.setBarraCarga(View.GONE);
        guardarProductoActivity.finish();
    }

    /**
     * Procedimiento al fallar en guardar un producto
     */
    private void falloGuardar() {
        Toast.makeText(guardarProductoActivity, guardarProductoActivity.getString(R.string.falloGuardarProducto), Toast.LENGTH_SHORT).show();
        guardarProductoActivity.setBarraCarga(View.GONE);
    }

    /**
     * Cargar la imagen del producto
     *
     * @param producto  Producto del que queremos la imagen
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

    /**
     * Obetener la lista de todos los productos de Firebase
     */
    public void getProductos() {
        List<Producto> productos = new ArrayList<>();

        if (productosFragment != null) {
            productosFragment.setBarraCarga(View.VISIBLE);
        }

        if (tpvFragment != null) {
            tpvFragment.setBarraCarga(View.VISIBLE);
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference productosRef = database.getReference().child(NODO_BASE);
        productosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productos.clear();

                for (DataSnapshot productoSnapshot : dataSnapshot.getChildren()) {
                    Producto producto = productoSnapshot.getValue(Producto.class);
                    productos.add(producto);
                }

                // Producto fragment
                if (productosFragment != null) {
                    productosFragment.setProductos(productos);
                    productosFragment.setBarraCarga(View.GONE);
                }

                // Tpv fragment
                if (tpvFragment != null) {
                    tpvFragment.setProductos(productos);
                    tpvFragment.setBarraCarga(View.GONE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Producto fragment
                if (productosFragment != null) {
                    Toast.makeText(context, context.getString(R.string.falloCargarProductos), Toast.LENGTH_SHORT).show();
                }

                // Tpv fragment
                if (tpvFragment != null) {
                    Toast.makeText(context, context.getString(R.string.falloCargarProductos), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Eliminar un producto de Firebase
     *
     * @param context  Contexto de donde se llama a la función
     * @param producto Producto a eliminar
     */
    public void eliminarProducto(Context context, Producto producto) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference productoRef = database.getReference(NODO_BASE).child(producto.getCodigo());
        productoRef.removeValue().addOnSuccessListener(unused -> {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference productoStorageRef = storage.getReference().child(producto.getFoto());
            productoStorageRef.delete().addOnSuccessListener(unused1 -> {
                productosAdapter.borrarProducto(producto);
                Toast.makeText(context, context.getString(R.string.exitoEliminarProducto), Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                Toast.makeText(context, context.getString(R.string.falloEliminarProducto), Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(context, context.getString(R.string.falloEliminarProducto), Toast.LENGTH_SHORT).show();
        });
    }

}
