package controlador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import modelo.Conexion;
import modelo.ProveedorDatos;

public class Ctrl_Proveedor {

    public boolean guardarProveedor(ProveedorDatos objeto) {
        boolean respuesta = false;
        int idGenerado = objeto.getId_proveedor();

        try (Connection con = Conexion.getConnection();
             PreparedStatement consulta = con.prepareStatement(
                 "INSERT INTO proveedor (id_proveedor, tipo_identificacion, nombre, apellido, correo_electronico, telefono, direccion, estado, departamento, municipio) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

            consulta.setInt(1, objeto.getId_proveedor());
            consulta.setString(2, objeto.getTipoIdentificacion());
            consulta.setString(3, objeto.getNombre());
            consulta.setString(4, objeto.getApellido());
            consulta.setString(5, objeto.getCorreo_electronico());
            consulta.setString(6, objeto.getTelefono());
            consulta.setString(7, objeto.getDireccion());
            consulta.setString(8, objeto.getEstado() != null ? objeto.getEstado() : "activo"); // Usar el estado del objeto, por defecto "activo"
            consulta.setString(9, objeto.getDepartamento());
            consulta.setString(10, objeto.getMunicipio());

            if (consulta.executeUpdate() > 0) {
                if (objeto.getProductos() != null && !objeto.getProductos().isEmpty()) {
                    for (String producto : objeto.getProductos()) {
                        int idInventario = obtenerIdInventarioPorNombre(producto);
                        if (idInventario != -1) {
                            if (!guardarSuministra(idGenerado, idInventario)) {
                                JOptionPane.showMessageDialog(null, "Error al asociar producto: " + producto);
                                return false;
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Producto no encontrado en inventario: " + producto);
                            return false;
                        }
                    }
                }
                respuesta = true;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar Proveedor: " + e.getMessage());
            e.printStackTrace();
        }
        return respuesta;
    }

    public ProveedorDatos obtenerProveedorPorid(int id_proveedor) {
        ProveedorDatos datosproveedor = null;
        String sql = "SELECT * FROM proveedor WHERE id_proveedor = ?";

        try (Connection con = Conexion.getConnection();
             PreparedStatement consulta = con.prepareStatement(sql)) {
            consulta.setInt(1, id_proveedor);
            try (ResultSet rs = consulta.executeQuery()) {
                if (rs.next()) {
                    datosproveedor = new ProveedorDatos();
                    datosproveedor.setId_proveedor(rs.getInt("id_proveedor"));
                    datosproveedor.setTipoIdentificacion(rs.getString("tipo_identificacion"));
                    datosproveedor.setNombre(rs.getString("nombre"));
                    datosproveedor.setApellido(rs.getString("apellido"));
                    datosproveedor.setCorreo_electronico(rs.getString("correo_electronico"));
                    datosproveedor.setTelefono(rs.getString("telefono"));
                    datosproveedor.setDireccion(rs.getString("direccion"));
                    datosproveedor.setEstado(rs.getString("estado")); // Mantener el estado actual
                    datosproveedor.setDepartamento(rs.getString("departamento"));
                    datosproveedor.setMunicipio(rs.getString("municipio"));
                    // Cargar productos
                    datosproveedor.setProductos(obtenerProductosDeProveedor(id_proveedor));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener proveedor: " + e.getMessage());
        }
        return datosproveedor;
    }

    public boolean editar(ProveedorDatos proveedor, int idProveedor) {
        String sql = "UPDATE proveedor SET tipo_identificacion = ?, nombre = ?, apellido = ?, telefono = ?, correo_electronico = ?, direccion = ?, estado = ?, departamento = ?, municipio = ? WHERE id_proveedor = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, proveedor.getTipoIdentificacion());
            stmt.setString(2, proveedor.getNombre());
            stmt.setString(3, proveedor.getApellido() != null ? proveedor.getApellido() : "");
            stmt.setString(4, proveedor.getTelefono());
            stmt.setString(5, proveedor.getCorreo_electronico());
            stmt.setString(6, proveedor.getDireccion());
            stmt.setString(7, proveedor.getEstado());
            stmt.setString(8, proveedor.getDepartamento());
            stmt.setString(9, proveedor.getMunicipio());
            stmt.setInt(10, idProveedor);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Proveedor actualizado exitosamente.");
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el proveedor o no se pudo actualizar.");
                return false;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al editar proveedor: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void eliminarSuministra(int id_proveedor) {
        String sql = "DELETE FROM suministra WHERE proveedor_id_proveedor = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, id_proveedor);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al eliminar relaciones de productos: " + e.getMessage());
        }
    }

    public boolean eliminar(int id_proveedor) {
        boolean respuesta = false;
        String sql = "UPDATE proveedor SET estado = 'inactivo' WHERE id_proveedor = ?";

        try (Connection con = Conexion.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, id_proveedor);
            if (stmt.executeUpdate() > 0) {
                respuesta = true;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al desactivar proveedor: " + e.getMessage());
            e.printStackTrace();
        }
        return respuesta;
    }

    public boolean tieneProductos(int id_proveedor) {
        String sql = "SELECT COUNT(*) FROM suministra WHERE proveedor_id_proveedor = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, id_proveedor);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al verificar productos del proveedor: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean desactivar(int id_proveedor) {
        String sql = "UPDATE proveedor SET estado = 'inactivo' WHERE id_proveedor = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, id_proveedor);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al desactivar proveedor: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<String> obtenerTodosNombresInventario() {
        List<String> nombres = new ArrayList<>();
        String sql = "SELECT nombre FROM inventario";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                nombres.add(rs.getString("nombre"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return nombres;
    }

    public int obtenerIdInventarioPorNombre(String nombre) {
        String sql = "SELECT id_inventario FROM inventario WHERE nombre = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_inventario");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean guardarSuministra(int idProveedor, int idInventario) {
        String sql = "INSERT INTO suministra (proveedor_id_proveedor, inventario_id_inventario) VALUES (?, ?)";
        try (Connection con = Conexion.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, idProveedor);
            stmt.setInt(2, idInventario);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al guardar relación proveedor-producto: " + e.getMessage());
            return false;
        }
    }

    public List<ProveedorDatos> obtenerProveedoresConProductos() {
        List<ProveedorDatos> resultados = new ArrayList<>();
        String sql = "SELECT p.*, i.nombre AS nombre_producto " +
                     "FROM proveedor p " +
                     "LEFT JOIN suministra s ON p.id_proveedor = s.proveedor_id_proveedor " +
                     "LEFT JOIN inventario i ON s.inventario_id_inventario = i.id_inventario";

        try (Connection con = Conexion.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            Map<Integer, ProveedorDatos> proveedorMap = new HashMap<>();
            while (rs.next()) {
                int idProveedor = rs.getInt("id_proveedor");
                ProveedorDatos proveedor = proveedorMap.getOrDefault(idProveedor, new ProveedorDatos());
                if (proveedor.getId_proveedor() == 0) {
                    proveedor.setId_proveedor(idProveedor);
                    proveedor.setTipoIdentificacion(rs.getString("tipo_identificacion"));
                    proveedor.setNombre(rs.getString("nombre"));
                    proveedor.setApellido(rs.getString("apellido"));
                    proveedor.setCorreo_electronico(rs.getString("correo_electronico"));
                    proveedor.setTelefono(rs.getString("telefono"));
                    proveedor.setDireccion(rs.getString("direccion"));
                    proveedor.setEstado(rs.getString("estado"));
                    proveedor.setDepartamento(rs.getString("departamento"));
                    proveedor.setMunicipio(rs.getString("municipio"));
                    proveedor.setProductos(new ArrayList<>());
                }
                String nombreProducto = rs.getString("nombre_producto");
                if (nombreProducto != null) {
                    proveedor.getProductos().add(nombreProducto);
                }
                proveedorMap.put(idProveedor, proveedor);
            }
            resultados.addAll(proveedorMap.values());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener proveedores con productos: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("Proveedores cargados: " + resultados.size());
        return resultados;
    }

    public List<String> obtenerProductosDeProveedor(int idProveedor) {
        List<String> productos = new ArrayList<>();
        String sql = "SELECT i.nombre FROM suministra s " +
                     "JOIN inventario i ON s.inventario_id_inventario = i.id_inventario " +
                     "WHERE s.proveedor_id_proveedor = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProveedor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productos.add(rs.getString("nombre"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al obtener productos del proveedor: " + e.getMessage());
        }
        return productos;
    }
    
    public boolean activar(int id_proveedor) {
        String sql = "UPDATE proveedor SET estado = 'activo' WHERE id_proveedor = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, id_proveedor);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Proveedor activado exitosamente.");
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el proveedor o no se pudo activar.");
                return false;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al activar proveedor: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}