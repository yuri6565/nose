    /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package vista.Cotizacion;

import vista.Ventas.*;
import controlador.Ctrl_Cliente;
import controlador.Ctrl_Pedido;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import modelo.Cliente;
import modelo.Conexion;
import vista.Cotizacion.HistorialCot;
import modelo.Pedido;
import modelo.PedidoDetalle;

/**
 *
 * @author ZenBook
 */
public class DetallesCot extends javax.swing.JPanel {

    private Ctrl_Pedido controlador;
    private int idPedido;
    private boolean editando = false;
    public boolean clienteGuardado = false;
    public Cliente cliente;
    private List<Cliente> clientes;

    public DetallesCot(String codigo, JPanel contenedor) {
        this.controlador = new Ctrl_Pedido(); // Considera crear un Ctrl_Cotizacion si no existe
        try {
            this.idPedido = Integer.parseInt(codigo); // Cambiar a idCotizacion si es necesario
        } catch (NumberFormatException e) {
            this.idPedido = -1;
        }
        initComponents();
        cargarClientes();
        cargarDatosCotizacion(); // Cambiar este método para cargar datos de cotización
        deshabilitarEdicion();

        tablaDetalles.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        TableColumn descripcionColumn = tablaDetalles.getColumnModel().getColumn(0);
        descripcionColumn.setPreferredWidth(43); // Ajustar el ancho de la columna

        TableColumn cantidadColumn = tablaDetalles.getColumnModel().getColumn(1);
        cantidadColumn.setPreferredWidth(16); // Ajustar el ancho de la columna

        TableColumn dimensionColumn = tablaDetalles.getColumnModel().getColumn(2);
        dimensionColumn.setPreferredWidth(30); // Ajustar el ancho de la columna

        TableColumn preciouColumn = tablaDetalles.getColumnModel().getColumn(3);
        preciouColumn.setPreferredWidth(30); // Ajustar el ancho de la columna

        TableColumn subtotalColumn = tablaDetalles.getColumnModel().getColumn(4);
        subtotalColumn.setPreferredWidth(30); // Ajustar el ancho de la columna

        // Configurar la columna "Detalle"
        TableColumn deleteColumn = tablaDetalles.getColumnModel().getColumn(5);
        deleteColumn.setPreferredWidth(18); // Ajustar el ancho de la columna

        cargarDatosCotizacion(); // Cargar los datos al inicializar

        deshabilitarEdicion();

    }

    private void cargarDatosCotizacion() {

        if (idPedido == -1) {
            JOptionPane.showMessageDialog(this, "Código de cotización inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection con = Conexion.getConnection(); PreparedStatement pstmt = con.prepareStatement(
                "SELECT c.*, cl.nombre, cl.apellido "
                + "FROM cotizacion c "
                + "LEFT JOIN cliente cl ON c.cliente_codigo = cl.codigo "
                + "WHERE c.id_cotizacion = ?")) {

            pstmt.setInt(1, idPedido);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Mostrar información básica
                lblNumeroPedido.setText(String.valueOf(rs.getInt("id_cotizacion")));
                txtNombrePedido.setText("Cotización " + rs.getInt("id_cotizacion"));

                // Mostrar cliente
                String nombreCliente = rs.getString("nombre") + " " + rs.getString("apellido");
                cmbCliente.setSelectedItem(nombreCliente);

                // Mostrar fechas
                dateFechaInicio.setDate(rs.getDate("fecha"));

                // Cargar detalles de la cotización
                cargarDetallesCotizacion();

                // Mostrar total
                lblTotal.setText(String.format("%.2f", rs.getDouble("total")));
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró la cotización.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar cotización: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarTotal() {
        DefaultTableModel model = (DefaultTableModel) tablaDetalles.getModel();
        double total = 0.0;

        for (int i = 0; i < model.getRowCount(); i++) {
            try {
                double subtotal = Double.parseDouble(model.getValueAt(i, 4).toString());
                total += subtotal;
            } catch (NumberFormatException e) {
                // Ignorar filas con valores inválidos
            }
        }
        lblTotal.setText(String.format("%.2f", total));
    }

    private void deshabilitarEdicion() {
        txtNombrePedido.setEnabled(false);
        //cmbEstado.setEnabled(false);
        cmbCliente.setEnabled(false);
        dateFechaInicio.setEnabled(false);
        tablaDetalles.setEnabled(false);
        btnAñadir.setVisible(false);
        btnGuardar.setVisible(false);
    }

    private void habilitarEdicion() {
        txtNombrePedido.setEnabled(true);
        cmbCliente.setEnabled(true); // Cliente no editable por ahora
        dateFechaInicio.setEnabled(true);
        tablaDetalles.setEnabled(true);
        btnAñadir.setVisible(true);
        btnGuardar.setVisible(true);
    }

    private void guardarCambios() {
        Pedido pedido = new Pedido();
        pedido.setId_pedido(idPedido);
        pedido.setNombre(txtNombrePedido.getText().trim());

        // Obtener y validar las fechas
        java.util.Date fechaInicio = dateFechaInicio.getDate();

        if (fechaInicio == null) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una fecha de inicio válida.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        pedido.setFecha_inicio(fechaInicio);

        // Obtener cliente_codigo basado en el cliente seleccionado
        String clienteSeleccionado = (String) cmbCliente.getSelectedItem();
        if (clienteSeleccionado == null || clienteSeleccionado.equals("Seleccione cliente:")) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un cliente válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Cliente clienteSeleccionadoObj = null;
        for (Cliente cli : clientes) {
            String nombreCompleto = cli.getNombre() + " " + cli.getApellido();
            if (nombreCompleto.equals(clienteSeleccionado)) {
                clienteSeleccionadoObj = cli;
                break;
            }
        }

        if (clienteSeleccionadoObj != null) {
            pedido.setIdCliente(clienteSeleccionadoObj.getId_cliente());
        } else {
            JOptionPane.showMessageDialog(this, "Error al obtener el cliente seleccionado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtener usuario_id_usuario (esto debería venir de la base de datos original si es necesario)
        // Nota: Si no necesitas actualizar el usuario_id_usuario, puedes omitir esta parte o obtenerlo de otra fuente
        Ctrl_Pedido.MaterialConDetalles materialOriginal = controlador.obtenerPedidoPorId(idPedido);
        if (materialOriginal != null) {
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo obtener los datos originales del pedido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtener detalles de la tabla
        DefaultTableModel model = (DefaultTableModel) tablaDetalles.getModel();
        List<PedidoDetalle> detalles = new java.util.ArrayList<>();

        for (int i = 0; i < model.getRowCount(); i++) {
            // Descripción (ya es String)
            String descripcion = model.getValueAt(i, 0).toString();

            // Cantidad
            Object cantidadObj = model.getValueAt(i, 1);
            int cantidad = 0;
            try {
                cantidad = (cantidadObj != null)
                        ? (cantidadObj instanceof Number ? ((Number) cantidadObj).intValue()
                                : Integer.parseInt(cantidadObj.toString())) : 0;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Cantidad inválida en fila " + (i + 1), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Dimensiones
            String medida = model.getValueAt(i, 2).toString();

            // Precio unitario
            Object precioUnitarioObj = model.getValueAt(i, 3);
            double precioUnitario = 0.0;
            try {
                precioUnitario = (precioUnitarioObj != null)
                        ? (precioUnitarioObj instanceof Number ? ((Number) precioUnitarioObj).doubleValue()
                                : Double.parseDouble(precioUnitarioObj.toString())) : 0.0;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Precio unitario inválido en fila " + (i + 1), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Subtotal
            Object subtotalObj = model.getValueAt(i, 4);
            double subtotal = 0.0;
            try {
                subtotal = (subtotalObj != null)
                        ? (subtotalObj instanceof Number ? ((Number) subtotalObj).doubleValue()
                                : Double.parseDouble(subtotalObj.toString())) : 0.0;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Subtotal inválido en fila " + (i + 1), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validaciones adicionales
            if (descripcion.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Descripción vacía en fila " + (i + 1), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (cantidad <= 0 || precioUnitario <= 0) {
                JOptionPane.showMessageDialog(this, "Valores deben ser positivos en fila " + (i + 1), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            PedidoDetalle detalle = new PedidoDetalle();
            detalle.setDescripcion(descripcion);
            detalle.setCantidad(cantidad);
            detalle.setDimensiones(medida);
            detalle.setPrecioUnitario(precioUnitario);
            detalle.setSubtotal(subtotal);
            detalle.setTotal(subtotal); // Total igual al subtotal por ahora
            detalle.setPedidoIdPedido(idPedido);
            detalles.add(detalle);
        }

        // Guardar cambios en la base de datos
        Connection con = null;
        try {
            con = Conexion.getConnection();
            con.setAutoCommit(false); // Iniciar transacción

            // Actualizar el pedido
            if (!controlador.actualizar(pedido)) {
                throw new SQLException("Error al actualizar el pedido.");
            }

            // Eliminar detalles antiguos
            String sqlDelete = "DELETE FROM detalle_pedido WHERE pedido_id_pedido = ?";
            try (PreparedStatement stmtDelete = con.prepareStatement(sqlDelete)) {
                stmtDelete.setInt(1, idPedido);
                stmtDelete.executeUpdate();
            }

            // Insertar nuevos detalles
            String sqlInsert = "INSERT INTO detalle_pedido (descripcion, cantidad, dimension, precio_unitario, subtotal, total, pedido_id_pedido) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmtInsert = con.prepareStatement(sqlInsert)) {
                for (PedidoDetalle detalle : detalles) {
                    stmtInsert.setString(1, detalle.getDescripcion());
                    stmtInsert.setInt(2, detalle.getCantidad());
                    stmtInsert.setString(3, detalle.getDimensiones());
                    stmtInsert.setDouble(4, detalle.getPrecioUnitario());
                    stmtInsert.setDouble(5, detalle.getSubtotal());
                    stmtInsert.setDouble(6, detalle.getTotal());
                    stmtInsert.setInt(7, idPedido);
                    stmtInsert.executeUpdate();
                }
            }

            con.commit(); // Confirmar transacción
            JOptionPane.showMessageDialog(this, "Pedido actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarDatosCotizacion(); // Recargar datos
            deshabilitarEdicion();
        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback(); // Revertir transacción en caso de error
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error al revertir la transacción: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            JOptionPane.showMessageDialog(this, "Error al actualizar los datos en la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true); // Restaurar auto-commit
                    con.close();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error al cerrar la conexión: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void cargarClientes() {
        try {
            Ctrl_Cliente ctrl = new Ctrl_Cliente();
            clientes = ctrl.obtenerClientes();
            cmbCliente.removeAllItems();
            cmbCliente.addItem("Seleccione cliente:");
            if (clientes != null) {
                for (Cliente cli : clientes) {
                    String nombreCompleto = cli.getNombre() + " " + cli.getApellido();
                    cmbCliente.addItem(nombreCompleto);
                }
            } else {
                System.out.println("Lista de clientes es null. Verifica Ctrl_Cliente.obtenerClientes().");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los clientes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            cmbCliente.addItem("Error al cargar clientes");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnVolver = new RSMaterialComponent.RSButtonShape();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        lblNumeroPedido = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtNombrePedido = new RSMaterialComponent.RSTextFieldMaterial();
        jLabel19 = new javax.swing.JLabel();
        cmbCliente = new RSMaterialComponent.RSComboBoxMaterial();
        jLabel9 = new javax.swing.JLabel();
        dateFechaInicio = new com.toedter.calendar.JDateChooser();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel17 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaDetalles = new RSMaterialComponent.RSTableMetroCustom();
        btnAñadir = new RSMaterialComponent.RSButtonShape();
        jLabel16 = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        btnEditar1 = new RSMaterialComponent.RSButtonShape();
        btnGuardar = new RSMaterialComponent.RSButtonShape();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(1304, 742));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnVolver.setBackground(new java.awt.Color(46, 49, 82));
        btnVolver.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        btnVolver.setIcon(new javax.swing.ImageIcon(getClass().getResource("/volver (1).png"))); // NOI18N
        btnVolver.setText(" Volver");
        btnVolver.setBackgroundHover(new java.awt.Color(67, 150, 209));
        btnVolver.setFont(new java.awt.Font("Roboto Bold", 1, 17)); // NOI18N
        btnVolver.setForma(RSMaterialComponent.RSButtonShape.FORMA.ROUND);
        btnVolver.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnVolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVolverActionPerformed(evt);
            }
        });
        add(btnVolver, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 30, 130, 30));

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setText("Detalles del Pedido #");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 10, 190, 20));

        lblNumeroPedido.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblNumeroPedido.setText("jlabel");
        jPanel1.add(lblNumeroPedido, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 10, 60, 20));

        add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 70, 910, 40));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel11.setText("Pedido: ");
        add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 120, 80, 20));

        txtNombrePedido.setForeground(new java.awt.Color(0, 0, 0));
        txtNombrePedido.setColorMaterial(new java.awt.Color(204, 204, 204));
        txtNombrePedido.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        txtNombrePedido.setPhColor(new java.awt.Color(0, 0, 0));
        txtNombrePedido.setPlaceholder("Ingrese el nombre...");
        txtNombrePedido.setSelectionColor(new java.awt.Color(0, 0, 0));
        txtNombrePedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNombrePedidoActionPerformed(evt);
            }
        });
        add(txtNombrePedido, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 121, 180, 21));

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel19.setText("Cliente: ");
        add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 160, 80, 20));

        cmbCliente.setColorMaterial(new java.awt.Color(0, 0, 0));
        cmbCliente.setFont(new java.awt.Font("Roboto Bold", 0, 16)); // NOI18N
        add(cmbCliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 158, 163, 26));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel9.setText("Fecha inicio:");
        add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 120, 110, 20));

        dateFechaInicio.setBackground(new java.awt.Color(255, 255, 255));
        dateFechaInicio.setToolTipText("");
        add(dateFechaInicio, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 120, 160, 25));
        add(jSeparator3, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 200, 910, 10));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel17.setText("Productos incluidos:");
        add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 210, 190, 20));

        tablaDetalles.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Descripcion", "Cantidad", "Dimesiones", "Precio unitario", "Subtotal", "Accion"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, true, true, true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaDetalles.setBackgoundHead(new java.awt.Color(46, 49, 82));
        tablaDetalles.setBackgoundHover(new java.awt.Color(109, 160, 221));
        tablaDetalles.setBorderHead(null);
        tablaDetalles.setBorderRows(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        tablaDetalles.setColorBorderHead(new java.awt.Color(46, 49, 82));
        tablaDetalles.setColorBorderRows(new java.awt.Color(46, 49, 82));
        tablaDetalles.setColorPrimaryText(new java.awt.Color(0, 0, 0));
        tablaDetalles.setColorSecondary(new java.awt.Color(255, 255, 255));
        tablaDetalles.setColorSecundaryText(new java.awt.Color(0, 0, 0));
        tablaDetalles.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        tablaDetalles.setFontHead(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        tablaDetalles.setFontRowHover(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        tablaDetalles.setFontRowSelect(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        tablaDetalles.setSelectionBackground(new java.awt.Color(109, 160, 221));
        tablaDetalles.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaDetallesMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tablaDetalles);
        tablaDetalles.getColumnModel().getColumn(0).setPreferredWidth(10);

        add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 270, 810, 250));

        btnAñadir.setBackground(new java.awt.Color(46, 49, 82));
        btnAñadir.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        btnAñadir.setText(" Añadir");
        btnAñadir.setBackgroundHover(new java.awt.Color(67, 150, 209));
        btnAñadir.setFont(new java.awt.Font("Roboto Bold", 1, 15)); // NOI18N
        btnAñadir.setForma(RSMaterialComponent.RSButtonShape.FORMA.ROUND);
        btnAñadir.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnAñadir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAñadirActionPerformed(evt);
            }
        });
        add(btnAñadir, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 230, 110, 30));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel16.setText("Total del pedido:");
        add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 530, -1, -1));

        lblTotal.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTotal.setText("0.00");
        add(lblTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 530, -1, -1));
        add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 580, 910, 10));

        btnEditar1.setBackground(new java.awt.Color(46, 49, 82));
        btnEditar1.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        btnEditar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pencil (1)_1.png"))); // NOI18N
        btnEditar1.setText(" Editar pedido");
        btnEditar1.setBackgroundHover(new java.awt.Color(67, 150, 209));
        btnEditar1.setFont(new java.awt.Font("Roboto Bold", 1, 17)); // NOI18N
        btnEditar1.setForma(RSMaterialComponent.RSButtonShape.FORMA.ROUND);
        btnEditar1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnEditar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditar1ActionPerformed(evt);
            }
        });
        add(btnEditar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 600, 160, 30));

        btnGuardar.setBackground(new java.awt.Color(46, 49, 82));
        btnGuardar.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        btnGuardar.setText("Guardar cambios");
        btnGuardar.setBackgroundHover(new java.awt.Color(67, 150, 209));
        btnGuardar.setFont(new java.awt.Font("Roboto Bold", 1, 17)); // NOI18N
        btnGuardar.setForma(RSMaterialComponent.RSButtonShape.FORMA.ROUND);
        btnGuardar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });
        add(btnGuardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 600, 160, 30));
    }// </editor-fold>//GEN-END:initComponents

    private void btnVolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVolverActionPerformed
         HistorialCot h = null;
        try {
            h = new HistorialCot(jPanel1, true); // Añadir el segundo parámetro
        } catch (SQLException ex) {
            Logger.getLogger(DetallesCot.class.getName()).log(Level.SEVERE, null, ex);
        }
        h.setSize(1290, 730);
        h.setLocation(0, 0);
        jPanel1.removeAll();
        jPanel1.add(h);
        jPanel1.revalidate();
        jPanel1.repaint();
    }//GEN-LAST:event_btnVolverActionPerformed

    private void txtNombrePedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombrePedidoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombrePedidoActionPerformed

    private void tablaDetallesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaDetallesMouseClicked
        int column = tablaDetalles.getColumnModel().getColumnIndexAtX(evt.getX());
        int row = evt.getY() / tablaDetalles.getRowHeight();

        if (row < tablaDetalles.getRowCount() && row >= 0 && column == 5 && editando) { // Si se hace clic en "Eliminar"
            DefaultTableModel model = (DefaultTableModel) tablaDetalles.getModel();
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de que desea eliminar esta fila?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                model.removeRow(row);
                actualizarTotal();
            }
        }
    }//GEN-LAST:event_tablaDetallesMouseClicked

    private void btnAñadirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAñadirActionPerformed
        DefaultTableModel model = (DefaultTableModel) tablaDetalles.getModel();
        // Agregar una nueva fila vacía
        model.addRow(new Object[]{"", 0, "", 0.0, 0.0, "Eliminar"});
        actualizarTotal();
    }//GEN-LAST:event_btnAñadirActionPerformed

    private void btnEditar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditar1ActionPerformed
        editando = true;
        habilitarEdicion();
    }//GEN-LAST:event_btnEditar1ActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        guardarCambios();
    }//GEN-LAST:event_btnGuardarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private RSMaterialComponent.RSButtonShape btnAñadir;
    private RSMaterialComponent.RSButtonShape btnEditar1;
    private RSMaterialComponent.RSButtonShape btnGuardar;
    private RSMaterialComponent.RSButtonShape btnVolver;
    private RSMaterialComponent.RSComboBoxMaterial cmbCliente;
    private com.toedter.calendar.JDateChooser dateFechaInicio;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel lblNumeroPedido;
    private javax.swing.JLabel lblTotal;
    private RSMaterialComponent.RSTableMetroCustom tablaDetalles;
    private RSMaterialComponent.RSTextFieldMaterial txtNombrePedido;
    // End of variables declaration//GEN-END:variables

    private void cargarDetallesCotizacion() {
        DefaultTableModel model = (DefaultTableModel) tablaDetalles.getModel();
        model.setRowCount(0);

        try (Connection con = Conexion.getConnection(); PreparedStatement pstmt = con.prepareStatement(
                "SELECT detalle, unidad, cantidad, valor_unitario, sub_total "
                + "FROM cotizacion WHERE id_cotizacion = ?")) {

            pstmt.setInt(1, idPedido);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("detalle"),
                    rs.getInt("cantidad"),
                    rs.getString("unidad"),
                    rs.getDouble("valor_unitario"),
                    rs.getDouble("sub_total"),
                    "Eliminar"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar detalles: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
