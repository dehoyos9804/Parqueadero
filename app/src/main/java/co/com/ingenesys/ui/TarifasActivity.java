package co.com.ingenesys.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import co.com.ingenesys.R;
import co.com.ingenesys.utils.Preferences;

public class TarifasActivity extends AppCompatActivity {

    //etiqueta para la depuracion
    private static final String TAG = TarifasActivity.class.getSimpleName();
    private Gson gson = new Gson();
    private CollapsingToolbarLayout collapser;
    private String codigoCita;

    private TextView txtDependenciaElegida;
    private TextView txtTemaElegido;
    private TextView txtFechaDetalleCita;
    private TextView txtHoraDetalleCita;
    private TextView txtFuncionarioCargo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarifas);

        setToolbar();
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
        collapser.setTitle("Tarifas Estacionamiento"); // Cambiar título
    }


}
