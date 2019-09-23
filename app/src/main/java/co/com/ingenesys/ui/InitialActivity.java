package co.com.ingenesys.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GaeRequestHandler;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import co.com.ingenesys.R;
import co.com.ingenesys.fragment.ExplorarMapsFragment;
import co.com.ingenesys.fragment.MenuFragment;
import co.com.ingenesys.fragment.PruebaFragment;
import co.com.ingenesys.modelo.Parqueaderos;
import co.com.ingenesys.utils.Constantes;
import co.com.ingenesys.utils.Utilidades;
import co.com.ingenesys.web.VolleySingleton;


public class InitialActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    BottomNavigationView  bottomNavigationView;
    private GoogleMap mMap;//representa el mapa

    //Instancia GSON
    private Gson gson = new Gson();

    //etiqueta para la depuracion
    private static final String TAG = InitialActivity.class.getSimpleName();

    private double latitud = 0.0;
    private double longitud = 0.0;
    private String direccion = "";
    private static  String mensaje = "";
    private LocationManager locationManager;

    //marcadores
    private Marker markerParqueadero;

    //Arrays para los marcadores y parqueaderos
    private Marker[] markerParking;
    private ArrayList<Parqueaderos> parking;

    //atributos del bottom sheet
    private BottomSheetBehavior sheetBehavior;
    private LinearLayout bottom_sheet;
    private Button btn_cerrar_button_sheet;
    private ImageView imgParqueadero;
    private TextView txtNombreParqueadero;
    private Button btnVerTarifas;
    private Button btnApartarCupo;
    private Button btnVerRuta;

    private GeoApiContext geoApiContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        setToolbar(); //añadimos el toolbar
        initBottonNavigationView(savedInstanceState);//inicio mi menu navigation view

        init();//iniciar instancias

        if (savedInstanceState == null){
            selectItem();//coloco el fragment por defecto
        }
    }

    private void init(){
        bottom_sheet = (LinearLayout) findViewById(R.id.bottom_shent);
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        bottom_sheet.setVisibility(View.GONE);//oculto el bottom sheet

        btn_cerrar_button_sheet = (Button) bottom_sheet.findViewById(R.id.btn_cerrar_button_sheet);
        imgParqueadero = (ImageView) bottom_sheet.findViewById(R.id.imgParqueadero);
        txtNombreParqueadero = (TextView) bottom_sheet.findViewById(R.id.txtNombreParqueadero);
        btnVerTarifas = (Button) bottom_sheet.findViewById(R.id.btnVerTarifas);
        btnApartarCupo = (Button) bottom_sheet.findViewById(R.id.btnApartarCupo);
        btnVerRuta = (Button) bottom_sheet.findViewById(R.id.btnVerRuta);

        //colocar oculto el botton sheet
        btn_cerrar_button_sheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bottom_sheet.setVisibility(View.GONE);//oculto el botton sheet
                }
            }
        });

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        bottom_sheet.setVisibility(View.VISIBLE);//colocando visible el botton sheet
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        bottom_sheet.setVisibility(View.GONE);//oculto el botton sheet
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

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
                        boolean fragmentTrasition = false;
                        boolean isMapa = false;
                        Fragment fragment = null;

                        switch (menuItem.getItemId()){
                            case R.id.menu_home_nav://explorar
                                isMapa = true;
                            case R.id.menu_2_nav://historial
                                //fragment = new PruebaFragment();
                                //fragmentTrasition = true;
                                break;
                            case R.id.menu_nav://lista de asesores
                                fragment = new MenuFragment();
                                fragmentTrasition = true;
                                break;
                        }

                        if(fragmentTrasition){
                            getSupportFragmentManager().beginTransaction().replace(R.id.main_content, fragment).commit();
                            menuItem.setChecked(true);
                            getSupportActionBar().setTitle(menuItem.getTitle());

                            if(sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
                                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                bottom_sheet.setVisibility(View.GONE);//oculto el botton sheet
                            }

                        }

                        if (isMapa){
                            ExplorarMapsFragment fragment1 = new ExplorarMapsFragment();
                            FragmentManager frm = getSupportFragmentManager();
                            frm.beginTransaction().replace(R.id.main_content,fragment1).commit();
                            menuItem.setChecked(true);
                            getSupportActionBar().setTitle(menuItem.getTitle());
                            fragment1.getMapAsync(InitialActivity.this);


                        }

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

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        bottom_sheet.setVisibility(View.VISIBLE);//colocando visible el botton sheet
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        bottom_sheet.setVisibility(View.GONE);//oculto el botton sheet
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
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

        //if(marker != null) marker.remove();

        //mMap.addMarker(new MarkerOptions().position(coordenadas).title("Mi ubicacion actual ").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_mi_ubicacion_primary)));
        //mMap.addMarker(new MarkerOptions().position(coordenadas).title("Mi ubicacion").icon(BitmapDescriptorFactory.fromResource(R.drawable.mi_ubicacion)));
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


    private LocationListener listener = new LocationListener() {
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

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
    /**
     * método que permite obtener mi ubicacion en tiempo real
     * **/
    private void miUbicacion(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constantes.PETICION_PERMISO_LOCALIZACION);

            return;

        }else{
            //locationStart();
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            updateUbicacion(location);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1200, 0, listener);

            //LatLng mimarca = new LatLng(9.300805, -75.397397);
            //markerParqueadero = mMap.addMarker(new MarkerOptions().position(mimarca).title("Mi Parqueadero").snippet("parqueadero favorito jejeje").icon(BitmapDescriptorFactory.fromResource(R.drawable.parking2)));

            //calculateDirections(markerParqueadero);
            getParqueaderosHTTP();
            //agrego el boton de mi ubicacion
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);

            //webServiceObtenerRuta(String.valueOf(latitud), String.valueOf(longitud), "9.300805", "-75.397397");
            //pintarRutas();


            //eventos
            mMap.setOnMarkerClickListener(this);
        }


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


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == Constantes.PETICION_PERMISO_LOCALIZACION){
            //¿permisos asignado?
            if(permissions.length > 0 && permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //mMap.setMyLocationEnabled(true);
                miUbicacion();
            }else{
                showAlert();
            }
        }
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        /*if(marker.equals(markerParqueadero)){
            bottom_sheet.setVisibility(View.VISIBLE);//coloco visible el bottom sheet
            if(sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                //disparar eventos de ver tarifas
                btnVerTarifas.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(InitialActivity.this, TarifasActivity.class);
                        startActivity(intent);
                        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                });
            }

            return true;//evita que se muestre el infoWindows
        }*/
        final String latitudInicial = String.valueOf(latitud);
        final String longitudInicial = String.valueOf(longitud);

        for (int i=0; i < markerParking.length; i++){
            if (marker.equals(markerParking[i])){

                bottom_sheet.setVisibility(View.VISIBLE);//coloco visible el bottom sheet
                if(sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    //cambiar datos de la ventana

                    txtNombreParqueadero.setText(parking.get(i).getRazonSocial());
                    final String latitudFinal = parking.get(i).getUbicacionLat();
                    final String longitudFinal = parking.get(i).getUbicacionLon();

                    //disparar eventos de ver tarifas
                    btnVerTarifas.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(InitialActivity.this, TarifasActivity.class);
                            startActivity(intent);
                            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }
                    });

                    //disparar el evento de ver rutas
                    btnVerRuta.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            webServiceObtenerRuta(latitudInicial, longitudInicial, latitudFinal, longitudFinal);
                            pintarRutas();
                        }
                    });


                }else{
                    txtNombreParqueadero.setText("");
                }

                return true;//evita que se muestre el infoWindows
            }
        }
        return false;
    }

    /*
     * Carga el adaptador con las Consultas obtenidas
     * en la respuesta
     */
    public void getParqueaderosHTTP() {
        // Petición GET
        VolleySingleton.
                getInstance(this).
                addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                Constantes.GET_PARQUEADEROS,
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
                                        Utilidades.showToast(InitialActivity.this, "Error al cargar los datos: " + error.toString());
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
                        parking = new ArrayList<>();
                        //llenar los parqueaderos de la lista
                        for(int i = 0; i < mensaje.length(); i++){
                            JSONObject object = (JSONObject) mensaje.get(i);
                            parking.add(new Parqueaderos(object.getString("id"),object.getString("CodigoCamaraComercio"), object.getString("RazonSocial"), object.getString("TELEFONO"),object.getString("DIRECCION"), object.getString("usuario_id"), object.getString("UbicacionLat"), object.getString("UbicacionLon"),object.getString("Foto"), object.getString("Descripcion")));
                            //Utilidades.showToast(this, "parking size-->" + object.getString("UbicacionLat"));
                        }

                        markerParking = new Marker[parking.size()];

                        //llenar los diferentes parqueaderos
                        for(int j = 0; j < parking.size(); j++){
                            //LatLng mimarca = new LatLng(9.300805, -75.397397);
                            //markerParqueadero = mMap.addMarker(new MarkerOptions().position(mimarca).title("Mi Parqueadero").snippet("parqueadero favorito jejeje").icon(BitmapDescriptorFactory.fromResource(R.drawable.parking2)));
                            double latitud = Double.parseDouble(parking.get(j).getUbicacionLat().trim());
                            double longitud = Double.parseDouble(parking.get(j).getUbicacionLon().trim());
                            //Utilidades.showToast(this, "LAT=> " + parking.get(j).getUbicacionLat().trim() + "LOG=>" +parking.get(j).getUbicacionLon().trim());
                            LatLng my_ubicacion = new LatLng(latitud, longitud);

                            markerParking[j] = mMap.addMarker(new MarkerOptions().position(my_ubicacion).title(parking.get(j).getRazonSocial()).snippet(parking.get(j).getDescripcion()).icon(BitmapDescriptorFactory.fromResource(R.drawable.parking2)));
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

    /**desde aqui se implementa toda la logica de las rutas*/

    /**permite hacer peticiones http https para obtener la ruta en json */
    private void webServiceObtenerRuta(String latitudInicial, String longitudInicial, String latidudFinal, String longitudFinal){
        String newURL = Constantes.GET_RUTAS_API_GOOGLE + "?origin=" + latitudInicial + "," + longitudInicial + "&destination=" + latidudFinal + "," + longitudFinal +"&key=" + getString(R.string.google_maps_key);

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
                                        //aquí parseamos el JSONObject que retorna del API de Rutas de Google devolviendo
                                        //una lista del lista de HashMap Strings con el listado de Coordenadas de Lat y Long,
                                        //con la cual se podrá dibujar polilineas que describan la ruta entre 2 puntos.
                                        JSONArray jRoutes = null;
                                        JSONArray jLegs = null;
                                        JSONArray jSteps = null;

                                        try {
                                            jRoutes = response.getJSONArray("routes");

                                            /**Recorriendo todas las rutas @routes*/
                                            for (int i = 0; i < jRoutes.length(); i++){
                                                jLegs = ( (JSONObject) jLegs.get(i)).getJSONArray("legs");
                                                List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();

                                                /**Recorriendo todas las piernas @legs*/
                                                for (int j = 0; j < jLegs.length(); j++){
                                                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                                                    /**Recorriendo todas los pasos @steps*/
                                                    for (int k = 0; k <jSteps.length(); k++){
                                                        String polyline = "";
                                                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                                                        List<LatLng> list = decodePoly(polyline);

                                                        /**Recorriendo todas los puntos @points*/
                                                        for (int l = 0; l < list.size(); l++){
                                                            HashMap<String, String> hm = new HashMap<String, String>();
                                                            hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                                                            hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                                                            path.add(hm);
                                                        }
                                                    }
                                                    Utilidades.routes.add(path);
                                                }
                                            }

                                            pintarRutas();

                                        }catch (JSONException e){
                                            Utilidades.showToast(InitialActivity.this, "Error en el Json" + e.getLocalizedMessage());
                                        }catch (Exception e){
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Utilidades.showToast(InitialActivity.this, "Error al cargar las rutas: " + error.toString());
                                        Log.d(TAG, "Error Volley: " + error.toString());
                                    }
                                }

                        )
                );
    }

    public List<List<HashMap<String, String>>> parse(JSONObject jObject){
        //Este método PARSEA el JSONObject que retorna del API de Rutas de Google devolviendo
        //una lista del lista de HashMap Strings con el listado de Coordenadas de Lat y Long,
        //con la cual se podrá dibujar pollinas que describan la ruta entre 2 puntos.
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;
        try{
            jRoutes = jObject.getJSONArray("routes");

            /**Recorriendo todas las rutas @routes*/
            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();

                /**Recorriendo todas las piernas @legs*/
                for(int j=0;j<jLegs.length();j++){
                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                    /**Recorriendo todas los pasos @steps*/
                    for(int k=0;k<jSteps.length();k++){
                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        /**Recorriendo todas los puntos @points*/
                        for(int l=0;l<list.size();l++){
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                            hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                            path.add(hm);
                        }
                    }
                    Utilidades.routes.add(path);
                }
            }
        }catch (JSONException e){
            Utilidades.showToast(InitialActivity.this, "Error en el Json" + e.getLocalizedMessage());
        }catch (Exception e){

        }

        return Utilidades.routes;
    }

    private List<LatLng> decodePoly(String encoded){
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0;
        int len = encoded.length();
        int lat = 0;
        int lng = 0;

        while (index < len){
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);

            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    private void pintarRutas(){
        LatLng center = null;
        ArrayList<LatLng> points = null;
        PolylineOptions lineOptions = null;

        // recorriendo todas las rutas
        for (int i = 0; i < Utilidades.routes.size(); i++){
            points = new ArrayList<LatLng>();
            lineOptions = new PolylineOptions();

            // Obteniendo el detalle de la ruta
            List<HashMap<String, String>> path = Utilidades.routes.get(i);

            // Obteniendo todos los puntos y/o coordenadas de la ruta
            for (int j = 0; j < path.size(); j++){
                HashMap<String,String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                if (center == null) {
                    //Obtengo la 1ra coordenada para centrar el mapa en la misma.
                    center = new LatLng(lat, lng);
                }

                points.add(position);
            }

            // Agregamos todos los puntos en la ruta al objeto LineOptions
            lineOptions.addAll(points);

            //Definimos el grosor de las Polilíneas
            lineOptions.width(2);

            //Definimos el color de la Polilíneas
            lineOptions.color(Color.CYAN);
        }

        // Dibujamos las Polilineas en el Google Map para cada ruta
        mMap.addPolyline(lineOptions);

        //Agredo una marker que marca desde el vehiculo
        LatLng origen = new LatLng(Utilidades.coordenadas.getLatitudInicial(), Utilidades.coordenadas.getLongitudInicial());
        mMap.addMarker(new MarkerOptions().position(origen).icon(BitmapDescriptorFactory.fromResource(R.drawable.mi_ubicacion)));
        //LatLng origen = new LatLng(Utilidades.coordenadas.getLatitudInicial(), Utilidades.coordenadas.getLongitudInicial());
        //mMap.addMarker(new MarkerOptions().position(origen).title("Lat: "+Utilidades.coordenadas.getLatitudInicial()+" - Long: "+Utilidades.coordenadas.getLongitudInicial()));

        //LatLng destino = new LatLng(Utilidades.coordenadas.getLatitudFinal(), Utilidades.coordenadas.getLongitudFinal());
        //mMap.addMarker(new MarkerOptions().position(destino).title("Lat: "+Utilidades.coordenadas.getLatitudFinal()+" - Long: "+Utilidades.coordenadas.getLongitudFinal()));

        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center, 15));
    }

    private void calculateDirections(Marker marker){
        Log.d(TAG, "calculateDirections: calculating directions.");

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(geoApiContext);

        directions.alternatives(true);
        directions.origin(
                new com.google.maps.model.LatLng(
                        latitud,
                        longitud
                )
        );
        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d(TAG, "onResult: routes: " + result.routes[0].toString());
                Log.d(TAG, "onResult: duration: " + result.routes[0].legs[0].duration.toString());
                Log.d(TAG, "onResult: distance: " + result.routes[0].legs[0].distance.toString());
                Log.d(TAG, "onResult: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "onFailure: " + e.getMessage() );

            }
        });
    }

    private void addPolylinesToMap(final DirectionsResult result){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + result.routes.length);

                for(DirectionsRoute route: result.routes){
                    Log.d(TAG, "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for(com.google.maps.model.LatLng latLng: decodedPath){

//                        Log.d(TAG, "run: latlng: " + latLng.toString());

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(InitialActivity.this, R.color.colorAccent));
                    polyline.setClickable(true);

                }
            }
        });
    }
}