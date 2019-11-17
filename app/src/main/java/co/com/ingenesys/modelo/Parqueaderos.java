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
    //private String Foto;
    private String Descripcion;

    //constructor
    public Parqueaderos(String id, String codigoCamaraComercio, String razonSocial, String TELEFONO, String DIRECCION, String usuario_id, String ubicacionLat, String ubicacionLon, String descripcion) {
        this.id = id;
        CodigoCamaraComercio = codigoCamaraComercio;
        RazonSocial = razonSocial;
        this.TELEFONO = TELEFONO;
        this.DIRECCION = DIRECCION;
        this.usuario_id = usuario_id;
        UbicacionLat = ubicacionLat;
        UbicacionLon = ubicacionLon;
        Descripcion = descripcion;
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
    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String Descripcion) {
        this.Descripcion = Descripcion;
    }
}
