package co.com.ingenesys.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bashizip.bhlib.BusinessHours;
import com.bashizip.bhlib.BusinessHoursWeekView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.com.ingenesys.R;
import co.com.ingenesys.adapter.AdaptadorMenu;
import co.com.ingenesys.modelo.Horarios;
import co.com.ingenesys.modelo.MenuModels;
import co.com.ingenesys.modelo.Parqueaderos;
import co.com.ingenesys.ui.InitialActivity;
import co.com.ingenesys.ui.RegistroHorarioActivity;
import co.com.ingenesys.utils.Constantes;
import co.com.ingenesys.utils.Preferences;
import co.com.ingenesys.utils.Utilidades;
import co.com.ingenesys.web.VolleySingleton;

public class HorarioFragment extends Fragment implements View.OnClickListener {
    //Etiqueta de depuracion
    private static final String TAG = MenuFragment.class.getSimpleName();

    private View view;

    private FloatingActionButton fab_horario;
    private BusinessHoursWeekView bh_view;
    private TextView data_empty;

    List<BusinessHours> businessHoursList;
    private ArrayList<Horarios> horarios;
    private boolean isHorario = false;

    //costructor del fragmento
    public HorarioFragment() {

    }


    /**
     * Crea una instancia prefabricada de {@link ExplorarMapsFragment}
     *
     * @return Instancia dle fragmento
     */
    public static HorarioFragment newInstance() {
        HorarioFragment fragment = new HorarioFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_section_horario, container, false);
        init();//inicio las instancia
        return view;
    }

    private void init(){
        fab_horario = (FloatingActionButton) view.findViewById(R.id.fab_horario);
        data_empty = (TextView) view.findViewById(R.id.data_empty);
        bh_view = (BusinessHoursWeekView) view.findViewById(R.id.bh_view);

        getHorariosHTTP();

        fab_horario.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab_horario:
                if(isHorario){
                    showSnackBar("Ya Registro Su Horario ");
                }else{
                    Intent intent = new Intent(getActivity(), RegistroHorarioActivity.class);
                    startActivity(intent);
                }
                break;
        }
    }

    /**
     * Proyecta una {@link Snackbar} con el string usado
     *
     * @param msg Mensaje
     */
    private void showSnackBar(String msg) {
        Snackbar
                .make(view.findViewById(R.id.coordinator_horario), msg, Snackbar.LENGTH_LONG)
                .show();
    }

    /*
     * Carga el adaptador con las Consultas obtenidas
     * en la respuesta
     */
    public void getHorariosHTTP() {
        String newURL = Constantes.GET_HORARIOS_PARQUEADEROS + "?parqueadero_id=" + Preferences.getPreferenceString(getActivity(), Constantes.PREFERENCIA_PARQUEADERO_ID);
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
                                        procesarRespuestaHTTP(response);
                                        Log.i(TAG, "processanddo respuesta..." + response);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Utilidades.showToast(getActivity(), "Error al cargar los datos: " + error.toString());
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
                        JSONArray mensaje = response.getJSONArray("tbl_horarios");;
                        horarios = new ArrayList<>();
                        //llenar los parqueaderos de la lista
                        for(int i = 0; i < mensaje.length(); i++){
                            JSONObject object = (JSONObject) mensaje.get(i);
                            horarios.add(new Horarios(object.getString("id"),object.getString("parqueadero_id"), object.getString("diasemana"), object.getString("horaI"),object.getString("horaF")));
                            //Utilidades.showToast(this, "parking size-->" + object.getString("UbicacionLat"));
                        }

                         businessHoursList = new ArrayList<>();

                        //llenar los diferentes parqueaderos
                        for(int j = 0; j < horarios.size(); j++){
                            businessHoursList.add(new BusinessHours(horarios.get(j).getDiasemana(), horarios.get(j).getHoraI(), horarios.get(j).getHoraF()));
                        }

                        bh_view.setModel(businessHoursList);
                        bh_view.setVisibility(view.VISIBLE);
                        data_empty.setText("");
                        isHorario = true;

                    } catch (JSONException e) {
                        Log.i(TAG, "Error al llenar Adaptador " + e.getLocalizedMessage());
                    }
                    break;
                case "2":
                    String mensaje2 = response.getString("mensaje");
                    //loading.dismiss();
                    showSnackBar(mensaje2);
                    bh_view.setVisibility(view.GONE);
                    break;
            }
        } catch (JSONException je) {
            Log.d(TAG, je.getMessage());
        }
    }
}
