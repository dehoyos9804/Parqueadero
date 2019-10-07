package co.com.ingenesys.ui;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import co.com.ingenesys.R;
import co.com.ingenesys.fragment.ParqueaderoFragment;
import co.com.ingenesys.utils.Constantes;
import co.com.ingenesys.utils.Preferences;
import co.com.ingenesys.utils.Utilidades;
import de.hdodenhof.circleimageview.CircleImageView;

public class InitialAdministradorActivity extends AppCompatActivity {

    //instancia del drawer
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView txt_username;
    private TextView txt_usuario;
    private CircleImageView circle_image;
    private Fragment fragment = null;


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
                                //ParqueaderoFragment fragmentParkin = new ParqueaderoFragment();
                                //FragmentManager manager = getSupportFragmentManager();
                                //manager.beginTransaction().replace(R.id.main_content, fragmentParkin).commit();
                                fragment = new ParqueaderoFragment();
                                fragmentTrasition = true;
                                break;
                            case R.id.nav_cerrar_sesion://cerramos la sesión del usuario actual
                                Utilidades.cerrarSesion(InitialAdministradorActivity.this);
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
        fragment = ParqueaderoFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_content,fragment).commit();
        setTitle(R.string.home_item);
    }
}
