package controlador;

import modelo.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CotizacionDAO {

    // Definir constantes SQL
    private static final String SQL_INSERT
            = "INSERT INTO cotizacion (detalle, unidad, cantidad, fecha, "
            + "valor_unitario, sub_total, total, usuario_id_usuario, cliente_codigo) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_SELECT
            = "SELECT * FROM cotizacion ORDER BY id_cotizacion DESC LIMIT ?, ?";

    public int guardarCotizacionDTO(CotizacionDTO cotizacionDTO) throws SQLException {
        try (Connection conn = Conexion.getConnection(); PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            for (ProductoCotizacionDTO producto : cotizacionDTO.getProductos()) {
                ps.setString(1, producto.getDetalle());
                ps.setString(2, producto.getUnidad());
                ps.setInt(3, producto.getCantidad());
                ps.setDate(4, new java.sql.Date(cotizacionDTO.getFecha().getTime()));
                ps.setDouble(5, producto.getValorUnitario());
                ps.setDouble(6, producto.getSubtotal()); // Usar el método getSubtotal()
                ps.setDouble(7, cotizacionDTO.getTotal());
                ps.setInt(8, cotizacionDTO.getUsuarioId());
                ps.setInt(9, cotizacionDTO.getClienteCodigo());
                ps.addBatch();
            }

            int[] results = ps.executeBatch();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return -1;
        }
    }

    public int guardarCotizaciones(List<Cotizacion> cotizaciones) throws SQLException {
        Connection con = null;
        try {
            con = Conexion.getConnection();
            con.setAutoCommit(false);

            String sql = "INSERT INTO cotizacion (detalle, unidad, cantidad, fecha, "
                    + "valor_unitario, sub_total, total, usuario_id_usuario, cliente_codigo) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                for (Cotizacion cot : cotizaciones) {
                    pstmt.setString(1, cot.getDetalle());
                    pstmt.setString(2, cot.getUnidad());
                    pstmt.setInt(3, cot.getCantidad());
                    pstmt.setDate(4, new java.sql.Date(cot.getFecha().getTime()));
                    pstmt.setDouble(5, cot.getValor_unitario());
                    pstmt.setDouble(6, cot.getSub_total());
                    pstmt.setDouble(7, cot.getTotal());
                    pstmt.setObject(8, cot.getUsuario_id_usuario());
                    pstmt.setObject(9, cot.getCliente_codigo());
                    pstmt.addBatch();
                }

                int[] results = pstmt.executeBatch();
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
                con.commit();
                return 1;
            }
        } finally {
            if (con != null) {
                con.close();
            }
        }
    }

    public List<Cotizacion> obtenerCotizaciones(int offset, int limit) throws SQLException {
        List<Cotizacion> cotizaciones = new ArrayList<>();
        String sql = "SELECT * FROM cotizacion ORDER BY id_cotizacion DESC LIMIT ?, ?";

        try (Connection con = Conexion.getConnection(); PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, offset);
            pstmt.setInt(2, limit);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Cotizacion cot = new Cotizacion();
                    cot.setId_cotizacion(rs.getInt("id_cotizacion"));
                    // Setear otros campos...
                    cotizaciones.add(cot);
                }
            }
        }
        return cotizaciones;
    }
}
