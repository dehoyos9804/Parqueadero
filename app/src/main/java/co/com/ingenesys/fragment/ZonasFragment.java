package co.com.ingenesys.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import co.com.ingenesys.R;
import co.com.ingenesys.modelo.CuposParqueaderos;
import co.com.ingenesys.modelo.TableDynamic;
import co.com.ingenesys.modelo.TableDynamicZonas;
import co.com.ingenesys.modelo.TipoVehiculo;
import co.com.ingenesys.modelo.Zonas;
import co.com.ingenesys.ui.InitialActivity;
import co.com.ingenesys.ui.InitialAdministradorActivity;
import co.com.ingenesys.utils.Constantes;
import co.com.ingenesys.utils.Preferences;
import co.com.ingenesys.utils.Utilidades;
import co.com.ingenesys.web.VolleySingleton;

public class ZonasFragment  extends Fragment {

    //etiqueta para la depuracion
    private static final String TAG = ZonasFragment.class.getSimpleName();

    private View view;
    private TableLayout table;
    private ArrayList<String[]> row = new ArrayList<>();
    private TableDynamicZonas tableDynamic;
    private ArrayList<Zonas> zonas;
    //constructor
    public ZonasFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_zonas, container, false);

        init();
        return view;
    }

    /**
     * iniciar instancia**/
    private void init(){
        table = (TableLayout) view.findViewById(R.id.table_zonas);

        //crear la tabla dimanica
        tableDynamic = new TableDynamicZonas(table, getContext());

        llenarTabla(getContext(), zonas, tableDynamic, row);
    }

    public static void llenarTabla(final Context context, final ArrayList<Zonas> zonas, final TableDynamicZonas tableDynamic, final ArrayList<String[]> row){
        //Añadir parametros a la URL de webservice
        String parqueadero_id = Preferences.getPreferenceString(context, Constantes.PREFERENCIA_PARQUEADERO_ID);
        String newURL = Constantes.GET_ALL_ZONAS + "?parqueadero_id=" + parqueadero_id;

        //petición GET
        VolleySingleton.
                getInstance(context).
                addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                newURL,
                                null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        // Procesar la respuesta Json
                                        procesarRespuesta(response, zonas, tableDynamic, row);
                                        Log.i(TAG, "processanddo respuesta..." + response);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        //descartar el diálogo de progreso
                                        Utilidades.showToast((Activity) context, "Error de red: " + error.getLocalizedMessage());
                                        Log.d(TAG, "Error Volley: " + error.toString());
                                    }
                                }
                        )
                );
    }

    private static void procesarRespuesta(JSONObject response, ArrayList<Zonas> zonas, TableDynamicZonas tableDynamic, ArrayList<String[]> row){
        try {
            // Obtener atributo "estado"
            String estado = response.getString("estado");
            switch (estado){
                case "1":// EXITO
                    try {
                        // Obtener array "consulta" Json
                        JSONArray datos_zonas = response.getJSONArray("tbl_capacidades");
                        zonas = new ArrayList<>();

                        for (int i = 0; i < datos_zonas.length(); i++){
                            JSONObject object = (JSONObject) datos_zonas.get(i);
                            zonas.add(new Zonas(object.getString("id"), object.getString("numero_zona"), object.getString("estado"), object.getString("capacidad_id")));
                        }

                        //llenar datos de la tabla
                        for (int j = 0; j < zonas.size(); j++){
                            row.add(new String[]{zonas.get(j).getId(), zonas.get(j).getNumero_zona(), zonas.get(j).getEstado(), zonas.get(j).getCapacidad_id()});
                        }

                        tableDynamic.addData(row);//agrego las filas
                        //tableDynamic.lineColor(getActivity().getResources().getColor(R.color.colorDark));


                    }catch (JSONException e){
                        Log.i(TAG,"Error al llenar Adaptador " +e.getLocalizedMessage());
                    }
                    break;
                case "2":
                    String mensaje2 = response.getString("mensaje");
                    //showSnackBar(mensaje2);
                    break;
                case "3":
                    String mensaje3 = response.getString("mensaje");
                    break;
            }
        }catch (JSONException je){
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
                .make(view.findViewById(R.id.coordinator_zonas), msg, Snackbar.LENGTH_LONG)
                .show();
    }
}
