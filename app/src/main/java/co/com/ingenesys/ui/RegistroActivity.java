package co.com.ingenesys.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import co.com.ingenesys.R;
import co.com.ingenesys.utils.Constantes;
import co.com.ingenesys.utils.DateDialog;
import co.com.ingenesys.utils.Utilidades;
import co.com.ingenesys.web.VolleySingleton;

public class RegistroActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, View.OnClickListener{
    //etiqueta para la depuracion
    private static final String TAG = RegistroActivity.class.getSimpleName();

    //instancia
    private EditText txtNumeroCedula;
    private EditText txtNombres;
    private EditText txtApellidos;
    private EditText txtTelefono;
    private EditText txtCorreo;
    private EditText txtClave;
    private TextView txtFechaNacimiento;
    private RadioGroup radioSexo;
    private RadioButton radioM;
    private RadioButton radioF;
    private RadioGroup radioTipoUsuario;
    private RadioButton radioU;
    private RadioButton radioA;

    private Button btnEnviar;

    private ProgressDialog loading = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        init();
    }

    //instacia
    private void init(){
        txtFechaNacimiento = (TextView) findViewById(R.id.txtFechaNacimiento);
        txtNumeroCedula = (EditText) findViewById(R.id.txtNumeroCedula);
        txtNombres = (EditText) findViewById(R.id.txtNombres);
        txtApellidos = (EditText) findViewById(R.id.txtApellidos);
        txtTelefono = (EditText) findViewById(R.id.txtTelefono);
        txtCorreo = (EditText) findViewById(R.id.txtCorreo);
        txtClave = (EditText) findViewById(R.id.txtClave);
        radioSexo = (RadioGroup) findViewById(R.id.radioSexo);
        radioM = (RadioButton) findViewById(R.id.radioM);
        radioF = (RadioButton) findViewById(R.id.radioF);
        radioTipoUsuario = (RadioGroup) findViewById(R.id.radioTipoUsuario);
        radioU = (RadioButton) findViewById(R.id.radioU);
        radioA = (RadioButton) findViewById(R.id.radioA);
        btnEnviar = (Button) findViewById(R.id.btnEnviar);

        txtFechaNacimiento.setOnClickListener(this);
        btnEnviar.setOnClickListener(this);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd", Locale.getDefault());
        Date date = null;

        final int mesActual = month + 1;
        String diaFormat = (dayOfMonth < 10) ? 0 + String.valueOf(dayOfMonth) : String.valueOf(dayOfMonth);
        String mesFormat = (mesActual< 10) ? 0 + String.valueOf(mesActual) : String.valueOf(mesActual);
        try{
            date = dateFormat.parse(year + "-" + mesFormat + "-" + diaFormat);
        }catch (ParseException e){
            e.printStackTrace();
        }

        String outDate = dateFormat.format(date);
        txtFechaNacimiento.setText(outDate);
    }

    /*
     * Método que permite validar si algún campo esta sin llenar
     * */
    private boolean validarFormulario(){
        boolean estado = false;
        if(txtNumeroCedula.getText().toString().trim().length() == 0){
            showSnackBar("Campo identificacion Vacio");
            txtNumeroCedula.requestFocus();
        }else{
            if(txtNombres.getText().toString().trim().length() == 0){
                showSnackBar("Campo Nombre Vacio");
                txtNombres.requestFocus();
            }else{
                if(txtApellidos.getText().toString().trim().length() == 0){
                    showSnackBar("Campo Apellido Vacio");
                    txtApellidos.requestFocus();
                }else{
                    if (txtTelefono.getText().toString().trim().length() == 0){
                        showSnackBar("Campo Telefono vacio");
                        txtTelefono.requestFocus();
                    }else{
                        if(txtCorreo.getText().toString().trim().length() == 0){
                            showSnackBar("Campo Direccion Vacio");
                            txtCorreo.requestFocus();
                        }else{
                            if(txtClave.getText().toString().trim().length() == 0){
                                showSnackBar("Campo Correo Vacio");
                                txtClave.requestFocus();
                            }else{
                                estado = true;
                            }
                        }
                    }
                }
            }
        }
        return estado;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txtFechaNacimiento:
                String tag = "DatePicker";
                new DateDialog().show(getSupportFragmentManager(),tag);
                break;
            case R.id.btnEnviar:
                if (validarFormulario()){
                    guardarUsuario();
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
                .make(findViewById(R.id.coordinator_register), msg, Snackbar.LENGTH_LONG)
                .show();
    }


    /**
     * Guarda los cambios de una meta editada.
     * <p>
     * Si está en modo inserción, entonces crea una nueva
     * meta en la base de datos
     */
    private void guardarUsuario() {
        //mostrar el diálogo de progreso
        loading = ProgressDialog.show(this,"guardando...","Espere por favor...",false,false);

        String cedula = txtNumeroCedula.getText().toString().trim();
        String nombre = txtNombres.getText().toString().trim();
        String apellido = txtApellidos.getText().toString().trim();
        String telefono = txtTelefono.getText().toString().trim();
        String email = txtCorreo.getText().toString().trim();
        String clave = txtClave.getText().toString().trim();
        String genero = getSeleccionarRadio(radioSexo);
        String tipousuario = getSeleccionarRadio(radioTipoUsuario);
        String fechaNacimiento = txtFechaNacimiento.getText().toString().trim();


        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();// Mapeo previo
        map.put("cedula",cedula);
        map.put("nombres", nombre);
        map.put("apellidos", apellido);
        map.put("telefono", telefono);
        map.put("correo", email);
        map.put("genero", genero);
        map.put("fechanacimiento", fechaNacimiento);
        map.put("clave", clave);
        map.put("tipousuario", tipousuario);

        // Crear nuevo objeto Json basado en el mapa
        JSONObject jobject = new JSONObject(map);
        // Depurando objeto Json...
        Log.i(TAG, "map.." + map.toString());
        Log.d(TAG, "json productor..."+jobject);

        // Actualizar datos en el servidor
        VolleySingleton.getInstance(this).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        Constantes.INSERT_NEW_USUARIO,
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
                    finish();
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

    private String getSeleccionarRadio(RadioGroup radioGroup){
        String cadena = "";
            int idRadioSeleccionado = radioGroup.getCheckedRadioButtonId();
            if(idRadioSeleccionado == radioM.getId()){
                cadena = radioM.getText().toString();
            }else{
                if(idRadioSeleccionado == radioF.getId()){
                    cadena = radioF.getText().toString();
                }else{
                    if(idRadioSeleccionado == radioU.getId()){
                        cadena = radioU.getText().toString();
                    }else{
                        if(idRadioSeleccionado == radioA.getId()){
                            cadena = radioA.getText().toString();
                        }
                    }
                }
            }

        return cadena;
    }
}
