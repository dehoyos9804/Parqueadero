package co.com.ingenesys.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import co.com.ingenesys.R;
import co.com.ingenesys.adapter.AdaptadorConvenios;
import co.com.ingenesys.adapter.AdaptadorTarifas;
import co.com.ingenesys.modelo.Convenios;
import co.com.ingenesys.modelo.Tarifa;
import co.com.ingenesys.modelo.TipoVehiculo;
import co.com.ingenesys.utils.Constantes;
import co.com.ingenesys.utils.Preferences;
import co.com.ingenesys.web.VolleySingleton;

public class MisConveniosFragment extends Fragment {
    private static final String TAG = MisConveniosFragment.class.getSimpleName();
    //instaciar
    //adaptador del RecicleView
    private AdaptadorConvenios adapter;
    //instancia global de RecicleView
    private RecyclerView recyclerView;
    //private TextView emptyView;
    //instancia global del administrador
    private RecyclerView.LayoutManager lManager;
    private TextView data_empty_convenio;
    private Gson gson = new Gson();

    private ProgressDialog loading = null;
    private View view;

    //Constructor
    public MisConveniosFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_convenios, container, false);

        init();

        return view;
    }

    //instacion todas los elementos xml
    private void init(){
        recyclerView = (RecyclerView) view.findViewById(R.id.reciclador_convenio);
        recyclerView.setHasFixedSize(true);

        //Usar el Administrador para LinearLayout
        lManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(lManager);
        data_empty_convenio = (TextView) view.findViewById(R.id.data_empty_convenio);
    }

    @Override
    public void onResume() {
        super.onResume();
        getHTTP();
    }

    /*
     * Carga el adaptador con las Consultas obtenidas
     * en la respuesta
     */
    public void getHTTP() {
        String parqueadero_id = Preferences.getPreferenceString(getContext(), Constantes.PREFERENCIA_PARQUEADERO_ID);
        String newURL = Constantes.GET_CONVENIOS_PARQUEADEROS_ID + "?parqueadero_id=" + parqueadero_id;
        // Petición GET
        VolleySingleton.
                getInstance(getActivity()).
                addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                newURL,
                                null,
                                new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {
                                        // Procesar la respuesta Json
                                        data_empty_convenio.setText("cargando...");
                                        procesarRespuestaHTTP(response);
                                        Log.i(TAG, "processanddo respuesta..." + response);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        data_empty_convenio.setText(getActivity().getResources().getString(R.string.empty_list));
                                        showSnackBar("Error al cargar los datos: " + error.toString());
                                        Log.d(TAG, "Error Volley: " + error.toString());
                                    }
                                }

                        )
                );
    }


    private void procesarRespuestaHTTP(JSONObject response) {
        try {
            // Obtener atributo "estado"
            String estado = response.getString("estado");
            switch (estado) {
                case "1":// EXITO
                    try {
                        // Obtener array "consulta" Json
                        JSONArray mensaje = response.getJSONArray("tbl_empresas");;
                        // Parsear con Gson
                        Convenios[] convenios = gson.fromJson(mensaje.toString(), Convenios[].class);

                        // Inicializar adaptador
                        adapter = new AdaptadorConvenios(Arrays.asList(convenios), getContext());

                        // Setear adaptador a la lista
                        recyclerView.setAdapter(adapter);
                        data_empty_convenio.setText("");

                    } catch (JSONException e) {
                        Log.i(TAG, "Error al llenar Adaptador " + e.getLocalizedMessage());
                    }
                    break;
                case "2":
                    String mensaje2 = response.getString("mensaje");
                    //loading.dismiss();
                    data_empty_convenio.setText(getActivity().getResources().getString(R.string.empty_list));
                    showSnackBar(mensaje2);
                    break;
                case "3":
                    String mensaje3 = response.getString("mensaje");
                    //loading.dismiss();
                    data_empty_convenio.setText(getActivity().getResources().getString(R.string.empty_list));
                    showSnackBar(mensaje3);
                    break;
            }
        } catch (JSONException je) {
            Log.d(TAG, je.getMessage());
        }
    }

    /**
     * Proyecta una {@link Snackbar} con el string usado
     *
     * @param msg Mensaje
     */
    private void showSnackBar(String msg) {
        Snackbar
                .make(view.findViewById(R.id.coordinator_convenio), msg, Snackbar.LENGTH_LONG)
                .show();
    }
}
