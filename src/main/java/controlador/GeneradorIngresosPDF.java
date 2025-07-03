package controlador;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.Desktop;

public class GeneradorIngresosPDF {

    // Configuración de diseño
    private static final float MARGIN_LEFT = 50;
    private static final float MARGIN_TOP = 70;
    private static final float LOGO_WIDTH = 120;
    private static final float LOGO_HEIGHT = 80;
    private static final float ROW_HEIGHT = 20;
    private static final float FOOTER_Y = 50;
    private static final float PAGE_WIDTH = PDRectangle.LETTER.getWidth();

    // Formatos
    private final DecimalFormat decimalFormat = new DecimalFormat("$#,##0.00");
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private String montoTotal; // Variable de clase agregada
    private String pagado;
    private String debido;

    public void generarPDF(String nombreCliente, int codigoCliente, String telefonoCliente,
            String direccionCliente, String departamentoCliente, String municipioCliente, DefaultTableModel tablaModel, String montoTotal,
            String archivoSalida, String fechaPedido, String numPedido, String pagado, String debido) {
        this.montoTotal = montoTotal; // Asignar el valor a la variable de clase
        this.pagado = pagado;
        this.debido = debido;

        try (PDDocument document = new PDDocument()) {
            // Configuración inicial
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            try {
                float yPosition = setupPage(document, contentStream, page, nombreCliente, codigoCliente, telefonoCliente,
                        direccionCliente, departamentoCliente, municipioCliente, fechaPedido, numPedido);
                yPosition = addTable(contentStream, document, page, tablaModel, yPosition);
                addFooter(contentStream, yPosition);
            } finally {
                if (contentStream != null) {
                    contentStream.close();
                }
            }

            // Guardar y abrir
            File file = new File(archivoSalida);
            document.save(file);
            abrirDocumento(file);

        } catch (Exception e) {
            mostrarError("Error al generar PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private float setupPage(PDDocument document, PDPageContentStream contentStream,
            PDPage page, String nombreCliente, int codigoCliente, String telefonoCliente,
            String direccionCliente, String departamentoCliente, String municipioCliente, String fechaPedido, String numPedido) throws IOException {
        float yPosition = agregarLogoYEncabezado(document, contentStream);
        yPosition = agregarInformacionCliente(contentStream, yPosition, nombreCliente, codigoCliente, telefonoCliente,
                direccionCliente, departamentoCliente, municipioCliente, fechaPedido, numPedido);
        return yPosition;
    }

    private void agregarLogo(PDDocument document, PDPageContentStream contentStream) throws IOException {
        try {
            URL imageUrl = getClass().getResource("/imagenes/logo_azul_sin_letras.png");
            if (imageUrl != null) {
                PDImageXObject pdImage = PDImageXObject.createFromFile(imageUrl.getPath(), document);
                float logoX = MARGIN_LEFT; // A la izquierda
                float logoY = PDRectangle.A4.getHeight() - MARGIN_TOP - LOGO_HEIGHT;
                contentStream.drawImage(pdImage, logoX, logoY, LOGO_WIDTH, LOGO_HEIGHT);
            }
        } catch (IOException e) {
            System.err.println("No se pudo cargar el logo: " + e.getMessage());
        }
    }

    private float agregarTitulo(PDPageContentStream contentStream, float yPosition) throws IOException {
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
        escribirTextoCentrado(contentStream, "COMPROBANTE DE INGRESO", yPosition);
        return yPosition - 40;
    }

    private float agregarInformacionCliente(PDPageContentStream contentStream, float yPosition,
            String nombreCliente, int codigoCliente, String telefonoCliente, String direccionCliente, String departamentoCliente, String municipioCliente, String fechaPedido, String numPedido) throws IOException {

        float anchoPagina = PDRectangle.A4.getWidth();
        float margenDerecho = 50;

        // Pedido N° y Fecha Emisión alineados a la derecha
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);

        String textoPedido = "Pedido N°: " + safeString(numPedido);
        float anchoTextoPedido = PDType1Font.HELVETICA_BOLD.getStringWidth(textoPedido) / 1000 * 12;
        float xPedido = anchoPagina - margenDerecho - anchoTextoPedido;
        escribirTexto(contentStream, textoPedido, xPedido, yPosition);
        yPosition -= 20;

        String textoFechaEmision = "Fecha de Emisión: " + dateFormat.format(new Date());
        float anchoTextoFecha = PDType1Font.HELVETICA_BOLD.getStringWidth(textoFechaEmision) / 1000 * 12;
        float xFecha = anchoPagina - margenDerecho - anchoTextoFecha;
        escribirTexto(contentStream, textoFechaEmision, xFecha, yPosition);
        yPosition -= 20;

        // Información del cliente y pedido
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 11);
        escribirTexto(contentStream, "Información del cliente", MARGIN_LEFT, yPosition);
        yPosition -= 20;

        contentStream.setFont(PDType1Font.HELVETICA, 10);
        escribirTexto(contentStream, "Cliente: " + safeString(nombreCliente), MARGIN_LEFT, yPosition);
        yPosition -= 15;

        escribirTexto(contentStream, "ID: " + safeString(codigoCliente),
                MARGIN_LEFT, yPosition);
        yPosition -= 15;

        escribirTexto(contentStream, "Teléfono: " + safeString(telefonoCliente),
                MARGIN_LEFT, yPosition);
        yPosition -= 15;

        // Línea 3: Dirección completa (puede ocupar varias líneas si es muy larga)
        String direccionCompleta = "Dirección: " + safeString(departamentoCliente) + "-" + safeString(municipioCliente) + " " + safeString(direccionCliente);
        if (direccionCompleta.length() > 60) { // Si es muy larga, partimos en dos líneas
            String parte1 = direccionCompleta.substring(0, 60);
            String parte2 = direccionCompleta.substring(60);

            escribirTexto(contentStream, parte1, MARGIN_LEFT, yPosition);
            yPosition -= 15;
            escribirTexto(contentStream, parte2, MARGIN_LEFT, yPosition);
        } else {
            escribirTexto(contentStream, direccionCompleta, MARGIN_LEFT, yPosition);
        }

        return yPosition - 20;
    }

    private float addTable(PDPageContentStream contentStream, PDDocument document,
            PDPage page, DefaultTableModel tablaModel, float yPosition) throws IOException {
        // Encabezados de tabla
        String[] headers = {"Descripción", "Cantidad", "Dimensiones", "Precio Unitario", "Subtotal"};
        float[] columnWidths = {120, 60, 130, 90, 80};

        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
        yPosition = dibujarEncabezadosTabla(contentStream, headers, columnWidths, yPosition);

        // Datos de la tabla
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        int totalRows = tablaModel.getRowCount() - 1; // Excluye fila de totales

        for (int row = 0; row < totalRows; row++) {
            if (yPosition < FOOTER_Y + 50) {
                contentStream.close();
                page = nuevaPagina(document);
                contentStream = new PDPageContentStream(document, page);
                yPosition = PDRectangle.A4.getHeight() - MARGIN_TOP;
                yPosition = dibujarEncabezadosTabla(contentStream, headers, columnWidths, yPosition);
            }

            yPosition = dibujarFila(contentStream, tablaModel, row, columnWidths, yPosition);
        }

        return yPosition;
    }

    private PDPage nuevaPagina(PDDocument document) {
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);
        return page;
    }

    private float dibujarEncabezadosTabla(PDPageContentStream contentStream,
            String[] headers, float[] widths, float y) throws IOException {
        float x = MARGIN_LEFT;
        float desplazamientoTexto = 6; // Baja el texto dentro de la celda
        for (int i = 0; i < headers.length; i++) {
            escribirTexto(contentStream, headers[i], x, y - desplazamientoTexto);
            x += widths[i];
        }
        y -= 15;
        dibujarLinea(contentStream, MARGIN_LEFT, y, calcularAnchoTotal(widths));
        return y - 10;
    }

    private float dibujarFila(PDPageContentStream contentStream, DefaultTableModel model,
            int row, float[] widths, float y) throws IOException {
        float x = MARGIN_LEFT;
        for (int col = 0; col < widths.length; col++) {
            Object value = model.getValueAt(row, col);
            String text = formatearValor(value, col >= 4); // Formatea números para columnas 4+
            escribirTextoTabla(contentStream, text, x, y);
            x += widths[col];
        }
        y -= 15;
        dibujarLinea(contentStream, MARGIN_LEFT, y, calcularAnchoTotal(widths), 0.5f);
        return y - 5;
    }

    private void addFooter(PDPageContentStream contentStream, float yPosition) throws IOException {
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);

        float columnaDerechaX = PAGE_WIDTH - MARGIN_LEFT - 170; // Ajusta 150 según el espacio deseado

        escribirTexto(contentStream, "Total General:", columnaDerechaX, yPosition - 20);
        escribirTexto(contentStream, safeString(montoTotal), columnaDerechaX + 70, yPosition - 20);

        escribirTexto(contentStream, "Pagado:", columnaDerechaX, yPosition - 35);
        escribirTexto(contentStream, safeString(pagado), columnaDerechaX + 70, yPosition - 35);

        escribirTexto(contentStream, "Debido:", columnaDerechaX, yPosition - 50);
        escribirTexto(contentStream, safeString(debido), columnaDerechaX + 70, yPosition - 50);

        // Espacio extra antes de las firmas
        yPosition -= 120;

        dibujarLinea(contentStream, MARGIN_LEFT, yPosition, 200);
        dibujarLinea(contentStream, MARGIN_LEFT + 300, yPosition, 200);

        escribirTexto(contentStream, "Firma del Cliente", MARGIN_LEFT + 50, yPosition - 15);
        escribirTexto(contentStream, "Firma del Responsable", MARGIN_LEFT + 350, yPosition - 15);

        // Pie de página
        contentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, 9);
        escribirTextoCentrado(contentStream, "Gracias por su preferencia - "
                + new SimpleDateFormat("yyyy").format(new Date()), FOOTER_Y);
    }

    // Métodos auxiliares
    private String safeString(Object value, String defaultValue) {
        return value != null ? value.toString() : defaultValue;
    }

    private String safeString(Object value) {
        return safeString(value, "");
    }

    private String formatearValor(Object value, boolean esMoneda) {
        if (value == null) {
            return "";
        }
        String text = value.toString();
        if (esMoneda) {
            try {
                double num = Double.parseDouble(text.replaceAll("[^\\d.]", ""));
                return decimalFormat.format(num);
            } catch (NumberFormatException e) {
                return "$0.00";
            }
        }
        return text;
    }

    private float calcularAnchoTotal(float[] widths) {
        float total = 0;
        for (float w : widths) {
            total += w;
        }
        return total;
    }

    private void escribirTexto(PDPageContentStream stream, String text, float x, float y) throws IOException {
        stream.beginText();
        stream.newLineAtOffset(x, y);
        stream.showText(text);
        stream.endText();
    }

    private void escribirTextoCentrado(PDPageContentStream stream, String text, float y) throws IOException {
        float textWidth = PDType1Font.HELVETICA.getStringWidth(text) / 1000 * 10;
        float x = (PDRectangle.A4.getWidth() - textWidth) / 2;
        escribirTexto(stream, text, x, y);
    }

    private void dibujarLinea(PDPageContentStream stream, float x, float y, float length) throws IOException {
        dibujarLinea(stream, x, y, length, 1f);
    }

    private void dibujarLinea(PDPageContentStream stream, float x, float y, float length, float width) throws IOException {
        stream.setLineWidth(width);
        stream.moveTo(x, y);
        stream.lineTo(x + length, y);
        stream.stroke();
    }

    private void abrirDocumento(File file) throws IOException {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(file);
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void escribirTextoTabla(PDPageContentStream stream, String text, float x, float y) throws IOException {
        float textoDesplazamiento = 10;
        stream.beginText();
        stream.newLineAtOffset(x, y - textoDesplazamiento);
        stream.showText(text);
        stream.endText();
    }

    private float agregarLogoYEncabezado(PDDocument document, PDPageContentStream contentStream) throws IOException {
        float yPosition = PDRectangle.A4.getHeight() - 40;

        try {
            URL imageUrl = getClass().getResource("/imagenes/logo_azul_sin_letras.png");
            if (imageUrl != null) {
                PDImageXObject pdImage = PDImageXObject.createFromFile(imageUrl.getPath(), document);

                float logoX = MARGIN_LEFT;
                float logoY = yPosition - LOGO_HEIGHT;

                contentStream.drawImage(pdImage, logoX, logoY, LOGO_WIDTH, LOGO_HEIGHT);

                // Mover el texto más a la derecha (original + 30)
                float textoX = logoX + LOGO_WIDTH + 50;
                float textoY = logoY + LOGO_HEIGHT - 20;

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                escribirTexto(contentStream, "Carpintería y Muebles JoseAbel", textoX, textoY);

                contentStream.setFont(PDType1Font.HELVETICA, 11);
                escribirTexto(contentStream, "Duitama - Boyacá", textoX, textoY - 15);
                escribirTexto(contentStream, "Calle 26 #17-46", textoX, textoY - 30);
                escribirTexto(contentStream, "Cel: 314 7352468", textoX, textoY - 45);

                // Línea de separación debajo del logo y el texto en color gris
                float lineaY = logoY - 10;
                contentStream.setStrokingColor(183, 183, 183); // Gris claro RGB
                dibujarLinea(contentStream, MARGIN_LEFT, lineaY, PDRectangle.A4.getWidth() - 2 * MARGIN_LEFT);

                // Restaurar color a negro por si se dibujan más líneas luego
                contentStream.setStrokingColor(0, 0, 0);

                return lineaY - 20; // Devuelves nueva posición Y para continuar
            }
        } catch (IOException e) {
            System.err.println("No se pudo cargar el logo: " + e.getMessage());
        }

        return yPosition - LOGO_HEIGHT - 20;
    }

}
