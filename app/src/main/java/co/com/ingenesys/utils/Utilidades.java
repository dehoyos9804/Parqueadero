package co.com.ingenesys.utils;

import android.app.Activity;
import android.os.Build;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.com.ingenesys.modelo.Punto;

public class Utilidades {
    /**
    * Método que permite colocar un mensajes en pantalla
    */
    public static void showToast(Activity activity, String msg){
        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * Determina si la aplicación corre en versiones superiores o iguales
     * a Android LOLLIPOP
     *
     * @return booleano de confirmación
     */
    public static boolean materialDesign(){
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }

    /**
     * Complementos para encontrar las rutas a la cual deseamos ir
     * */
    public static List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
    public static Punto coordenadas = new Punto();
}
