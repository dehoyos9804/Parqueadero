package co.com.ingenesys.ui;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import co.com.ingenesys.R;
import co.com.ingenesys.fragment.CuposFragment;
import co.com.ingenesys.fragment.DetalleParqueaderoFragment;
import co.com.ingenesys.fragment.HorarioFragment;
import co.com.ingenesys.fragment.ParqueaderoFragment;
import co.com.ingenesys.fragment.TarifasFragment;
import co.com.ingenesys.fragment.ZonasFragment;
import co.com.ingenesys.modelo.NumeroParqueadero;
import co.com.ingenesys.modelo.Tarifa;
import co.com.ingenesys.modelo.Usuarios;
import co.com.ingenesys.utils.Constantes;
import co.com.ingenesys.utils.Preferences;
import co.com.ingenesys.utils.Utilidades;
import co.com.ingenesys.web.VolleySingleton;
import de.hdodenhof.circleimageview.CircleImageView;

public class InitialAdministradorActivity extends AppCompatActivity {

    //instancia del drawer
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView txt_username;
    private TextView txt_usuario;
    private CircleImageView circle_image;
    private Fragment fragment = null;

    private Gson gson = new Gson();

    private int isExisteParqueadero = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_administrador);

        setToolbar(); //añadimos el toolbar


        initNavegationView(savedInstanceState);

        if (savedInstanceState == null){
            selectItem();//coloco el fragment por defecto
        }
    }

    //Agrego mi toolbar
    private void setToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();

        if(actionBar != null){
            // Poner ícono del drawer toggle
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_dark);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //añado mi drawer
    private void initNavegationView(Bundle savedInstanceState){
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        //aqui modifico los texto del navigationView por defecto, se modifican con datos del usuario
        View vista = navigationView.getHeaderView(0);
        txt_username = (TextView) vista.findViewById(R.id.txt_username);
        txt_usuario = (TextView) vista.findViewById(R.id.txt_usuario);
        circle_image = (CircleImageView) vista.findViewById(R.id.circle_image);

        txt_username.setText(Preferences.getPreferenceString(this, Constantes.PREFERENCIA_NOMBRE_CLAVE) + " " + Preferences.getPreferenceString(this, Constantes.PREFERENCIA_APELLIDO_CLAVE));
        txt_usuario.setText(Preferences.getPreferenceString(this, Constantes.PREFERENCIA_CORREO_CLAVE));

        if(navigationView != null){
            setupDrawerContent(navigationView);
        }
    }

    //método que remplaza fragmento en el contenido principal
    private void setupDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        boolean fragmentTrasition = false;
                        //Fragment fragment = null;

                        switch (menuItem.getItemId()){
                            case R.id.nav_home://inicio
                                getHTTP();
                                break;
                            case R.id.nav_horario:
                                fragment = new HorarioFragment();
                                fragmentTrasition = true;
                                break;
                            case R.id.nav_tarifas://registrar tarifas
                                fragment = new TarifasFragment();
                                fragmentTrasition = true;
                                break;
                            case R.id.nav_mis_zonas:
                                fragment = new ZonasFragment();
                                fragmentTrasition = true;
                                break;
                            case R.id.nav_add_zonas:
                                fragment = new CuposFragment();
                                fragmentTrasition = true;
                                break;
                            case R.id.nav_cerrar_sesion://cerramos la sesión del usuario actual
                                Utilidades.cerrarSesion(InitialAdministradorActivity.this);
                                Preferences.savePreferenceString(InitialAdministradorActivity.this,  "", Constantes.PREFERENCIA_PARQUEADERO_ID);
                                break;
                        }

                        if(fragmentTrasition){
                            getSupportFragmentManager().beginTransaction().replace(R.id.main_content, fragment).commit();
                            menuItem.setChecked(true);
                            getSupportActionBar().setTitle(menuItem.getTitle());
                        }

                        drawerLayout.closeDrawers();//cerramos el drawer
                        return true;
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!drawerLayout.isDrawerOpen(GravityCompat.START)){
            //getMenuInflater().inflate(R.menu.menu_search_initial, menu);
            //MenuItem searchItem = menu.findItem(R.id.men_action_search);
            //SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            //searchView.setOnQueryTextListener(this);

            return true;
        }

        return true;
    }

    //permite implementar la logica cuando se seleccionan los items del menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //método que permite colocar el fragmento por defecto
    private void selectItem(){
        getHTTP();
    }

    /**
     * permite saber si el usuario ya registro un parqueadero
     */
    private void getHTTP() {

        String usuario_id = Preferences.getPreferenceString(this, Constantes.PREFERENCIA_IDUSUARIO_CLAVE);
        String newURL = Constantes.GET_EXISTE_PARQUEADERO + "?usuario_id=" + usuario_id;
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
                                        procesar(response);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        //showSnackBar("Error al cargar los datos: " + error.toString());
                                        Log.d("TAG", "Error Volley: " + error.toString());
                                    }
                                }

                        )
                );

    }

    private void procesar(JSONObject response){
        try {
            // Obtener atributo "estado"
            String estado = response.getString("estado");
            switch (estado) {
                case "1":// EXITO
                    try {
                        // Obtener array "consulta" Json
                        JSONObject datos = response.getJSONObject("tbl_parqueaderos");;
                        //llenar los parqueaderos de la lista
                        NumeroParqueadero is = gson.fromJson(datos.toString(),NumeroParqueadero.class);

                        isExisteParqueadero = Integer.parseInt(is.getCantidad());

                        if(isExisteParqueadero == 0){
                            fragment = ParqueaderoFragment.newInstance();
                        }else{
                            fragment = DetalleParqueaderoFragment.newInstance();
                        }
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.main_content,fragment).commit();
                        getSupportActionBar().setTitle(R.string.home_admin);

                    } catch (JSONException e) {
                        Log.i("TAG", "Error al llenar Adaptador " + e.getLocalizedMessage());
                    }
                    break;
                case "2":
                    String mensaje2 = response.getString("mensaje");
                    //loading.dismiss();
                    //Utilidades.showToast(this, mensaje2);
                    break;
            }
        } catch (JSONException je) {
            Log.d("TAG", je.getMessage());
        }
    }
}
