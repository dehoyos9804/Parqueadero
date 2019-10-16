package co.com.ingenesys.modelo;

public class Capacidad {
    private String id;
    private String parqueadero_id;
    private String tipoVehiculo_id;
    private String cupos;

    //constructor
    public Capacidad(String id, String parqueadero_id, String tipoVehiculo_id, String cupos) {
        this.id = id;
        this.parqueadero_id = parqueadero_id;
        this.tipoVehiculo_id = tipoVehiculo_id;
        this.cupos = cupos;
    }

    //getter y setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParqueadero_id() {
        return parqueadero_id;
    }

    public void setParqueadero_id(String parqueadero_id) {
        this.parqueadero_id = parqueadero_id;
    }

    public String getTipovehiculo_id() {
        return tipoVehiculo_id;
    }

    public void setTipovehiculo_id(String tipovehiculo_id) {
        this.tipoVehiculo_id = tipovehiculo_id;
    }

    public String getCupos() {
        return cupos;
    }

    public void setCupos(String cupos) {
        this.cupos = cupos;
    }
}
