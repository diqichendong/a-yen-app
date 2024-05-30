package com.example.ayenapp.vista.adaptadores;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.ayenapp.vista.RegistroComprasFragment;
import com.example.ayenapp.vista.RegistroVentasFragment;

public class ScreenSlidePagerAdapter extends FragmentStateAdapter {

    public  ScreenSlidePagerAdapter(FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = new RegistroVentasFragment();
                break;
            case 1:
                fragment = new RegistroComprasFragment();
                break;
        }

        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
