package com.example.ayenapp.vista;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.ayenapp.R;
import com.example.ayenapp.modelo.Producto;
import com.example.ayenapp.servicio.ProductoService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ProductosFragment productosFragment;
    private TpvFragment tpvFragment;

    private BottomNavigationView bottomNav;
    private ProgressBar barraCarga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();
    }


    private void init() {
        productosFragment = new ProductosFragment();
        tpvFragment = new TpvFragment();

        bottomNav = findViewById(R.id.bottomNav);
        barraCarga = findViewById(R.id.barraCarga);

        initBottomNav();

        cargarFragment(tpvFragment);
    }

    /**
     * Establecer el estado de la barra de carga
     * @param estado Estado de la barra View.VISIBLE o View.GONE
     */
    public void setBarraCarga(int estado) {
        barraCarga.setVisibility(estado);
        if (estado == View.VISIBLE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    private void initBottomNav() {
        bottomNav.setOnItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.bottomNavTpv) {
                cargarFragment(tpvFragment);
            }
            if (menuItem.getItemId() == R.id.bottomNavProductos) {
                cargarFragment(productosFragment);
            }
            return true;
        });
    }

    private void cargarFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}