package co.com.ingenesys.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.util.Base64;
import android.widget.Toast;

import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.com.ingenesys.modelo.Punto;
import co.com.ingenesys.ui.MainActivity;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

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

    /**
    *permite eliminar la sesion abierta por algún usuario
    * */
    //método que permite cerrar la sesion del usuario actual
    public static void cerrarSesion(Activity activity){
        Intent cerrarSesion = new Intent(activity, MainActivity.class);

        Preferences.savePreferenceString(activity, "", Constantes.PREFERENCIA_IDUSUARIO_CLAVE);
        Preferences.savePreferenceString(activity, "", Constantes.PREFERENCIA_CEDULA_CLAVE);
        Preferences.savePreferenceString(activity, "", Constantes.PREFERENCIA_NOMBRE_CLAVE);
        Preferences.savePreferenceString(activity, "", Constantes.PREFERENCIA_APELLIDO_CLAVE);
        Preferences.savePreferenceString(activity, "", Constantes.PREFERENCIA_TELEFONO_CLAVE);
        Preferences.savePreferenceString(activity, "", Constantes.PREFERENCIA_CORREO_CLAVE);
        Preferences.savePreferenceString(activity, "", Constantes.PREFERENCIA_GENERO_CLAVE);
        Preferences.savePreferenceString(activity, "", Constantes.PREFERENCIA_FECHA_NACIMIENTO_CLAVE);
        Preferences.savePreferenceString(activity, "", Constantes.PREFERENCIA_TIPO_USUARIO_CLAVE);
        Preferences.savePreferenceBoolean(activity, Constantes.ESTADO_PREFERENCIA_FALSE, Constantes.PREFERENCIA_MANTENER_SESION_CLAVE);

        activity.startActivity(cerrarSesion);
        activity.finish();
    }

    /**
     * Verifica los permisos del dispositivo para la camara
     */
    public static boolean checkExternalStoragePermission(final Activity activity){
        //pregunto la version de mis dispositivo android
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return true;
        }

        //si los dos permisos estan activos podemos abrir la camara
        if((activity.checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED) && (activity.checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)){
            return true;
        }

        //si se deben pedir los permisos para la camara y la escritura
        if((activity.shouldShowRequestPermissionRationale(CAMERA)) || (activity.shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))){
            AlertDialog.Builder dialogo = new AlertDialog.Builder(activity);

            dialogo.setTitle("Permisos Desactivados");
            dialogo.setMessage("Debe aceptar los permisos");
            dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    activity.requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, Constantes.CODE_PERMISON_CAMERA_AND_WRITE_STORAGE);
                }
            });

            dialogo.show();
        }else{
            activity.requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, Constantes.CODE_PERMISON_CAMERA_AND_WRITE_STORAGE);
        }
        return false;
    }

    public static void solicitarPermisoManual(final Activity activity){
        final CharSequence[] opciones = {"si", "no"};
        final AlertDialog.Builder alerta = new AlertDialog.Builder(activity);
        alerta.setTitle("¿Desea Configurar los permisos de forma manua?");
        alerta.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(opciones[which].equals("si")){
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_SETTINGS);
                    Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                    intent.setData(uri);
                    activity.startActivity(intent);
                }else {
                    Toast.makeText(activity, "Los permisos no fueron aceptados", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            }
        });
        alerta.show();
    }

    /**
     * Convierte un bitmap a cadena*/
    public static String bitmapToString(Bitmap bitmap){
        ByteArrayOutputStream array = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, array);
        byte[] imagenByte = array.toByteArray();
        String imageString = Base64.encodeToString(imagenByte, Base64.DEFAULT);

        return imageString;
    }
}
