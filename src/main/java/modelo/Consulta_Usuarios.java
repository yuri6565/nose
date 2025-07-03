package modelo;

import controlador.Ctrl_Usuarios;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import static modelo.Conexion.getConnection;

public class Consulta_Usuarios extends Conexion {

    public String generarCodigo() {
        Random rand = new Random();
        int codigo = 100000 + rand.nextInt(900000);
        return String.valueOf(codigo);
    }

    public String recuperarCuenta(String usuario, String correo_electronico) {
        PreparedStatement ps = null;
        Connection con = getConnection();
        ResultSet res = null;
        String codigoRecuperacion = null;
        String query = "SELECT id_usuario FROM usuario WHERE usuario=? AND correo_electronico=?";

        try {
            ps = con.prepareStatement(query);
            ps.setString(1, usuario);
            ps.setString(2, correo_electronico);
            res = ps.executeQuery();

            if (res.next()) {
                int idUsuario = res.getInt("id_usuario");
                codigoRecuperacion = generarCodigo();

                String insertQuery = "INSERT INTO recuperacion (correo_electronico, codigo, fecha_generacion, usuario_id_usuario) VALUES (?, ?, NOW(), ?)";
                try (PreparedStatement psInsert = con.prepareStatement(insertQuery)) {
                    psInsert.setString(1, correo_electronico);
                    psInsert.setInt(2, Integer.parseInt(codigoRecuperacion));
                    psInsert.setInt(3, idUsuario);
                    psInsert.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Consulta_Usuarios.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try { if (res != null) res.close(); } catch (SQLException ex) {}
            try { if (ps != null) ps.close(); } catch (SQLException ex) {}
            try { if (con != null) con.close(); } catch (SQLException ex) {}
        }

        return codigoRecuperacion;
    }

    public String obtenerCodigoDesdeCorreo(String correo) {
        PreparedStatement ps = null;
        Connection con = getConnection();
        ResultSet res = null;
        String usuario = null;
        String query = "SELECT usuario FROM usuario WHERE correo_electronico=?";

        try {
            if (correo == null || correo.trim().isEmpty()) {
                System.out.println("ERROR: El correo es nulo o vacío.");
                return null;
            }

            ps = con.prepareStatement(query);
            ps.setString(1, correo.trim());
            res = ps.executeQuery();

            if (res.next()) {
                usuario = res.getString("usuario");
                System.out.println("Usuario encontrado: " + usuario);
            } else {
                System.out.println("No se encontró un usuario con ese correo.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (res != null) res.close(); } catch (SQLException ex) {}
            try { if (ps != null) ps.close(); } catch (SQLException ex) {}
            try { if (con != null) con.close(); } catch (SQLException ex) {}
        }

        return usuario;
    }

    public boolean actualizarContrasena(String correo, String nuevaContrasena) {
        PreparedStatement ps = null;
        Connection con = getConnection();
        boolean actualizado = false;

        String query = "UPDATE usuario SET contrasena = ? WHERE correo_electronico = ?";

        try {
            ps = con.prepareStatement(query);
            ps.setString(1, nuevaContrasena);
            ps.setString(2, correo);

            int filasAfectadas = ps.executeUpdate();
            actualizado = filasAfectadas > 0;

        } catch (SQLException ex) {
            Logger.getLogger(Consulta_Usuarios.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try { if (ps != null) ps.close(); } catch (SQLException ex) {}
        }

        return actualizado;
    }

    public boolean validarcodigo(String correo, String codigo) {
        PreparedStatement ps = null;
        Connection con = getConnection();
        ResultSet res = null;
        boolean codigovalido = false;

        String query = """
            SELECT codigo 
            FROM recuperacion 
            WHERE correo_electronico = ? 
              AND fecha_generacion >= NOW() - INTERVAL 5 MINUTE
            ORDER BY fecha_generacion DESC 
            LIMIT 1
        """;

        try {
            ps = con.prepareStatement(query);
            ps.setString(1, correo);
            res = ps.executeQuery();

            if (res.next()) {
                String ultimoCodigo = res.getString("codigo");
                codigovalido = ultimoCodigo.equals(codigo);
            }

        } catch (SQLException ex) {
            Logger.getLogger(Consulta_Usuarios.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try { if (res != null) res.close(); } catch (SQLException ex) {}
            try { if (ps != null) ps.close(); } catch (SQLException ex) {}
            try { if (con != null) con.close(); } catch (SQLException ex) {}
        }

        return codigovalido;
    }

    public String reenviarCodigo(String correo) {
        Connection con = getConnection();
        PreparedStatement psUsuario = null;
        ResultSet res = null;
        String usuario = null;
        String codigo = generarCodigo();

        try {
            String queryUsuario = "SELECT id_usuario, usuario FROM usuario WHERE correo_electronico = ?";
            psUsuario = con.prepareStatement(queryUsuario);
            psUsuario.setString(1, correo);
            res = psUsuario.executeQuery();

            if (res.next()) {
                int idUsuario = res.getInt("id_usuario");
                usuario = res.getString("usuario");

                String deleteQuery = "DELETE FROM recuperacion WHERE correo_electronico = ?";
                try (PreparedStatement psDelete = con.prepareStatement(deleteQuery)) {
                    psDelete.setString(1, correo);
                    psDelete.executeUpdate();
                }

                String insertQuery = "INSERT INTO recuperacion (correo_electronico, codigo, fecha_generacion, usuario_id_usuario) VALUES (?, ?, NOW(), ?)";
                try (PreparedStatement psInsert = con.prepareStatement(insertQuery)) {
                    psInsert.setString(1, correo);
                    psInsert.setInt(2, Integer.parseInt(codigo));
                    psInsert.setInt(3, idUsuario);
                    psInsert.executeUpdate();
                }

                Ctrl_Usuarios.enviarCodigoRecuperacion(correo, usuario, codigo);
                return codigo;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try { if (res != null) res.close(); } catch (Exception e) {}
            try { if (psUsuario != null) psUsuario.close(); } catch (Exception e) {}
            try { if (con != null) con.close(); } catch (Exception e) {}
        }

        return null;
    }
}
