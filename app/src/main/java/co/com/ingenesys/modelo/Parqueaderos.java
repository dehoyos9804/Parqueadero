package co.com.ingenesys.modelo;

public class Parqueaderos {
    private String id;
    private String CodigoCamaraComercio;
    private String RazonSocial;
    private String TELEFONO;
    private String DIRECCION;
    private String usuario_id;
    private String UbicacionLat;
    private String UbicacionLon;
    private String Foto;
    private String Descripcion;
    private String horaI;
    private String horaF;
    private String diasemana;
    private String cupos;

    //constructor
    public Parqueaderos(String id, String CodigoCamaraComercio, String RazonSocial, String TELEFONO,
                        String DIRECCION, String usuario_id, String UbicacionLat, String UbicacionLon,
                        String Foto, String Descripcion, String horaI, String horaF, String diasemana, String cupos) {
        this.id = id;
        this.CodigoCamaraComercio = CodigoCamaraComercio;
        this.RazonSocial = RazonSocial;
        this.TELEFONO = TELEFONO;
        this.DIRECCION = DIRECCION;
        this.usuario_id = usuario_id;
        this.UbicacionLat = UbicacionLat;
        this.UbicacionLon = UbicacionLon;
        this.Foto = Foto;
        this.Descripcion = Descripcion;
        this.horaI = horaI;
        this.horaF = horaF;
        this.diasemana = diasemana;
        this.cupos = cupos;
    }

    //getter y setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCodigoCamaraComercio() {
        return CodigoCamaraComercio;
    }

    public void setCodigoCamaraComercio(String CodigoCamaraComercio) {
        this.CodigoCamaraComercio = CodigoCamaraComercio;
    }

    public String getRazonSocial() {
        return RazonSocial;
    }

    public void setRazonSocial(String RazonSocial) {
        this.RazonSocial = RazonSocial;
    }

    public String getTELEFONO() {
        return TELEFONO;
    }

    public void setTELEFONO(String TELEFONO) {
        this.TELEFONO = TELEFONO;
    }

    public String getDIRECCION() {
        return DIRECCION;
    }

    public void setDIRECCION(String DIRECCION) {
        this.DIRECCION = DIRECCION;
    }

    public String getUsuario_id() {
        return usuario_id;
    }

    public void setUsuario_id(String usuario_id) {
        this.usuario_id = usuario_id;
    }

    public String getUbicacionLat() {
        return UbicacionLat;
    }

    public void setUbicacionLat(String UbicacionLat) {
        this.UbicacionLat = UbicacionLat;
    }

    public String getUbicacionLon() {
        return UbicacionLon;
    }

    public void setUbicacionLon(String UbicacionLon) {
        this.UbicacionLon = UbicacionLon;
    }

    public String getFoto() {
        return Foto;
    }

    public void setFoto(String Foto) {
        this.Foto = Foto;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String Descripcion) {
        this.Descripcion = Descripcion;
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

    public String getDiasemana() {
        return diasemana;
    }

    public void setDiasemana(String diasemana) {
        this.diasemana = diasemana;
    }

    public String getCupos() {
        return cupos;
    }

    public void setCupos(String cupos) {
        this.cupos = cupos;
    }
}
