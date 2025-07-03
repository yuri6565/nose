/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import modelo.Caja;
import modelo.Conexion;
import modelo.Ingresos;

/**
 *
 * @author ADSO
 */
public class Ctrl_CajaIngresos {

    private Connection conn;

    public Ctrl_CajaIngresos() {
        conn = Conexion.getConnection();
    }

    public class IngresoConDetalles {

        private int idPedido;
        private String numPedido;
        private String nombreCliente;
        private String nombrePedido;
        private double montoTotal;
        private double pagado;
        private double debido;
        private List<Ingresos> abonos;
        private int codigoCliente;  // Nuevo campo
        private String telefonoCliente; // Nuevo campo
        private String direccionCliente; // Nuevo campo
        private String departamentoCliente; // Nuevo campo
        private String municipioCliente;    // Nuevo campo

        public IngresoConDetalles(int idPedido, String numPedido, String nombreCliente, String nombrePedido, double montoTotal, double pagado, double debido, List<Ingresos> abonos, int codigoCliente, String telefonoCliente, String direccionCliente, String departamentoCliente, String municipioCliente) {
            this.idPedido = idPedido;
            this.numPedido = numPedido;
            this.nombreCliente = nombreCliente;
            this.nombrePedido = nombrePedido;
            this.montoTotal = montoTotal;
            this.pagado = pagado;
            this.debido = debido;
            this.abonos = abonos;
            this.codigoCliente = codigoCliente;
            this.telefonoCliente = telefonoCliente;
            this.direccionCliente = direccionCliente;
            this.departamentoCliente = departamentoCliente;
            this.municipioCliente = municipioCliente;
        }

        public String getDepartamentoCliente() {
            return departamentoCliente;
        }

        public void setDepartamentoCliente(String departamentoCliente) {
            this.departamentoCliente = departamentoCliente;
        }

        public String getMunicipioCliente() {
            return municipioCliente;
        }

        public void setMunicipioCliente(String municipioCliente) {
            this.municipioCliente = municipioCliente;
        }

        public int getCodigoCliente() {
            return codigoCliente;
        }

        public void setCodigoCliente(int codigoCliente) {
            this.codigoCliente = codigoCliente;
        }

        public String getTelefonoCliente() {
            return telefonoCliente;
        }

        public void setTelefonoCliente(String telefonoCliente) {
            this.telefonoCliente = telefonoCliente;
        }

        public String getDireccionCliente() {
            return direccionCliente;
        }

        public void setDireccionCliente(String direccionCliente) {
            this.direccionCliente = direccionCliente;
        }

        // Getters
        public int getIdPedido() {
            return idPedido;
        }

        public String getNumPedido() {
            return numPedido;
        }

        public String getNombreCliente() {
            return nombreCliente;
        }

        public String getNombrePedido() { // Nuevo getter
            return nombrePedido;
        }

        public double getMontoTotal() {
            return montoTotal;
        }

        public double getPagado() {
            return pagado;
        }

        public double getDebido() {
            return debido;
        }

        public List<Ingresos> getAbonos() {
            return abonos;
        }
    }

    public List<IngresoConDetalles> obtenerIngresos() {
        List<IngresoConDetalles> ingresos = new ArrayList<>();
        String sql = "SELECT p.id_pedido, p.num_pedido, p.nombre AS nombre_pedido, "
                + "c.codigo AS codigo_cliente, "
                + "CONCAT(c.nombre, ' ', c.apellido) AS nombre_cliente, "
                + "c.telefono AS telefono_cliente, "
                + "c.direccion AS direccion_cliente, "
                + "c.departamento AS departamento_cliente, "
                + "c.municipio AS municipio_cliente, "
                + "COALESCE(SUM(dp.subtotal), 0) AS monto_total "
                + "FROM pedido p "
                + "LEFT JOIN cliente c ON p.cliente_codigo = c.codigo "
                + "LEFT JOIN detalle_pedido dp ON p.id_pedido = dp.pedido_id_pedido "
                + "GROUP BY p.id_pedido, p.num_pedido, p.nombre, c.codigo, c.nombre, c.apellido, "
                + "c.telefono, c.direccion, c.departamento, c.municipio";

        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int idPedido = rs.getInt("id_pedido");
                String numPedido = rs.getString("num_pedido");
                String nombreCliente = rs.getString("nombre_cliente");
                String nombrePedido = rs.getString("nombre_pedido");
                double montoTotal = rs.getDouble("monto_total");
                int codigoCliente = rs.getInt("codigo_cliente");
                String telefonoCliente = rs.getString("telefono_cliente");
                String direccionCliente = rs.getString("direccion_cliente");
                String departamentoCliente = rs.getString("departamento_cliente");
                String municipioCliente = rs.getString("municipio_cliente");

                List<Ingresos> abonos = obtenerAbonosPorPedido(idPedido);
                double pagado = abonos.stream().mapToDouble(Ingresos::getMonto).sum();
                double debido = montoTotal - pagado;

                ingresos.add(new IngresoConDetalles(
                        idPedido, numPedido, nombreCliente, nombrePedido,
                        montoTotal, pagado, debido, abonos,
                        codigoCliente, telefonoCliente, direccionCliente, departamentoCliente, municipioCliente
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ingresos;
    }

    public List<Ingresos> obtenerAbonosPorPedido(int idPedido) {
        List<Ingresos> abonos = new ArrayList<>();
        String sql = "SELECT id_abono, num_abono, fecha_pago, monto, metodo_pago, referencia, observacion, pedido_id_pedido "
                + "FROM pago_abono WHERE pedido_id_pedido = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPedido);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    abonos.add(new Ingresos(
                            rs.getInt("id_abono"),
                            rs.getInt("num_abono"), // Cambiado a rs.getInt
                            rs.getDate("fecha_pago"),
                            rs.getDouble("monto"),
                            rs.getString("metodo_pago"),
                            rs.getString("referencia"),
                            rs.getString("observacion"),
                            rs.getInt("pedido_id_pedido")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return abonos;
    }

    public boolean registrarAbono(int idPedido, double monto, String metodoPago, String referencia, String observacion) {
        // Cambiamos la consulta para obtener el máximo num_abono POR PEDIDO
        String sqlSelectMax = "SELECT COALESCE(MAX(num_abono), 0) AS max_num FROM pago_abono WHERE pedido_id_pedido = ?";
        String sqlInsert = "INSERT INTO pago_abono (pedido_id_pedido, num_abono, fecha_pago, monto, metodo_pago, referencia, observacion) "
                + "VALUES (?, ?, NOW(), ?, ?, ?, ?)";

        int newNumAbono = 1; // Valor por defecto si es el primer abono para este pedido

        try (PreparedStatement stmtSelect = conn.prepareStatement(sqlSelectMax)) {
            stmtSelect.setInt(1, idPedido); // Filtramos por pedido_id_pedido
            try (ResultSet rs = stmtSelect.executeQuery()) {
                if (rs.next()) {
                    newNumAbono = rs.getInt("max_num") + 1; // Incrementamos el máximo para este pedido
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al obtener el siguiente número de abono: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try (PreparedStatement stmt = conn.prepareStatement(sqlInsert)) {
            stmt.setInt(1, idPedido);
            stmt.setInt(2, newNumAbono);
            stmt.setDouble(3, monto);
            stmt.setString(4, metodoPago);
            stmt.setString(5, referencia);
            stmt.setString(6, observacion);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al registrar el abono: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean eliminarAbono(int idAbono) {
        String sql = "DELETE FROM pago_abono WHERE id_abono = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idAbono);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarAbono(int idAbono, double monto, String metodoPago, String referencia, String observacion) {
        String sql = "UPDATE pago_abono SET monto = ?, metodo_pago = ?, referencia = ?, observacion = ? WHERE id_abono = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, monto);
            stmt.setString(2, metodoPago);
            stmt.setString(3, referencia);
            stmt.setString(4, observacion);
            stmt.setInt(5, idAbono);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public IngresoConDetalles obtenerDetallesIngreso(int idPedido) {
        String sql = "SELECT p.id_pedido, p.num_pedido, p.nombre AS nombre_pedido, "
                + "c.codigo AS codigo_cliente, "
                + "CONCAT(c.nombre, ' ', c.apellido) AS nombre_cliente, "
                + "c.telefono AS telefono_cliente, "
                + "c.direccion AS direccion_cliente, "
                + "c.departamento AS departamento_cliente, "
                + "c.municipio AS municipio_cliente, "
                + "COALESCE(SUM(dp.subtotal), 0) AS monto_total "
                + "FROM pedido p "
                + "LEFT JOIN cliente c ON p.cliente_codigo = c.codigo "
                + "LEFT JOIN detalle_pedido dp ON p.id_pedido = dp.pedido_id_pedido "
                + "WHERE p.id_pedido = ? "
                + "GROUP BY p.id_pedido, p.num_pedido, p.nombre, c.codigo, c.nombre, c.apellido, "
                + "c.telefono, c.direccion, c.departamento, c.municipio";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPedido);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String numPedido = rs.getString("num_pedido");
                    String nombreCliente = rs.getString("nombre_cliente");
                    String nombrePedido = rs.getString("nombre_pedido");
                    double montoTotal = rs.getDouble("monto_total");
                    int codigoCliente = rs.getInt("codigo_cliente");
                    String telefonoCliente = rs.getString("telefono_cliente");
                    String direccionCliente = rs.getString("direccion_cliente");
                    String departamentoCliente = rs.getString("departamento_cliente");
                    String municipioCliente = rs.getString("municipio_cliente");

                    List<Ingresos> abonos = obtenerAbonosPorPedido(idPedido);
                    double pagado = abonos.stream().mapToDouble(Ingresos::getMonto).sum();
                    double debido = montoTotal - pagado;

                    return new IngresoConDetalles(
                            idPedido,
                            numPedido,
                            nombreCliente,
                            nombrePedido,
                            montoTotal,
                            pagado,
                            debido,
                            abonos,
                            codigoCliente,
                            telefonoCliente,
                            direccionCliente,
                            departamentoCliente,
                            municipioCliente
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al obtener detalles del ingreso: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    public Ingresos obtenerAbonoPorId(int idAbono) {
        String sql = "SELECT * FROM pago_abono WHERE id_abono = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idAbono);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Ingresos(
                            rs.getInt("id_abono"),
                            rs.getInt("num_abono"),
                            rs.getDate("fecha_pago"),
                            rs.getDouble("monto"),
                            rs.getString("metodo_pago"),
                            rs.getString("referencia"),
                            rs.getString("observacion"),
                            rs.getInt("pedido_id_pedido")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean eliminarIngreso(int idPedido) {
        // 1. Primero verificar si el pago está completo
        IngresoConDetalles ingreso = obtenerDetallesIngreso(idPedido);

        if (ingreso == null) {
            JOptionPane.showMessageDialog(null, "No se encontró el pedido con ID: " + idPedido,
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validar que el pago esté completo (debido <= 0)
        if (ingreso.getDebido() > 0) {
            JOptionPane.showMessageDialog(null,
                    "No se puede eliminar el pedido #" + ingreso.getNumPedido()
                    + "\nRazón: Tiene un saldo pendiente de " + ingreso.getDebido(),
                    "Pago Incompleto", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        Connection conn = null;
        try {
            conn = Conexion.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción

            // 2. Eliminar primero los abonos relacionados
            if (!eliminarAbonosPorPedido(conn, idPedido)) {
                conn.rollback();
                return false;
            }

            // 3. Eliminar el registro de caja principal
            if (!eliminarRegistroCaja(conn, idPedido)) {
                conn.rollback();
                return false;
            }

            conn.commit(); // Confirmar transacción
            return true;

        } catch (SQLException ex) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException e) {
                System.err.println("Error al hacer rollback: " + e.getMessage());
            }
            JOptionPane.showMessageDialog(null, "Error al eliminar ingreso: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException ex) {
                System.err.println("Error al cerrar conexión: " + ex.getMessage());
            }
        }
    }

// Método auxiliar para eliminar abonos de un pedido
    private boolean eliminarAbonosPorPedido(Connection conn, int idPedido) throws SQLException {
        String sql = "DELETE FROM pago_abono WHERE pedido_id_pedido = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPedido);
            stmt.executeUpdate();
            return true; // Consideramos éxito incluso si no había abonos (0 filas afectadas)
        }
    }

// Método auxiliar para eliminar registro de caja
    private boolean eliminarRegistroCaja(Connection conn, int idPedido) throws SQLException {
        String sql = "DELETE FROM caja WHERE id_pedido = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPedido);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

}
