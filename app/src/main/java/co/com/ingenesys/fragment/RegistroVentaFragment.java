package co.com.ingenesys.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import co.com.ingenesys.R;

public class RegistroVentaFragment extends Fragment {
    private View view;

    //Constructor
    public RegistroVentaFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_registro_venta, container, false);

        return view;
    }

}
