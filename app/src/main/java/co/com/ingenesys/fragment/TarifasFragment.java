package co.com.ingenesys.fragment;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import android.widget.ImageView;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import co.com.ingenesys.R;
import co.com.ingenesys.adapter.AdaptadorTarifas;
import co.com.ingenesys.modelo.Tarifa;
import co.com.ingenesys.modelo.TipoVehiculo;
import co.com.ingenesys.ui.InitialActivity;
import co.com.ingenesys.ui.TarifasActivity;
import co.com.ingenesys.utils.Constantes;
import co.com.ingenesys.utils.Preferences;
import co.com.ingenesys.utils.Utilidades;
import co.com.ingenesys.web.VolleySingleton;

public class TarifasFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = TarifasFragment.class.getSimpleName();
    private View view;
    //instaciar
    //adaptador del RecicleView
    private AdaptadorTarifas adapter;
    //instancia global de RecicleView
    private RecyclerView recyclerView;
    //private TextView emptyView;
    //instancia global del administrador
    private RecyclerView.LayoutManager lManager;
    private TextView emptyView;
    private Gson gson = new Gson();

    //atributos del bottom sheet
    private BottomSheetBehavior sheetBehavior;
    private LinearLayout bottom_sheet;
    private Spinner spinner_tipo_tiempo;
    private Spinner spinner_tipo_vehiculo;
    private ArrayList<TipoVehiculo> lista;
    private ArrayAdapter<TipoVehiculo> adapter_spinner_tipo_vehiculo;
    private EditText txtPrecio;
    private Button btnCancelar;
    private Button btnRegistrar;
    //private Button btn_cerrar_button_sheet;

    private String tipovehiculo_id;

    private FloatingActionButton fab;

    private ProgressDialog loading = null;
    //fragmento para el contenido principal
    public TarifasFragment(){

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tarifas, container, false);

        init();

        return view;
    }

    //instacion todas los elementos xml
    private void init(){
        recyclerView = (RecyclerView) view.findViewById(R.id.reciclador_tarifas);
        recyclerView.setHasFixedSize(true);

        //Usar el Administrador para LinearLayout
        lManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(lManager);
        emptyView = (TextView) view.findViewById(R.id.recyclerview_data_empty);

        bottom_sheet = (LinearLayout) view.findViewById(R.id.bottom_shent);
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        bottom_sheet.setVisibility(View.GONE);//oculto el bottom sheet
        spinner_tipo_tiempo = (Spinner) bottom_sheet.findViewById(R.id.spinner_tipo_tiempo);
        spinner_tipo_vehiculo = (Spinner) bottom_sheet.findViewById(R.id.spinner_tipo_vehiculo);
        txtPrecio = (EditText) bottom_sheet.findViewById(R.id.txtPrecio);
        btnCancelar = (Button) bottom_sheet.findViewById(R.id.btnCancelar);
        btnRegistrar = (Button) bottom_sheet.findViewById(R.id.btnRegistrar);

        //lleno el spinner
        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(getActivity(),R.array.tipo_tiempo ,android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_tipo_tiempo.setAdapter(arrayAdapter);
        llenarSpinner();//llenar spinner tipo vehicuo

        //colocar oculto el botton sheet
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bottom_sheet.setVisibility(View.GONE);//oculto el botton sheet
                }
            }
        });

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        //escuchadores
        fab.setOnClickListener(this);


        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        bottom_sheet.setVisibility(View.VISIBLE);//colocando visible el botton sheet
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        bottom_sheet.setVisibility(View.GONE);//oculto el botton sheet
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        if(sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            bottom_sheet.setVisibility(View.GONE);//oculto el botton sheet
        }

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

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                bottom_sheet.setVisibility(View.VISIBLE);//coloco visible el bottom sheet
                if(sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    //disparar el evento de ver rutas
                    btnRegistrar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(validarDatosGUI()){
                                guardarTarifa();
                            }
                        }
                    });
                }
                break;
        }
    }

    private boolean validarDatosGUI(){
        boolean estado = false;
        if(spinner_tipo_tiempo.getSelectedItemPosition() == 0){
            showSnackBar("selecciona el tipo de tiempo");
            spinner_tipo_tiempo.performClick();
        }else{
            if(spinner_tipo_vehiculo.getSelectedItemPosition() == 0){
                showSnackBar("seleccione el tipo de vehiculo");
                spinner_tipo_vehiculo.performClick();
            }else{
                if(txtPrecio.getText().toString().trim().isEmpty()){
                    showSnackBar("agregue el precio de la tarifa");
                    txtPrecio.requestFocus();
                }else{
                    estado = true;
                }
            }
        }
        return estado;
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
        String newURL = Constantes.GET_TARIFAS_PARQUEADEROS + "?parqueadero_id=" + parqueadero_id;
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
                                        emptyView.setText("cargando...");
                                        procesarRespuestaHTTP(response);
                                        Log.i(TAG, "processanddo respuesta..." + response);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        emptyView.setText(getActivity().getResources().getString(R.string.empty_list));
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
                        JSONArray mensaje = response.getJSONArray("tbl_parqueaderos");;
                        // Parsear con Gson
                        Tarifa[] tarifas = gson.fromJson(mensaje.toString(), Tarifa[].class);

                        // Inicializar adaptador
                        adapter = new AdaptadorTarifas(Arrays.asList(tarifas), getContext());

                        // Setear adaptador a la lista
                        recyclerView.setAdapter(adapter);
                        emptyView.setText("");

                    } catch (JSONException e) {
                        Log.i(TAG, "Error al llenar Adaptador " + e.getLocalizedMessage());
                    }
                    break;
                case "2":
                    String mensaje2 = response.getString("mensaje");
                    //loading.dismiss();
                    emptyView.setText(getActivity().getResources().getString(R.string.empty_list));
                    showSnackBar(mensaje2);
                    break;
                case "3":
                    String mensaje3 = response.getString("mensaje");
                    //loading.dismiss();
                    emptyView.setText(getActivity().getResources().getString(R.string.empty_list));
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
                .make(view.findViewById(R.id.coordinator_tarifas), msg, Snackbar.LENGTH_LONG)
                .show();
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
                            adapter_spinner_tipo_vehiculo = new ArrayAdapter<TipoVehiculo>(getActivity(), android.R.layout.simple_spinner_item, lista);
                            spinner_tipo_vehiculo.setAdapter(adapter_spinner_tipo_vehiculo);
                        }else{
                            Log.i("TAG","Datos Vacios"+lista);
                        }

                    } catch (JSONException e) {
                        Log.i("TAG", "Error al llenar La Lista " + e.getLocalizedMessage());
                    }
                    break;
                case "2":
                    String mensaje2 = response.getString("mensaje");
                    break;
            }
        } catch (JSONException je) {
            Log.d("TAG", je.getMessage());
        }
    }

    /**
     * Guarda los datos en la tabla tarifa.
     * <p>
     * Si está en modo inserción, entonces crea una nueva
     * meta en la base de datos
     */
    private void guardarTarifa() {
        //mostrar el diálogo de progreso
        loading = ProgressDialog.show(getContext(),"guardando...","Espere por favor...",false,false);

        String parqueadero_id = Preferences.getPreferenceString(getContext(), Constantes.PREFERENCIA_PARQUEADERO_ID);
        String tipo_tiempo = spinner_tipo_tiempo.getSelectedItem().toString();
        String precio = txtPrecio.getText().toString().trim();


        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();// Mapeo previo
        map.put("parqueadero_id",parqueadero_id);
        map.put("tipo_tiempo", tipo_tiempo);
        map.put("precio", precio);
        map.put("tipovehiculo_id", tipovehiculo_id);

        // Crear nuevo objeto Json basado en el mapa
        JSONObject jobject = new JSONObject(map);
        // Depurando objeto Json...
        Log.i(TAG, "map.." + map.toString());
        Log.d(TAG, "json productor..."+jobject);

        // Actualizar datos en el servidor
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        Constantes.INSERTAR_TARIFAS,
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
                    // Mostrar mensaje
                    showSnackBar(mensaje);
                    if(sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
                        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        bottom_sheet.setVisibility(View.GONE);//oculto el botton sheet
                    }
                    getHTTP();
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
}
