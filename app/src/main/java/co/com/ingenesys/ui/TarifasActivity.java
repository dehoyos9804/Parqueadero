package co.com.ingenesys.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.LinearGradient;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

import co.com.ingenesys.R;
import co.com.ingenesys.modelo.Parqueaderos;
import co.com.ingenesys.modelo.Tarifa;
import co.com.ingenesys.utils.Constantes;
import co.com.ingenesys.utils.Preferences;
import co.com.ingenesys.utils.Utilidades;
import co.com.ingenesys.web.VolleySingleton;

public class TarifasActivity extends AppCompatActivity {

    //etiqueta para la depuracion
    private static final String TAG = TarifasActivity.class.getSimpleName();
    private Gson gson = new Gson();
    private CollapsingToolbarLayout collapser;
    private String parqueadero_id;
    private String horaI;
    private String horaF;

    private ArrayList<Tarifa> item;

    private ViewGroup layout_grupo;
    private TextView txtHoraI;
    private TextView txtHoraF;
    private ImageView icono_tipo_vehiculo;
    private TextView txtTipoVehiculo;
    private TextView txtPrecioTiempo;

    /**
     * Inicia una nueva instancia de la actividad
     *
     * @param activity Contexto desde donde se lanzará
     * @param consecutivo   Identificador de las consultas a detallar
     */
    public static void launch(Activity activity, String consecutivo, String horaI, String horaF) {
        Intent intent = getLaunchIntent(activity, consecutivo, horaI, horaF);
        activity.startActivityForResult(intent, Constantes.PARQUEADERO);
    }

    /**
     * Construye un Intent a partir del contexto y la actividad
     * de detalle.
     *
     * @param context Contexto donde se inicia
     * @param consecutivo  Identificador de las consultas
     * @return Intent listo para usar
     */
    public static Intent getLaunchIntent(Context context, String consecutivo, String horaI, String horaF){
        Intent intent = new Intent(context, TarifasActivity.class);
        intent.putExtra(Constantes.EXTRA_PARQUEADERO_ID, consecutivo);
        intent.putExtra(Constantes.EXTRA_HORA_INICIAL, horaI);
        intent.putExtra(Constantes.EXTRA_HORA_FINAL, horaF);
        return intent;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarifas);

        setToolbar();
        // Retener instancia
        if(getIntent().getStringExtra(Constantes.EXTRA_PARQUEADERO_ID) != null && getIntent().getStringExtra(Constantes.EXTRA_HORA_INICIAL) != null && getIntent().getStringExtra(Constantes.EXTRA_HORA_FINAL) != null){
            parqueadero_id = getIntent().getStringExtra(Constantes.EXTRA_PARQUEADERO_ID);
            horaI = getIntent().getStringExtra(Constantes.EXTRA_HORA_INICIAL);
            horaF = getIntent().getStringExtra(Constantes.EXTRA_HORA_FINAL);
        }
        init();//iniciar componentes
    }

    private void setToolbar() {
        // Añadir la Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void init(){
        if(getSupportActionBar()!=null)//habilitar up button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapser = (CollapsingToolbarLayout) findViewById(R.id.collapser_detalle);
        collapser.setTitle("Tarifa"); // Cambiar título

        txtHoraI = (TextView) findViewById(R.id.txtHoraI);
        txtHoraF = (TextView) findViewById(R.id.txtHoraF);

        txtHoraI.setText(horaI);
        txtHoraF.setText(horaF);

        layout_grupo = (ViewGroup) findViewById(R.id.layer_tarifa);
        getHTTP();
    }

    /*
     * Carga el adaptador con las Consultas obtenidas
     * en la respuesta
     */
    public void getHTTP() {
        String newURL = Constantes.GET_TARIFAS_PARQUEADEROS + "?parqueadero_id=" + parqueadero_id;
        // Petición GET
        VolleySingleton.
                getInstance(this).
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
                        item = new ArrayList<>();
                        //llenar los parqueaderos de la lista
                        for(int i = 0; i < mensaje.length(); i++){
                            JSONObject object = (JSONObject) mensaje.get(i);
                            item.add(new Tarifa(object.getString("id"),object.getString("tipoTiempo"), object.getString("precio"), object.getString("nombre")));
                        }

                        for (int j = 0; j < item.size(); j++){
                            LayoutInflater inflater = LayoutInflater.from(this);
                            int id = R.layout.layout_detalle_tarifa;
                            LinearLayout linearLayout = (LinearLayout) inflater.inflate(id,null, false);

                            //layer_tarifa = (LinearLayout) linearLayout.findViewById(R.id.layer_tarifa);
                            icono_tipo_vehiculo = (ImageView) linearLayout.findViewById(R.id.icono_tipo_vehiculo);
                            txtTipoVehiculo = (TextView) linearLayout.findViewById(R.id.txtTipoVehiculo);
                            txtPrecioTiempo = (TextView) linearLayout.findViewById(R.id.txtPrecioTiempo);

                            txtTipoVehiculo.setText(item.get(j).getNombre());
                            txtPrecioTiempo.setText(("$" + item.get(j).getPrecio() + " " + item.get(j).getTipoTiempo()));

                            layout_grupo.addView(linearLayout);
                        }


                    } catch (JSONException e) {
                        Log.i(TAG, "Error al llenar Adaptador " + e.getLocalizedMessage());
                    }
                    break;
                case "2":
                    String mensaje2 = response.getString("mensaje");
                    //loading.dismiss();
                    Utilidades.showToast(this, mensaje2);
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
                .make(findViewById(R.id.coordinator), msg, Snackbar.LENGTH_LONG)
                .show();
    }

}
