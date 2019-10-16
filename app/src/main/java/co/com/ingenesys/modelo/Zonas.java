package co.com.ingenesys.modelo;

public class Zonas {
    private String id;
    private String numero_zona;
    private String estado;
    private String capacidad_id;

    //constructor
    public Zonas(String id, String numero_zona, String estado, String capacidad_id) {
        this.id = id;
        this.numero_zona = numero_zona;
        this.estado = estado;
        this.capacidad_id = capacidad_id;
    }

    //getter y setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumero_zona() {
        return numero_zona;
    }

    public void setNumero_zona(String numero_zona) {
        this.numero_zona = numero_zona;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCapacidad_id() {
        return capacidad_id;
    }

    public void setCapacidad_id(String capacidad_id) {
        this.capacidad_id = capacidad_id;
    }
}
