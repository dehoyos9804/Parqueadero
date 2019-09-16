package co.com.ingenesys.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import co.com.ingenesys.R;

public class ExplorarMapsFragment extends SupportMapFragment {
    //Etiqueta de depuracion
    private static final String TAG = ExplorarMapsFragment.class.getSimpleName();

    private GoogleMap mMap;

    //costructor del fragmento
    public ExplorarMapsFragment() {
    }

    /**
     * Crea una instancia prefabricada de {@link ExplorarMapsFragment}
     *
     * @return Instancia dle fragmento
     */
    public static ExplorarMapsFragment newInstance() {
        ExplorarMapsFragment fragment = new ExplorarMapsFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        return view;
    }

}
