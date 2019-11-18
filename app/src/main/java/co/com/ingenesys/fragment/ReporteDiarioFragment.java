package co.com.ingenesys.fragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import co.com.ingenesys.R;
import co.com.ingenesys.adapter.AdaptadorConvenios;
import co.com.ingenesys.adapter.AdaptadorReporteDiarios;
import co.com.ingenesys.modelo.Convenios;
import co.com.ingenesys.modelo.Reportes;
import co.com.ingenesys.modelo.TemplatePDF;
import co.com.ingenesys.utils.Constantes;
import co.com.ingenesys.utils.DateDialog;
import co.com.ingenesys.utils.Preferences;
import co.com.ingenesys.utils.SelectDateFragment;
import co.com.ingenesys.utils.Utilidades;
import co.com.ingenesys.web.VolleySingleton;

public class ReporteDiarioFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = MisConveniosFragment.class.getSimpleName();
    //adaptador del RecicleView
    private AdaptadorReporteDiarios adapter;
    //instancia global de RecicleView
    private RecyclerView recyclerView;
    //private TextView emptyView;
    //instancia global del administrador
    private RecyclerView.LayoutManager lManager;
    private TextView data_empty_reporte;
    private Gson gson = new Gson();

    private TextView txtFechaDiaria;
    private TextView txtTotal;

    private double total = 0.0;

    private TemplatePDF templatePDF;
    private String[] headers = {"# Venta", "Fecha Ingreso", "Fecha Salida", "Cedula", "Usuario", "Precio", "Vehiculo", "Subtotal"};
    private String shortText = "Información General";
    private String longText = "Reporte de ventas diarias, filtrando por fecha especificadas";

    private ProgressDialog loading = null;
    private View view;

    DialogFragment newFragment;
    //Obtener fecha actual del sistema
    final Calendar c= Calendar.getInstance();
    int year=c.get(Calendar.YEAR);
    int month=c.get(Calendar.MONTH);
    int day=c.get(Calendar.DAY_OF_MONTH);

    //Constructor
    public ReporteDiarioFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_reporte_diario, container, false);
        setHasOptionsMenu(true);
        init();
        return view;
    }

    //instacion todas los elementos xml
    private void init(){
        recyclerView = (RecyclerView) view.findViewById(R.id.reciclador_reporte);
        recyclerView.setHasFixedSize(true);

        //Usar el Administrador para LinearLayout
        lManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(lManager);
        data_empty_reporte = (TextView) view.findViewById(R.id.data_empty_reporte);
        txtFechaDiaria = (TextView) view.findViewById(R.id.txtFechaDiaria);
        txtTotal = (TextView) view.findViewById(R.id.txtTotal);

        txtFechaDiaria.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getHTTP("2019-11-17", "2019-11-18");
    }

    /*
     * Carga el adaptador con las Consultas obtenidas
     * en la respuesta
     */
    public void getHTTP(String fechainicial, String fechafinal) {
        //mostrar el diálogo de progreso
        loading = ProgressDialog.show(getActivity(),"guardando...","Espere por favor...",false,false);
        String parqueadero_id = Preferences.getPreferenceString(getContext(), Constantes.PREFERENCIA_PARQUEADERO_ID);
        String newURL = Constantes.GET_REPORTE_VENTA + "?parqueadero_id=" + parqueadero_id + "&fechainicial=" + fechainicial + "&fechafinal=" + fechafinal;
        // Petición GET
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
                                        data_empty_reporte.setText("cargando...");
                                        procesarRespuestaHTTP(response);
                                        Log.i(TAG, "processanddo respuesta..." + response);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        data_empty_reporte.setText(getActivity().getResources().getString(R.string.empty_list));
                                        loading.dismiss();
                                        showSnackBar("Error al cargar los datos: " + error.toString());
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
                        loading.dismiss();
                        // Obtener array "consulta" Json
                        JSONArray mensaje = response.getJSONArray("tbl_registros");;
                        // Parsear con Gson
                        Reportes[] reportes = gson.fromJson(mensaje.toString(), Reportes[].class);

                        // Inicializar adaptador
                        adapter = new AdaptadorReporteDiarios(Arrays.asList(reportes), getContext());

                        // Setear adaptador a la lista
                        recyclerView.setAdapter(adapter);
                        data_empty_reporte.setText("");
                        addTotal(Arrays.asList(reportes));

                        if (Utilidades.checkExternalStoragePermission(getActivity())) {
                            templatePDF = new TemplatePDF(getContext());
                            templatePDF.openDocument();
                            templatePDF.addMetaData("Cliente", "Ventas", "C.U.N");
                            templatePDF.addTitles("REPORTE DE VENTA DIARIAS", "Venta individual de parqueaderos", "2019-11-17");
                            templatePDF.addParagraph(shortText);
                            templatePDF.addParagraph(longText);
                            templatePDF.createTable(headers, getCliente(Arrays.asList(reportes)));
                            templatePDF.closeDocument();
                        }

                    } catch (JSONException e) {
                        Log.i(TAG, "Error al llenar Adaptador " + e.getLocalizedMessage());
                    }
                    break;
                case "2":
                    String mensaje2 = response.getString("mensaje");
                    loading.dismiss();
                    // Inicializar adaptador
                    adapter = null;

                    if (Utilidades.checkExternalStoragePermission(getActivity())) {
                        templatePDF = new TemplatePDF(getContext());
                        templatePDF.openDocument();
                        templatePDF.addMetaData("Cliente", "Ventas", "C.U.N");
                        templatePDF.addTitles("REPORTE DE VENTA DIARIAS", "Venta individual de parqueaderos", "2019-11-17");
                        templatePDF.addParagraph(shortText);
                        templatePDF.addParagraph(longText);
                        templatePDF.closeDocument();
                    }
                    // Setear adaptador a la lista
                    recyclerView.setAdapter(adapter);
                    data_empty_reporte.setText("");
                    data_empty_reporte.setText(getActivity().getResources().getString(R.string.empty_list));
                    showSnackBar(mensaje2);
                    break;
                case "3":
                    String mensaje3 = response.getString("mensaje");
                    loading.dismiss();
                    data_empty_reporte.setText(getActivity().getResources().getString(R.string.empty_list));
                    showSnackBar(mensaje3);
                    break;
            }
        } catch (JSONException je) {
            Log.d(TAG, je.getMessage());
        }
    }

    /**
     * Proyecta una {@link Snackbar} con el string usado
     *
     * @param msg Mensaje
     */
    private void showSnackBar(String msg) {
        Snackbar
                .make(view.findViewById(R.id.coordinator_reporte), msg, Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_reporte, menu);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txtFechaDiaria:
                String tag = "DatePicker";
                //new DateDialog().show(getActivity().getSupportFragmentManager(),tag);
                //newFragment = new SelectDateFragment();
                //newFragment.show(getFragmentManager(), tag);
                    DatePickerDialog datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd", Locale.getDefault());
                            Date date = null;
                            Date fecha_final = null;

                            final int mesActual = month + 1;
                            String diaFormat = (dayOfMonth < 10) ? 0 + String.valueOf(dayOfMonth) : String.valueOf(dayOfMonth);
                            String mesFormat = (mesActual< 10) ? 0 + String.valueOf(mesActual) : String.valueOf(mesActual);
                            try{
                                date = dateFormat.parse(year + "-" + mesFormat + "-" + diaFormat);
                                fecha_final = dateFormat.parse(year + "-" + mesFormat + "-" + (diaFormat+1));
                            }catch (ParseException e){
                                e.printStackTrace();
                            }

                            String outDate = dateFormat.format(date);
                            String fechaf = dateFormat.format(fecha_final);
                            txtFechaDiaria.setText(outDate);
                            Log.i("ONDATE", fechaf);
                            getHTTP(outDate, fechaf);
                        }
                    }, year, month, day);
                    datePicker.show();
                break;
        }
    }


    private void addTotal(List<Reportes> reportes){
        total = 0.0;

        for (int i = 0; i < reportes.size(); i++){
            total += Double.parseDouble(reportes.get(i).getDescuentototal());
        }

        txtTotal.setText(" $" + total);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == Constantes.CODE_PERMISON_CAMERA_AND_WRITE_STORAGE){
            if(grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                //abrir la camara
                //abrir la camara
                Intent lanzarcamara = null;
                lanzarcamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(lanzarcamara, 5);
            }else{
                Utilidades.solicitarPermisoManual(getActivity());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_pdf:
                templatePDF.viewPDF();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private ArrayList<String[]> getCliente(List<Reportes> reportes){
        ArrayList<String[]> rows = new ArrayList<>();

        for (int i = 0; i< reportes.size(); i++){
            rows.add(new String[]{reportes.get(i).getNumeroVenta(),
                    reportes.get(i).getFechaHoraIngreso(),
                    reportes.get(i).getFechaHoraSalida(),
                    reportes.get(i).getCEDULA(),
                    reportes.get(i).getNOMBRE() + " " + reportes.get(i).getAPELLIDO(),
                    reportes.get(i).getPrecioTarifa(),
                    reportes.get(i).getTipovehiculo(),
                    reportes.get(i).getDescuentototal()});
        }

        return rows;
    }
}
