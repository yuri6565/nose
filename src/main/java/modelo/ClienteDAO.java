package modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClienteDAO {
    public Cliente obtenerPorCodigo(int codigo) throws SQLException {
        String sql = "SELECT * FROM cliente WHERE codigo = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Cliente cliente = new Cliente();
                    cliente.setCodigo(rs.getInt("codigo"));
                    cliente.setIdentificacion(rs.getString("identificacion"));
                    cliente.setNombre(rs.getString("nombre"));
                    cliente.setApellido(rs.getString("apellido"));
                    cliente.setTelefono(rs.getString("telefono"));
                    cliente.setDepartamento(rs.getString("departamento"));
                    cliente.setMunicipio(rs.getString("municipio"));
                    cliente.setDireccion(rs.getString("direccion"));
                    return cliente;
                }
            }
        }
        return null;
    }
}