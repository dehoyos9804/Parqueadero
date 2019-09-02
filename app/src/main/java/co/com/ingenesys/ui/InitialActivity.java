package co.com.ingenesys.ui;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import co.com.ingenesys.R;
import co.com.ingenesys.fragment.ExplorarMapsFragment;
import co.com.ingenesys.fragment.MenuFragment;

public class InitialActivity extends AppCompatActivity implements OnMapReadyCallback {

    BottomNavigationView  bottomNavigationView;
    private GoogleMap mMap;//representa el mapa
    private Marker marker;
    private double latitud = 0.0;
    private double longitud = 0.0;
    private String direccion = "";
    private static  String mensaje = "";
    private static int PETICION_PERMISO_LOCALIZACION = 101;
    private LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        setToolbar(); //añadimos el toolbar
        initBottonNavigationView(savedInstanceState);//inicio mi menu navigation view

        if (savedInstanceState == null){
            selectItem();//coloco el fragment por defecto
        }
    }


    //añado mi boton navigation
    private void initBottonNavigationView(Bundle savedInstanceState){
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.botonNavigation);

        if(bottomNavigationView != null){
            setupDrawerContent(bottomNavigationView);
        }
    }

    //método que remplaza fragmento en el contenido principal
    private void setupDrawerContent(BottomNavigationView bottomNavigationView){
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        //boolean fragmentTrasition = false;
                        //Fragment fragment = null;

                        switch (menuItem.getItemId()){
                            case R.id.menu_home_nav://explorar
                                ExplorarMapsFragment ef = new ExplorarMapsFragment();
                                //fragmentTrasition = true;
                                getSupportFragmentManager().beginTransaction().replace(R.id.main_content, ef).commit();
                                menuItem.setChecked(true);
                                getSupportActionBar().setTitle(menuItem.getTitle());
                                ef.getMapAsync(InitialActivity.this);
                                break;
                            case R.id.menu_2_nav://historial
                                //fragment = new ListaHistorialFragment();
                                //fragmentTrasition = true;
                                break;
                            case R.id.menu_nav://lista de asesores
                                //fragment = new ListaAsesoresFragment();
                                //fragmentTrasition = true;
                                MenuFragment mf = new MenuFragment();
                                getSupportFragmentManager().beginTransaction().replace(R.id.main_content, mf).commit();
                                menuItem.setChecked(true);
                                getSupportActionBar().setTitle(menuItem.getTitle());
                                break;
                        }

                        /*if(fragmentTrasition){
                            getSupportFragmentManager().beginTransaction().replace(R.id.main_content, fragment).commit();
                            menuItem.setChecked(true);
                            getSupportActionBar().setTitle(menuItem.getTitle());

                        }*/

                        return true;
                    }
                }
        );
    }

    //permite implementar la logica cuando se seleccionan los items del menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Agrego mi toolbar
    private void setToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();

        if(actionBar != null){
            // Poner ícono del drawer toggle
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_dark);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //método que permite colocar el fragmento por defecto
    private void selectItem(){
        ExplorarMapsFragment fragment = ExplorarMapsFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_content,fragment).commit();
        setTitle(R.string.home_item);

        fragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        /*mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sincelejo = new LatLng(9.3154294, -75.4329806);
        mMap.addMarker(new MarkerOptions().position(sincelejo).title("Sincelejo Sucre"));

        //posicion de la camara
        CameraPosition cameraPosition = CameraPosition.builder().target(sincelejo).zoom(10).build();

        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sincelejo));
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/

        mMap = googleMap;
        miUbicacion();
    }



    /*
    * método que permite obtener la direcion de la calle a partir de la latitud y longitud
    * */
    private void setLocation(Location location){
        if(location.getLatitude() != 0.0 && location.getLongitude() != 0.0){
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                if(!list.isEmpty()){
                    Address address = list.get(0);
                    direccion = (address.getAddressLine(0));
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }


    /**
    *método que permite agregar un nuevo marcador para el mapa
    * */
    private void addMarcador(double latitud, double longitud){
        LatLng coordenadas = new LatLng(latitud, longitud);
        CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 16);

        if(marker != null) marker.remove();

        //mMap.addMarker(new MarkerOptions().position(coordenadas).title("Mi ubicacion actual ").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_mi_ubicacion_primary)));
        mMap.addMarker(new MarkerOptions().position(coordenadas).title("Mi ubicacion").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_miubicacion_primary)));
        mMap.animateCamera(miUbicacion);
    }

    /**
     * método que permite actualizar la ubicacion ubicacion
     * */
    private void updateUbicacion(Location location){
        if(location != null){
            latitud = location.getLatitude();
            longitud = location.getLongitude();
            addMarcador(latitud, longitud);
        }
    }

    private void mensajeToast(){
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();

    }

    /**
     * método que permite obtener mi ubicacion en tiempo real
     * **/
    private void miUbicacion(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PETICION_PERMISO_LOCALIZACION);

            return;

        }
        //locationStart();
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        updateUbicacion(location);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1200, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateUbicacion(location);
                setLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                //mensaje = "Gps Activado";
                //mensajeToast();
            }

            @Override
            public void onProviderDisabled(String provider) {
                //mensaje = "Gps Desactivado";
                //mensajeToast();
            }
        });

    }


    private void showAlert(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("Activar GPS para continuar...");
        dialog.setPositiveButton("Activar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intentGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intentGPS);
            }
        });
        dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private boolean isLocationEnabled(){
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean chekLocation(){
        if(!isLocationEnabled()){
            showAlert();
        }
        return isLocationEnabled();
    }
}