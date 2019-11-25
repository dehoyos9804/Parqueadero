package co.com.ingenesys.modelo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import co.com.ingenesys.R;
import co.com.ingenesys.fragment.ZonasFragment;
import co.com.ingenesys.utils.Constantes;
import co.com.ingenesys.utils.Utilidades;
import co.com.ingenesys.web.VolleySingleton;

public class TableDynamicZonas {
    private TableLayout tableLayout;
    private Context context;
    private TableRow tableRow;
    private Switch estado_zonas;
    private ArrayList<String[]> data;
    private int index_row;
    private int index_column;

    //constructor
    public TableDynamicZonas(TableLayout tableLayout, Context context) {
        this.tableLayout = tableLayout;
        this.context = context;
    }

    //método que reibirá datos de la tabla para las celdas
    public void addData(ArrayList<String[]> data){
        this.data = data;
        createDataTable();
    }

    //método que permite agregar una nueva fila a nuestra tabla
    private void newRow(){
        tableRow = new TableRow(context);
        //tableRow.setBackground(context.getResources().getDrawable(R.drawable.ic_backgroup_table));
    }

    //método que permite agregar una nueva celda a nuestra tabla
    private void newCell(){
        estado_zonas = new Switch(context);
        estado_zonas.setGravity(Gravity.CENTER);
        estado_zonas.setBackground(context.getResources().getDrawable(R.drawable.ic_backgroup_table));
        estado_zonas.setTextOff("off");
        estado_zonas.setTextOn("on");
    }

    //método que permite crear las filas con los datos enviados
    private void createDataTable(){
        //String info;
        int numero_fila = 0;
        //recorro las filas
        for (index_column = 0; index_column < data.size(); index_column += 4){
            newRow();
            //recorrer las columnas
            for (index_row = index_column; index_row < (index_column + 4); index_row++){
                newCell();
                if(index_row < data.size()) {
                    final String[] rows = data.get(index_row);
                    final boolean ischechek = ((rows[2].equalsIgnoreCase("ocupado")) ? true : false);
                    estado_zonas.setChecked(ischechek);
                    estado_zonas.setText(rows[1]);
                    estado_zonas.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String estado;
                            if(ischechek){
                                estado = "desocupado";
                            }else{
                                estado = "ocupado";
                            }

                            updateEstadoZona(rows[0], estado, rows[1]);
                        }
                    });
                }else{
                    estado_zonas.setEnabled(false);
                }

                tableRow.addView(estado_zonas, newTableRowsParams());

            }
            //tableLayout.getChildAt(numero_fila).setBackgroundColor(Color.BLACK);
            tableLayout.addView(tableRow);
            numero_fila++;
        }
    }

    public void lineColor(int color){
        index_row = 0;
        while (index_row < data.size()){
            tableLayout.getChildAt(index_row++).setBackgroundColor(color);
        }
    }

    //obtener las filas
    private TableRow getRow(int index){
        return (TableRow) tableLayout.getChildAt(index);
    }

    //método que permite colocar un margen a nuestra celdas
    private TableRow.LayoutParams newTableRowsParams(){
        TableRow.LayoutParams params = new TableRow.LayoutParams();
        params.setMargins(1,1,1,1);
        params.weight = 1;
        return params;
    }

    /**
     * Actualiza los cambios de una zona.
     */
    private void updateEstadoZona(String idzona, String estado, String numero_zonas) {

        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();// Mapeo previo
        map.put("idzona",idzona);
        map.put("estado", estado);
        map.put("numero_zona", numero_zonas);

        // Crear nuevo objeto Json basado en el mapa
        JSONObject jobject = new JSONObject(map);
        // Depurando objeto Json...
        Log.i("TAG", "map.." + map.toString());
        Log.d("TAG", "json productor..."+jobject);

        // Actualizar datos en el servidor
        VolleySingleton.getInstance(context).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        Constantes.UPDATE_ESTADO_ZONA,
                        jobject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.i("TAG", "Ver Response-->"+response);
                                // Procesar la respuesta del servidor
                                procesarRespuestaUpdate(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //descartar el diálogo de progreso
                                Log.e("TAG", "Error Volley: " + error.getLocalizedMessage());
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
    private void procesarRespuestaUpdate(JSONObject response) {

        try {
            // Obtener estado
            String estado = response.getString("estado");
            // Obtener mensaje
            String mensaje = response.getString("mensaje");

            switch (estado) {
                case "1":
                    // Mostrar mensaje
                    Utilidades.showToast((Activity) context, mensaje);
                    break;

                case "2":
                    // Mostrar mensaje
                    Utilidades.showToast((Activity) context, mensaje);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
