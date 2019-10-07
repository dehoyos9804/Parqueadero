package co.com.ingenesys.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

import co.com.ingenesys.R;
import co.com.ingenesys.modelo.TipoVehiculo;
import co.com.ingenesys.ui.InitialActivity;
import co.com.ingenesys.utils.Constantes;
import co.com.ingenesys.utils.Preferences;
import co.com.ingenesys.utils.Utilidades;
import co.com.ingenesys.web.VolleySingleton;

public class DialogoReservar {
    /**
     * Método que permite crear el dialogo de para finalizar la cita
     **/
    public static void showDialogReservar(final Activity activity, final String parqueadero_id, String nombreParqueadero, String tiempo_llegada){
        final Dialog d = new Dialog(activity);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setCancelable(false);
        d.setContentView(R.layout.dialog_reservar_cupo);

        //instancias
        TextView txtNombreParqueadero = (TextView) d.findViewById(R.id.txtNombreParqueadero);
        TextView txtTiempoLlegadaReserva = (TextView) d.findViewById(R.id.txtTiempoLlegadaReserva);
        Spinner spinner_tipo_vehiculo = (Spinner) d.findViewById(R.id.spinner_tipo_vehiculo);
        TextView txtFechaReserva = (TextView) d.findViewById(R.id.txtFechaReserva);
        TextView txtHoraReserva = (TextView) d.findViewById(R.id.txtHoraReserva);
        EditText txtcedulaUsuario = (EditText) d.findViewById(R.id.txtcedulaUsuario);
        EditText txtUsuarioN = (EditText) d.findViewById(R.id.txtUsuarioN);

        Button btnCancelar = (Button) d.findViewById(R.id.btnCancelar);
        Button btnReservacion = (Button) d.findViewById(R.id.btnReservacion);

        txtcedulaUsuario.setText(Preferences.getPreferenceString(activity, Constantes.PREFERENCIA_CEDULA_CLAVE));
        txtUsuarioN.setText((Preferences.getPreferenceString(activity, Constantes.PREFERENCIA_NOMBRE_CLAVE) + " " + Preferences.getPreferenceString(activity, Constantes.PREFERENCIA_APELLIDO_CLAVE)));

        //colocar la fecha y hora actual
        Calendar localCalendar = Calendar.getInstance(TimeZone.getDefault());

        //obtener la fecha y hora actual
        SimpleDateFormat fechaactual = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat horaActual = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat diaSemana = new SimpleDateFormat("EEEE");
        Date date = new Date();
        String dayofTheWeek = diaSemana.format(date);
        String formatoFecha = fechaactual.format(localCalendar.getTime());
        String formatoHora = horaActual.format(localCalendar.getTime());

        final String fecha_hora_actual = formatoFecha + " " + formatoHora;
        //final String tipo_vehiculo_id;

        int currenAp = localCalendar.get(Calendar.AM_PM);
        String meridiano = "";
        if(currenAp != 0){
            meridiano = "P.M";
        }else{
            meridiano = "A.M";
        }

        txtNombreParqueadero.setText(nombreParqueadero);
        txtFechaReserva.setText(formatoFecha);
        txtHoraReserva.setText(formatoHora + meridiano);

        llenarSpinner(spinner_tipo_vehiculo, activity);


        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();//finalizo el dialogo
            }
        });

        btnReservacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogo = new AlertDialog.Builder(activity);
                dialogo.setTitle("Reservar");
                dialogo.setMessage("¿Esta seguro que quieres reservar");
                dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //mostrar el diálogo de progreso
                        final ProgressDialog progress = ProgressDialog.show(activity,"guardando...","Espere por favor...",false,false);

                        String usuario_id = Preferences.getPreferenceString(activity, Constantes.PREFERENCIA_IDUSUARIO_CLAVE);

                        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();// Mapeo previo
                        map.put("usuario_id",usuario_id);
                        map.put("parqueadero_id", parqueadero_id);
                        map.put("fecha", fecha_hora_actual);
                        map.put("tiempo_llegada", "25");
                        map.put("tipo_vehiculo_id", "1");

                        // Crear nuevo objeto Json basado en el mapa
                        JSONObject jobject = new JSONObject(map);
                        // Depurando objeto Json...
                        Log.i("TAG", "map.." + map.toString());
                        Log.d("TAG", "json productor..."+jobject);

                        // Actualizar datos en el servidor
                        VolleySingleton.getInstance(activity).addToRequestQueue(
                                new JsonObjectRequest(
                                        Request.Method.POST,
                                        Constantes.INSERT_NEW_RESERVA,
                                        jobject,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    // Obtener estado
                                                    String estado = response.getString("estado");
                                                    // Obtener mensaje
                                                    String mensaje = response.getString("mensaje");

                                                    switch (estado) {
                                                        case "1":
                                                            //descartar el diálogo de progreso
                                                            progress.dismiss();
                                                            // Mostrar mensaje
                                                            Utilidades.showToast(activity, mensaje);
                                                            //limpiar();
                                                            // Terminar actividad
                                                            d.dismiss();
                                                            break;

                                                        case "2":
                                                            //descartar el diálogo de progreso
                                                            progress.dismiss();
                                                            // Mostrar mensaje
                                                            Utilidades.showToast(activity, mensaje);
                                                            d.dismiss();
                                                            // Enviar código de falla
                                                            //limpiar();
                                                            break;
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                //descartar el diálogo de progreso
                                                progress.dismiss();
                                                Log.e("TAG", "Error Volley: " + error.getLocalizedMessage());
                                                //showSnackBar("Error Volley: " + error.getMessage());
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
                });
                dialogo.show();
            }
        });

        d.show();//abro el dialogo
    }


    /**
     * permite hacer una peticion HTTP y llenar el spinner
     * */
    private static void llenarSpinner(final Spinner spinner, final Activity activity){
        String newURL = Constantes.GET_ALL_TIPO_VEHICULO;
        // Petición GET
        VolleySingleton.
                getInstance(activity).
                addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                newURL,
                                null,
                                new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {
                                        // Procesar la respuesta Json
                                        respuestaHTTPSpinner(response, spinner, activity);
                                        Log.i("TAG", "processanddo respuesta..." + response);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        //loading.dismiss();
                                        //Utilidades.showToast(AgendarCitaActivity.this, "Error al cargar los datos: " + error.toString());
                                        Log.d("TAG", "Error Volley: " + error.toString());
                                    }
                                }

                        )
                );
    }

    private static void respuestaHTTPSpinner(JSONObject response, Spinner spinner, Activity activity) {
        try {
            // Obtener atributo "estado"
            String estado = response.getString("estado");
            switch (estado) {
                case "1":// EXITO
                    try {
                        // Obtener array "consulta" Json
                        JSONArray tbltipovehiculo = response.getJSONArray("tbl_tipovehiculos");
                        ArrayList<TipoVehiculo> lista = new ArrayList<TipoVehiculo>();
                        //lista.add(new TipoVehiculo("0", "Seleccionar Temas"));

                        for (int i = 0; i < tbltipovehiculo.length(); i++){
                            JSONObject object = (JSONObject) tbltipovehiculo.get(i);
                            lista.add(new TipoVehiculo(object.getString("id"), object.getString("nombre")));
                        }

                        //llenar el adaptador
                        if(lista != null){
                            ArrayAdapter<TipoVehiculo> adapter = new ArrayAdapter<TipoVehiculo>(activity, android.R.layout.simple_spinner_item, lista);
                            spinner.setAdapter(adapter);
                        }else{
                            Log.i("TAG","Datos Vacios"+lista);
                        }


                    } catch (JSONException e) {
                        Log.i("TAG", "Error al llenar La Lista " + e.getLocalizedMessage());
                    }
                    break;
                case "2":
                    String mensaje2 = response.getString("mensaje");
                    //loading.dismiss();
                    Utilidades.showToast(activity, mensaje2);
                    break;
            }
        } catch (JSONException je) {
            Log.d("TAG", je.getMessage());
        }
    }
}
