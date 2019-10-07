package co.com.ingenesys.utils;

public class Constantes {
    public static final String STRING_PREFERENCES = "co.com.ingenesys.parqueadero.utils.preferences";

    //verifica las peticiones de permiso
    public static final int PETICION_PERMISO_LOCALIZACION = 101;

    //Puerto Utilizado en la conexion
    private static final String PUERTO_HOST = ":80";
    //Dirección IP
    public static final String IP = "192.168.43.226";

    //rutas web service ~ parqueadero
    public static final String GET_PARQUEADEROS = "http://" + IP + PUERTO_HOST + "/Parqueaderos/web/parqueadero/obtener_parqueaderos.php";
    public static final String GET_TARIFAS_PARQUEADEROS = "http://" + IP + PUERTO_HOST + "/Parqueaderos/web/parqueadero/obtener_tarifa_parqueadero.php";
    public static final String INSERT_NEW_RESERVA = "http://" + IP + PUERTO_HOST + "/Parqueaderos/web/parqueadero/insertar_new_reserva.php";
    public static final String GET_ALL_TIPO_VEHICULO = "http://" + IP + PUERTO_HOST + "/Parqueaderos/web/tipovehiculo/obtener_tipo_vehiculo.php";
    public static final String INSERT_NEW_USUARIO = "http://" + IP + PUERTO_HOST + "/Parqueaderos/web/usuario/insertar_new_usuario.php";
    public static final String GET_INICIAR_SESION = "http://" + IP + PUERTO_HOST + "/Parqueaderos/web/usuario/iniciar_sesion_user.php";
    public static final String INSERT_NEW_PARKING = "http://" + IP + PUERTO_HOST + "/Parqueaderos/web/parqueadero/insertar_new_parqueadero.php";
    //public static final String GET_PARQUEADEROS = "https://parking-sincelejo.000webhostapp.com/web/parqueadero/obtener_parqueaderos.php";

    //ruta Api para poder obtener las rutas
    public static final String GET_RUTAS_API_GOOGLE = "https://maps.googleapis.com/maps/api/directions/json";

    //code para las extras
    public static final int PARQUEADERO = 100;

    //extras
    public static final String EXTRA_PARQUEADERO_ID = "codigo_parqueadero";
    public static final String EXTRA_HORA_INICIAL = "hora_inicial";
    public static final String EXTRA_HORA_FINAL = "hora_final";

    //estado para la preferencia
    public static final boolean ESTADO_PREFERENCIA_TRUE = true;
    public static final boolean ESTADO_PREFERENCIA_FALSE = false;

    //clave para las preferencias
    public static final String PREFERENCIA_IDUSUARIO_CLAVE = "id";
    public static final String PREFERENCIA_CEDULA_CLAVE = "cedula";
    public static final String PREFERENCIA_NOMBRE_CLAVE = "nombres";
    public static final String PREFERENCIA_APELLIDO_CLAVE = "apellidos";
    public static final String PREFERENCIA_TELEFONO_CLAVE = "telefono";
    public static final String PREFERENCIA_CORREO_CLAVE = "correo";
    public static final String PREFERENCIA_GENERO_CLAVE = "genero";
    public static final String PREFERENCIA_FECHA_NACIMIENTO_CLAVE = "fechanacimiento";
    public static final String PREFERENCIA_TIPO_USUARIO_CLAVE = "tipo_usuario";
    public static final String PREFERENCIA_MANTENER_SESION_CLAVE = "mantener_sesion";

    //constante para los permisos de la camara
    public static final int CODE_PERMISON_CAMERA_AND_WRITE_STORAGE = 111;
}
