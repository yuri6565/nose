package controlador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import modelo.Cliente;
import modelo.Conexion;

public class Ctrl_Cliente {
    private String lastError = "";

    public boolean guardar(Cliente objeto) {
        boolean respuesta = false;
        Connection con = Conexion.getConnection();

        try {
            if (existeCliente(objeto.getId_cliente())) {
                JOptionPane.showMessageDialog(null, "El código del cliente ya está registrado.");
                return false;
            }

            String sql = "INSERT INTO cliente (codigo, identificacion, nombre, apellido, telefono, telefono2, direccion, departamento, municipio, activo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement consulta = con.prepareStatement(sql);

            consulta.setInt(1, objeto.getId_cliente());
            consulta.setString(2, objeto.getIdentificacion());
            consulta.setString(3, objeto.getNombre());
            consulta.setString(4, objeto.getApellido());
            consulta.setString(5, objeto.getTelefono());
            consulta.setString(6, objeto.getTelefono2() != null ? objeto.getTelefono2() : "");
            consulta.setString(7, objeto.getDireccion());
            consulta.setString(8, objeto.getDepartamento());
            consulta.setString(9, objeto.getMunicipio());
            consulta.setBoolean(10, objeto.isActivo());

            if (consulta.executeUpdate() > 0) {
                respuesta = true;
            }

            consulta.close();
            con.close();

        } catch (SQLException e) {
            lastError = e.getMessage();
            JOptionPane.showMessageDialog(null, "Error al guardar cliente: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return respuesta;
    }

    public boolean existeCliente(int codigo) {
        boolean existe = false;
        Connection con = Conexion.getConnection();

        try {
            String sql = "SELECT COUNT(*) FROM cliente WHERE codigo = ?";
            PreparedStatement consulta = con.prepareStatement(sql);
            consulta.setInt(1, codigo);

            ResultSet rs = consulta.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                existe = true;
            }

            rs.close();
            consulta.close();
            con.close();

        } catch (SQLException e) {
            lastError = e.getMessage();
            JOptionPane.showMessageDialog(null, "Error al verificar cliente: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return existe;
    }

    public List<Cliente> obtenerClientes() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT codigo, identificacion, nombre, apellido, telefono, telefono2, departamento, municipio, direccion, activo FROM cliente";

        try (Connection con = Conexion.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setId_cliente(rs.getInt("codigo"));
                cliente.setIdentificacion(rs.getString("identificacion"));
                cliente.setNombre(rs.getString("nombre"));
                cliente.setApellido(rs.getString("apellido"));
                cliente.setTelefono(rs.getString("telefono"));
                cliente.setTelefono2(rs.getString("telefono2"));
                cliente.setDepartamento(rs.getString("departamento"));
                cliente.setMunicipio(rs.getString("municipio"));
                cliente.setDireccion(rs.getString("direccion"));
                cliente.setActivo(rs.getBoolean("activo"));
                lista.add(cliente);
            }

        } catch (SQLException e) {
            lastError = e.getMessage();
            JOptionPane.showMessageDialog(null, "Error al obtener clientes: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }

    public Cliente obtenerClientePorId(int codigo) {
        Cliente cliente = null;
        Connection con = Conexion.getConnection();

        try {
            String sql = "SELECT codigo, identificacion, nombre, apellido, telefono, telefono2, departamento, municipio, direccion, activo FROM cliente WHERE codigo = ?";
            PreparedStatement consulta = con.prepareStatement(sql);
            consulta.setInt(1, codigo);

            ResultSet rs = consulta.executeQuery();

            if (rs.next()) {
                cliente = new Cliente();
                cliente.setId_cliente(rs.getInt("codigo"));
                cliente.setIdentificacion(rs.getString("identificacion"));
                cliente.setNombre(rs.getString("nombre"));
                cliente.setApellido(rs.getString("apellido"));
                cliente.setTelefono(rs.getString("telefono"));
                cliente.setTelefono2(rs.getString("telefono2"));
                cliente.setDepartamento(rs.getString("departamento"));
                cliente.setMunicipio(rs.getString("municipio"));
                cliente.setDireccion(rs.getString("direccion"));
                cliente.setActivo(rs.getBoolean("activo"));
            }

            rs.close();
            consulta.close();
            con.close();
        } catch (SQLException e) {
            lastError = e.getMessage();
            JOptionPane.showMessageDialog(null, "Error al obtener cliente: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return cliente;
    }

    public boolean editar(Cliente objeto, int codigo) {
        boolean respuesta = false;
        Connection con = Conexion.getConnection();

        try {
            String sql = "UPDATE cliente SET identificacion = ?, nombre = ?, apellido = ?, telefono = ?, telefono2 = ?, " +
                         "direccion = ?, departamento = ?, municipio = ?, activo = ? WHERE codigo = ?";
            PreparedStatement consulta = con.prepareStatement(sql);
            consulta.setString(1, objeto.getIdentificacion());
            consulta.setString(2, objeto.getNombre());
            consulta.setString(3, objeto.getApellido() != null ? objeto.getApellido() : "");
            consulta.setString(4, objeto.getTelefono());
            consulta.setString(5, objeto.getTelefono2() != null ? objeto.getTelefono2() : "");
            consulta.setString(6, objeto.getDireccion());
            consulta.setString(7, objeto.getDepartamento() != null ? objeto.getDepartamento() : "");
            consulta.setString(8, objeto.getMunicipio() != null ? objeto.getMunicipio() : "");
            consulta.setBoolean(9, objeto.isActivo());
            consulta.setInt(10, codigo);

            int rowsAffected = consulta.executeUpdate();
            if (rowsAffected > 0) {
                respuesta = true;
            } else {
                lastError = "No se actualizó ninguna fila.";
            }

            consulta.close();
            con.close();
        } catch (SQLException e) {
            lastError = e.getMessage();
            JOptionPane.showMessageDialog(null, "Error al editar cliente: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return respuesta;
    }

    public String getLastError() {
        return lastError;
    }

    public boolean eliminar(int codigo) {
        boolean respuesta = false;
        Connection con = Conexion.getConnection();
        try {
            String sql = "DELETE FROM cliente WHERE codigo = ?";
            PreparedStatement consulta = con.prepareStatement(sql);
            consulta.setInt(1, codigo);
            if (consulta.executeUpdate() > 0) {
                respuesta = true;
            }
            consulta.close();
        } catch (SQLException e) {
            lastError = e.getMessage();
            JOptionPane.showMessageDialog(null, "Error al eliminar cliente: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return respuesta;
    }

    public boolean tienePedidos(int codigo) {
        boolean tienePedidos = false;
        Connection con = Conexion.getConnection();
        try {
            String sql = "SELECT COUNT(*) FROM pedido WHERE cliente_codigo = ?";
            PreparedStatement consulta = con.prepareStatement(sql);
            consulta.setInt(1, codigo);
            ResultSet rs = consulta.executeQuery();
            if (rs.next()) {
                tienePedidos = rs.getInt(1) > 0;
            }
            rs.close();
            consulta.close();
        } catch (SQLException e) {
            lastError = e.getMessage();
            JOptionPane.showMessageDialog(null, "Error al verificar pedidos: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return tienePedidos;
    }

    public boolean desactivar(int codigo) {
        boolean respuesta = false;
        Connection con = Conexion.getConnection();
        try {
            String sql = "UPDATE cliente SET activo = false WHERE codigo = ?";
            PreparedStatement consulta = con.prepareStatement(sql);
            consulta.setInt(1, codigo);
            if (consulta.executeUpdate() > 0) {
                respuesta = true;
            }
            consulta.close();
        } catch (SQLException e) {
            lastError = e.getMessage();
            JOptionPane.showMessageDialog(null, "Error al desactivar cliente: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return respuesta;
    }

    public boolean activar(int codigo) {
        boolean respuesta = false;
        Connection con = Conexion.getConnection();
        try {
            String sql = "UPDATE cliente SET activo = true WHERE codigo = ?";
            PreparedStatement consulta = con.prepareStatement(sql);
            consulta.setInt(1, codigo);
            if (consulta.executeUpdate() > 0) {
                respuesta = true;
            }
            consulta.close();
        } catch (SQLException e) {
            lastError = e.getMessage();
            JOptionPane.showMessageDialog(null, "Error al activar cliente: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return respuesta;
    }

    public Cliente buscarClientePorCodigo(int codigo) {
        Cliente cliente = null;
        Connection con = Conexion.getConnection();

        try {
            String sql = "SELECT codigo, identificacion, nombre, apellido, telefono, telefono2, departamento, municipio, direccion, activo " +
                         "FROM cliente WHERE codigo = ?";
            PreparedStatement consulta = con.prepareStatement(sql);
            consulta.setInt(1, codigo);

            ResultSet rs = consulta.executeQuery();

            if (rs.next()) {
                cliente = new Cliente();
                cliente.setId_cliente(rs.getInt("codigo"));
                cliente.setIdentificacion(rs.getString("identificacion"));
                cliente.setNombre(rs.getString("nombre"));
                cliente.setApellido(rs.getString("apellido"));
                cliente.setTelefono(rs.getString("telefono"));
                cliente.setTelefono2(rs.getString("telefono2"));
                cliente.setDepartamento(rs.getString("departamento"));
                cliente.setMunicipio(rs.getString("municipio"));
                cliente.setDireccion(rs.getString("direccion"));
                cliente.setActivo(rs.getBoolean("activo"));
            }

            rs.close();
            consulta.close();
            con.close();
        } catch (SQLException e) {
            lastError = e.getMessage();
            JOptionPane.showMessageDialog(null, "Error al buscar cliente por código: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return cliente;
    }

    public List<Cliente> buscarClientePorNombre(String nombre) {
        List<Cliente> lista = new ArrayList<>();
        Connection con = Conexion.getConnection();

        try {
            String sql = "SELECT codigo, identificacion, nombre, apellido, telefono, telefono2, departamento, municipio, direccion, activo " +
                         "FROM cliente WHERE nombre LIKE ?";
            PreparedStatement consulta = con.prepareStatement(sql);
            consulta.setString(1, "%" + nombre + "%");

            ResultSet rs = consulta.executeQuery();

            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setId_cliente(rs.getInt("codigo"));
                cliente.setIdentificacion(rs.getString("identificacion"));
                cliente.setNombre(rs.getString("nombre"));
                cliente.setApellido(rs.getString("apellido"));
                cliente.setTelefono(rs.getString("telefono"));
                cliente.setTelefono2(rs.getString("telefono2"));
                cliente.setDepartamento(rs.getString("departamento"));
                cliente.setMunicipio(rs.getString("municipio"));
                cliente.setDireccion(rs.getString("direccion"));
                cliente.setActivo(rs.getBoolean("activo"));
                lista.add(cliente);
            }

            rs.close();
            consulta.close();
            con.close();
        } catch (SQLException e) {
            lastError = e.getMessage();
            JOptionPane.showMessageDialog(null, "Error al buscar cliente por nombre: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return lista;
    }

    public List<Cliente> buscarClientePorTodos(String textoBusqueda) {
        List<Cliente> lista = new ArrayList<>();
        Connection con = Conexion.getConnection();

        try {
            String sql = "SELECT codigo, identificacion, nombre, apellido, telefono, telefono2, departamento, municipio, direccion, activo " +
                         "FROM cliente WHERE identificacion LIKE ? OR nombre LIKE ? OR apellido LIKE ? OR telefono LIKE ? OR telefono2 LIKE ? " +
                         "OR departamento LIKE ? OR municipio LIKE ? OR direccion LIKE ?";
            PreparedStatement consulta = con.prepareStatement(sql);
            String patron = "%" + textoBusqueda + "%";
            for (int i = 1; i <= 8; i++) {
                consulta.setString(i, patron);
            }

            ResultSet rs = consulta.executeQuery();

            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setId_cliente(rs.getInt("codigo"));
                cliente.setIdentificacion(rs.getString("identificacion"));
                cliente.setNombre(rs.getString("nombre"));
                cliente.setApellido(rs.getString("apellido"));
                cliente.setTelefono(rs.getString("telefono"));
                cliente.setTelefono2(rs.getString("telefono2"));
                cliente.setDepartamento(rs.getString("departamento"));
                cliente.setMunicipio(rs.getString("municipio"));
                cliente.setDireccion(rs.getString("direccion"));
                cliente.setActivo(rs.getBoolean("activo"));
                lista.add(cliente);
            }

            rs.close();
            consulta.close();
            con.close();
        } catch (SQLException e) {
            lastError = e.getMessage();
            JOptionPane.showMessageDialog(null, "Error al buscar cliente por todos los campos: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return lista;
    }
}