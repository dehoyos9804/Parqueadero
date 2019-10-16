package co.com.ingenesys.modelo;

import android.content.Context;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

import co.com.ingenesys.R;

/**
 * Clase que permite realizar una tabla dinamica*/
public class TableDynamic {
    private TableLayout tableLayout;
    private Context context;
    private String[] header;
    private ArrayList<String[]> data;
    private TableRow tableRow;
    private TextView txtCell;
    private int indexCell;
    private int indexRow;
    private boolean multiColor = false;
    private int firstColor;
    private int secondColor;
    private int textColor;

    //constructor
    public TableDynamic(TableLayout tableLayout, Context context) {
        this.tableLayout = tableLayout;
        this.context = context;
    }

    //métodos que recibirá datos de la tabla para el encabezado
    public void addHeader(String[] header){
        this.header = header;
        createHeader();
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
        txtCell = new TextView(context);
        txtCell.setGravity(Gravity.CENTER);
        //txtCell.setTextSize(25);
    }

    //método que permite agregar el encabezado de la tabla
    private void createHeader(){
        indexCell = 0;

        newRow();

        while(indexCell < header.length){
            newCell();
            txtCell.setText(header[indexCell++]);

            tableRow.addView(txtCell, newTableRowsParams());
        }

        //agregamos la fila
        tableLayout.addView(tableRow);
    }

    //obtener las filas
    private TableRow getRow(int index){
        return (TableRow) tableLayout.getChildAt(index);
    }

    private TextView getCell(int rowIndex, int columnIndex){
        tableRow = getRow(rowIndex);

        return (TextView) tableRow.getChildAt(columnIndex);

    }

    //método que permite cambiar el color del encabezado
    public void backgroundHeader(int color){
        indexCell = 0;
        while(indexCell < header.length){
            txtCell = getCell(0, indexCell++);
            txtCell.setBackgroundColor(color);
        }
    }

    //método que permite cambiar el color de las filas de datos
    public void backgroundData(int firstColor, int secondColor){
        //recorro las filas
        for (indexRow = 1; indexRow <= data.size(); indexRow++){
            multiColor = !multiColor;
            //recorrer las columnas
            for (indexCell = 0; indexCell < header.length; indexCell++){
                txtCell = getCell(indexRow, indexCell);
                txtCell.setBackgroundColor((multiColor)? firstColor : secondColor);
            }
        }

        this.firstColor = firstColor;
        this.secondColor = secondColor;
    }

    public void reColoring(){
        indexCell = 0;
        multiColor = !multiColor;
        while(indexCell < header.length){
            txtCell = getCell(data.size()-1, indexCell++);
            txtCell.setBackgroundColor((multiColor)? firstColor : secondColor);
            txtCell.setTextColor(textColor);
        }
    }

    //método que permite crear las filas con los datos enviados
    private void createDataTable(){
        String info;
        //recorro las filas
        for (indexRow = 1; indexRow <= data.size(); indexRow++){
            newRow();
            //recorrer las columnas
            for (indexCell = 0; indexCell < header.length; indexCell++){
                newCell();
                String[] rows = data.get(indexRow - 1);

                info = (indexCell < rows.length) ? rows[indexCell] : "";

                txtCell.setText(info);

                tableRow.addView(txtCell, newTableRowsParams());
            }

            tableLayout.addView(tableRow);
        }
    }

    //metodo que permite agregar datos a fila
    public void addItems(String[] items){
        String info;
        data.add(items);
        indexCell = 0;
        newRow();

        while(indexCell < header.length){
            newCell();
            info = (indexCell < items.length) ? items[indexCell++] : "";
            txtCell.setText(info);
            tableRow.addView(txtCell, newTableRowsParams());
        }

        tableLayout.addView(tableRow, (data.size()));
        reColoring();
    }

    public void lineColor(int color){
        indexRow = 0;
        while (indexRow < data.size()){
            getRow(indexRow++).setBackgroundColor(color);
        }
    }

    public void textColorData(int color){
        for (indexRow = 1; indexRow <= data.size(); indexRow++){
            //recorrer las columnas
            for (indexCell = 0; indexCell < header.length; indexCell++){
                getCell(indexRow, indexCell).setTextColor(color);
            }
        }

        this.textColor = color;
    }

    public void textColorHeader(int color){
        indexCell = 0;
        while (indexCell < header.length){
            getCell(0, indexCell++).setTextColor(color);
        }
    }

    //método que permite colocar un margen a nuestra celdas
    private TableRow.LayoutParams newTableRowsParams(){
        TableRow.LayoutParams params = new TableRow.LayoutParams();
        params.setMargins(1,1,1,1);
        params.weight = 1;
        return params;
    }
}
