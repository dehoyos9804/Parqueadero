package co.com.ingenesys.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import co.com.ingenesys.R;
import co.com.ingenesys.modelo.Usuarios;
import co.com.ingenesys.utils.Constantes;
import co.com.ingenesys.utils.Preferences;
import co.com.ingenesys.web.VolleySingleton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //etiqueta para la depuracion
    private static final String TAG = MainActivity.class.getSimpleName();

    private Button btnIniciarSesion;
    private TextView txtCrearCuenta;
    private EditText txtUsuario;
    private EditText txtClave;

    private ProgressDialog loading = null;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //inicio las preferencias guardadas
        if(Preferences.getPreferenceBoolean(this, Constantes.PREFERENCIA_MANTENER_SESION_CLAVE)){
            switch (Preferences.getPreferenceString(this, Constantes.PREFERENCIA_TIPO_USUARIO_CLAVE)){
                case "USUARIO":
                    Intent intent = new Intent(this, InitialActivity.class);
                    startActivity(intent);
                    finish();//cierro la actividad de iniciar sesion
                    break;
                case "ADMINISTRADOR":
                    Intent administrador = new Intent(this, InitialAdministradorActivity.class);
                    startActivity(administrador);
                    finish();//cierro la actividad de iniciar sesion*/
                    break;
            }

        }
        init();
    }

    private void init(){
        txtUsuario = (EditText) findViewById(R.id.txtUsuario);
        txtClave = (EditText) findViewById(R.id.txtClave);
        btnIniciarSesion = (Button) findViewById(R.id.btnIniciarSesion);
        txtCrearCuenta = (TextView) findViewById(R.id.txtCrearCuenta);

        btnIniciarSesion.setOnClickListener(this);
        txtCrearCuenta.setOnClickListener(this);
    }

    //método que permite validar los campos
    private boolean validarDatos(){
        boolean  estado = false;

        if (txtUsuario.getText().toString().trim().isEmpty()){
            showSnackBar( "Campo Usuario Vacio");
            txtUsuario.requestFocus();
        }else{
            if (txtClave.getText().toString().trim().isEmpty()){
                showSnackBar("Campo Contraseña Vacio");
                txtClave.requestFocus();
            }else{
                estado = true;
            }
        }

        return estado;
    }

    private void limpiar(){
        txtUsuario.setText("");
        txtClave.setText("");
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnIniciarSesion:
                if(validarDatos()){
                    peticionHTTP(txtUsuario.getText().toString().trim(), txtClave.getText().toString().trim());
                }
                break;
            case R.id.txtCrearCuenta:
                Intent registro = new Intent(this, RegistroActivity.class);
                startActivity(registro);
                break;
        }
    }

    private void peticionHTTP(String usuario, String clave){

        //Añadir parametros a la URL de webservice
        String newURL = Constantes.GET_INICIAR_SESION + "?usuario=" + usuario + "&clave=" + clave;

        //inicio progressDialog
        loading = ProgressDialog.show(this,"Autenticando...","Espere por favor...",false,false);
        //petición GET
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
                                        procesarRespuesta(response);
                                        Log.i(TAG, "processanddo respuesta..." + response);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        //descartar el diálogo de progreso
                                        loading.dismiss();
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
                        JSONObject datos = response.getJSONObject("tbl_usuarios");
                        //Parsear objeto
                        Usuarios is = gson.fromJson(datos.toString(),Usuarios.class);
                        loading.dismiss();//finalizo el dialogo
                        //iniciamos la sesion
                        AutenticacionValida(is);

                    }catch (JSONException e){
                        Log.i(TAG,"Error al llenar Adaptador " +e.getLocalizedMessage());
                    }
                    break;
                case "2":
                    String mensaje2 = response.getString("mensaje");
                    loading.dismiss();//finalizo el dialogo
                    limpiar();
                    showSnackBar(mensaje2);
                    break;
                case "3":
                    String mensaje3 = response.getString("mensaje");
                    loading.dismiss();//finalizo el dialogo
                    showSnackBar(mensaje3);
                    break;
            }
        }catch (JSONException je){
            Log.d(TAG, je.getMessage());
        }
    }

    private void AutenticacionValida(Usuarios u){
        switch (u.getTipousuario()){
            case "USUARIO":
                Intent usuario = new Intent(this, InitialActivity.class);

                Preferences.savePreferenceString(this, u.getId(), Constantes.PREFERENCIA_IDUSUARIO_CLAVE);
                Preferences.savePreferenceString(this, u.getCEDULA(), Constantes.PREFERENCIA_CEDULA_CLAVE);
                Preferences.savePreferenceString(this, u.getNOMBRE(), Constantes.PREFERENCIA_NOMBRE_CLAVE);
                Preferences.savePreferenceString(this, u.getAPELLIDO(), Constantes.PREFERENCIA_APELLIDO_CLAVE);
                Preferences.savePreferenceString(this, u.getTELEFONO(), Constantes.PREFERENCIA_TELEFONO_CLAVE);
                Preferences.savePreferenceString(this, u.getCORREO(), Constantes.PREFERENCIA_CORREO_CLAVE);
                Preferences.savePreferenceString(this, u.getGENERO(), Constantes.PREFERENCIA_GENERO_CLAVE);
                Preferences.savePreferenceString(this, u.getFNACIMIENTO(), Constantes.PREFERENCIA_FECHA_NACIMIENTO_CLAVE);
                Preferences.savePreferenceString(this, u.getTipousuario(), Constantes.PREFERENCIA_TIPO_USUARIO_CLAVE);
                Preferences.savePreferenceBoolean(this, Constantes.ESTADO_PREFERENCIA_TRUE, Constantes.PREFERENCIA_MANTENER_SESION_CLAVE);

                startActivity(usuario);
                finish();//finalizar actividad
                break;
            case "ADMINISTRADOR":
                Intent admin = new Intent(this, InitialAdministradorActivity.class);

                Preferences.savePreferenceString(this, u.getId(), Constantes.PREFERENCIA_IDUSUARIO_CLAVE);
                Preferences.savePreferenceString(this, u.getCEDULA(), Constantes.PREFERENCIA_CEDULA_CLAVE);
                Preferences.savePreferenceString(this, u.getNOMBRE(), Constantes.PREFERENCIA_NOMBRE_CLAVE);
                Preferences.savePreferenceString(this, u.getAPELLIDO(), Constantes.PREFERENCIA_APELLIDO_CLAVE);
                Preferences.savePreferenceString(this, u.getTELEFONO(), Constantes.PREFERENCIA_TELEFONO_CLAVE);
                Preferences.savePreferenceString(this, u.getCORREO(), Constantes.PREFERENCIA_CORREO_CLAVE);
                Preferences.savePreferenceString(this, u.getGENERO(), Constantes.PREFERENCIA_GENERO_CLAVE);
                Preferences.savePreferenceString(this, u.getFNACIMIENTO(), Constantes.PREFERENCIA_FECHA_NACIMIENTO_CLAVE);
                Preferences.savePreferenceString(this, u.getTipousuario(), Constantes.PREFERENCIA_TIPO_USUARIO_CLAVE);
                Preferences.savePreferenceBoolean(this, Constantes.ESTADO_PREFERENCIA_TRUE, Constantes.PREFERENCIA_MANTENER_SESION_CLAVE);

                startActivity(admin);
                finish();//finalizar actividad
                break;
        }
    }

}
