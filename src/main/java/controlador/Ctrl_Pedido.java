/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import modelo.Conexion;
import modelo.Pedido;
import modelo.PedidoDetalle;

/**
 *
 * @author ZenBook
 */
public class Ctrl_Pedido {

    // Clase para combinar pedido y nombre del cliente
    public static class MaterialConDetalles {

        private Pedido pedido;
        private String nombreCliente;

        public MaterialConDetalles(Pedido pedido, String nombreCliente) {
            this.pedido = pedido;
            this.nombreCliente = nombreCliente;
        }

        public Pedido getPedido() {
            return pedido;
        }

        public String getNombreCliente() {
            return nombreCliente != null ? nombreCliente : "Sin cliente";
        }
    }

    public String generarCodigoPedido(int idCliente) {
        // 1. Obtener número del cliente (desde INT en BD)
        String numeroCliente = obtenerNumeroCliente(idCliente);

        // 2. Extraer últimos 5 dígitos (rellenar con ceros si es necesario)
        String ultimos5Digitos = String.format("%05d", Integer.parseInt(numeroCliente))
                .substring(Math.max(0, numeroCliente.length() - 5));

        // 3. Generar prefijo base
        String prefijo = "PED-" + ultimos5Digitos + "-";

        // 4. Contar pedidos existentes con este prefijo
        String sql = "SELECT COUNT(*) FROM pedido WHERE num_pedido LIKE ?";
        try (Connection con = Conexion.getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, prefijo + "%");
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1) + 1;

                // Ajustar dinámicamente el número de dígitos:
                int digitosNecesarios = (count < 10) ? 2
                        : (count < 100) ? 2
                                : (count < 1000) ? 3 : 4; // Hasta 9999 pedidos

                return String.format("%s%0" + digitosNecesarios + "d", prefijo, count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return prefijo + "001"; // Valor por defecto con 3 dígitos
    }

// Método auxiliar para obtener el número del cliente (INT)
    public String obtenerNumeroCliente(int idCliente) {
        String sql = "SELECT codigo FROM cliente WHERE codigo = ?";
        try (Connection con = Conexion.getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, idCliente);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return String.valueOf(rs.getInt("codigo")); // Convertir INT a String
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "0"; // Valor por defecto si no se encuentra
    }

    // Insertar un pedido y sus detalles
    public int insertar(Pedido pedido, List<PedidoDetalle> detalles) {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int idPedido = -1;

        try {
            con = Conexion.getConnection();
            con.setAutoCommit(false); // Iniciar transacción

            // Generar num_pedido
            String numPedido = generarCodigoPedido(pedido.getIdCliente());
            pedido.setNum_pedido(numPedido); // Asignar al objeto Pedido

            // Insertar el pedido
            String sql = "INSERT INTO pedido (nombre, estado, fecha_inicio, fecha_fin, cliente_codigo, num_pedido) VALUES (?, ?, ?, ?, ?, ?)";
            stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, pedido.getNombre());
            stmt.setString(2, pedido.getEstado());
            stmt.setDate(3, new java.sql.Date(pedido.getFecha_inicio().getTime()));
            stmt.setDate(4, new java.sql.Date(pedido.getFecha_fin().getTime()));
            stmt.setInt(5, pedido.getIdCliente());
            stmt.setString(6, numPedido);
            stmt.executeUpdate();

            // Obtener el ID del pedido generado
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                idPedido = rs.getInt(1);
                pedido.setId_pedido(idPedido); // Asignar el ID al objeto Pedido
            }

// Insertar los detalles y luego los registros en produccion
            if (detalles != null && !detalles.isEmpty()) {
                String sqlDetalle = "INSERT INTO detalle_pedido (descripcion, cantidad, dimension, precio_unitario, subtotal, total, pedido_id_pedido) VALUES (?, ?, ?, ?, ?, ?, ?)";
                String sqlProduccion = "INSERT INTO produccion (fecha_inicio, fecha_fin, estado, detalle_pedido_iddetalle_pedido) VALUES (?, ?, ?, ? )"; // Corregido: 4 placeholders

                stmt = con.prepareStatement(sqlDetalle, Statement.RETURN_GENERATED_KEYS);
                PreparedStatement stmtProduccion = con.prepareStatement(sqlProduccion);

                for (PedidoDetalle detalle : detalles) {
                    // Insertar detalle
                    stmt.setString(1, detalle.getDescripcion());
                    stmt.setInt(2, detalle.getCantidad());
                    stmt.setString(3, detalle.getDimensiones());
                    stmt.setDouble(4, detalle.getPrecioUnitario());
                    stmt.setDouble(5, detalle.getSubtotal());
                    stmt.setDouble(6, detalle.getTotal());
                    stmt.setInt(7, idPedido);
                    stmt.executeUpdate();

                    // Obtener el ID del detalle generado
                    rs = stmt.getGeneratedKeys();
                    int idDetalle = -1;
                    if (rs.next()) {
                        idDetalle = rs.getInt(1);
                    }

                    // Insertar en la tabla produccion
                    stmtProduccion.setDate(1, new java.sql.Date(pedido.getFecha_inicio().getTime()));
                    stmtProduccion.setDate(2, new java.sql.Date(pedido.getFecha_fin().getTime()));
                    // Mapear el estado del pedido al ENUM de produccion
                    stmtProduccion.setString(3, "pendiente");
                    stmtProduccion.setInt(4, idDetalle); // Corregido: 4to parámetro
                    stmtProduccion.executeUpdate();
                }
            }
            con.commit(); // Confirmar transacción
            return idPedido;

        } catch (SQLException e) {
            try {
                if (con != null) {
                    con.rollback(); // Revertir transacción en caso de error
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(null, "Error al insertar el pedido: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return -1;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Obtener todos los pedidos con el nombre del cliente
    public List<MaterialConDetalles> obtenerMateriales() {
        List<MaterialConDetalles> lista = new ArrayList<>();
        String sql = "SELECT p.*, CONCAT(c.nombre, ' ', c.apellido) AS nombre_cliente_completo "
                + "FROM pedido p "
                + "LEFT JOIN cliente c ON p.cliente_codigo = c.codigo";

        try (Connection con = Conexion.getConnection(); PreparedStatement stmt = con.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Pedido pedido = new Pedido(
                        rs.getInt("id_pedido"),
                        rs.getString("num_pedido"), // Incluir num_pedido
                        rs.getString("nombre"),
                        rs.getString("estado"),
                        rs.getDate("fecha_inicio"),
                        rs.getDate("fecha_fin"),
                        rs.getInt("cliente_codigo")
                );

                String nombreClienteCompleto = rs.getString("nombre_cliente_completo");
                if (rs.wasNull()) {
                    nombreClienteCompleto = "Sin cliente";
                }

                lista.add(new MaterialConDetalles(pedido, nombreClienteCompleto));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener materiales: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return lista;
    }

    public List<MaterialConDetalles> buscarPedidos(String criterio) {
        List<MaterialConDetalles> lista = new ArrayList<>();
        String sql = "SELECT p.*, CONCAT(c.nombre, ' ', c.apellido) AS nombre_cliente_completo "
                + "FROM pedido p "
                + "LEFT JOIN cliente c ON p.cliente_codigo = c.codigo "
                + "WHERE p.num_pedido LIKE ? OR p.id_pedido LIKE ? OR p.nombre LIKE ? "
                + "OR CONCAT(c.nombre, ' ', c.apellido) LIKE ? "
                + "ORDER BY p.id_pedido DESC";

        try (Connection con = Conexion.getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {

            String likeParam = "%" + criterio + "%";
            stmt.setString(1, likeParam);
            stmt.setString(2, likeParam);
            stmt.setString(3, likeParam);
            stmt.setString(4, likeParam);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Pedido pedido = new Pedido(
                        rs.getInt("id_pedido"),
                        rs.getString("num_pedido"),
                        rs.getString("nombre"),
                        rs.getString("estado"),
                        rs.getDate("fecha_inicio"),
                        rs.getDate("fecha_fin"),
                        rs.getInt("cliente_codigo")
                );

                lista.add(new MaterialConDetalles(pedido, rs.getString("nombre_cliente_completo")));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al buscar pedidos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return lista;
    }

    public MaterialConDetalles obtenerPedidoPorId(int idPedido) {
        String sql = "SELECT p.*, CONCAT(c.nombre, ' ', c.apellido) AS nombre_cliente_completo "
                + "FROM pedido p "
                + "LEFT JOIN cliente c ON p.cliente_codigo = c.codigo "
                + "WHERE p.id_pedido = ?";

        try (Connection con = Conexion.getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, idPedido);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Pedido pedido = new Pedido(
                        rs.getInt("id_pedido"),
                        rs.getString("num_pedido"), // Incluir num_pedido
                        rs.getString("nombre"),
                        rs.getString("estado"),
                        rs.getDate("fecha_inicio"),
                        rs.getDate("fecha_fin"),
                        rs.getInt("cliente_codigo")
                );
                String nombreClienteCompleto = rs.getString("nombre_cliente_completo");
                if (rs.wasNull()) {
                    nombreClienteCompleto = "Sin cliente";
                }
                return new MaterialConDetalles(pedido, nombreClienteCompleto);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener el pedido: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return null;
    }

    public List<PedidoDetalle> obtenerDetallesPorPedido(int idPedido) {
        List<PedidoDetalle> detalles = new ArrayList<>();
        String sql = "SELECT * FROM detalle_pedido WHERE pedido_id_pedido = ?";

        try (Connection con = Conexion.getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, idPedido);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                PedidoDetalle detalle = new PedidoDetalle(
                        rs.getInt("iddetalle_pedido"),
                        rs.getString("descripcion"),
                        rs.getInt("cantidad"),
                        rs.getString("dimension"),
                        rs.getDouble("precio_unitario"),
                        rs.getDouble("subtotal"),
                        rs.getDouble("total"),
                        rs.getInt("pedido_id_pedido")
                );
                detalles.add(detalle);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener detalles del pedido: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return detalles;
    }

    // Actualizar un pedido
    public boolean actualizar(Pedido pedido) {
        String sql = "UPDATE pedido SET nombre = ?, estado = ?, fecha_inicio = ?, fecha_fin = ?, cliente_codigo = ?, num_pedido = ? WHERE id_pedido = ?";

        try (Connection con = Conexion.getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, pedido.getNombre());
            stmt.setString(2, pedido.getEstado());
            stmt.setDate(3, new java.sql.Date(pedido.getFecha_inicio().getTime()));
            stmt.setDate(4, new java.sql.Date(pedido.getFecha_fin().getTime()));
            stmt.setInt(5, pedido.getIdCliente());
            stmt.setString(6, pedido.getNum_pedido()); // Mantener num_pedido
            stmt.setInt(7, pedido.getId_pedido());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar pedido: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    // Eliminar un pedido y sus detalles
    public boolean eliminarPedido(int idPedido) {
        Connection con = null;
        try {
            con = Conexion.getConnection();
            con.setAutoCommit(false); // Iniciar transacción

            // Eliminar detalles asociados
            String sqlDeleteDetalles = "DELETE FROM detalle_pedido WHERE pedido_id_pedido = ?";
            try (PreparedStatement stmtDetalles = con.prepareStatement(sqlDeleteDetalles)) {
                stmtDetalles.setInt(1, idPedido);
                stmtDetalles.executeUpdate();
            }

            // Eliminar el pedido
            String sqlDeletePedido = "DELETE FROM pedido WHERE id_pedido = ?";
            try (PreparedStatement stmtPedido = con.prepareStatement(sqlDeletePedido)) {
                stmtPedido.setInt(1, idPedido);
                int rowsAffected = stmtPedido.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("No se encontró el pedido con ID: " + idPedido);
                }
            }

            con.commit(); // Confirmar transacción
            return true;

        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback(); // Revertir transacción en caso de error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Método auxiliar para mapear el estado del pedido al ENUM de produccion
    private String mapearEstadoProduccion(String estadoPedido) {
        // Mapear el estado del pedido al ENUM permitido en la tabla produccion
        switch (estadoPedido.toLowerCase()) {
            case "pendiente":
                return "pendiente";
            case "proceso":
                return "proceso";
            case "completado":
            case "finalizado":
                return "finalizado";
            default:
                return "pendiente"; // Estado por defecto
        }
    }
    // ... [otros métodos existentes] ...

    /**
     * Actualiza el estado de una producción basado en sus etapas
     */
    public boolean actualizarEstadoProduccionSegunEtapas(int idProduccion) throws SQLException {
        Connection con = null;
        try {
            con = Conexion.getConnection();
            con.setAutoCommit(false);
            
            // 1. Verificar estados de todas las etapas
            String sql = "SELECT estado FROM etapa_produccion WHERE produccion_id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idProduccion);
            ResultSet rs = ps.executeQuery();
            
            boolean todasCompletadas = true;
            boolean algunaEnProceso = false;
            boolean algunaPendiente = false;
            
            while (rs.next()) {
                String estado = rs.getString("estado").toLowerCase();
                
                if ("pendiente".equals(estado)) {
                    todasCompletadas = false;
                    algunaPendiente = true;
                } else if ("proceso".equals(estado)) {
                    todasCompletadas = false;
                    algunaEnProceso = true;
                } else if (!"completado".equals(estado)) {
                    todasCompletadas = false;
                }
            }
            
            // 2. Determinar nuevo estado
            String nuevoEstado;
            if (todasCompletadas) {
                nuevoEstado = "finalizado";
            } else if (algunaEnProceso) {
                nuevoEstado = "proceso";
            } else {
                nuevoEstado = "pendiente";
            }
            
            // 3. Actualizar producción
            String sqlUpdate = "UPDATE produccion SET estado = ? WHERE id_produccion = ?";
            PreparedStatement psUpdate = con.prepareStatement(sqlUpdate);
            psUpdate.setString(1, nuevoEstado);
            psUpdate.setInt(2, idProduccion);
            psUpdate.executeUpdate();
            
            // 4. Obtener ID del pedido para actualizarlo también
            String sqlPedido = "SELECT dp.pedido_id_pedido FROM detalle_pedido dp " +
                              "JOIN produccion p ON dp.iddetalle_pedido = p.detalle_pedido_iddetalle_pedido " +
                              "WHERE p.id_produccion = ?";
            
            PreparedStatement psPedido = con.prepareStatement(sqlPedido);
            psPedido.setInt(1, idProduccion);
            rs = psPedido.executeQuery();
            
            if (rs.next()) {
                int idPedido = rs.getInt("pedido_id_pedido");
                actualizarEstadoPedidoSegunProducciones(idPedido, con);
            }
            
            con.commit();
            return true;
        } catch (SQLException e) {
            if (con != null) con.rollback();
            throw e;
        } finally {
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        }
    }

    /**
     * Actualiza el estado del pedido basado en sus producciones
     */
    private void actualizarEstadoPedidoSegunProducciones(int idPedido, Connection con) throws SQLException {
        // 1. Verificar estados de todas las producciones del pedido
        String sql = "SELECT estado FROM produccion p " +
                   "JOIN detalle_pedido dp ON p.detalle_pedido_iddetalle_pedido = dp.iddetalle_pedido " +
                   "WHERE dp.pedido_id_pedido = ?";
        
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, idPedido);
        ResultSet rs = ps.executeQuery();
        
        boolean todasFinalizadas = true;
        boolean algunaEnProceso = false;
        
        while (rs.next()) {
            String estado = rs.getString("estado").toLowerCase();
            
            if ("pendiente".equals(estado)) {
                todasFinalizadas = false;
            } else if ("proceso".equals(estado)) {
                todasFinalizadas = false;
                algunaEnProceso = true;
            } else if (!"finalizado".equals(estado)) {
                todasFinalizadas = false;
            }
        }
        
        // 2. Determinar nuevo estado del pedido
        String nuevoEstado;
        if (todasFinalizadas) {
            nuevoEstado = "finalizado";
        } else if (algunaEnProceso) {
            nuevoEstado = "proceso";
        } else {
            nuevoEstado = "pendiente";
        }
        
        // 3. Actualizar pedido
        String sqlUpdate = "UPDATE pedido SET estado = ? WHERE id_pedido = ?";
        PreparedStatement psUpdate = con.prepareStatement(sqlUpdate);
        psUpdate.setString(1, nuevoEstado);
        psUpdate.setInt(2, idPedido);
        psUpdate.executeUpdate();
    }
}
