package co.com.ingenesys.modelo;

public class Usuarios {
    private String id;
    private String CEDULA;
    private String NOMBRE;
    private String APELLIDO;
    private String TELEFONO;
    private String CORREO;
    private String GENERO;
    private String FNACIMIENTO;
    private String CONTRASEÑA;
    private String tipousuario;

    public Usuarios(String id, String CEDULA, String NOMBRE, String APELLIDO, String TELEFONO, String CORREO, String GENERO, String FNACIMIENTO, String CONTRASEÑA, String tipousuario) {
        this.id = id;
        this.CEDULA = CEDULA;
        this.NOMBRE = NOMBRE;
        this.APELLIDO = APELLIDO;
        this.TELEFONO = TELEFONO;
        this.CORREO = CORREO;
        this.GENERO = GENERO;
        this.FNACIMIENTO = FNACIMIENTO;
        this.CONTRASEÑA = CONTRASEÑA;
        this.tipousuario = tipousuario;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTELEFONO() {
        return TELEFONO;
    }

    public void setTELEFONO(String TELEFONO) {
        this.TELEFONO = TELEFONO;
    }

    public String getCORREO() {
        return CORREO;
    }

    public void setCORREO(String CORREO) {
        this.CORREO = CORREO;
    }

    public String getGENERO() {
        return GENERO;
    }

    public void setGENERO(String GENERO) {
        this.GENERO = GENERO;
    }

    public String getFNACIMIENTO() {
        return FNACIMIENTO;
    }

    public void setFNACIMIENTO(String FNACIMIENTO) {
        this.FNACIMIENTO = FNACIMIENTO;
    }

    public String getCONTRASEÑA() {
        return CONTRASEÑA;
    }

    public void setCONTRASEÑA(String CONTRASEÑA) {
        this.CONTRASEÑA = CONTRASEÑA;
    }

    public String getTipousuario() {
        return tipousuario;
    }

    public void setTipousuario(String tipousuario) {
        this.tipousuario = tipousuario;
    }
}
