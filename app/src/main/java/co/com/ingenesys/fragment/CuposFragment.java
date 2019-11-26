package co.com.ingenesys.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import co.com.ingenesys.R;
import co.com.ingenesys.modelo.CuposParqueaderos;
import co.com.ingenesys.modelo.TableDynamic;
import co.com.ingenesys.modelo.TipoVehiculo;
import co.com.ingenesys.utils.Constantes;
import co.com.ingenesys.utils.Preferences;
import co.com.ingenesys.utils.Utilidades;
import co.com.ingenesys.web.VolleySingleton;

public class CuposFragment extends Fragment {

    //etiqueta para la depuracion
    private String TAG = CuposFragment.class.getSimpleName();

    private Spinner spinner_tipo_vehiculo;
    private ArrayList<TipoVehiculo> lista;
    private ArrayAdapter<TipoVehiculo> adapter;

    private EditText txtCupos;
    private Button btnGuardarCupos;

    private TableLayout table;
    private View view;
    private String[] header = {"Tipo Vehiculo", "Cupos", "Opciones"};//encabezado para la tabla dinamica
    private ArrayList<String[]> row = new ArrayList<>(); //agregamos las filas de la tabla
    private TableDynamic tableDynamic;
    private String tipovehiculo_id;
    private ArrayList<String> cupo_id = new ArrayList<>();

    private ArrayList<CuposParqueaderos> cupos;

    private ProgressDialog loading = null;

    //constructor
    public CuposFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_registrar_capacidad, container, false);

        init();
        return view;
    }

    /**
     * iniciar instancia**/
    private void init(){
        spinner_tipo_vehiculo = (Spinner) view.findViewById(R.id.spinner_tipo_vehiculo);
        txtCupos = (EditText) view.findViewById(R.id.txtCupos);
        btnGuardarCupos = (Button) view.findViewById(R.id.btnGuardarCupos);

        table = (TableLayout) view.findViewById(R.id.table);

        //crear la tabla dimanica
        tableDynamic = new TableDynamic(table, getContext());

        llenarTabla();


        llenarSpinner();

        spinner_tipo_vehiculo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                tipovehiculo_id = ((TipoVehiculo) item).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnGuardarCupos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validarDatosGUI()){
                    guardarCupos();
                }
            }
        });
    }



    /**
     * permite hacer una peticion HTTP y llenar el spinner
     * */
    private void llenarSpinner(){
        String newURL = Constantes.GET_ALL_TIPO_VEHICULO;
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
                                        respuestaHTTPSpinner(response);
                                        Log.i("TAG", "processanddo respuesta..." + response);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d("TAG", "Error Volley: " + error.toString());
                                    }
                                }

                        )
                );
    }

    private  void respuestaHTTPSpinner(JSONObject response) {
        try {
            // Obtener atributo "estado"
            String estado = response.getString("estado");
            switch (estado) {
                case "1":// EXITO
                    try {
                        // Obtener array "consulta" Json
                        JSONArray tbltipovehiculo = response.getJSONArray("tbl_tipovehiculos");
                        lista = new ArrayList<TipoVehiculo>();
                        lista.add(new TipoVehiculo("0", "=Seleccionar="));

                        for (int i = 0; i < tbltipovehiculo.length(); i++){
                            JSONObject object = (JSONObject) tbltipovehiculo.get(i);
                            lista.add(new TipoVehiculo(object.getString("id"), object.getString("nombre")));
                        }

                        //llenar el adaptador
                        if(lista != null){
                            adapter = new ArrayAdapter<TipoVehiculo>(getActivity(), android.R.layout.simple_spinner_item, lista);
                            spinner_tipo_vehiculo.setAdapter(adapter);
                        }else{
                            Log.i("TAG","Datos Vacios"+lista);
                        }

                    } catch (JSONException e) {
                        Log.i("TAG", "Error al llenar La Lista " + e.getLocalizedMessage());
                    }
                    break;
                case "2":
                    String mensaje2 = response.getString("mensaje");
                    Utilidades.showToast(getActivity(), mensaje2);
                    break;
            }
        } catch (JSONException je) {
            Log.d("TAG", je.getMessage());
        }
    }

    /**
     * Proyecta una {@link Snackbar} con el string usado
     *
     * @param msg Mensaje
     */
    private void showSnackBar(String msg) {
        Snackbar
                .make(view.findViewById(R.id.coordinator_cupos), msg, Snackbar.LENGTH_LONG)
                .show();
    }

    private void llenarTabla(){
        //Añadir parametros a la URL de webservice
        String parqueadero_id = Preferences.getPreferenceString(getActivity(), Constantes.PREFERENCIA_PARQUEADERO_ID);
        String newURL = Constantes.GET_CAPACIDADES_PARQUEADERO_ID + "?parqueadero_id=" + parqueadero_id;

        //petición GET
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
                                        procesarRespuesta(response);
                                        Log.i(TAG, "processanddo respuesta..." + response);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        //descartar el diálogo de progreso
                                        showSnackBar("Error de red: " + error.getLocalizedMessage());
                                        Log.d(TAG, "Error Volley: " + error.toString());
                                    }
                                }
                        )
                );
    }

    private void procesarRespuesta(JSONObject response){
        try {
            // Obtener atributo "estado"
            String estado = response.getString("estado");
            switch (estado){
                case "1":// EXITO
                    try {
                        // Obtener array "consulta" Json
                        JSONArray datos_cupos = response.getJSONArray("tbl_capacidades");
                        cupos = new ArrayList<>();

                        for (int i = 0; i < datos_cupos.length(); i++){
                            JSONObject object = (JSONObject) datos_cupos.get(i);
                            cupos.add(new CuposParqueaderos(object.getString("id"), object.getString("tipoVehiculo_id"), object.getString("nombre"), object.getString("cupos")));
                        }

                        //llenar datos de la tabla
                        for (int j = 0; j < cupos.size(); j++){
                            row.add(new String[]{cupos.get(j).getNombre(), cupos.get(j).getCupos(), "Eliminar, Editar"});
                            cupo_id.add(cupos.get(j).getTipoVehiculo_id());
                        }

                        tableDynamic.addHeader(header);//agrego la cabecera
                        tableDynamic.addData(row);//agrego las filas
                        tableDynamic.backgroundHeader(getActivity().getResources().getColor(R.color.colorDark));
                        tableDynamic.backgroundData(getActivity().getResources().getColor(R.color.colorSecondColor), getActivity().getResources().getColor(R.color.colorFirstColor));
                        tableDynamic.lineColor(getActivity().getResources().getColor(R.color.colorBlanco));
                        tableDynamic.textColorHeader(getActivity().getResources().getColor(R.color.colorBlanco));
                        tableDynamic.textColorData(getActivity().getResources().getColor(R.color.colorBlanco));


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
     * Guarda los cambios de una meta editada.
     * <p>
     * Si está en modo inserción, entonces crea una nueva
     * meta en la base de datos
     */
    private void guardarCupos() {
        //mostrar el diálogo de progreso
        loading = ProgressDialog.show(getContext(),"guardando...","Espere por favor...",false,false);

        String parqueadero_id = Preferences.getPreferenceString(getContext(), Constantes.PREFERENCIA_PARQUEADERO_ID);
        String cupos = txtCupos.getText().toString().trim();


        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();// Mapeo previo
        map.put("parqueadero_id",parqueadero_id);
        map.put("tipovehiculo_id", tipovehiculo_id);
        map.put("cupos", cupos);

        // Crear nuevo objeto Json basado en el mapa
        JSONObject jobject = new JSONObject(map);
        // Depurando objeto Json...
        Log.i(TAG, "map.." + map.toString());
        Log.d(TAG, "json productor..."+jobject);

        // Actualizar datos en el servidor
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        Constantes.INSERTAR_CAPACIDADES,
                        jobject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.i(TAG, "Ver Response-->"+response);
                                // Procesar la respuesta del servidor
                                procesarRespuestaInsertar(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //descartar el diálogo de progreso
                                loading.dismiss();
                                Log.e(TAG, "Error Volley: " + error.getLocalizedMessage());
                                showSnackBar("Error Volley: " + error.getMessage());
                            }
                        }

                ) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        headers.put("Accept", "application/json");
                        return headers;
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8" + getParamsEncoding();
                    }
                }
        );

    }

    /**
     * Procesa la respuesta obtenida desde el sevidor
     *
     * @param response Objeto Json
     */
    private void procesarRespuestaInsertar(JSONObject response) {

        try {
            // Obtener estado
            String estado = response.getString("estado");
            // Obtener mensaje
            String mensaje = response.getString("mensaje");

            switch (estado) {
                case "1":
                    //descartar el diálogo de progreso
                    loading.dismiss();
                    llenarTabla();
                    // Mostrar mensaje
                    showSnackBar(mensaje);
                    break;

                case "2":
                    //descartar el diálogo de progreso
                    loading.dismiss();
                    // Mostrar mensaje
                    showSnackBar(mensaje);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean validarDatosGUI(){
        boolean estado = false;
        if(spinner_tipo_vehiculo.getSelectedItemPosition() == 0){
            showSnackBar("Debe seleccionar un tipo de vehiculo");
            spinner_tipo_vehiculo.performClick();
        }else{
            if(txtCupos.getText().toString().trim().isEmpty()){
                showSnackBar("Debe Agregar el numero de cupos");
                txtCupos.requestFocus();
            }else{
                /*if(cupo_id.size()> 0){
                    for(int i = 0; i < cupo_id.size(); i++){
                        if(tipovehiculo_id.equalsIgnoreCase(cupo_id.get(i))){
                            showSnackBar("ya existen cupos asignados para este tipo de vehiculo");
                            spinner_tipo_vehiculo.performClick();
                        }
                    }
                }else{
                    estado = true;
                }*/
                estado = true;
            }
        }

        return estado;
    }
}
