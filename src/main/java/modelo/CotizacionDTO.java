package modelo;

import java.util.Date;
import java.util.List;

/**
 * Clase DTO (Data Transfer Object) para representar una cotización
 * con todos sus datos asociados.
 */
public class CotizacionDTO {
    private Integer idCotizacion;
    private Integer clienteCodigo;
    private Date fecha;
    private Integer usuarioId;
    private Double total;
    private String departamento;
    private String municipio;
    private List<ProductoCotizacionDTO> productos;

    // Constructor vacío
    public CotizacionDTO() {
    }

    // Constructor con parámetros básicos
    public CotizacionDTO(Integer clienteCodigo, Date fecha, Integer usuarioId, 
                        String departamento, String municipio) {
        this.clienteCodigo = clienteCodigo;
        this.fecha = fecha;
        this.usuarioId = usuarioId;
        this.departamento = departamento;
        this.municipio = municipio;
    }

    // Getters y Setters

    public Integer getIdCotizacion() {
        return idCotizacion;
    }

    public void setIdCotizacion(Integer idCotizacion) {
        this.idCotizacion = idCotizacion;
    }

    public Integer getClienteCodigo() {
        return clienteCodigo;
    }

    public void setClienteCodigo(Integer clienteCodigo) {
        this.clienteCodigo = clienteCodigo;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
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

    public List<ProductoCotizacionDTO> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoCotizacionDTO> productos) {
        this.productos = productos;
        // Calcular el total automáticamente al asignar productos
        if (productos != null && !productos.isEmpty()) {
            this.total = calcularTotal();
        }
    }

    /**
     * Calcula el total de la cotización basado en los productos
     * @return El total calculado
     */
    public Double calcularTotal() {
        if (productos == null || productos.isEmpty()) {
            return 0.0;
        }
        return productos.stream()
                .mapToDouble(p -> p.getCantidad() * p.getValorUnitario())
                .sum();
    }

    /**
     * Método para validar los datos básicos de la cotización
     * @return true si los datos son válidos
     */
    public boolean validar() {
        return clienteCodigo != null && 
               fecha != null && 
               usuarioId != null && 
               departamento != null && !departamento.isEmpty() &&
               municipio != null && !municipio.isEmpty() &&
               productos != null && !productos.isEmpty();
    }

    @Override
    public String toString() {
        return "CotizacionDTO{" +
                "idCotizacion=" + idCotizacion +
                ", clienteCodigo=" + clienteCodigo +
                ", fecha=" + fecha +
                ", usuarioId=" + usuarioId +
                ", total=" + total +
                ", departamento='" + departamento + '\'' +
                ", municipio='" + municipio + '\'' +
                ", productos=" + productos +
                '}';
    }
}