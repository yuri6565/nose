package controlador;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Ctrl_Usuarios {

    private static final String REMITENTE = "carpinteriajoseabel2025@gmail.com";
    private static final String CLAVE_REMITENTE = "cmiujrxlppkdubyp"; // App password, no tu contraseña personal

    public static boolean enviarCodigoRecuperacion(String correo, String usuario, String codigo) {
        if (codigo != null) {
            try {
                Properties props = new Properties();
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "587");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.connectiontimeout", "10000"); // 10s
                props.put("mail.smtp.timeout", "10000"); // 10s
                props.put("mail.debug", "true"); // Habilita consola debug SMTP

                Session session = Session.getInstance(props, new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(REMITENTE, CLAVE_REMITENTE);
                    }
                });

                Message mensaje = new MimeMessage(session);
                mensaje.setFrom(new InternetAddress(REMITENTE));
                mensaje.setRecipient(Message.RecipientType.TO, new InternetAddress(correo));
                mensaje.setSubject("Código de Recuperación");
                mensaje.setText("Hola " + usuario + ",\n\n"
                        + "Recibimos una solicitud para restablecer tu contraseña.\n"
                        + "Tu código de recuperación es: " + codigo + "\n\n"
                        + "Este código es válido por 5 minutos.\n"
                        + "Si no solicitaste este cambio, ignora este mensaje.\n\n"
                        + "Atentamente,\n"
                        + "El equipo de Carpintería José Abel");

                Transport.send(mensaje);
                Logger.getLogger(Ctrl_Usuarios.class.getName()).log(Level.INFO, "Email enviado exitosamente a {0}", correo);
                return true;
            } catch (MessagingException ex) {
                Logger.getLogger(Ctrl_Usuarios.class.getName()).log(Level.SEVERE, "Error al enviar correo a " + correo, ex);
            }
        } else {
            Logger.getLogger(Ctrl_Usuarios.class.getName()).log(Level.WARNING, "No se recibió código para el usuario {0}, correo {1}", new Object[]{usuario, correo});
        }
        return false;
    }
}
