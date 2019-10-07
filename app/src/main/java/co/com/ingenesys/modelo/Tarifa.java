package co.com.ingenesys.modelo;

public class Tarifa {
    private String id;
    private String tipoTiempo;
    private String precio;
    private String nombre;

    public Tarifa(String id, String tipoTiempo, String precio, String nombre) {
        this.id = id;
        this.tipoTiempo = tipoTiempo;
        this.precio = precio;
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
