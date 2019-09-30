package co.com.ingenesys.utils;

public class Constantes {
    public static final String STRING_PREFERENCES = "co.com.ingenesys.parqueadero.utils.preferences";

    //verifica las peticiones de permiso
    public static final int PETICION_PERMISO_LOCALIZACION = 101;

    //Puerto Utilizado en la conexion
    private static final String PUERTO_HOST = ":80";
    //Dirección IP
    public static final String IP = "10.14.80.23";

    //rutas web service ~ parqueadero
    //public static final String GET_PARQUEADEROS = "http://" + IP + PUERTO_HOST + "/Parqueaderos/web/parqueadero/obtener_parqueaderos.php";
    public static final String GET_PARQUEADEROS = "https://parking-sincelejo.000webhostapp.com/web/parqueadero/obtener_parqueaderos.php";

    //ruta Api para poder obtener las rutas
    public static final String GET_RUTAS_API_GOOGLE = "https://maps.googleapis.com/maps/api/directions/json";

}
