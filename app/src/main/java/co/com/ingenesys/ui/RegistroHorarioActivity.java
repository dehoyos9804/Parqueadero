package co.com.ingenesys.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import co.com.ingenesys.R;
import co.com.ingenesys.utils.Constantes;
import co.com.ingenesys.utils.Preferences;
import co.com.ingenesys.utils.Utilidades;
import co.com.ingenesys.web.VolleySingleton;

public class RegistroHorarioActivity extends AppCompatActivity implements View.OnClickListener {

    //etiqueta para la depuracion
    private static final String TAG = RegistroHorarioActivity.class.getSimpleName();

    private ProgressDialog loading = null;
    private Gson gson = new Gson();

    private TextView data_empty;
    private FloatingActionButton fab_usuario;

    private LinearLayout lyt_hours_1;
    private LinearLayout lyt_hours_2;
    private LinearLayout lyt_hours_3;
    private LinearLayout lyt_hours_4;
    private LinearLayout lyt_hours_5;
    private LinearLayout lyt_hours_6;
    private LinearLayout lyt_hours_7;

    private SwitchCompat switch_open_1;
    private SwitchCompat switch_open_2;
    private SwitchCompat switch_open_3;
    private SwitchCompat switch_open_4;
    private SwitchCompat switch_open_5;
    private SwitchCompat switch_open_6;
    private SwitchCompat switch_open_7;

    private AppCompatSpinner spin_bh_from_1, spin_bh_to_1;
    private AppCompatSpinner spin_bh_from_2, spin_bh_to_2;
    private AppCompatSpinner spin_bh_from_3, spin_bh_to_3;
    private AppCompatSpinner spin_bh_from_4, spin_bh_to_4;
    private AppCompatSpinner spin_bh_from_5, spin_bh_to_5;
    private AppCompatSpinner spin_bh_from_6, spin_bh_to_6;
    private AppCompatSpinner spin_bh_from_7, spin_bh_to_7;

    private int dias_seleccionados = 0;
    private int number = 0;//va permitir comparar los dias seleccionados para finalizar la actividad

    private AppCompatButton btn_apply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_horario);

        init();//inicio las instancias
    }


    private void init(){
        lyt_hours_1 = (LinearLayout) findViewById(R.id.lyt_hours_1);
        lyt_hours_2 = (LinearLayout) findViewById(R.id.lyt_hours_2);
        lyt_hours_3 = (LinearLayout) findViewById(R.id.lyt_hours_3);
        lyt_hours_4 = (LinearLayout) findViewById(R.id.lyt_hours_4);
        lyt_hours_5 = (LinearLayout) findViewById(R.id.lyt_hours_5);
        lyt_hours_6 = (LinearLayout) findViewById(R.id.lyt_hours_6);
        lyt_hours_7 = (LinearLayout) findViewById(R.id.lyt_hours_7);

        switch_open_1 = (SwitchCompat) findViewById(R.id.switch_open_1);
        switch_open_2 = (SwitchCompat) findViewById(R.id.switch_open_2);
        switch_open_3 = (SwitchCompat) findViewById(R.id.switch_open_3);
        switch_open_4 = (SwitchCompat) findViewById(R.id.switch_open_4);
        switch_open_5 = (SwitchCompat) findViewById(R.id.switch_open_5);
        switch_open_6 = (SwitchCompat) findViewById(R.id.switch_open_6);
        switch_open_7 = (SwitchCompat) findViewById(R.id.switch_open_7);

        spin_bh_from_1 = (AppCompatSpinner) findViewById(R.id.spin_bh_from_1);
        spin_bh_from_2 = (AppCompatSpinner) findViewById(R.id.spin_bh_from_2);
        spin_bh_from_3 = (AppCompatSpinner) findViewById(R.id.spin_bh_from_3);
        spin_bh_from_4 = (AppCompatSpinner) findViewById(R.id.spin_bh_from_4);
        spin_bh_from_5 = (AppCompatSpinner) findViewById(R.id.spin_bh_from_5);
        spin_bh_from_6 = (AppCompatSpinner) findViewById(R.id.spin_bh_from_6);
        spin_bh_from_7 = (AppCompatSpinner) findViewById(R.id.spin_bh_from_7);

        spin_bh_to_1 = (AppCompatSpinner) findViewById(R.id.spin_bh_to_1);
        spin_bh_to_2 = (AppCompatSpinner) findViewById(R.id.spin_bh_to_2);
        spin_bh_to_3 = (AppCompatSpinner) findViewById(R.id.spin_bh_to_3);
        spin_bh_to_4 = (AppCompatSpinner) findViewById(R.id.spin_bh_to_4);
        spin_bh_to_5 = (AppCompatSpinner) findViewById(R.id.spin_bh_to_5);
        spin_bh_to_6 = (AppCompatSpinner) findViewById(R.id.spin_bh_to_6);
        spin_bh_to_7 = (AppCompatSpinner) findViewById(R.id.spin_bh_to_7);

        btn_apply = (AppCompatButton) findViewById(R.id.btn_apply);

        lyt_hours_1.setVisibility(View.GONE);
        lyt_hours_2.setVisibility(View.GONE);
        lyt_hours_3.setVisibility(View.GONE);
        lyt_hours_4.setVisibility(View.GONE);
        lyt_hours_5.setVisibility(View.GONE);
        lyt_hours_6.setVisibility(View.GONE);
        lyt_hours_7.setVisibility(View.GONE);


        //agrego los eventos a los switch
        addEventSwitch();
        btn_apply.setOnClickListener(this);
    }

    private void setVisibleLayout(final SwitchCompat swith_compat){
        swith_compat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (swith_compat.getId()){
                    case R.id.switch_open_1:
                        if(switch_open_1.isChecked()){
                            lyt_hours_1.setVisibility(View.VISIBLE);
                        }else{
                            lyt_hours_1.setVisibility(View.GONE);
                        }
                        break;
                    case R.id.switch_open_2:
                        if(switch_open_2.isChecked()){
                            lyt_hours_2.setVisibility(View.VISIBLE);
                        }else{
                            lyt_hours_2.setVisibility(View.GONE);
                        }
                        break;
                    case R.id.switch_open_3:
                        if(switch_open_3.isChecked()){
                            lyt_hours_3.setVisibility(View.VISIBLE);
                        }else{
                            lyt_hours_3.setVisibility(View.GONE);
                        }
                        break;
                    case R.id.switch_open_4:
                        if(switch_open_4.isChecked()){
                            lyt_hours_4.setVisibility(View.VISIBLE);
                        }else{
                            lyt_hours_4.setVisibility(View.GONE);
                        }
                        break;
                    case R.id.switch_open_5:
                        if(switch_open_5.isChecked()){
                            lyt_hours_5.setVisibility(View.VISIBLE);
                        }else{
                            lyt_hours_5.setVisibility(View.GONE);
                        }
                        break;
                    case R.id.switch_open_6:
                        if(switch_open_6.isChecked()){
                            lyt_hours_6.setVisibility(View.VISIBLE);
                        }else{
                            lyt_hours_6.setVisibility(View.GONE);
                        }
                        break;
                    case R.id.switch_open_7:
                        if(switch_open_7.isChecked()){
                            lyt_hours_7.setVisibility(View.VISIBLE);
                        }else{
                            lyt_hours_7.setVisibility(View.GONE);
                        }
                        break;
                }
            }
        });
    }

    private void addEventSwitch(){
        for (int i = 1; i<= 7; i++){
            if(i == 1){
                setVisibleLayout(switch_open_1);
            }
            if(i == 2){
                setVisibleLayout(switch_open_2);
            }
            if(i == 3){
                setVisibleLayout(switch_open_3);
            }
            if(i == 4){
                setVisibleLayout(switch_open_4);
            }
            if(i == 5){
                setVisibleLayout(switch_open_5);
            }
            if(i == 6){
                setVisibleLayout(switch_open_6);
            }
            if(i == 7){
                setVisibleLayout(switch_open_7);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_apply:
                validarDatosGUI();
                break;
        }
    }

    private void validarSwith(AppCompatSpinner s1, AppCompatSpinner s2){
        if(s1.getSelectedItemPosition() == 0){
            showSnackBar("Debe seleccionar una hora");
            s1.performClick();
        }else{
            if(s2.getSelectedItemPosition() == 0){
                showSnackBar("Debe seleccionar una hora");
                s2.performClick();
            }
        }
    }

    private void validarDatosGUI(){
        boolean estado = true;
        if(!switch_open_1.isChecked() && !switch_open_1.isChecked()
                && !switch_open_3.isChecked()
                && !switch_open_4.isChecked()
                && !switch_open_5.isChecked()
                && !switch_open_6.isChecked()
                && !switch_open_7.isChecked()){
            showSnackBar("Debe Seleccionar al menos un dia");
        }

        if(switch_open_1.isChecked()){
            validarSwith(spin_bh_from_1, spin_bh_to_1);
            dias_seleccionados++;
        }

        if(switch_open_2.isChecked()){
            validarSwith(spin_bh_from_2, spin_bh_to_2);
            dias_seleccionados++;
        }

        if(switch_open_3.isChecked()){
            validarSwith(spin_bh_from_3, spin_bh_to_3);
            dias_seleccionados++;
        }

        if(switch_open_4.isChecked()){
            validarSwith(spin_bh_from_4, spin_bh_to_4);
            dias_seleccionados++;
        }

        if(switch_open_5.isChecked()){
            validarSwith(spin_bh_from_5, spin_bh_to_5);
            dias_seleccionados++;
        }

        if(switch_open_6.isChecked()){
            validarSwith(spin_bh_from_6, spin_bh_to_6);
            dias_seleccionados++;
        }

        if(switch_open_7.isChecked()){
            validarSwith(spin_bh_from_7, spin_bh_to_7);
            dias_seleccionados++;
        }

        registro();
    }

    private void registro(){
        if (switch_open_1.isChecked()){
            guardarHorario("Lunes", spin_bh_from_1.getSelectedItem().toString(), spin_bh_to_1.getSelectedItem().toString());
        }

        if (switch_open_2.isChecked()){
            guardarHorario("Martes", spin_bh_from_2.getSelectedItem().toString(), spin_bh_to_2.getSelectedItem().toString());
        }

        if (switch_open_3.isChecked()){
            guardarHorario("Miercoles", spin_bh_from_3.getSelectedItem().toString(), spin_bh_to_3.getSelectedItem().toString());
        }

        if (switch_open_4.isChecked()){
            guardarHorario("Jueves", spin_bh_from_4.getSelectedItem().toString(), spin_bh_to_4.getSelectedItem().toString());
        }

        if (switch_open_5.isChecked()){
            guardarHorario("Viernes", spin_bh_from_5.getSelectedItem().toString(), spin_bh_to_5.getSelectedItem().toString());
        }

        if (switch_open_6.isChecked()){
            guardarHorario("Sabado", spin_bh_from_6.getSelectedItem().toString(), spin_bh_to_6.getSelectedItem().toString());
        }

        if (switch_open_7.isChecked()){
            guardarHorario("Domingo", spin_bh_from_7.getSelectedItem().toString(), spin_bh_to_7.getSelectedItem().toString());
        }
    }

    /**
     * Proyecta una {@link Snackbar} con el string usado
     *
     * @param msg Mensaje
     */

    private void showSnackBar(String msg) {
        Snackbar
                .make(findViewById(R.id.coordinator_re_horario), msg, Snackbar.LENGTH_LONG)
                .show();
    }

    /**
     * Guarda los cambios de una meta editada.
     * <p>
     * Si está en modo inserción, entonces crea una nueva
     * meta en la base de datos
     */
    private void guardarHorario(String diasemana, String horaI, String horaF) {
        //mostrar el diálogo de progreso
        loading = ProgressDialog.show(this,"guardando...","Espere por favor...",false,false);

        String parqueadero_id = Preferences.getPreferenceString(this, Constantes.PREFERENCIA_PARQUEADERO_ID);


        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();// Mapeo previo
        map.put("parqueadero_id",parqueadero_id);
        map.put("diasemana", diasemana);
        map.put("horaI", horaI);
        map.put("horaF", horaF);

        // Crear nuevo objeto Json basado en el mapa
        JSONObject jobject = new JSONObject(map);
        // Depurando objeto Json...
        Log.i(TAG, "map.." + map.toString());
        Log.d(TAG, "json productor..."+jobject);

        // Actualizar datos en el servidor
        VolleySingleton.getInstance(this).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        Constantes.INSERT_NEW_HORARIO,
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
                    Utilidades.showToast(this, mensaje);
                    // Enviar código de éxito
                    setResult(Activity.RESULT_OK);
                    //limpiar();
                    // Terminar actividad
                    number++;
                    if (number == dias_seleccionados){
                        finish();
                    }
                    break;

                case "2":
                    //descartar el diálogo de progreso
                    loading.dismiss();
                    // Mostrar mensaje
                    Utilidades.showToast(this, mensaje);
                    // Enviar código de falla
                    setResult(Activity.RESULT_CANCELED);
                    //limpiar();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
