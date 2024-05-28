package com.example.ayenapp.vista;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ayenapp.R;
import com.example.ayenapp.vista.adaptadores.ScreenSlidePagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class RegistrosFragment extends Fragment {

    private MainActivity mainActivity;

    private View view;
    private TabLayout tabLayout;
    private ViewPager2 viewpager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_registros, container, false);

        init();

        return view;
    }

    /**
     * Inicializar componentes de la vista
     */
    private void init() {
        mainActivity = (MainActivity) getActivity();
        tabLayout = view.findViewById(R.id.tabLayout);
        viewpager = view.findViewById(R.id.viewpager);

        viewpager.setAdapter(new ScreenSlidePagerAdapter(getActivity()));
        new TabLayoutMediator(tabLayout, viewpager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(R.string.tabVentas);
                    break;
                case 1:
                    tab.setText(R.string.tabCompras);
                    break;
            }
        }).attach();
    }
}