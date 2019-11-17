package co.com.ingenesys.modelo;

public class Reportes {
    private String id;
    private String numeroVenta;
    private String fechaHoraIngreso;
    private String NoCupo;
    private String fechaHoraSalida;
    private String CEDULA;
    private String NOMBRE;
    private String APELLIDO;
    private String tarifa_id;
    private String precioTarifa;
    private String convenio_id;
    private String descuento;
    private String descuentototal;
    private String tipovehiculo;

    public Reportes(String id, String numeroVenta, String fechaHoraIngreso, String NoCupo, String fechaHoraSalida, String CEDULA, String NOMBRE, String APELLIDO, String tarifa_id, String precioTarifa, String convenio_id, String descuento, String descuentototal, String tipovehiculo) {
        this.id = id;
        this.numeroVenta = numeroVenta;
        this.fechaHoraIngreso = fechaHoraIngreso;
        this.NoCupo = NoCupo;
        this.fechaHoraSalida = fechaHoraSalida;
        this.CEDULA = CEDULA;
        this.NOMBRE = NOMBRE;
        this.APELLIDO = APELLIDO;
        this.tarifa_id = tarifa_id;
        this.precioTarifa = precioTarifa;
        this.convenio_id = convenio_id;
        this.descuento = descuento;
        this.descuentototal = descuentototal;
        this.tipovehiculo = tipovehiculo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumeroVenta() {
        return numeroVenta;
    }

    public void setNumeroVenta(String numeroVenta) {
        this.numeroVenta = numeroVenta;
    }

    public String getFechaHoraIngreso() {
        return fechaHoraIngreso;
    }

    public void setFechaHoraIngreso(String fechaHoraIngreso) {
        this.fechaHoraIngreso = fechaHoraIngreso;
    }

    public String getNoCupo() {
        return NoCupo;
    }

    public void setNoCupo(String noCupo) {
        NoCupo = noCupo;
    }

    public String getFechaHoraSalida() {
        return fechaHoraSalida;
    }

    public void setFechaHoraSalida(String fechaHoraSalida) {
        this.fechaHoraSalida = fechaHoraSalida;
    }

    public String getCEDULA() {
        return CEDULA;
    }

    public void setCEDULA(String CEDULA) {
        this.CEDULA = CEDULA;
    }

    public String getNOMBRE() {
        return NOMBRE;
    }

    public void setNOMBRE(String NOMBRE) {
        this.NOMBRE = NOMBRE;
    }

    public String getAPELLIDO() {
        return APELLIDO;
    }

    public void setAPELLIDO(String APELLIDO) {
        this.APELLIDO = APELLIDO;
    }

    public String getTarifa_id() {
        return tarifa_id;
    }

    public void setTarifa_id(String tarifa_id) {
        this.tarifa_id = tarifa_id;
    }

    public String getPrecioTarifa() {
        return precioTarifa;
    }

    public void setPrecioTarifa(String precioTarifa) {
        this.precioTarifa = precioTarifa;
    }

    public String getConvenio_id() {
        return convenio_id;
    }

    public void setConvenio_id(String convenio_id) {
        this.convenio_id = convenio_id;
    }

    public String getDescuento() {
        return descuento;
    }

    public void setDescuento(String descuento) {
        this.descuento = descuento;
    }

    public String getDescuentototal() {
        return descuentototal;
    }

    public void setDescuentototal(String descuentototal) {
        this.descuentototal = descuentototal;
    }

    public String getTipovehiculo() {
        return tipovehiculo;
    }

    public void setTipovehiculo(String tipovehiculo) {
        this.tipovehiculo = tipovehiculo;
    }
}
