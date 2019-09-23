package co.com.ingenesys.modelo;

/**
 * Objeto que tiene  los puntos iniciales y puntos finales para poder marc*/
public class Punto {
    private double latitudInicial;
    private double longitudInicial;
    private double latitudFinal;
    private double longitudFinal;

    //constructor
    public Punto(){

    }

    //constructor con parametros
    public Punto(double latitudInicial, double longitudInicial, double latitudFinal, double longitudFinal) {
        this.latitudInicial = latitudInicial;
        this.longitudInicial = longitudInicial;
        this.latitudFinal = latitudFinal;
        this.longitudFinal = longitudFinal;
    }

    //getter y setter
    public double getLatitudInicial() {
        return latitudInicial;
    }

    public void setLatitudInicial(double latitudInicial) {
        this.latitudInicial = latitudInicial;
    }

    public double getLongitudInicial() {
        return longitudInicial;
    }

    public void setLongitudInicial(double longitudInicial) {
        this.longitudInicial = longitudInicial;
    }

    public double getLatitudFinal() {
        return latitudFinal;
    }

    public void setLatitudFinal(double latitudFinal) {
        this.latitudFinal = latitudFinal;
    }

    public double getLongitudFinal() {
        return longitudFinal;
    }

    public void setLongitudFinal(double longitudFinal) {
        this.longitudFinal = longitudFinal;
    }
}