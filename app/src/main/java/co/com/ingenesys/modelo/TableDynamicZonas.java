package co.com.ingenesys.modelo;

import android.content.Context;
import android.view.Gravity;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

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
    }

    //método que permite agregar una nueva celda a nuestra tabla
    private void newCell(){
        estado_zonas = new Switch(context);
        estado_zonas.setGravity(Gravity.CENTER);
        //estado_zonas.setTextOff("Apagado");
        //estado_zonas.setTextOn("Encendido");
        //txtCell.setTextSize(25);
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
                tableRow.addView(estado_zonas, newTableRowsParams());
            }

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
}
