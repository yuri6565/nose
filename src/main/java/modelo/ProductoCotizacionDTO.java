package modelo;

public class ProductoCotizacionDTO {

    private String detalle;
    private String unidad;
    private int cantidad;
    private double valorUnitario;

    // Constructor con parámetros
    public ProductoCotizacionDTO(String detalle, String unidad, int cantidad, double valorUnitario) {
        this.detalle = detalle;
        this.unidad = unidad;
        this.cantidad = cantidad;
        this.valorUnitario = valorUnitario;
    }

    // Getters y Setters
    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(double valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    // Método para calcular subtotal
    public double getSubtotal() {
        return cantidad * valorUnitario;
    }
}
