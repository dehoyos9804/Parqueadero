package co.com.ingenesys.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;

import co.com.ingenesys.R;
import co.com.ingenesys.adapter.AdaptadorMenu;
import co.com.ingenesys.modelo.MenuModels;

public class MenuFragment extends Fragment implements View.OnClickListener {
    //Etiqueta de depuracion
    private static final String TAG = MenuFragment.class.getSimpleName();
    //adaptador del RecicleView
    private AdaptadorMenu adapter;
    //instancia global de RecicleView
    private RecyclerView recyclerView;
    //private TextView emptyView;
    //instancia global del administrador
    private RecyclerView.LayoutManager lManager;

    //costructor del fragmento
    public MenuFragment() {

    }

    /**
     * Crea una instancia prefabricada de {@link ExplorarMapsFragment}
     *
     * @return Instancia dle fragmento
     */
    public static MenuFragment newInstance() {
        MenuFragment fragment = new MenuFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.reciclador);
        recyclerView.setHasFixedSize(true);

        //Usar el Administrador para LinearLayout
        lManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(lManager);



        //cargardatos en el adaptador
        adapter = new AdaptadorMenu(MenuModels.listaMenu(), getActivity());

        // Setear adaptador a la lista
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onClick(View v) {

    }
}
