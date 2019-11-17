package co.com.ingenesys.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import co.com.ingenesys.R;
import co.com.ingenesys.modelo.AdminParqueadero;
import co.com.ingenesys.modelo.Capacidad;
import co.com.ingenesys.modelo.Horarios;
import co.com.ingenesys.modelo.Tarifas;
import co.com.ingenesys.modelo.Usuarios;
import co.com.ingenesys.utils.Constantes;
import co.com.ingenesys.utils.Preferences;
import co.com.ingenesys.utils.Utilidades;
import co.com.ingenesys.web.VolleySingleton;

public class DetalleParqueaderoFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    //etiqueta para la depuracion
    private static final String TAG = DetalleParqueaderoFragment.class.getSimpleName();


    private TextView txtRazonSocial;
    private TextView txtCamaraComercio;
    private TextView txtTelefono;
    private TextView txtHorario;
    private TextView txtCapacidades;
    private TextView txtDisponibles;
    private FloatingActionButton fab;
    private TextView txtDescripcion;
    private TextView txtDireccion;
    private TextView text_tarifas;

    private double latitud = 0.0;
    private double longitud = 0.0;

    private ViewGroup layout_grupo;

    private Gson gson = new Gson();

    private GoogleMap map;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_detalle_parking, container, false);

        init();//inicia las instancias
        return view;
    }

    private void init(){
        txtRazonSocial = (TextView) view.findViewById(R.id.txtRazonSocial);
        txtCamaraComercio = (TextView) view.findViewById(R.id.txtCamaraComercio);
        txtTelefono = (TextView) view.findViewById(R.id.txtTelefono);
        txtHorario = (TextView) view.findViewById(R.id.txtHorario);
        txtCapacidades = (TextView) view.findViewById(R.id.txtCapacidades);
        txtDisponibles = (TextView) view.findViewById(R.id.txtDisponibles);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        txtDescripcion = (TextView) view.findViewById(R.id.txtDescripcion);
        txtDireccion = (TextView) view.findViewById(R.id.txtDireccion);
        text_tarifas = (TextView) view.findViewById(R.id.text_tarifas);
        layout_grupo = (ViewGroup) view.findViewById(R.id.layer_tarifa_admin);

        peticionHTTP();
    }

    //costructor del fragmento
    public DetalleParqueaderoFragment() {

    }

    /**
     * Crea una instancia prefabricada de {@link PruebaFragment}
     *
     * @return Instancia dle fragmento
     */
    public static DetalleParqueaderoFragment newInstance() {
        DetalleParqueaderoFragment fragment = new DetalleParqueaderoFragment();
        return fragment;
    }

    /**
     * Proyecta una {@link Snackbar} con el string usado
     *
     * @param msg Mensaje
     */
    private void showSnackBar(String msg) {
        Snackbar
                .make(view.findViewById(R.id.coordinator_detalle_parking), msg, Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.maps_detalle_parking);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constantes.PETICION_PERMISO_LOCALIZACION);

            return;

        }else{

            //agrego el boton de mi ubicacion
            addMarcador(latitud, longitud);
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
                    addMarcador(9.30869741, -75.40327802);
                }
            }else{
                showAlert();
            }
        }

    }

    /**
     *método que permite agregar un nuevo marcador para el mapa
     * */
    private void addMarcador(double latitud, double longitud){
        LatLng coordenadas = new LatLng(latitud, longitud);
        CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(coordenadas, 16);

        map.addMarker(new MarkerOptions().position(coordenadas).title("Mi ubicacion").icon(BitmapDescriptorFactory.fromResource(R.drawable.parking2)));
        map.animateCamera(miUbicacion);

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

    private void peticionHTTP(){

        //Añadir parametros a la URL de webservice
        String idusuario = Preferences.getPreferenceString(getActivity(), Constantes.PREFERENCIA_IDUSUARIO_CLAVE);
        String newURL = Constantes.GET_DETALLE_PARQUEADERO + "?usuario_id=" + idusuario;

        //petición GET
        VolleySingleton.
                getInstance(getActivity()).
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
                        JSONObject datos_parking = response.getJSONObject("tbl_parqueaderos");
                        JSONArray datos_horarios = response.getJSONArray("tbl_horarios");
                        JSONArray datos_tarifas = response.getJSONArray("tbl_tarifas");
                        JSONArray datos_capacidad = response.getJSONArray("tbl_capacidad");
                        //showSnackBar("capacidad" + datos_capacidad.toString());
                        //Parsear objeto
                        AdminParqueadero parking = gson.fromJson(datos_parking.toString(),AdminParqueadero.class);
                        Horarios[] horarios = gson.fromJson(datos_horarios.toString(), Horarios[].class);
                        Tarifas[] tarifas = gson.fromJson(datos_tarifas.toString(), Tarifas[].class);
                        Capacidad[] capacidad = gson.fromJson(datos_capacidad.toString(), Capacidad[].class);

                        addDatosParqueadero(parking);//agregamos datos perteneciente al parqueadero
                        addHorariosParqueadero(Arrays.asList(horarios));//agregaa datos perteneciente al horario del parqueadero
                        addTarifaParqueadero(Arrays.asList(tarifas));//agrega datos perteneciente a la tarifa del parqueadero
                        addCapacidadParqueadero(Arrays.asList(capacidad));//agrega datos pertenenciente a la capacidad del parqueadero

                    }catch (JSONException e){
                        Log.i(TAG,"Error al llenar Adaptador " +e.getLocalizedMessage());
                    }
                    break;
                case "2":
                    String mensaje2 = response.getString("mensaje");
                    showSnackBar(mensaje2);
                    break;
                case "3":
                    String mensaje3 = response.getString("mensaje");
                    break;
            }
        }catch (JSONException je){
            Log.d(TAG, je.getMessage());
        }
    }

    private void addDatosParqueadero(AdminParqueadero p){
        txtRazonSocial.setText(p.getRazonSocial());
        txtCamaraComercio.setText(p.getCodigoCamaraComercio());
        txtTelefono.setText(p.getTELEFONO());
        txtDescripcion.setText(p.getDescripcion());
        txtDireccion.setText(p.getDIRECCION());

        latitud = Double.parseDouble(p.getUbicacionLat());
        longitud = Double.parseDouble(p.getUbicacionLon());

        //guardo las preferencia para mi parqueadero_id en memoria
        Preferences.savePreferenceString(getContext(), p.getId(), Constantes.PREFERENCIA_PARQUEADERO_ID);
    }

    private void addHorariosParqueadero(List<Horarios> horario){
        if (horario.isEmpty()){
            txtHorario.setText("Actualmente no se han definido horarios");
        }else{
            String horaI = "";
            String horaF = "";
            String diasemanaI = "";
            String diasemanaF = "";

            for (int i = 0; i < horario.size();i++){
                if (i==0){
                    horaI = horario.get(i).getHoraI();
                    diasemanaI = horario.get(i).getDiasemana();
                }

                if(i == (horario.size() - 1)){
                    horaF = horario.get(i).getHoraF();
                    diasemanaF = horario.get(i).getDiasemana();
                }
            }

            txtHorario.setText(horaI + " - " + horaF + " " + diasemanaI + " - " +diasemanaF);
        }
    }

    private void addTarifaParqueadero(List<Tarifas> t){
        if(t.isEmpty()){
            text_tarifas.setTextColor(getResources().getColor(R.color.colorPrimary));
            text_tarifas.setText("Falta Agregar Tarifas");
        }else{
            for (int i = 0; i < t.size(); i++){
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                int id = R.layout.layout_detalle_tarifa;
                LinearLayout linearLayout = (LinearLayout) inflater.inflate(id,null, false);

                //layer_tarifa = (LinearLayout) linearLayout.findViewById(R.id.layer_tarifa);
                ImageView icono_tipo_vehiculo = (ImageView) linearLayout.findViewById(R.id.icono_tipo_vehiculo);
                TextView txtTipoVehiculo = (TextView) linearLayout.findViewById(R.id.txtTipoVehiculo);
                TextView txtPrecioTiempo = (TextView) linearLayout.findViewById(R.id.txtPrecioTiempo);

                txtTipoVehiculo.setText(t.get(i).getNombre());
                txtPrecioTiempo.setText(("$" + t.get(i).getPrecio() + " " + t.get(i).getTipoTiempo()));

                layout_grupo.addView(linearLayout);
            }
        }
    }

    private void addCapacidadParqueadero(List<Capacidad> c){
        if(c.isEmpty()){
            txtCapacidades.setTextColor(getResources().getColor(R.color.colorPrimary));
            txtCapacidades.setText("Falta Agregar Las Capacidades");
            txtDisponibles.setText("");
        }else{
            txtCapacidades.setText("Zonas: " + c.get(0).getCupos());
            txtDisponibles.setText(c.get(0).getEstado() + " Disponibles");
        }
    }
}
