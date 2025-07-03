package controlador;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GeneradorCotizacionPDF {
    public void generarPDF(String cliente, DefaultTableModel tablaModel, String total, String archivoSalida) {
        System.out.println("Iniciando generación de PDF...");
        try (PDDocument document = new PDDocument()) {
            System.out.println("Documento PDF creado");
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            System.out.println("Página creada");

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
                System.out.println("ContentStream creado");

                // Agregar imagen fija en la parte superior derecha
                try {
                    // Obtener la ruta relativa desde los recursos
                    URL imageUrl = getClass().getResource("/logo_azul.png");
                    if (imageUrl == null) {
                        throw new IOException("No se encontró la imagen 'logo_azul.png' en los recursos.");
                    }

                    // Crear PDImageXObject desde el archivo en los recursos
                    PDImageXObject pdImage = PDImageXObject.createFromFile(imageUrl.getPath(), document);
                    // Escalar la imagen a 100x100 puntos (ajusta según necesites)
                    float imageWidth = 100;
                    float imageHeight = 100;
                    // Posición en la esquina superior derecha (A4: 595 puntos de ancho, 842 de alto)
                    float xPosition = 595 - imageWidth - 30; // 30 puntos de margen desde el borde derecho
                    float yPositionImage = 842 - imageHeight - 30; // 30 puntos de margen desde el borde superior
                    contentStream.drawImage(pdImage, xPosition, yPositionImage, imageWidth, imageHeight);
                    System.out.println("Imagen añadida en la posición: (" + xPosition + ", " + yPositionImage + ")");
                } catch (IOException e) {
                    System.out.println("Error al cargar la imagen: " + e.getMessage());
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error al cargar la imagen: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    return; // Salir del método si falla la carga de la imagen
                }

                // Título "Cotización"
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("Cotización");
                contentStream.endText();

                // Fecha
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 730);
                contentStream.showText("Fecha: " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
                contentStream.endText();

                // Datos del cliente
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 700);
                contentStream.showText("Cliente: " + cliente);
                contentStream.endText();

                // Tabla de productos
                float yPosition = 650;
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText("Producto");
                contentStream.newLineAtOffset(150, 0);
                contentStream.showText("Unidad");
                contentStream.newLineAtOffset(80, 0);
                contentStream.showText("Cantidad");
                contentStream.newLineAtOffset(80, 0);
                contentStream.showText("Valor Unitario");
                contentStream.newLineAtOffset(120, 0);
                contentStream.showText("Subtotal");
                contentStream.endText();

                // Dibujar línea horizontal debajo del encabezado
                yPosition -= 5;
                contentStream.setLineWidth(0.5f);
                contentStream.moveTo(50, yPosition);
                contentStream.lineTo(550, yPosition); // Cambiado de 500 a 550
                contentStream.stroke();

                yPosition -= 15;
                contentStream.setFont(PDType1Font.HELVETICA, 12);

                System.out.println("Procesando tabla con " + tablaModel.getRowCount() + " filas");
                for (int i = 0; i < tablaModel.getRowCount(); i++) {
                    yPosition -= 20;
                    if (yPosition < 50) {
                        contentStream.endText();
                        contentStream.close();
                        page = new PDPage(PDRectangle.A4);
                        document.addPage(page);
                        try (PDPageContentStream newContentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
                            System.out.println("Nueva página creada");
                            newContentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                            newContentStream.beginText();
                            newContentStream.newLineAtOffset(50, 750);
                            newContentStream.showText("Producto");
                            newContentStream.newLineAtOffset(150, 0);
                            newContentStream.showText("Unidad");
                            newContentStream.newLineAtOffset(80, 0);
                            newContentStream.showText("Cantidad");
                            newContentStream.newLineAtOffset(80, 0);
                            newContentStream.showText("Valor Unitario");
                            newContentStream.newLineAtOffset(120, 0);
                            newContentStream.showText("Subtotal");
                            newContentStream.endText();

                            // Dibujar línea horizontal debajo del encabezado en nueva página
                            yPosition = 745;
                            newContentStream.setLineWidth(0.5f);
                            newContentStream.moveTo(50, yPosition);
                            newContentStream.lineTo(550, yPosition); // Cambiado de 500 a 550
                            newContentStream.stroke();

                            yPosition -= 15;
                            newContentStream.setFont(PDType1Font.HELVETICA, 12);
                            newContentStream.beginText();
                            newContentStream.newLineAtOffset(50, yPosition);
                            newContentStream.showText(tablaModel.getValueAt(i, 0).toString()); // Producto
                            newContentStream.newLineAtOffset(150, 0);
                            newContentStream.showText(tablaModel.getValueAt(i, 1).toString()); // Unidad
                            newContentStream.newLineAtOffset(80, 0);
                            newContentStream.showText(tablaModel.getValueAt(i, 2).toString()); // Cantidad
                            newContentStream.newLineAtOffset(80, 0);
                            newContentStream.showText(tablaModel.getValueAt(i, 3).toString()); // Valor Unitario
                            newContentStream.newLineAtOffset(120, 0);
                            newContentStream.showText(tablaModel.getValueAt(i, 4).toString()); // Subtotal
                            newContentStream.endText();

                            // Dibujar línea horizontal debajo de la fila
                            yPosition -= 5;
                            newContentStream.moveTo(50, yPosition);
                            newContentStream.lineTo(550, yPosition); // Cambiado de 500 a 550
                            newContentStream.stroke();
                        }
                    } else {
                        contentStream.beginText();
                        contentStream.newLineAtOffset(50, yPosition);
                        contentStream.showText(tablaModel.getValueAt(i, 0).toString()); // Producto
                        contentStream.newLineAtOffset(150, 0);
                        contentStream.showText(tablaModel.getValueAt(i, 1).toString()); // Unidad
                        contentStream.newLineAtOffset(80, 0);
                        contentStream.showText(tablaModel.getValueAt(i, 2).toString()); // Cantidad
                        contentStream.newLineAtOffset(80, 0);
                        contentStream.showText(tablaModel.getValueAt(i, 3).toString()); // Valor Unitario
                        contentStream.newLineAtOffset(120, 0);
                        contentStream.showText(tablaModel.getValueAt(i, 4).toString()); // Subtotal
                        contentStream.endText();

                        // Dibujar línea horizontal debajo de la fila
                        yPosition -= 5;
                        contentStream.moveTo(50, yPosition);
                        contentStream.lineTo(550, yPosition); // Cambiado de 500 a 550
                        contentStream.stroke();
                        yPosition -= 15;
                    }
                }

                // Total
                yPosition -= 40;
                contentStream.beginText();
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText("Total: " + total);
                contentStream.endText();

                // Dibujar línea horizontal debajo de Total
                yPosition -= 5;
                contentStream.moveTo(50, yPosition);
                contentStream.lineTo(180, yPosition); // Cambiado de 500 a 550
                contentStream.stroke();

                // Mensaje
                yPosition -= 40;
                contentStream.beginText();
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText("¡Gracias por su cotización!");
                contentStream.endText();

                // Dibujar línea horizontal debajo del mensaje
               
            }

            System.out.println("Guardando documento en: " + archivoSalida);
            document.save(archivoSalida);
            System.out.println("Documento guardado");
            JOptionPane.showMessageDialog(null, "PDF generado en: " + archivoSalida);
        } catch (IOException e) {
            System.out.println("Error al generar PDF: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al generar PDF: " + e.getMessage());
        }
    }
}