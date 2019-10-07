package co.com.ingenesys.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintDocumentAdapter;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import co.com.ingenesys.R;
import co.com.ingenesys.utils.Constantes;
import co.com.ingenesys.utils.Preferences;
import co.com.ingenesys.utils.Utilidades;
import co.com.ingenesys.web.VolleySingleton;

import static android.app.Activity.RESULT_OK;

public class ParqueaderoFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {
    //etiqueta para la depuracion
    private static final String TAG = ParqueaderoFragment.class.getSimpleName();

    private View view;
    private GoogleMap googleMap;

    private double latitud = 0.0;
    private double longitud = 0.0;
    private String direccion = "";
    private LocationManager locationManager;

    private EditText txtLatitud;
    private EditText txtLongitud;
    private EditText txtDireccion;
    private EditText txtCodigoComercio;
    private EditText txtNombreParking;
    private EditText txtTelefono;
    private EditText txtDescripcion;

    private Button btnGuardar;
    private Button btnHabilitarEdicion;
    private Button btnAgregarFoto;
    private ImageView imagen_parking;
    private Dialog myDialog;

    private TextView txtClose;
    private LinearLayout customGaleria;
    private LinearLayout customCamera;
    private ProgressDialog loading;

    private static final int PICK_IMAGE_REQUEST = 1;
    public static final int PICK_CAMERA_REQUEST = 2;
    private Bitmap bmatp;

    //costructor del fragmento
    public ParqueaderoFragment() {

    }

    /**
     * Crea una instancia prefabricada de {@link PruebaFragment}
     *
     * @return Instancia dle fragmento
     */
    public static ParqueaderoFragment newInstance() {
        ParqueaderoFragment fragment = new ParqueaderoFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_parqueaderos, container, false);

        init();//inicia las instancias
        return view;
    }

    private void init(){

        myDialog = new Dialog(getActivity());

        txtLatitud = (EditText) view.findViewById(R.id.txtLatitud);
        txtLongitud = (EditText) view.findViewById(R.id.txtLongitud);
        txtDireccion = (EditText) view.findViewById(R.id.txtDireccion);
        txtCodigoComercio = (EditText) view.findViewById(R.id.txtCodigoComercio);
        txtNombreParking = (EditText) view.findViewById(R.id.txtNombreParking);
        txtTelefono = (EditText) view.findViewById(R.id.txtTelefono);
        txtDescripcion = (EditText) view.findViewById(R.id.txtDescripcion);
        btnHabilitarEdicion = (Button) view.findViewById(R.id.btnHabilitarEdicion);
        btnAgregarFoto = (Button) view.findViewById(R.id.btnAgregarFoto);
        btnGuardar = (Button) view.findViewById(R.id.btnGuardar);
        imagen_parking = (ImageView) view.findViewById(R.id.imagen_parking);


        btnHabilitarEdicion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //habilito los campos de texto para editar las latitudes y longitudes
                txtLatitud.setEnabled(true);
                txtLongitud.setEnabled(true);
                txtLatitud.requestFocus();
            }
        });

        btnAgregarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup();//muestro el dialogo para escoger que accion vamos a elegir entre galeria y camara
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validarDatosGUI()){
                    guardarParqueadero();
                }
            }
        });
    }

    //Abrir mi dialogo
    private void showPopup(){
        myDialog.setContentView(R.layout.dialog_custompop);
        txtClose = (TextView) myDialog.findViewById(R.id.txtClose);
        customGaleria = (LinearLayout) myDialog.findViewById(R.id.customGaleria);
        customCamera = (LinearLayout) myDialog.findViewById(R.id.customCamera);

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();

        txtClose.setOnClickListener(this);
        customGaleria.setOnClickListener(this);
        customCamera.setOnClickListener(this);
    }

    private boolean validarDatosGUI(){
        boolean estado = false;
            if(txtLatitud.getText().toString().trim().isEmpty()){
                showSnackBar("Campo latitud vacio");
                txtLatitud.requestFocus();
            }else{
                if(txtLatitud.getText().toString().trim().isEmpty()){
                    showSnackBar("Campo longitud vacio");
                    txtLongitud.requestFocus();
                }else{
                    if(txtDireccion.getText().toString().trim().isEmpty()){
                        showSnackBar("Campo Direccion vacion");
                        txtDireccion.requestFocus();
                    }else{
                        if(txtCodigoComercio.getText().toString().trim().isEmpty()){
                            showSnackBar("Campo Codigo comercio");
                            txtCodigoComercio.requestFocus();
                        }else{
                            if(txtNombreParking.getText().toString().trim().isEmpty()){
                                showSnackBar("Campo Nombre Parqueadero");
                                txtNombreParking.requestFocus();
                            }else{
                                if(txtTelefono.getText().toString().trim().isEmpty()){
                                    showSnackBar("Campo Telefono Parqueadero");
                                    txtTelefono.requestFocus();
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

    /**
     * Proyecta una {@link Snackbar} con el string usado
     *
     * @param msg Mensaje
     */
    private void showSnackBar(String msg) {
        Snackbar
                .make(view.findViewById(R.id.coordinator_parking), msg, Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.maps_parking);
        mapFragment.getMapAsync(this);
    }

    private LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            updateUbicacion(location);
            //setLocation(location);
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

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constantes.PETICION_PERMISO_LOCALIZACION);

            return;

        }else{

            //agrego el boton de mi ubicacion
            googleMap.setMyLocationEnabled(true);
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 10, listener);
            Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            updateUbicacion(location);
        }
    }

    /**
     * método que permite actualizar la ubicacion ubicacion
     * */
    private void updateUbicacion(Location location){
        if(location != null){
            latitud = location.getLatitude();
            longitud = location.getLongitude();
            addMarcador(latitud, longitud);

            setLocation(location);
        }
    }

    /**
     *método que permite agregar un nuevo marcador para el mapa
     * */
    private void addMarcador(double latitud, double longitud){
        LatLng coordenadas = new LatLng(latitud, longitud);
        CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 16);

        googleMap.addMarker(new MarkerOptions().position(coordenadas).title("Mi ubicacion").icon(BitmapDescriptorFactory.fromResource(R.drawable.parking2)));
        googleMap.animateCamera(miUbicacion);

        txtLatitud.setText(String.valueOf(latitud));
        txtLongitud.setText(String.valueOf(longitud));
    }

    /*
     * método que permite obtener la direcion de la calle a partir de la latitud y longitud
     * */
    private void setLocation(Location location){
        if(location.getLatitude() != 0.0 && location.getLongitude() != 0.0){
            try {
                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                if(!list.isEmpty()){
                    Address address = list.get(0);
                    direccion = (address.getAddressLine(0));

                    txtDireccion.setText(direccion);
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == Constantes.PETICION_PERMISO_LOCALIZACION){
            //¿permisos asignado?
            if(permissions.length > 0 && permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //mMap.setMyLocationEnabled(true);
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constantes.PETICION_PERMISO_LOCALIZACION);

                    return;

                }else{

                    //agrego el boton de mi ubicacion
                    googleMap.setMyLocationEnabled(true);
                    locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 10, listener);
                    Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

                    updateUbicacion(location);
                }
            }else{
                showAlert();
            }
        }

        if(requestCode == Constantes.CODE_PERMISON_CAMERA_AND_WRITE_STORAGE){
            if(grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                //abrir la camara
                //abrir la camara
                Intent lanzarcamara = null;
                lanzarcamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(lanzarcamara, PICK_CAMERA_REQUEST);
            }else{
                Utilidades.solicitarPermisoManual(getActivity());
            }
        }
    }

    private void showAlert(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
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

    private void tomarFotos() {
        //Vericamos los permisos para la camara
        if (Utilidades.checkExternalStoragePermission(getActivity())) {
            //creamos directorio donde se guardara la imagen tomada por el usuario

            //abrir la camara
            Intent lanzarcamara = null;
            lanzarcamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //Validomos si tenemos andriod superiores a 7.0
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                String authorities = getActivity().getApplicationContext().getPackageName() + ".provider";
                //Uri imageUri = FileProvider.getUriForFile(getActivity(), authorities, imagen);
                lanzarcamara.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            } else {
                lanzarcamara.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagen));
            }*/
            startActivityForResult(lanzarcamara, PICK_CAMERA_REQUEST);

        }

    }



    private void showFileChooser(int caso){
        switch (caso){
            case 1:
                //abrir la galeria y obtener una imagen
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Seleccionar Imagen"), PICK_IMAGE_REQUEST);
                break;
            case 2:
                tomarFotos();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txtClose:
                myDialog.dismiss();
                break;
            case R.id.customGaleria:
                showFileChooser(PICK_IMAGE_REQUEST);
                break;
            case R.id.customCamera:
                showFileChooser(PICK_CAMERA_REQUEST);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK &&  data != null && data.getData() != null){
            Uri filePath = data.getData();

            try {
                //obtengo el mapa de bits de la galeria
                bmatp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),filePath);
                //configuración del mapa de bits en ImageView
                imagen_parking.setImageBitmap(bmatp);
                myDialog.dismiss();
            }catch (IOException e){
                e.printStackTrace();
                Log.i(TAG, "Error en Foto.. "+e.getLocalizedMessage());
                Toast.makeText(this.getActivity(),"Error En Foto " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

    }


    /**
     * Guarda los cambios de una meta editada.
     * <p>
     * Si está en modo inserción, entonces crea una nueva
     * meta en la base de datos
     */
    private void guardarParqueadero() {
        //mostrar el diálogo de progreso
        loading = ProgressDialog.show(getContext(),"guardando...","Espere por favor...",false,false);

        String codigocamaracomercio = txtCodigoComercio.getText().toString().trim();
        String razonsocial = txtNombreParking.getText().toString().trim();
        String telefono = txtTelefono.getText().toString().trim();
        String direccion = txtDireccion.getText().toString().trim();
        String usuarioid = Preferences.getPreferenceString(getContext(), Constantes.PREFERENCIA_IDUSUARIO_CLAVE);
        String latitud = txtLatitud.getText().toString().trim();
        String longitud = txtLongitud.getText().toString().trim();
        String foto = "";
        String descripcion = "";


        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();// Mapeo previo
        map.put("codigocamaracomercio",codigocamaracomercio);
        map.put("razonsocial", razonsocial);
        map.put("telefono", telefono);
        map.put("direccion", direccion);
        map.put("usuarioid", usuarioid);
        map.put("latitud", latitud);
        map.put("longitud", longitud);
        map.put("foto", foto);
        map.put("descripcion", descripcion);

        // Crear nuevo objeto Json basado en el mapa
        JSONObject jobject = new JSONObject(map);
        // Depurando objeto Json...
        Log.i(TAG, "map.." + map.toString());
        Log.d(TAG, "json productor..."+jobject);

        // Actualizar datos en el servidor
        VolleySingleton.getInstance(getContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        Constantes.INSERT_NEW_PARKING,
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
                    showSnackBar(mensaje);
                    // Enviar código de éxito
                    //setResult(Activity.RESULT_OK);
                    //limpiar();
                    // Terminar actividad
                    //finish();
                    break;

                case "2":
                    //descartar el diálogo de progreso
                    loading.dismiss();
                    // Mostrar mensaje
                    showSnackBar(mensaje);
                    // Enviar código de falla
                    //setResult(Activity.RESULT_CANCELED);
                    //limpiar();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
