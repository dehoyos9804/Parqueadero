package co.com.ingenesys.modelo;

public class CuposParqueaderos {
    private String id;
    private String tipoVehiculo_id;
    private String nombre;
    private String cupos;

    //constructor
    public CuposParqueaderos(String id, String tipoVehiculo_id, String nombre, String cupos) {
        this.id = id;
        this.tipoVehiculo_id = tipoVehiculo_id;
        this.nombre = nombre;
        this.cupos = cupos;
    }

    //getter y setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipoVehiculo_id() {
        return tipoVehiculo_id;
    }

    public void setTipoVehiculo_id(String tipoVehiculo_id) {
        this.tipoVehiculo_id = tipoVehiculo_id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCupos() {
        return cupos;
    }

    public void setCupos(String cupos) {
        this.cupos = cupos;
    }
}
