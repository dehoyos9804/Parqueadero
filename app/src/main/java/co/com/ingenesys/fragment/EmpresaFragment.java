package co.com.ingenesys.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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
import co.com.ingenesys.modelo.Empresas;
import co.com.ingenesys.modelo.TableDynamic;
import co.com.ingenesys.modelo.TableDynamicEmpresas;
import co.com.ingenesys.utils.Constantes;
import co.com.ingenesys.utils.Preferences;
import co.com.ingenesys.web.VolleySingleton;

public class EmpresaFragment extends Fragment implements View.OnClickListener {

    private String TAG = EmpresaFragment.class.getSimpleName();

    private EditText txtNit;
    private EditText txtRazonSocial;
    private EditText txtTelefonoEmpresa;
    private EditText txtDireccionEmpresa;
    private Button btnRegistrar;
    private ProgressDialog loading = null;

    private TableLayout table_empresa;
    private String[] header = {"Nit", "Razon Social", "Direccion", "Telefono", "Operations"};//encabezado para la tabla dinamica
    private ArrayList<String[]> row = new ArrayList<>(); //agregamos las filas de la tabla
    private TableDynamicEmpresas tableDynamic;
    private ArrayList<String> empresa_id = new ArrayList<>();
    private ArrayList<Empresas> empresas;

    private View view;

    //Constructor
    public EmpresaFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_registro_empresas, container, false);

        init();
        return view;
    }

    private void init(){
        txtNit = (EditText) view.findViewById(R.id.txtNit);
        txtRazonSocial = (EditText) view.findViewById(R.id.txtRazonSocial);
        txtTelefonoEmpresa = (EditText) view.findViewById(R.id.txtTelefonoEmpresa);
        txtDireccionEmpresa = (EditText) view.findViewById(R.id.txtDireccionEmpresa);
        btnRegistrar = (Button) view.findViewById(R.id.btnRegistrar);

        table_empresa = (TableLayout) view.findViewById(R.id.table_empresa);

        //crear la tabla dimanica
        tableDynamic = new TableDynamicEmpresas(table_empresa, getContext());

        btnRegistrar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnRegistrar:
                if (validarDatosGUI()){
                    guardarEmpresas();
                }
                break;
        }
    }

    private void limpiar(){
        txtNit.setText("");
        txtRazonSocial.setText("");
        txtTelefonoEmpresa.setText("");
        txtDireccionEmpresa.setText("");
    }

    /**
     * Proyecta una {@link Snackbar} con el string usado
     *
     * @param msg Mensaje
     */
    private void showSnackBar(String msg) {
        Snackbar
                .make(view.findViewById(R.id.coordinator_empresa), msg, Snackbar.LENGTH_LONG)
                .show();
    }

    private boolean validarDatosGUI(){
        boolean estado = false;
        if (txtNit.getText().toString().trim().isEmpty()){
            showSnackBar("campo Nit vacio");
            txtNit.requestFocus();
        }else{
            if (txtRazonSocial.getText().toString().trim().isEmpty()) {
                showSnackBar("campo Razon Social vacio");
                txtRazonSocial.requestFocus();
            }else{
                if (txtTelefonoEmpresa.getText().toString().trim().isEmpty()) {
                    showSnackBar("campo Telefono vacio");
                    txtTelefonoEmpresa.requestFocus();
                }else{
                    if (txtDireccionEmpresa.getText().toString().trim().isEmpty()) {
                        showSnackBar("campo Direccion vacio");
                        txtDireccionEmpresa.requestFocus();
                    }else{
                        estado = true;
                    }
                }
            }
        }
        return estado;
    }

    private void llenarTabla(){
        //Añadir parametros a la URL de webservice
        String parqueadero_id = Preferences.getPreferenceString(getActivity(), Constantes.PREFERENCIA_PARQUEADERO_ID);
        String newURL = Constantes.GET_EMPRESA_PARQUEADERO_ID + "?parqueadero_id=" + parqueadero_id;

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
                        JSONArray datos_cupos = response.getJSONArray("tbl_empresas");
                        empresas = new ArrayList<>();

                        for (int i = 0; i < datos_cupos.length(); i++){
                            JSONObject object = (JSONObject) datos_cupos.get(i);
                            empresas.add(new Empresas(object.getString("nit"), object.getString("razonSocial"), object.getString("direccion"), object.getString("telefono")));
                        }

                        //llenar datos de la tabla
                        for (int j = 0; j < empresas.size(); j++){
                            row.add(new String[]{empresas.get(j).getNit(), empresas.get(j).getRazonSocial(), empresas.get(j).getDireccion(), empresas.get(j).getTelefono()});
                        }

                        tableDynamic.addHeader(header);//agrego la cabecera
                        tableDynamic.addData(row);//agrego las filas
                        tableDynamic.backgroundHeader(getActivity().getResources().getColor(R.color.colorPrimary));
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

    @Override
    public void onResume() {
        super.onResume();
        llenarTabla();
    }

    /**
     * Guarda los cambios de una meta editada.
     * <p>
     * Si está en modo inserción, entonces crea una nueva
     * meta en la base de datos
     */
    private void guardarEmpresas() {
        //mostrar el diálogo de progreso
        loading = ProgressDialog.show(getContext(),"guardando...","Espere por favor...",false,false);

        String nit = txtNit.getText().toString().trim();
        String razonsocial = txtRazonSocial.getText().toString().trim();
        String direccion = txtDireccionEmpresa.getText().toString().trim();
        String telefono = txtTelefonoEmpresa.getText().toString().intern();
        String parqueadero_id = Preferences.getPreferenceString(getContext(), Constantes.PREFERENCIA_PARQUEADERO_ID);

        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();// Mapeo previo
        map.put("nit",nit);
        map.put("razonsocial", razonsocial);
        map.put("direccion", direccion);
        map.put("telefono", telefono);
        map.put("parqueadero_id", parqueadero_id);

        // Crear nuevo objeto Json basado en el mapa
        JSONObject jobject = new JSONObject(map);
        // Depurando objeto Json...
        Log.i(TAG, "map.." + map.toString());
        Log.d(TAG, "json productor..."+jobject);

        // Actualizar datos en el servidor
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        Constantes.INSERT_CONVENIOS,
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
                    onResume();
                    // Mostrar mensaje
                    showSnackBar(mensaje);
                    limpiar();
                    break;

                case "2":
                    //descartar el diálogo de progreso
                    loading.dismiss();
                    // Mostrar mensaje
                    showSnackBar(mensaje);
                    break;
                case "3":
                    //descartar el diálogo de progreso
                    loading.dismiss();
                    // Mostrar mensaje
                    showSnackBar(mensaje);
                    limpiar();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
