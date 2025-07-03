package modelo;

/**
 *
 * @author Personal
 */
public class Cliente {

    private int codigo; // Cambiado de id_cliente a codigo
    private String identificacion;
    private String nombre;
    private String apellido;
    private String telefono;
    private String telefono2;
    private String departamento;
    private String municipio;
    private String direccion;
    private String estado;
    private boolean activo;

    public Cliente(int codigo, String identificacion, String nombre, String apellido, String telefono, String telefono2, String departamento, String municipio, String direccion, boolean activo) {
        this.codigo = codigo; // Cambiado de id_cliente a codigo
        this.identificacion = identificacion;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.telefono2 = telefono2;
        this.departamento = departamento;
        this.municipio = municipio;
        this.direccion = direccion;
        this.activo = activo;
        this.estado = estado;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getId_cliente() {
        return codigo; // Mantén getId_cliente() para compatibilidad con la vista, pero usa codigo internamente
    }

    public void setId_cliente(int codigo) {
        this.codigo = codigo; // Cambiado de id_cliente a codigo
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getTelefono2() {
        return telefono2;
    }

    public void setTelefono2(String telefono2) {
        this.telefono2 = telefono2;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Cliente() {
    }
}
