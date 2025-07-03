/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package vista.Produccion;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import static javax.swing.SwingConstants.CENTER;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import modelo.Conexion;
import rojeru_san.RSButtonRiple;
import vista.TemaManager;

/**
 *
 * @author pc
 */
public final class Produccion extends javax.swing.JPanel {

    private java.awt.Frame parent;
    private int idProduccion;
    private TableRowSorter<DefaultTableModel> sorter;
    private JPopupMenu popupFiltros;
    private JCheckBox chkTodosFechas;
    private JCheckBox chkUltimos3Dias;
    private JCheckBox chkUltimos7Dias;
    private JCheckBox chkUltimos15Dias;
    private JCheckBox chkUltimoMes;
    private JCheckBox chkUltimos3Meses;
    private JCheckBox chkUltimos6Meses;
    private JCheckBox chkUltimoAno;
    private JCheckBox chkTodosEstados;
    private JCheckBox chkPendiente;
    private JCheckBox chkProceso;
    private JCheckBox chkFinalizado;
    private RSButtonRiple btnAplicarFiltros;

    /**
     * Creates new form produccionContenido
     */
    public Produccion(JFrame jFrame, boolean par) {
        initComponents();
        inicializarPopupFiltros();
        aplicarTema();
        Tabla1.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Configura el modelo de tabla correctamente
        DefaultTableModel model = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Codigo", "Nombre", "Fecha inicio", "Fecha Final", "Estado", "Detalle", "Editar", "Cantidad", "Dimensiones"}
        ) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        Tabla1.setModel(model);

        // Oculta las columnas adicionales después de establecer el modelo
        Tabla1.removeColumn(Tabla1.getColumnModel().getColumn(7)); // Oculta Cantidad
        Tabla1.removeColumn(Tabla1.getColumnModel().getColumn(7)); // Oculta Dimensiones

        // Configura el renderizador especial para la columna de estado (sobrescribe el general)
        Tabla1.getColumnModel().getColumn(4).setCellRenderer(new EstadoTableCellRenderer());

        // Configura el renderizador especial para la columna "Ver" (sobrescribe el general)
        Tabla1.getColumnModel().getColumn(5).setCellRenderer(new VerTableCellRenderer());
        Tabla1.getColumnModel().getColumn(6).setCellRenderer(new EditarTableCellRenderer());

        // Ajustar el ancho de la columna
        TableColumn cantidadColumn = Tabla1.getColumnModel().getColumn(5);
        cantidadColumn.setPreferredWidth(10);
        TableColumn cantidadColumn1 = Tabla1.getColumnModel().getColumn(6);
        cantidadColumn1.setPreferredWidth(10); // Ajustar el ancho de la columna
        // Carga los datos
        cargarTablaProduccion();
        TemaManager.getInstance().addThemeChangeListener(() -> {
            aplicarTema(); // Update theme when it changes
        });
    }

    private void inicializarPopupFiltros() {
        popupFiltros = new JPopupMenu();
        chkTodosFechas = new JCheckBox("Todos (Fechas)");
        chkUltimos3Dias = new JCheckBox("Últimos 3 días");
        chkUltimos7Dias = new JCheckBox("Últimos 7 días");
        chkUltimos15Dias = new JCheckBox("Últimos 15 días");
        chkUltimoMes = new JCheckBox("Último mes");
        chkUltimos3Meses = new JCheckBox("Últimos 3 meses");
        chkUltimos6Meses = new JCheckBox("Últimos 6 meses");
        chkUltimoAno = new JCheckBox("Último año");
        chkTodosEstados = new JCheckBox("Todos (Estados)");
        chkPendiente = new JCheckBox("Pendiente");
        chkProceso = new JCheckBox("Proceso");
        chkFinalizado = new JCheckBox("Finalizado");
        btnAplicarFiltros = new RSButtonRiple();
        btnAplicarFiltros.setText("Aplicar");
        btnAplicarFiltros.setBackground(new Color(46, 49, 82));
        btnAplicarFiltros.setColorHover(new Color(0, 153, 51));

        popupFiltros.add(new JLabel("Rango de Fechas:"));
        popupFiltros.add(chkTodosFechas);
        popupFiltros.add(chkUltimos3Dias);
        popupFiltros.add(chkUltimos7Dias);
        popupFiltros.add(chkUltimos15Dias);
        popupFiltros.add(chkUltimoMes);
        popupFiltros.add(chkUltimos3Meses);
        popupFiltros.add(chkUltimos6Meses);
        popupFiltros.add(chkUltimoAno);
        popupFiltros.add(new JLabel("Estado:"));
        popupFiltros.add(chkTodosEstados);
        popupFiltros.add(chkPendiente);
        popupFiltros.add(chkProceso);
        popupFiltros.add(chkFinalizado);
        popupFiltros.add(btnAplicarFiltros);

        // Inicializar selección por defecto
        chkTodosFechas.setSelected(true);
        chkTodosEstados.setSelected(true);

        // Acción del botón Aplicar
        btnAplicarFiltros.addActionListener(e -> {
            List<String> filtrosRangoFechas = new ArrayList<>();
            List<String> filtrosEstado = new ArrayList<>();

            if (chkTodosFechas.isSelected()) {
                filtrosRangoFechas.add("Todos");
            } else {
                if (chkUltimos3Dias.isSelected()) {
                    filtrosRangoFechas.add("Últimos 3 días");
                }
                if (chkUltimos7Dias.isSelected()) {
                    filtrosRangoFechas.add("Últimos 7 días");
                }
                if (chkUltimos15Dias.isSelected()) {
                    filtrosRangoFechas.add("Últimos 15 días");
                }
                if (chkUltimoMes.isSelected()) {
                    filtrosRangoFechas.add("Último mes");
                }
                if (chkUltimos3Meses.isSelected()) {
                    filtrosRangoFechas.add("Últimos 3 meses");
                }
                if (chkUltimos6Meses.isSelected()) {
                    filtrosRangoFechas.add("Últimos 6 meses");
                }
                if (chkUltimoAno.isSelected()) {
                    filtrosRangoFechas.add("Último año");
                }
            }

            if (chkTodosEstados.isSelected()) {
                filtrosEstado.add("Todos");
            } else {
                if (chkPendiente.isSelected()) {
                    filtrosEstado.add("Pendiente");
                }
                if (chkProceso.isSelected()) {
                    filtrosEstado.add("Proceso");
                }
                if (chkFinalizado.isSelected()) {
                    filtrosEstado.add("Finalizado");
                }
            }

            aplicarFiltros(filtrosRangoFechas, filtrosEstado);
            popupFiltros.setVisible(false);
        });

        // Lógica para "Todos" en fechas
        chkTodosFechas.addActionListener(e -> {
            boolean selected = chkTodosFechas.isSelected();
            chkUltimos3Dias.setSelected(false);
            chkUltimos7Dias.setSelected(false);
            chkUltimos15Dias.setSelected(false);
            chkUltimoMes.setSelected(false);
            chkUltimos3Meses.setSelected(false);
            chkUltimos6Meses.setSelected(false);
            chkUltimoAno.setSelected(false);
            if (!selected) {
                chkTodosFechas.setSelected(true); // Mantener "Todos" seleccionado si no hay otros
            }
        });

        // Lógica para desmarcar "Todos" si se selecciona otro rango de fechas
        ActionListener rangoListener = e -> {
            if (chkUltimos3Dias.isSelected() || chkUltimos7Dias.isSelected() || chkUltimos15Dias.isSelected()
                    || chkUltimoMes.isSelected() || chkUltimos3Meses.isSelected() || chkUltimos6Meses.isSelected()
                    || chkUltimoAno.isSelected()) {
                chkTodosFechas.setSelected(false);
            } else {
                chkTodosFechas.setSelected(true);
            }
        };
        chkUltimos3Dias.addActionListener(rangoListener);
        chkUltimos7Dias.addActionListener(rangoListener);
        chkUltimos15Dias.addActionListener(rangoListener);
        chkUltimoMes.addActionListener(rangoListener);
        chkUltimos3Meses.addActionListener(rangoListener);
        chkUltimos6Meses.addActionListener(rangoListener);
        chkUltimoAno.addActionListener(rangoListener);

        // Lógica para "Todos" en estados
        chkTodosEstados.addActionListener(e -> {
            boolean selected = chkTodosEstados.isSelected();
            chkPendiente.setSelected(false);
            chkProceso.setSelected(false);
            chkFinalizado.setSelected(false);
            if (!selected) {
                chkTodosEstados.setSelected(true); // Mantener "Todos" seleccionado si no hay otros
            }
        });

        // Lógica para desmarcar "Todos" si se selecciona otro estado
        ActionListener estadoListener = e -> {
            if (chkPendiente.isSelected() || chkProceso.isSelected() || chkFinalizado.isSelected()) {
                chkTodosEstados.setSelected(false);
            } else {
                chkTodosEstados.setSelected(true);
            }
        };
        chkPendiente.addActionListener(estadoListener);
        chkProceso.addActionListener(estadoListener);
        chkFinalizado.addActionListener(estadoListener);
    }

    public void aplicarFiltros(List<String> filtrosRangoFechas, List<String> filtrosEstado) {
        DefaultTableModel model = (DefaultTableModel) Tabla1.getModel();
        sorter = new TableRowSorter<>(model);
        Tabla1.setRowSorter(sorter);

        List<RowFilter<Object, Object>> filtros = new ArrayList<>();

        // Filtro por rango de fechas
        if (!filtrosRangoFechas.isEmpty() && !filtrosRangoFechas.contains("Todos")) {
            filtros.add(new RowFilter<Object, Object>() {
                @Override
                public boolean include(Entry<? extends Object, ? extends Object> entry) {
                    try {
                        String fechaInicioStr = entry.getStringValue(2); // Columna de Fecha inicio
                        String fechaFinalStr = entry.getStringValue(3); // Columna de Fecha final
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        Date fechaInicioRow = sdf.parse(fechaInicioStr);
                        Date fechaFinalRow = fechaFinalStr.equals("En proceso") ? null : sdf.parse(fechaFinalStr);
                        LocalDate hoy = LocalDate.now();
                        LocalDate fechaInicioLocal = fechaInicioRow.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        LocalDate fechaFinalLocal = fechaFinalRow != null
                                ? fechaFinalRow.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                                : null;

                        for (String rango : filtrosRangoFechas) {
                            LocalDate fechaLimite = null;
                            switch (rango) {
                                case "Últimos 3 días":
                                    fechaLimite = hoy.minusDays(3);
                                    break;
                                case "Últimos 7 días":
                                    fechaLimite = hoy.minusDays(7);
                                    break;
                                case "Últimos 15 días":
                                    fechaLimite = hoy.minusDays(15);
                                    break;
                                case "Último mes":
                                    fechaLimite = hoy.minusMonths(1);
                                    break;
                                case "Últimos 3 meses":
                                    fechaLimite = hoy.minusMonths(3);
                                    break;
                                case "Últimos 6 meses":
                                    fechaLimite = hoy.minusMonths(6);
                                    break;
                                case "Último año":
                                    fechaLimite = hoy.minusYears(1);
                                    break;
                            }
                            if (fechaLimite != null) {
                                // Incluir si fecha_inicio o fecha_fin están en el rango [fechaLimite, hoy]
                                boolean fechaInicioEnRango = !fechaInicioLocal.isBefore(fechaLimite) && !fechaInicioLocal.isAfter(hoy);
                                boolean fechaFinalEnRango = fechaFinalLocal != null && !fechaFinalLocal.isBefore(fechaLimite) && !fechaFinalLocal.isAfter(hoy);
                                if (fechaInicioEnRango || fechaFinalEnRango) {
                                    return true;
                                }
                            }
                        }
                        return false;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            });
        }

        // Filtro por estado
        if (!filtrosEstado.isEmpty() && !filtrosEstado.contains("Todos")) {
            filtros.add(RowFilter.regexFilter("(?i)^(" + String.join("|", filtrosEstado) + ")$", 4));
        }

        // Aplicar filtros combinados
        if (!filtros.isEmpty()) {
            sorter.setRowFilter(RowFilter.andFilter(filtros));
        } else {
            sorter.setRowFilter(null);
        }
    }

    public void aplicarTema() {
        boolean oscuro = TemaManager.getInstance().isOscuro();

        if (oscuro) {
            // Configuración para modo oscuro
            Color fondo = new Color(21, 21, 33);
            Color fondoTabla = new Color(30, 30, 45);
            Color encabezado = new Color(67, 71, 120);
            Color texto = Color.WHITE;

            // Panel principal
            jPanel1.setBackground(fondo);

            // Botón Notificación
            btnNotificacion1.setBackground(encabezado);
            btnNotificacion1.setForeground(texto);

            // Popup de filtros
            popupFiltros.setBackground(fondo);
            chkTodosFechas.setBackground(fondo);
            chkTodosFechas.setForeground(texto);
            chkUltimos3Dias.setBackground(fondo);
            chkUltimos3Dias.setForeground(texto);
            chkUltimos7Dias.setBackground(fondo);
            chkUltimos7Dias.setForeground(texto);
            chkUltimos15Dias.setBackground(fondo);
            chkUltimos15Dias.setForeground(texto);
            chkUltimoMes.setBackground(fondo);
            chkUltimoMes.setForeground(texto);
            chkUltimos3Meses.setBackground(fondo);
            chkUltimos3Meses.setForeground(texto);
            chkUltimos6Meses.setBackground(fondo);
            chkUltimos6Meses.setForeground(texto);
            chkUltimoAno.setBackground(fondo);
            chkUltimoAno.setForeground(texto);
            chkTodosEstados.setBackground(fondo);
            chkTodosEstados.setForeground(texto);
            chkPendiente.setBackground(fondo);
            chkPendiente.setForeground(texto);
            chkProceso.setBackground(fondo);
            chkProceso.setForeground(texto);
            chkFinalizado.setBackground(fondo);
            chkFinalizado.setForeground(texto);
            btnAplicarFiltros.setBackground(encabezado);
            btnAplicarFiltros.setColorHover(new Color(118, 142, 240));

            // Configuración COMPLETA de la tabla
            Tabla1.setBackground(fondoTabla);
            Tabla1.setForeground(texto);

            // Configuración de filas
            Tabla1.setColorPrimary(new Color(37, 37, 52));  // Filas impares
            Tabla1.setColorSecondary(new Color(30, 30, 45)); // Filas pares
            Tabla1.setColorPrimaryText(texto);
            Tabla1.setColorSecundaryText(texto);

            // Encabezados
            Tabla1.setBackgoundHead(encabezado);
            Tabla1.setForegroundHead(texto);
            Tabla1.setColorBorderHead(encabezado);

            // Selección y hover
            Tabla1.setSelectionBackground(new Color(67, 71, 120));
            Tabla1.setBackgoundHover(new Color(40, 50, 90));

            // Bordes y grid
            Tabla1.setColorBorderRows(new Color(60, 60, 60));
            Tabla1.setGridColor(new Color(80, 80, 80));
            Tabla1.setShowGrid(true);

            // Fuentes
            Tabla1.setFont(new Font("Tahoma", Font.PLAIN, 15));
            Tabla1.setFontHead(new Font("Tahoma", Font.BOLD, 15));
            Tabla1.setFontRowHover(new Font("Tahoma", Font.BOLD, 15));
            Tabla1.setFontRowSelect(new Font("Tahoma", Font.BOLD, 15));

            // Efectos
            Tabla1.setEffectHover(true);

            // Botón Eliminar
            btnElimi.setBackground(encabezado);
            btnElimi.setBackgroundHover(new Color(118, 142, 240));

        } else {
            Color fondo = new Color(242, 247, 255);
            Color texto = Color.BLACK;
            Color primario = new Color(72, 92, 188);

            jPanel1.setBackground(fondo);
            btnNotificacion1.setBackground(new Color(46, 49, 82));
            btnNotificacion1.setForeground(Color.WHITE);

            // Popup de filtros
            popupFiltros.setBackground(fondo);
            chkTodosFechas.setBackground(fondo);
            chkTodosFechas.setForeground(texto);
            chkUltimos3Dias.setBackground(fondo);
            chkUltimos3Dias.setForeground(texto);
            chkUltimos7Dias.setBackground(fondo);
            chkUltimos7Dias.setForeground(texto);
            chkUltimos15Dias.setBackground(fondo);
            chkUltimos15Dias.setForeground(texto);
            chkUltimoMes.setBackground(fondo);
            chkUltimoMes.setForeground(texto);
            chkUltimos3Meses.setBackground(fondo);
            chkUltimos3Meses.setForeground(texto);
            chkUltimos6Meses.setBackground(fondo);
            chkUltimos6Meses.setForeground(texto);
            chkUltimoAno.setBackground(fondo);
            chkUltimoAno.setForeground(texto);
            chkTodosEstados.setBackground(fondo);
            chkTodosEstados.setForeground(texto);
            chkPendiente.setBackground(fondo);
            chkPendiente.setForeground(texto);
            chkProceso.setBackground(fondo);
            chkProceso.setForeground(texto);
            chkFinalizado.setBackground(fondo);
            chkFinalizado.setForeground(texto);
            btnAplicarFiltros.setBackground(new Color(46, 49, 82));
            btnAplicarFiltros.setColorHover(new Color(67, 150, 209));

            Tabla1.setBackground(new Color(255, 255, 255));
            Tabla1.setBackgoundHead(new Color(46, 49, 82));
            Tabla1.setForegroundHead(Color.WHITE);
            Tabla1.setBackgoundHover(new Color(67, 150, 209));
            Tabla1.setFont(new Font("Tahoma", Font.PLAIN, 15));
            Tabla1.setColorPrimary(new Color(242, 242, 242));
            Tabla1.setColorPrimaryText(texto);
            Tabla1.setColorSecondary(new Color(255, 255, 255));
            Tabla1.setColorSecundaryText(texto);
            Tabla1.setColorBorderHead(primario);
            Tabla1.setColorBorderRows(new Color(0, 0, 0));
            Tabla1.setFontHead(new Font("Tahoma", Font.BOLD, 15));
            Tabla1.setFontRowHover(new Font("Tahoma", Font.BOLD, 15));
            Tabla1.setFontRowSelect(new Font("Tahoma", Font.BOLD, 15));
            Tabla1.setEffectHover(true);
            Tabla1.setSelectionBackground(new Color(67, 150, 209));
            Tabla1.setShowGrid(true);
            Tabla1.setGridColor(Color.BLACK);
            Tabla1.setBackground(Color.WHITE);
            Tabla1.setColorPrimary(new Color(242, 242, 242)); // Fondo filas impares
            Tabla1.setColorSecondary(Color.WHITE); // Fondo filas pares
            Tabla1.setForeground(Color.BLACK);
            btnElimi.setBackground(new Color(46, 49, 82));
        }
        Tabla1.repaint();
        Tabla1.getTableHeader().repaint();
    }

    private class EditarTableCellRenderer extends DefaultTableCellRenderer {

        private final Font fontNormal = new Font("Tahoma", Font.PLAIN, 14);
        private final Font fontBold = new Font("Tahoma", Font.BOLD, 14);

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            boolean oscuro = TemaManager.getInstance().isOscuro();
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // Configuración basada en el tema
            if (oscuro) {
                if (isSelected) {
                    c.setBackground(new Color(67, 71, 120)); // Seleccionado
                    c.setForeground(Color.WHITE);
                } else {
                    // Alternar colores para filas pares/impares
                    c.setBackground(row % 2 == 0 ? new Color(37, 37, 52) : new Color(30, 30, 45));
                    c.setForeground(Color.WHITE);
                }
            } else {
                if (isSelected) {
                    c.setBackground(table.getSelectionBackground());
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(row % 2 == 0 ? new Color(242, 242, 242) : Color.WHITE);
                    c.setForeground(Color.BLACK);
                }
            }
            setHorizontalAlignment(CENTER);
            setText("Editar");
            setBorder(BorderFactory.createLineBorder(oscuro ? new Color(153, 153, 153) : new Color(153, 153, 153), 1));
            setFont(isSelected ? fontBold : fontNormal);

            return c;
        }
    }

    private class EstadoTableCellRenderer extends DefaultTableCellRenderer {

        public EstadoTableCellRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            boolean oscuro = TemaManager.getInstance().isOscuro();
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            label.setHorizontalAlignment(CENTER);
            label.setText(value != null ? value.toString() : "");

            if (isSelected) {
                label.setForeground(oscuro ? Color.WHITE : Color.BLACK);
                label.setBackground(oscuro ? new Color(67, 71, 120) : table.getSelectionBackground());
            } else {
                label.setForeground(oscuro ? Color.WHITE : Color.BLACK);

                String estado = value != null ? value.toString() : "";
                if (oscuro) {
                    switch (estado.toLowerCase()) {
                        case "pendiente":
                            label.setBackground(new Color(153, 0, 51)); // Rojo oscuro
                            break;
                        case "proceso":
                            label.setBackground(new Color(251, 139, 36)); // Amarillo oscuro
                            break;
                        case "finalizado":
                            label.setBackground(new Color(31, 123, 21)); // Verde oscuro
                            break;
                        default:
                            label.setBackground(new Color(37, 37, 52));
                            break;
                    }
                } else {
                    switch (estado.toLowerCase()) {
                        case "pendiente":
                            label.setBackground(new Color(255, 204, 204)); // Rojo claro
                            break;
                        case "proceso":
                            label.setBackground(new Color(255, 255, 153)); // Amarillo claro
                            break;
                        case "finalizado":
                            label.setBackground(new Color(204, 255, 204)); // Verde claro
                            break;
                        default:
                            label.setBackground(Color.WHITE);
                            break;
                    }
                }
            }

            label.setBorder(BorderFactory.createLineBorder(oscuro ? new Color(153, 153, 153) : new Color(153, 153, 153), 1));
            return label;
        }
    }

    private class VerTableCellRenderer extends DefaultTableCellRenderer {

        private final Font fontNormal = new Font("Tahoma", Font.PLAIN, 14);
        private final Font fontBold = new Font("Tahoma", Font.BOLD, 14);

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            boolean oscuro = TemaManager.getInstance().isOscuro();
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (oscuro) {
                if (isSelected) {
                    c.setBackground(new Color(67, 71, 120)); // Seleccionado
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(row % 2 == 0 ? new Color(37, 37, 52) : new Color(30, 30, 45));
                    c.setForeground(Color.WHITE);
                }
            } else {
                if (isSelected) {
                    c.setBackground(table.getSelectionBackground());
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(row % 2 == 0 ? new Color(242, 242, 242) : Color.WHITE);
                    c.setForeground(Color.BLACK);
                }
            }

            setHorizontalAlignment(CENTER);
            setText("Ver");
            setBorder(BorderFactory.createLineBorder(oscuro ? new Color(153, 153, 153) : new Color(153, 153, 153), 1));
            setFont(isSelected ? fontBold : fontNormal);

            return c;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     *
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        txtbuscar = new RSMaterialComponent.RSTextFieldMaterialIcon();
        btnElimi = new RSMaterialComponent.RSButtonShape();
        jScrollPane4 = new javax.swing.JScrollPane();
        Tabla1 = new RSMaterialComponent.RSTableMetroCustom();
        btnNotificacion1 = new rojerusan.RSLabelIcon();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(1250, 630));

        jPanel1.setBackground(new java.awt.Color(242, 247, 255));

        txtbuscar.setBackground(new java.awt.Color(245, 245, 245));
        txtbuscar.setForeground(new java.awt.Color(29, 30, 91));
        txtbuscar.setColorIcon(new java.awt.Color(29, 30, 111));
        txtbuscar.setColorMaterial(new java.awt.Color(29, 30, 111));
        txtbuscar.setIcons(rojeru_san.efectos.ValoresEnum.ICONS.SEARCH);
        txtbuscar.setPlaceholder("Buscar");
        txtbuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtbuscarActionPerformed(evt);
            }
        });

        btnElimi.setBackground(new java.awt.Color(46, 49, 82));
        btnElimi.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        btnElimi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/delete (1).png"))); // NOI18N
        btnElimi.setText(" Eliminar");
        btnElimi.setBackgroundHover(new java.awt.Color(67, 150, 209));
        btnElimi.setFont(new java.awt.Font("Roboto Bold", 1, 18)); // NOI18N
        btnElimi.setForma(RSMaterialComponent.RSButtonShape.FORMA.ROUND);
        btnElimi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnElimiActionPerformed(evt);
            }
        });

        Tabla1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Codigo", "Nombre", "Fecha inicio", "Fecha final", "Estado", "Detalle"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        Tabla1.setBackgoundHead(new java.awt.Color(46, 49, 82));
        Tabla1.setBackgoundHover(new java.awt.Color(109, 160, 221));
        Tabla1.setBorderHead(null);
        Tabla1.setBorderRows(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        Tabla1.setColorBorderHead(new java.awt.Color(46, 49, 82));
        Tabla1.setColorBorderRows(new java.awt.Color(46, 49, 82));
        Tabla1.setColorPrimaryText(new java.awt.Color(0, 0, 0));
        Tabla1.setColorSecondary(new java.awt.Color(255, 255, 255));
        Tabla1.setColorSecundaryText(new java.awt.Color(0, 0, 0));
        Tabla1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        Tabla1.setFontHead(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        Tabla1.setFontRowHover(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        Tabla1.setFontRowSelect(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        Tabla1.setPreferredSize(new java.awt.Dimension(450, 499));
        Tabla1.setRowHeight(23);
        Tabla1.setSelectionBackground(new java.awt.Color(109, 160, 221));
        Tabla1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Tabla1MouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(Tabla1);
        Tabla1.getColumnModel().getColumn(0).setPreferredWidth(10);

        btnNotificacion1.setBackground(new java.awt.Color(255, 255, 255));
        btnNotificacion1.setForeground(new java.awt.Color(255, 255, 255));
        btnNotificacion1.setIcons(rojeru_san.efectos.ValoresEnum.ICONS.TUNE);
        btnNotificacion1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnNotificacion1MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtbuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 430, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnNotificacion1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(592, 592, 592)
                        .addComponent(btnElimi, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 1211, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtbuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnElimi, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNotificacion1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 536, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(97, 97, 97))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 646, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtbuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtbuscarActionPerformed
        filtrarTabla();
    }//GEN-LAST:event_txtbuscarActionPerformed

    private void btnElimiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnElimiActionPerformed
// 1. Obtener filas seleccionadas
        int[] selectedRows = Tabla1.getSelectedRows();

        // 2. Validar si hay filas seleccionadas
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this,
                    "Por favor seleccione al menos una fila para eliminar",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3. Mostrar confirmación
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea eliminar los " + selectedRows.length + " registros seleccionados?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        // 4. Si el usuario no confirma, salir
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // 5. Eliminar registros
        try (Connection con = new Conexion().getConnection()) {
            String sql = "DELETE FROM produccion WHERE id_produccion = ?";
            DefaultTableModel model = (DefaultTableModel) Tabla1.getModel();

            // Eliminar en orden inverso para evitar problemas con los índices
            for (int i = selectedRows.length - 1; i >= 0; i--) {
                int row = selectedRows[i];
                int id = (int) model.getValueAt(row, 0); // ID está en la columna 0

                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                    model.removeRow(row); // Eliminar de la tabla visual
                }
            }

            JOptionPane.showMessageDialog(this,
                    "Registros eliminados correctamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al eliminar: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnElimiActionPerformed

    private void Tabla1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Tabla1MouseClicked

        try {
            // 1. Obtener posición del clic
            int column = Tabla1.columnAtPoint(evt.getPoint());
            int viewRow = Tabla1.rowAtPoint(evt.getPoint());

            // 2. Validar que el clic fue en una fila y columna válida
            if (viewRow < 0 || column < 0) {
                return;
            }

            // 3. Convertir índice de vista a modelo (importante con filtros)
            int modelRow = Tabla1.convertRowIndexToModel(viewRow);
            DefaultTableModel model = (DefaultTableModel) Tabla1.getModel();

            // 4. Obtener el ID de producción (columna 0)
            int idProduccion = obtenerIdProduccion(model, modelRow);
            if (idProduccion <= 0) {
                return;
            }

            // 5. Determinar qué acción ejecutar según la columna clickeada
            switch (column) {
                case 5: // Columna "Ver Detalle"
                    mostrarDetalleProduccion(model, modelRow, idProduccion);
                    break;

                case 6: // Columna "Editar"
                    editarProduccion(model, modelRow, idProduccion);
                    break;
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al procesar clic: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        cargarTablaProduccion();
    }

// Método auxiliar para obtener el ID de producción con validación
    private int obtenerIdProduccion(DefaultTableModel model, int modelRow) {
        try {
            Object idObj = model.getValueAt(modelRow, 0);
            int id = Integer.parseInt(idObj.toString());
            if (id <= 0) {
                new Error_guardar(
                        (Frame) SwingUtilities.getWindowAncestor(this),
                        true,
                        "Error",
                        "ID de producción no válido"
                ).setVisible(true);
                return -1;
            }
            return id;
        } catch (NumberFormatException e) {
            new Error_guardar(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    true,
                    "Error",
                    "Formato de ID inválido"
            ).setVisible(true);
            return -1;
        }
    }

    private int obtenerIdProduccionDesdeBD(int idPedido) throws SQLException {
        String sql = "SELECT p.id_produccion "
                + "FROM produccion p "
                + "JOIN detalle_pedido dp ON p.detalle_pedido_iddetalle_pedido = dp.iddetalle_pedido "
                + "JOIN pedido ped ON dp.pedido_id_pedido = ped.id_pedido "
                + "WHERE ped.id_pedido = ?";

        try (Connection con = new Conexion().getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("id_produccion") : -1;
            }
        }
    }

    private int obtenerIdDetallePedido(int idProduccion) throws SQLException {
        String sql = "SELECT detalle_pedido_iddetalle_pedido FROM produccion WHERE id_produccion = ?";

        try (Connection con = new Conexion().getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProduccion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("detalle_pedido_iddetalle_pedido");
                } else {
                    throw new SQLException("No se encontró detalle de pedido para la producción: " + idProduccion);
                }
            }
        }
    }

// Método para mostrar el detalle de producción
    private void mostrarDetalleProduccion(DefaultTableModel model, int modelRow, int idProduccion) {
        try {
            if (idProduccion <= 0) {
                throw new IllegalArgumentException("ID de producción inválido: " + idProduccion);
            }

            String sql = "SELECT p.id_produccion, dp.descripcion, "
                    + "CONCAT(c.nombre, ' ', c.apellido) AS cliente, "
                    + "p.fecha_inicio, p.fecha_fin, p.estado, "
                    + "dp.cantidad, dp.dimension "
                    + "FROM produccion p "
                    + "JOIN detalle_pedido dp ON p.detalle_pedido_iddetalle_pedido = dp.iddetalle_pedido "
                    + "JOIN pedido ped ON dp.pedido_id_pedido = ped.id_pedido "
                    + "LEFT JOIN cliente c ON ped.cliente_codigo = c.codigo "
                    + "WHERE p.id_produccion = ?";

            try (Connection con = new Conexion().getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idProduccion);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                        String nombre = rs.getString("descripcion");
                        String cliente = rs.getString("cliente");
                        String fechaInicio = sdf.format(rs.getDate("fecha_inicio"));
                        String fechaFin = rs.getDate("fecha_fin") != null
                                ? sdf.format(rs.getDate("fecha_fin")) : "En proceso";
                        String estado = rs.getString("estado");
                        String cantidad = String.valueOf(rs.getInt("cantidad"));
                        String dimensiones = rs.getString("dimension");

                        DetalleProduProducto detallePanel = new DetalleProduProducto(
                                idProduccion, nombre, fechaInicio, fechaFin,
                                estado, cantidad, dimensiones, cliente
                        );

                        removeAll();
                        setLayout(new BorderLayout());
                        add(detallePanel, BorderLayout.CENTER);
                        revalidate();
                        repaint();
                    } else {
                        throw new SQLException("No se encontraron datos para la producción ID: " + idProduccion);
                    }
                }
            }
        } catch (Exception e) {
            new Error_guardar(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    true,
                    "Error",
                    "Error al mostrar detalle: " + e.getMessage()
            ).setVisible(true);
            e.printStackTrace();
        }
    }

    private String obtenerDimensionesDeBD(int idProduccion) {
        String sql = "SELECT dp.dimension FROM produccion p "
                + "JOIN detalle_pedido dp ON p.detalle_pedido_iddetalle_pedido = dp.iddetalle_pedido "
                + "WHERE p.id_produccion = ?";

        try (Connection con = new Conexion().getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idProduccion);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("dimension") : "";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
    }

// Método para editar producción
    private void editarProduccion(DefaultTableModel model, int modelRow, int idProduccion) {
        try {
            // 1. Obtener datos de la fila
            String nombre = obtenerValorCelda(model, modelRow, 1);
            String fechaInicio = obtenerValorCelda(model, modelRow, 2);
            String fechaFin = obtenerValorCelda(model, modelRow, 3, "En proceso");
            String estado = obtenerValorCelda(model, modelRow, 4);
            int cantidad = obtenerValorCeldaEntero(model, modelRow, 7);
            String dimensiones = obtenerValorCelda(model, modelRow, 8);

            // 2. Crear y configurar diálogo de edición
            EditProduccion dialog = new EditProduccion(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    true,
                    idProduccion
            );

            dialog.setDatos(
                    idProduccion,
                    nombre,
                    fechaInicio,
                    fechaFin,
                    estado,
                    cantidad,
                    dimensiones
            );

            // 3. Mostrar diálogo y recargar datos si hubo cambios
            dialog.setVisible(true);
            if (dialog.datosModificados()) {
                cargarTablaProduccion();
            }

        } catch (Exception e) {
            throw new RuntimeException("Error al editar producción: " + e.getMessage(), e);
        }
        cargarTablaProduccion();
    }

// Métodos auxiliares para obtener valores de celdas con valores por defecto
    private String obtenerValorCelda(DefaultTableModel model, int row, int col) {
        return obtenerValorCelda(model, row, col, "");
    }

    private String obtenerValorCelda(DefaultTableModel model, int row, int col, String valorPorDefecto) {
        Object value = model.getValueAt(row, col);
        return (value != null) ? value.toString() : valorPorDefecto;
    }

    private int obtenerValorCeldaEntero(DefaultTableModel model, int row, int col) {
        try {
            Object value = model.getValueAt(row, col);
            return (value != null) ? Integer.parseInt(value.toString()) : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }//GEN-LAST:event_Tabla1MouseClicked

    private void btnNotificacion1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnNotificacion1MouseClicked
        popupFiltros.show(btnNotificacion1, evt.getX(), evt.getY());
    }//GEN-LAST:event_btnNotificacion1MouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private RSMaterialComponent.RSTableMetroCustom Tabla1;
    private RSMaterialComponent.RSButtonShape btnElimi;
    private rojerusan.RSLabelIcon btnNotificacion1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane4;
    private RSMaterialComponent.RSTextFieldMaterialIcon txtbuscar;
    // End of variables declaration//GEN-END:variables

    private void filtrarTabla() {
        List<String> filtrosRangoFechas = new ArrayList<>();
        List<String> filtrosEstado = new ArrayList<>();

        if (chkTodosFechas.isSelected()) {
            filtrosRangoFechas.add("Todos");
        } else {
            if (chkUltimos3Dias.isSelected()) {
                filtrosRangoFechas.add("Últimos 3 días");
            }
            if (chkUltimos7Dias.isSelected()) {
                filtrosRangoFechas.add("Últimos 7 días");
            }
            if (chkUltimos15Dias.isSelected()) {
                filtrosRangoFechas.add("Últimos 15 días");
            }
            if (chkUltimoMes.isSelected()) {
                filtrosRangoFechas.add("Último mes");
            }
            if (chkUltimos3Meses.isSelected()) {
                filtrosRangoFechas.add("Últimos 3 meses");
            }
            if (chkUltimos6Meses.isSelected()) {
                filtrosRangoFechas.add("Últimos 6 meses");
            }
            if (chkUltimoAno.isSelected()) {
                filtrosRangoFechas.add("Último año");
            }
        }

        if (chkTodosEstados.isSelected()) {
            filtrosEstado.add("Todos");
        } else {
            if (chkPendiente.isSelected()) {
                filtrosEstado.add("Pendiente");
            }
            if (chkProceso.isSelected()) {
                filtrosEstado.add("Proceso");
            }
            if (chkFinalizado.isSelected()) {
                filtrosEstado.add("Finalizado");
            }
        }

        aplicarFiltros(filtrosRangoFechas, filtrosEstado);
    }

    public void cargarTablaProduccion() {
        DefaultTableModel model = (DefaultTableModel) Tabla1.getModel();
        model.setRowCount(0);

        try (Connection con = new Conexion().getConnection()) {
            String sql = "SELECT p.id_produccion, dp.descripcion, "
                    + "CONCAT(c.nombre, ' ', c.apellido) AS cliente, "
                    + "p.fecha_inicio, p.fecha_fin, p.estado, "
                    + "dp.cantidad, dp.dimension, ped.num_pedido "
                    + "FROM produccion p "
                    + "JOIN detalle_pedido dp ON p.detalle_pedido_iddetalle_pedido = dp.iddetalle_pedido "
                    + "JOIN pedido ped ON dp.pedido_id_pedido = ped.id_pedido "
                    + "LEFT JOIN cliente c ON ped.cliente_codigo = c.codigo "
                    + "ORDER BY p.estado ASC";

            try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("id_produccion"),
                        rs.getString("num_pedido"),
                        rs.getString("descripcion"),
                        rs.getString("cliente") != null ? rs.getString("cliente") : "Sin cliente",
                        sdf.format(rs.getDate("fecha_inicio")),
                        rs.getDate("fecha_fin") != null ? sdf.format(rs.getDate("fecha_fin")) : "En proceso",
                        rs.getInt("cantidad"),
                        rs.getString("estado"),
                        "ver",
                        rs.getString("dimension")
                    });
                }
            }
        } catch (SQLException e) {
            new Error_guardar(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    true,
                    "Error",
                    "Error al cargar datos: " + e.getMessage()
            ).setVisible(true);
            e.printStackTrace();
        }
    }

}
