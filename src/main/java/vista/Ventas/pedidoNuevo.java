/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package vista.Ventas;

import controlador.Ctrl_Cliente;
import controlador.Ctrl_Pedido;
import java.awt.Frame;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import modelo.Cliente;
import modelo.Pedido;
import modelo.PedidoDetalle;
import vista.alertas.DebesLLenarDatos;
import vista.alertas.PedidoIngreseNombre;
import vista.alertas.PedidoIngresefechaFIN;
import vista.alertas.PedidoIngresefechaINI;
import vista.alertas.Pedidoseleccionecliente;
import vista.crear_cliente;

/**
 *
 * @author ZenBook
 */
public class pedidoNuevo extends javax.swing.JDialog {

    private List<Cliente> clientes;
    private List<PedidoDetalle> detalles = new ArrayList<>(); // Lista para almacenar los detalles
    public boolean clienteGuardado = false;
    public Cliente cliente;
    private pedido parent; // Referencia a la clase pedido
    private Ctrl_Pedido controlador;

    /**
     * Creates new form pedidoNuevo
     */
    public pedidoNuevo(java.awt.Frame parent, boolean modal, pedido pedidoParent) {
        super(parent, modal);  // Corrección: usar 'parent' en lugar de 'parentFrame'
        this.parent = pedidoParent;
        this.controlador = new Ctrl_Pedido();
        initComponents();

        tablaDetalles.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        TableColumn descripcionColumn = tablaDetalles.getColumnModel().getColumn(0);
        descripcionColumn.setPreferredWidth(100); // Ajustar el ancho de la columna

        TableColumn cantidadColumn = tablaDetalles.getColumnModel().getColumn(1);
        cantidadColumn.setPreferredWidth(30); // Ajustar el ancho de la columna

        TableColumn dimensionesColumn = tablaDetalles.getColumnModel().getColumn(2);
        dimensionesColumn.setPreferredWidth(110); // Ajustar el ancho de la columna

        TableColumn subtotalColumn = tablaDetalles.getColumnModel().getColumn(4);
        subtotalColumn.setPreferredWidth(60); // Ajustar el ancho de la columna

        TableColumn editarColumn = tablaDetalles.getColumnModel().getColumn(5);
        editarColumn.setPreferredWidth(20); // Ajustar el ancho de la columna

        TableColumn eliminarColumn = tablaDetalles.getColumnModel().getColumn(6);
        eliminarColumn.setPreferredWidth(20); // Ajustar el ancho de la columna

        cargarClientes();

        // En el constructor después de initComponents():
        dateFinicio.addPropertyChangeListener("date", evt -> validarFechasEnTiempoReal());
        dateFfin.addPropertyChangeListener("date", evt -> validarFechasEnTiempoReal());

        tablaDetalles.getModel().addTableModelListener(e -> {
            if (e.getColumn() == 1 || e.getColumn() == 3) { // Si se edita "Cantidad" o "Precio unitario"
                int row = e.getFirstRow();
                DefaultTableModel model = (DefaultTableModel) tablaDetalles.getModel();

                try {
                    // Obtener y manejar valores nulos
                    Object cantidadObj = model.getValueAt(row, 1);
                    Object precioUnitarioObj = model.getValueAt(row, 3);

                    int cantidad = (cantidadObj != null) ? Integer.parseInt(cantidadObj.toString().trim().isEmpty() ? "0" : cantidadObj.toString()) : 0;
                    double precioUnitario = (precioUnitarioObj != null) ? Double.parseDouble(precioUnitarioObj.toString().trim().replace("$", "").replace(".", "")) : 0.0;

                    double subtotal = cantidad * precioUnitario;
                    // Formatear subtotal con signo de pesos y sin decimales si son .00
                    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
                    symbols.setGroupingSeparator('.'); // Usar punto como separador de miles
                    DecimalFormat df = new DecimalFormat("$#,##0.##", symbols);
                    model.setValueAt(df.format(subtotal), row, 4);
                } catch (NumberFormatException ex) {
                    model.setValueAt("$0", row, 4); // Si hay un error, establecer el subtotal a 0 con signo de pesos
                }

                actualizarTotal();
            }
        });

    }

    private void actualizarTotal() {
        DefaultTableModel model = (DefaultTableModel) tablaDetalles.getModel();
        double total = 0.0;

        for (int i = 0; i < model.getRowCount(); i++) {
            try {
                double subtotal = Double.parseDouble(model.getValueAt(i, 4).toString().replace("$", "").replace(".", ""));
                total += subtotal;
            } catch (NumberFormatException e) {
                // Ignorar filas con valores inválidos
            }
        }

        // Formatear total con signo de pesos y sin decimales si son .00
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator('.'); // Usar punto como separador de miles
        DecimalFormat df = new DecimalFormat("$#,##0.##", symbols);
        lblTotal.setText(df.format(total)); // Mostrar el total en el label
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

    private void editarFila(int row) {
        DefaultTableModel model = (DefaultTableModel) tablaDetalles.getModel();

        // Obtener los datos de la fila seleccionada
        String descripcion = (String) model.getValueAt(row, 0);
        Object cantidadObj = model.getValueAt(row, 1);
        String cantidad = (cantidadObj != null) ? String.valueOf(cantidadObj) : "";
        String dimensiones = (String) model.getValueAt(row, 2);
        String precioUnitario = (String) model.getValueAt(row, 3);

        // Abrir el diálogo de edición con los datos
        editarSubdetalles dialog = new editarSubdetalles(new javax.swing.JFrame(), true, descripcion, cantidad, dimensiones, precioUnitario);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);

        // Obtener el detalle editado
        PedidoDetalle detalle = dialog.getDetalle();
        if (detalle != null) {
            // Formatear precio unitario y subtotal
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
            symbols.setGroupingSeparator('.');
            DecimalFormat df = new DecimalFormat("$#,##0.##", symbols);

            // Actualizar la fila en la tabla
            model.setValueAt(detalle.getDescripcion(), row, 0);
            model.setValueAt(String.valueOf(detalle.getCantidad()), row, 1);
            model.setValueAt(detalle.getDimensiones(), row, 2);
            model.setValueAt(df.format(detalle.getPrecioUnitario()), row, 3);
            model.setValueAt(df.format(detalle.getSubtotal()), row, 4);

            // Actualizar el total
            actualizarTotal();
        }
    }

    private void eliminarFila(int row) {
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

    private void validarFechasEnTiempoReal() {
        if (dateFinicio.getDate() != null && dateFfin.getDate() != null) {
            if (dateFfin.getDate().before(dateFinicio.getDate())) {
                JOptionPane.showMessageDialog(this,
                        "La fecha final no puede ser anterior a la fecha inicial",
                        "Error en fechas",
                        JOptionPane.ERROR_MESSAGE);

                dateFfin.setDate(null); // Limpia la fecha incorrecta
                dateFfin.requestFocus(); // Enfoca el campo para corrección
            }
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

        panelP = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        cmbCliente = new RSMaterialComponent.RSComboBoxMaterial();
        btnCancelar = new rojeru_san.RSButtonRiple();
        btnGuardar = new rojeru_san.RSButtonRiple();
        lblTotal = new javax.swing.JLabel();
        dateFinicio = new com.toedter.calendar.JDateChooser();
        dateFfin = new com.toedter.calendar.JDateChooser();
        jLabel9 = new javax.swing.JLabel();
        txtNombre = new RSMaterialComponent.RSTextFieldMaterial();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaDetalles = new RSMaterialComponent.RSTableMetroCustom();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel11 = new javax.swing.JLabel();
        btnClienteN = new RSMaterialComponent.RSButtonShape();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        btnAñadir = new RSMaterialComponent.RSButtonShape();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelP.setBackground(new java.awt.Color(255, 255, 255));
        panelP.setPreferredSize(new java.awt.Dimension(500, 500));
        panelP.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(46, 49, 82));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Century751 BT", 1, 23)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Agregar pedido");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, -1, -1, 40));

        panelP.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 880, 40));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jLabel3.setText("Nombre:");
        panelP.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 50, -1, -1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jLabel7.setText("Fecha fin:");
        panelP.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 50, -1, -1));

        cmbCliente.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Seleccione cliente:" }));
        cmbCliente.setColorMaterial(new java.awt.Color(0, 0, 0));
        cmbCliente.setFont(new java.awt.Font("Roboto Bold", 0, 14)); // NOI18N
        cmbCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbClienteActionPerformed(evt);
            }
        });
        panelP.add(cmbCliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 160, 170, 30));

        btnCancelar.setBackground(new java.awt.Color(46, 49, 82));
        btnCancelar.setText("Cancelar");
        btnCancelar.setColorHover(new java.awt.Color(204, 0, 0));
        btnCancelar.setFont(new java.awt.Font("Humnst777 BlkCn BT", 1, 14)); // NOI18N
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });
        panelP.add(btnCancelar, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 610, 140, -1));

        btnGuardar.setBackground(new java.awt.Color(46, 49, 82));
        btnGuardar.setText("Guardar");
        btnGuardar.setColorHover(new java.awt.Color(0, 153, 51));
        btnGuardar.setFont(new java.awt.Font("Humnst777 BlkCn BT", 1, 14)); // NOI18N
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });
        panelP.add(btnGuardar, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 610, 140, -1));

        lblTotal.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblTotal.setText("0.00");
        panelP.add(lblTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 560, -1, -1));

        dateFinicio.setBackground(new java.awt.Color(255, 255, 255));
        dateFinicio.setToolTipText("");
        panelP.add(dateFinicio, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 80, 190, 30));

        dateFfin.setBackground(new java.awt.Color(255, 255, 255));
        dateFfin.setToolTipText("");
        panelP.add(dateFfin, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 80, 200, 30));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jLabel9.setText("Cliente:");
        panelP.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 130, -1, -1));

        txtNombre.setForeground(new java.awt.Color(0, 0, 0));
        txtNombre.setColorMaterial(new java.awt.Color(0, 0, 0));
        txtNombre.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtNombre.setPhColor(new java.awt.Color(0, 0, 0));
        txtNombre.setPlaceholder("Ingrese el nombre...");
        txtNombre.setSelectionColor(new java.awt.Color(0, 0, 0));
        txtNombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNombreActionPerformed(evt);
            }
        });
        panelP.add(txtNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 80, 200, 30));

        tablaDetalles.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Descripcion", "Cantidad", "Dimesiones", "Precio unitario", "Subtotal", "Editar", "Eliminar"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
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

        panelP.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 290, 840, 250));
        panelP.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 210, 840, 10));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jLabel11.setText("Fecha inicio:");
        panelP.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 50, -1, -1));

        btnClienteN.setBackground(new java.awt.Color(46, 49, 82));
        btnClienteN.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        btnClienteN.setIcon(new javax.swing.ImageIcon(getClass().getResource("/plus (2).png"))); // NOI18N
        btnClienteN.setBackgroundHover(new java.awt.Color(67, 150, 209));
        btnClienteN.setFont(new java.awt.Font("Roboto Bold", 1, 15)); // NOI18N
        btnClienteN.setForma(RSMaterialComponent.RSButtonShape.FORMA.ROUND);
        btnClienteN.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        btnClienteN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClienteNActionPerformed(evt);
            }
        });
        panelP.add(btnClienteN, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 160, 25, 25));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 17)); // NOI18N
        jLabel12.setText("Detalle del pedido: ");
        panelP.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 230, -1, -1));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel13.setText("Total del pedido:");
        panelP.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 560, -1, -1));

        btnAñadir.setBackground(new java.awt.Color(46, 49, 82));
        btnAñadir.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        btnAñadir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/anadir (1).png"))); // NOI18N
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
        panelP.add(btnAñadir, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 250, 110, 30));

        getContentPane().add(panelP, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 880, 670));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmbClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbClienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbClienteActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        clienteGuardado = false;
        dispose();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        String nombre = txtNombre.getText().trim();
        String clienteNombreCompleto = (String) cmbCliente.getSelectedItem();
        Date fechaInicio = dateFinicio.getDate();
        Date fechaFin = dateFfin.getDate();

        // Validaciones
        if (nombre.isEmpty()) {
            PedidoIngreseNombre dialog = new PedidoIngreseNombre((Frame) this.getParent(), true);
            dialog.setVisible(true);
            return;
        }

        if (clienteNombreCompleto == null || clienteNombreCompleto.equals("Seleccione cliente:")) {
            Pedidoseleccionecliente dialog = new Pedidoseleccionecliente((Frame) this.getParent(), true);
            dialog.setVisible(true);
            return;
        }

        if (fechaInicio == null) {
            PedidoIngresefechaINI dialog = new PedidoIngresefechaINI((Frame) this.getParent(), true);
            dialog.setVisible(true);
            return;
        }

        if (fechaFin == null) {
            PedidoIngresefechaFIN dialog = new PedidoIngresefechaFIN((Frame) this.getParent(), true);
            dialog.setVisible(true);
            return;
        }

        // Validar que haya al menos un detalle válido
        DefaultTableModel model = (DefaultTableModel) tablaDetalles.getModel();
        detalles.clear(); // Limpiar la lista antes de llenarla

        for (int i = 0; i < model.getRowCount(); i++) {
            String descripcion = (String) model.getValueAt(i, 0);
            String cantidadStr = ((String) model.getValueAt(i, 1)).replace("$", "").replace(",", "");
            String dimensiones = (String) model.getValueAt(i, 2);
            String precioUnitarioStr = (String) model.getValueAt(i, 3);
            String subtotalStr = ((String) model.getValueAt(i, 4)).replace("$", "").replace(",", "");

            // Validar descripción y dimensiones
            if (descripcion.isEmpty()) {
                JOptionPane.showMessageDialog(this, "La descripción en la fila " + (i + 1) + " no puede estar vacía.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (dimensiones.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Las dimensiones en la fila " + (i + 1) + " no pueden estar vacías.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validar cantidad
            int cantidad;
            try {
                cantidad = Integer.parseInt(cantidadStr.replace("$", "").replace(",", ""));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Cantidad inválida en la fila " + (i + 1) + ".", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validar precio unitario
            double precioUnitario;
            try {
                precioUnitario = Double.parseDouble(precioUnitarioStr.replace("$", "").replace(",", "").replace(".", ""));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Precio unitario inválido en la fila " + (i + 1) + ".", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validar subtotal
            double subtotal;
            try {
                subtotal = Double.parseDouble(subtotalStr.replace("$", "").replace(",", "").replace(".", ""));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Subtotal inválido en la fila " + (i + 1) + ".", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Crear DetallePedido
            PedidoDetalle detalle = new PedidoDetalle();
            detalle.setDescripcion(descripcion);
            detalle.setCantidad(cantidad); // Convertir a String porque el modelo lo define como VARCHAR
            detalle.setDimensiones(dimensiones); // Usamos "dimensiones" como "medida"
            detalle.setPrecioUnitario(precioUnitario);
            detalle.setSubtotal(subtotal);
            detalle.setTotal(subtotal); // Total igual al subtotal por ahora
            detalle.setPedidoIdPedido(0); // Se asignará después de insertar el pedido

            detalles.add(detalle);
        }

        if (detalles.isEmpty()) {
            DebesLLenarDatos dialog = new DebesLLenarDatos((Frame) this.getParent(), true);
            dialog.setVisible(true);
            return;
        }

        // Obtener cliente_codigo
        int clienteCodigo = -1;
        for (Cliente cli : clientes) {
            String nombreCompleto = cli.getNombre() + " " + cli.getApellido();
            if (nombreCompleto.equals(clienteNombreCompleto)) {
                clienteCodigo = cli.getId_cliente(); // Asegúrate de que Cliente tenga getCodigo()
                break;
            }
        }

        if (clienteCodigo == -1) {
            JOptionPane.showMessageDialog(this, "Error: No se encontró el cliente seleccionado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtener usuario_id_usuario (ajusta según tu lógica de autenticación)
        int usuarioIdUsuario = 1; // Ejemplo, reemplaza con el ID del usuario logueado

        // Estado fijo como "Pendiente"
        String estado = "Pendiente";

        // Crear el objeto Pedido con num_pedido vacío (se generará en el controlador)
        Pedido nuevoPedido = new Pedido(
            0, // id_pedido (se genera automáticamente)
            "", // num_pedido (se genera en Ctrl_Pedido)
            nombre,
            estado,
            fechaInicio,
            fechaFin,
            clienteCodigo
        );
        nuevoPedido.setDetalles(detalles);

        // Insertar pedido y detalles
        int nuevoIdPedido = controlador.insertar(nuevoPedido, detalles);
        if (nuevoIdPedido > 0) {
            String numeroPedido = nuevoPedido.getNum_pedido(); // Asegúrate de que Pedido tenga este getter
            JOptionPane.showMessageDialog(
                this,
                "Pedido guardado exitosamente.\nNúmero: " + numeroPedido,
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE
            );
            parent.cargarDatosIniciales();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar el pedido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void txtNombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombreActionPerformed

    private void tablaDetallesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaDetallesMouseClicked
        int column = tablaDetalles.getColumnModel().getColumnIndexAtX(evt.getX());
        int row = evt.getY() / tablaDetalles.getRowHeight();

        if (row < tablaDetalles.getRowCount() && row >= 0) {
            if (column == 5) { // Columna "Editar"
                editarFila(row);
            } else if (column == 6) { // Columna "Eliminar"
                eliminarFila(row);
            }
        }
    }//GEN-LAST:event_tablaDetallesMouseClicked

    private void btnClienteNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClienteNActionPerformed
        // Crear el diálogo de cliente
        crear_cliente dialog = new crear_cliente(new javax.swing.JFrame(), true);
        dialog.setLocationRelativeTo(null);

        // Configurar el listener para recargar clientes cuando se guarde uno nuevo
        dialog.setClienteGuardadoListener(() -> {
            cargarClientes(); // Recargar la lista de clientes
        });

        dialog.setVisible(true);

        // Volver a mostrar el diálogo de pedidoNuevo (si se ocultó)
        this.setVisible(true);
    }//GEN-LAST:event_btnClienteNActionPerformed

    private void btnAñadirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAñadirActionPerformed
        nuevoSubdetalles dialog = new nuevoSubdetalles(new javax.swing.JFrame(), true);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);

        // Obtener el detalle creado
        PedidoDetalle detalle = dialog.getDetalle();
        if (detalle != null) {
            // Formatear precio unitario y subtotal
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
            symbols.setGroupingSeparator('.');
            DecimalFormat df = new DecimalFormat("$#,##0.##", symbols);

            // Añadir la fila a la tabla
            DefaultTableModel model = (DefaultTableModel) tablaDetalles.getModel();
            model.addRow(new Object[]{
                detalle.getDescripcion(),
                String.valueOf(detalle.getCantidad()),
                detalle.getDimensiones(),
                df.format(detalle.getPrecioUnitario()),
                df.format(detalle.getSubtotal()),
                "Editar",
                "Eliminar"
            });

            // Actualizar el total
            actualizarTotal();
        }
    }//GEN-LAST:event_btnAñadirActionPerformed

    /**
     * @param args the command line arguments
     */
    /*
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
    //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
    /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
     */
 /*
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(pedidoNuevo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(pedidoNuevo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(pedidoNuevo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(pedidoNuevo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
 /*
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                pedidoNuevo dialog = new pedidoNuevo(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private RSMaterialComponent.RSButtonShape btnAñadir;
    private rojeru_san.RSButtonRiple btnCancelar;
    private RSMaterialComponent.RSButtonShape btnClienteN;
    private rojeru_san.RSButtonRiple btnGuardar;
    private RSMaterialComponent.RSComboBoxMaterial cmbCliente;
    private com.toedter.calendar.JDateChooser dateFfin;
    private com.toedter.calendar.JDateChooser dateFinicio;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JPanel panelP;
    private RSMaterialComponent.RSTableMetroCustom tablaDetalles;
    private RSMaterialComponent.RSTextFieldMaterial txtNombre;
    // End of variables declaration//GEN-END:variables
}
