package co.com.ingenesys.modelo;

public class Horarios {
    private String id;
    private String parqueadero_id;
    private String diasemana;
    private String horaI;
    private String horaF;

    //constructor
    public Horarios(String id, String parqueadero_id, String diasemana, String horaI, String horaF) {
        this.id = id;
        this.parqueadero_id = parqueadero_id;
        this.diasemana = diasemana;
        this.horaI = horaI;
        this.horaF = horaF;
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

    public String getDiasemana() {
        return diasemana;
    }

    public void setDiasemana(String diasemana) {
        this.diasemana = diasemana;
    }

    public String getHoraI() {
        return horaI;
    }

    public void setHoraI(String horaI) {
        this.horaI = horaI;
    }

    public String getHoraF() {
        return horaF;
    }

    public void setHoraF(String horaF) {
        this.horaF = horaF;
    }
}
