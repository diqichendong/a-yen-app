package com.example.ayenapp.servicio;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.ayenapp.R;
import com.example.ayenapp.modelo.Linea;
import com.example.ayenapp.modelo.Producto;
import com.example.ayenapp.vista.CompraFragment;
import com.example.ayenapp.vista.GuardarProductoActivity;
import com.example.ayenapp.vista.ProductosFragment;
import com.example.ayenapp.vista.TpvFragment;
import com.example.ayenapp.vista.adaptadores.ProductosAdapter;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
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

    private static final String NODO_BASE = "productos";

    private Context context;
    private GuardarProductoActivity guardarProductoActivity;
    private ProductosFragment productosFragment;
    private TpvFragment tpvFragment;
    private ProductosAdapter productosAdapter;
    private CompraFragment compraFragment;

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

    public ProductoService(ProductosAdapter adapter, Context context) {
        this.productosAdapter = adapter;
        this.context = context;
    }

    public ProductoService(Context context) {
        this.context = context;
    }

    public ProductoService(CompraFragment compraFragment) {
        this.compraFragment = compraFragment;
        this.context = compraFragment.getContext();
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

        List<Task<Void>> tareas = new ArrayList<>();

        if (uri != null) {  // Si hay cambio de foto
            tareas.add(storageRef.child(producto.getFoto()).putFile(uri).continueWithTask(task -> Tasks.forResult(null)));
            tareas.add(databaseRef.child(producto.getCodigo()).setValue(producto));
        } else {    // No hay cambio de foto
            tareas.add(databaseRef.child(producto.getCodigo()).setValue(producto));
        }

        Tasks.whenAll(tareas).addOnSuccessListener(unused -> {
            Toast.makeText(guardarProductoActivity, guardarProductoActivity.getString(R.string.exitoGuardarProducto), Toast.LENGTH_SHORT).show();
            guardarProductoActivity.finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(guardarProductoActivity, guardarProductoActivity.getString(R.string.falloGuardarProducto), Toast.LENGTH_SHORT).show();
        }).addOnCompleteListener(task -> {
            guardarProductoActivity.setBarraCarga(View.GONE);
        });
    }

    /**
     * Cargar la imagen del producto
     *
     * @param producto  Producto del que queremos la imagen
     * @param imageView ImageView que actualizaremos
     */
    public void loadFoto(Producto producto, ImageView imageView) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        storageRef.child(producto.getFoto()).getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(imageView).load(uri).into(imageView);
        }).addOnFailureListener(exception -> {
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
        if (compraFragment != null) {
            compraFragment.setBarraCarga(View.VISIBLE);
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

                if (productosFragment != null) {
                    productosFragment.setProductos(productos);
                    productosFragment.setBarraCarga(View.GONE);
                }
                if (tpvFragment != null) {
                    tpvFragment.setProductos(productos);
                    tpvFragment.setBarraCarga(View.GONE);
                }
                if (compraFragment != null) {
                    compraFragment.setProductos(productos);
                    compraFragment.setBarraCarga(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (productosFragment != null || tpvFragment != null || compraFragment != null) {
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
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference(NODO_BASE);
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        List<Task<Void>> tareas = new ArrayList<>();

        tareas.add(databaseRef.child(producto.getCodigo()).removeValue());
        tareas.add(storageRef.child(producto.getFoto()).delete());

        Tasks.whenAll(tareas).addOnSuccessListener(unused -> {
            productosAdapter.borrarProducto(producto);
            Toast.makeText(context, context.getString(R.string.exitoEliminarProducto), Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(context, context.getString(R.string.falloEliminarProducto), Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Actualiza el stock de los productos al realizar una venta
     *
     * @param lineas Lineas del pedido
     */
    public void actualizarStock(List<Linea> lineas) {
        if (tpvFragment != null) {
            tpvFragment.setBarraCarga(View.VISIBLE);
        }
        if (compraFragment != null) {
            compraFragment.setBarraCarga(View.VISIBLE);
        }

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference(NODO_BASE);

        List<Task<Void>> tareas = new ArrayList<>();

        lineas.forEach(linea -> {
            Integer actualStock = linea.getProducto().getStock();
            Integer nuevoStock = 0;

            if (tpvFragment != null) {
                nuevoStock = Math.max(actualStock - linea.getCantidad(), 0);
            }
            if (compraFragment != null) {
                nuevoStock = linea.getCantidad() + linea.getProducto().getStock();
            }

            Task<Void> tarea = databaseRef.child(linea.getProducto().getCodigo() + "/stock")
                    .setValue(nuevoStock);
            tareas.add(tarea);
        });

        Tasks.whenAll(tareas).addOnSuccessListener(task -> {
            if (tpvFragment != null) {
                tpvFragment.setBarraCarga(View.GONE);
            }
            if (compraFragment != null) {
                compraFragment.setBarraCarga(View.GONE);
            }
        });
    }

    /**
     * Actualiza el coste de los productos al realizar una compra
     *
     * @param lineas Lineas del pedido
     */
    public void actualizarCoste(List<Linea> lineas) {
        compraFragment.setBarraCarga(View.VISIBLE);

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference(NODO_BASE);

        List<Task<Void>> tareas = new ArrayList<>();

        lineas.forEach(linea -> {
            Task<Void> tarea = databaseRef.child(linea.getProducto().getCodigo() + "/coste")
                    .setValue(linea.getProducto().getCoste());
            tareas.add(tarea);
        });

        Tasks.whenAll(tareas).addOnSuccessListener(task -> {
            compraFragment.setBarraCarga(View.GONE);
        });
    }
}
