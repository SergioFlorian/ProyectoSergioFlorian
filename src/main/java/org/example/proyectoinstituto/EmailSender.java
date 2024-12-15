package org.example.proyectoinstituto;

import javax.mail.*;
import javax.mail.internet.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;

public class EmailSender {

    // Método para enviar correo usando Mailtrap
    public static void enviarCorreo(String destinatario, String correo, String contrasena, List<String> asignaturas) {
        // Configuración del servidor SMTP de Mailtrap
        String host = "smtp.mailtrap.io";
        final String usuario = "3d909122612e2a"; // Usar el username proporcionado por Mailtrap
        final String password = "25ab8cf2ce4c93"; // Usar el password proporcionado por Mailtrap

        // Establecer propiedades para la sesión
        Properties propiedades = new Properties();
        propiedades.put("mail.smtp.auth", "true");
        propiedades.put("mail.smtp.starttls.enable", "true");
        propiedades.put("mail.smtp.host", host);
        propiedades.put("mail.smtp.port", "587");

        // Crear la sesión con las propiedades y autenticación
        Session session = Session.getInstance(propiedades, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(usuario, password);
            }
        });

        try {
            // Crear el mensaje
            Message mensaje = new MimeMessage(session);
            mensaje.setFrom(new InternetAddress(usuario));
            mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            mensaje.setSubject("Bienvenido al Instituto");

            // Construir el contenido del correo
            StringBuilder contenido = new StringBuilder();
            contenido.append("Hola, \n\n");
            contenido.append("Te damos la bienvenida al Instituto. Aquí tienes los detalles de tu registro: \n\n");
            contenido.append("Correo: " + correo + "\n");
            contenido.append("Contraseña: " + contrasena + "\n");
            contenido.append("Asignaturas matriculadas: \n");
            for (String asignatura : asignaturas) {
                contenido.append("- " + asignatura + "\n");
            }

            // Establecer el contenido del correo
            mensaje.setText(contenido.toString());

            // Enviar el correo
            Transport.send(mensaje);
            System.out.println("Correo enviado correctamente.");
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Error al enviar el correo.");
        }
    }

    public static void enviarFalta(String destinatario, String nombreAlumno, String nombreProfesor, String nombreAsignatura, String comentario, LocalDate fecha_falta) {
        // Configuración del servidor SMTP de Mailtrap
        String host = "smtp.mailtrap.io";
        final String usuario = "3d909122612e2a"; // Usar el username proporcionado por Mailtrap
        final String password = "25ab8cf2ce4c93"; // Usar el password proporcionado por Mailtrap

        // Establecer propiedades para la sesión
        Properties propiedades = new Properties();
        propiedades.put("mail.smtp.auth", "true");
        propiedades.put("mail.smtp.starttls.enable", "true");
        propiedades.put("mail.smtp.host", host);
        propiedades.put("mail.smtp.port", "587");

        // Crear la sesión con las propiedades y autenticación
        Session session = Session.getInstance(propiedades, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(usuario, password);
            }
        });

        try {
            // Crear el mensaje
            Message mensaje = new MimeMessage(session);
            mensaje.setFrom(new InternetAddress(usuario));
            mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            mensaje.setSubject("Notificación de Falta");

            // Construir el contenido del correo
            StringBuilder contenido = new StringBuilder();
            contenido.append("Hola ").append(nombreAlumno).append(",\n\n");
            contenido.append("Se te ha registrado una falta en la asignatura ").append(nombreAsignatura).append(" impartida por el profesor ").append(nombreProfesor).append("del dia ").append(fecha_falta).append(".\n");
            contenido.append("Comentario del profesor: ").append(comentario).append("\n\n");
            contenido.append("Por favor, ponte en contacto con tu profesor si tienes alguna duda.\n\n");
            contenido.append("Saludos,\n");
            contenido.append("Instituto");

            // Establecer el contenido del correo
            mensaje.setText(contenido.toString());

            // Enviar el correo
            Transport.send(mensaje);
            System.out.println("Correo de falta enviado correctamente.");
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Error al enviar el correo de falta.");
        }
    }

    public static void enviarCorreoPersonal(String destinatario, String correoAlumno, String nombreProfesor, String comentario) {
        // Configuración del servidor SMTP de Mailtrap
        String host = "smtp.mailtrap.io";
        final String usuario = "3d909122612e2a"; // Usar el username proporcionado por Mailtrap
        final String password = "25ab8cf2ce4c93"; // Usar el password proporcionado por Mailtrap

        // Establecer propiedades para la sesión
        Properties propiedades = new Properties();
        propiedades.put("mail.smtp.auth", "true");
        propiedades.put("mail.smtp.starttls.enable", "true");
        propiedades.put("mail.smtp.host", host);
        propiedades.put("mail.smtp.port", "587");

        // Crear la sesión con las propiedades y autenticación
        Session session = Session.getInstance(propiedades, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(usuario, password);
            }
        });

        try {
            // Crear el mensaje
            Message mensaje = new MimeMessage(session);
            mensaje.setFrom(new InternetAddress(usuario));
            mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            mensaje.setSubject("Correo Del Profesor");

            // Construir el contenido del correo
            StringBuilder contenido = new StringBuilder();
            contenido.append("Hola ").append(correoAlumno).append(",\n\n");
            contenido.append("Este es un correo enviado desde el programa por parte del profesor ").append(nombreProfesor).append(".\n");
            contenido.append("Comentario del profesor: ").append(comentario).append("\n\n");
            contenido.append("Por favor, ponte en contacto con tu profesor si tienes alguna duda.\n\n");
            contenido.append("Saludos,\n");
            contenido.append("Instituto");

            // Establecer el contenido del correo
            mensaje.setText(contenido.toString());

            // Enviar el correo
            Transport.send(mensaje);
            System.out.println("Correo personal enviado correctamente.");
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Error al enviar el correo personal.");
        }
    }

    public static void enviarCorreoPersonalAlumno(String destinatario, String correoAlumno, String comentario) {
        // Configuración del servidor SMTP de Mailtrap
        String host = "smtp.mailtrap.io";
        final String usuario = "3d909122612e2a"; // Usar el username proporcionado por Mailtrap
        final String password = "25ab8cf2ce4c93"; // Usar el password proporcionado por Mailtrap

        // Establecer propiedades para la sesión
        Properties propiedades = new Properties();
        propiedades.put("mail.smtp.auth", "true");
        propiedades.put("mail.smtp.starttls.enable", "true");
        propiedades.put("mail.smtp.host", host);
        propiedades.put("mail.smtp.port", "587");

        // Crear la sesión con las propiedades y autenticación
        Session session = Session.getInstance(propiedades, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(usuario, password);
            }
        });

        try {
            // Crear el mensaje
            Message mensaje = new MimeMessage(session);
            mensaje.setFrom(new InternetAddress(usuario));
            mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            mensaje.setSubject("Correo Del Alumno");

            // Construir el contenido del correo
            StringBuilder contenido = new StringBuilder();
            contenido.append("Hola ").append(destinatario).append(",\n\n");
            contenido.append("Este es un correo enviado desde el programa por parte del alumno ").append(correoAlumno).append(".\n");
            contenido.append("Comentario del Alumno: ").append(comentario).append("\n\n");
            contenido.append("Saludos,\n");

            // Establecer el contenido del correo
            mensaje.setText(contenido.toString());

            // Enviar el correo
            Transport.send(mensaje);
            System.out.println("Correo personal enviado correctamente.");
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Error al enviar el correo personal.");
        }
    }
}
