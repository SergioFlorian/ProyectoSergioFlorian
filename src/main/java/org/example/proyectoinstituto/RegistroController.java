package org.example.proyectoinstituto;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.SecureRandom;
import java.sql.*;
import java.util.List;

public class RegistroController {

    @FXML
    private TextField nombreField;

    @FXML
    private TextField apellidosField;



    private List<String> asignaturasSeleccionadas;




    // Método para abrir la ventana de selección de asignaturas
    public void abrirVentanaAsignaturas() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("asignaturas.fxml"));
            Parent root = loader.load();

            AsignaturasController asignaturasController = loader.getController();
            asignaturasController.setOnAsignaturasSeleccionadas(this::procesarAsignaturasSeleccionadas);

            Stage stage = new Stage();
            stage.setTitle("Seleccionar Asignaturas");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            stage.setResizable(false);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método que recibe las asignaturas seleccionadas
    private void procesarAsignaturasSeleccionadas(List<String> asignaturas) {
        this.asignaturasSeleccionadas = asignaturas;
    }

    // Método para generar el correo del alumno
    private String generarCorreo(String nombre, String apellidos) {
        return nombre + apellidos + "@institutoproyecto.com";
    }

    // Método para generar una contraseña aleatoria
    private String generarPassword(int longitud) {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder contrasena = new StringBuilder(longitud);

        for (int i = 0; i < longitud; i++) {
            int indice = random.nextInt(caracteres.length());
            contrasena.append(caracteres.charAt(indice));
        }

        return contrasena.toString();
    }

    @FXML
    private void solicitarRegistro() {
        String nombre = nombreField.getText();
        String apellidos = apellidosField.getText();
        String correo = generarCorreo(nombre, apellidos);

        // Generar una contraseña de 8 caracteres
        String password = generarPassword(8);

        if (nombre.isEmpty() || apellidos.isEmpty()) {
            System.out.println("Por favor completa todos los campos.");
            return;
        }

        if (asignaturasSeleccionadas == null || asignaturasSeleccionadas.isEmpty()) {
            System.out.println("Por favor selecciona al menos una asignatura.");
            return;
        }

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/db/instituto.db")) {
            connection.setAutoCommit(false);

            // Insertar el alumno
            String insertAlumnoSQL = "INSERT INTO Alumno (nombre, apellidos, email, password) VALUES (?, ?, ?, ?)";
            try (PreparedStatement alumnoStmt = connection.prepareStatement(insertAlumnoSQL, Statement.RETURN_GENERATED_KEYS)) {
                alumnoStmt.setString(1, nombre);
                alumnoStmt.setString(2, apellidos);
                alumnoStmt.setString(3, correo);
                alumnoStmt.setString(4, password);
                alumnoStmt.executeUpdate();

                // Obtener el ID del alumno recién insertado
                ResultSet generatedKeys = alumnoStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int idAlumno = generatedKeys.getInt(1);

                    // Insertar las matrículas
                    String insertMatriculaSQL = "INSERT INTO Matriculas (id_alumno, id_asignatura) VALUES (?, ?)";
                    try (PreparedStatement matriculaStmt = connection.prepareStatement(insertMatriculaSQL)) {
                        for (String asignatura : asignaturasSeleccionadas) {
                            int idAsignatura = obtenerIdAsignatura(asignatura, connection);
                            if (idAsignatura > 0) {
                                matriculaStmt.setInt(1, idAlumno);
                                matriculaStmt.setInt(2, idAsignatura);
                                matriculaStmt.addBatch();
                            }
                        }
                        matriculaStmt.executeBatch();
                    }

                    // Enviar el correo al usuario con su correo, contraseña y asignaturas
                    EmailSender.enviarCorreo(correo, correo, password, asignaturasSeleccionadas);

                    // Alerta de éxito
                    mostrarAlerta(true);

                    // Cerrar la ventana de registro y abrir el login
                    cerrarVentana();
                    abrirLogin();
                }
            }

            connection.commit();
            System.out.println("Alumno y matrículas registrados exitosamente.");
        } catch (SQLException e) {
            e.printStackTrace();
            // Alerta de error en caso de fallo en el registro
            mostrarAlerta(false);
        }
    }

    // Método para mostrar la alerta
    private void mostrarAlerta(boolean exito) {
        Alert alert = new Alert(exito ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle(exito ? "Registro Exitoso" : "Error en el Registro");
        alert.setHeaderText(exito ? "¡Registro Exitoso!" : "Error en el Registro");
        alert.setContentText(exito ? "Revisa tu correo para más detalles." : "Hubo un error al registrar tus datos.");

        // Si el registro fue exitoso, el usuario puede aceptar y cerrar la ventana
        if (exito) {
            alert.getButtonTypes().setAll(ButtonType.OK);
            alert.showAndWait();
        } else {
            alert.getButtonTypes().setAll(ButtonType.OK);
            alert.showAndWait();
        }
    }

    // Método para cerrar la ventana de registro
    private void cerrarVentana() {
        Stage stage = (Stage) nombreField.getScene().getWindow();
        stage.close();
        
    }

    // Método para abrir la ventana de login
    public static void abrirLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(RegistroController.class.getResource("login.fxml"));
            Parent root = loader.load();
            Stage loginStage = new Stage();
            loginStage.setTitle("Login");
            loginStage.setScene(new Scene(root));
            loginStage.show();
            loginStage.setResizable(false);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private int obtenerIdAsignatura(String nombreAsignatura, Connection connection) throws SQLException {
        String query = "SELECT id_asignatura FROM Asignaturas WHERE nombre_asignatura = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nombreAsignatura);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_asignatura");
            }
        }
        return -1; // Devuelve -1 si la asignatura no existe
    }

    @FXML
    private void volver() {
        cerrarVentana();
        abrirLogin();
    }
}
