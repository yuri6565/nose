package controlador;

import modelo.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class Ctrl_Cotizacion {

    public static final int USUARIO_ID = 1; // O obtén este valor de la sesión

    private final CotizacionDAO cotizacionDAO;

    public Ctrl_Cotizacion() {
        this.cotizacionDAO = new CotizacionDAO();
    }

    public int guardarCotizacion(Integer clienteCodigo, DefaultTableModel modelo, double total) throws SQLException {
        CotizacionDTO dto = new CotizacionDTO();
        dto.setClienteCodigo(clienteCodigo);
        dto.setFecha(new java.util.Date());
        dto.setUsuarioId(USUARIO_ID);
        dto.setTotal(total);

        // Convertir tabla a lista de productos
        for (int i = 0; i < modelo.getRowCount(); i++) {
            ProductoCotizacionDTO producto = new ProductoCotizacionDTO(
                    modelo.getValueAt(i, 0).toString(),
                    modelo.getValueAt(i, 1).toString(),
                    Integer.parseInt(modelo.getValueAt(i, 2).toString()),
                    Double.parseDouble(modelo.getValueAt(i, 3).toString().replace("$", ""))
            );
            dto.getProductos().add(producto);
        }

        return cotizacionDAO.guardarCotizacionDTO(dto);
    }

    public List<Cotizacion> obtenerCotizaciones(int offset, int limit) throws SQLException {
        return cotizacionDAO.obtenerCotizaciones(offset, limit);
    }
}
