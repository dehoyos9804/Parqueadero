package co.com.ingenesys.modelo;

public class Tarifas {
    private String id;
    private String parqueadero_id;
    private String tipoTiempo;
    private String precio;
    private String tipovehiculo_id;
    private String nombre;

    //constructor
    public Tarifas(String id, String parqueadero_id, String tipoTiempo, String precio, String tipovehiculo_id, String nombre) {
        this.id = id;
        this.parqueadero_id = parqueadero_id;
        this.tipoTiempo = tipoTiempo;
        this.precio = precio;
        this.tipovehiculo_id = tipovehiculo_id;
        this.nombre = nombre;
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

    public String getTipoTiempo() {
        return tipoTiempo;
    }

    public void setTipoTiempo(String tipoTiempo) {
        this.tipoTiempo = tipoTiempo;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getTipovehiculo_id() {
        return tipovehiculo_id;
    }

    public void setTipovehiculo_id(String tipovehiculo_id) {
        this.tipovehiculo_id = tipovehiculo_id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
