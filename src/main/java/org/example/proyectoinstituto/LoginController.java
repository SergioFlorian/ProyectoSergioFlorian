package org.example.proyectoinstituto;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.proyectoinstituto.dto.Alumno;
import org.example.proyectoinstituto.dto.Profesor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField email;

    @FXML
    private TextField password;

    @FXML
    private ChoiceBox<String> picker;

    @FXML
    public void initialize() {
        // Agregar valores al ChoiceBox al inicializar
        picker.getItems().addAll("Alumno", "Profesor");
        picker.setValue("Elige"); // Valor predeterminado
    }

    @FXML
    protected void onHelloButtonClick(ActionEvent event) {
        // Lógica al hacer clic en el botón
        String emailValue = email.getText();
        String passwordValue = password.getText();
        String selectedRole = picker.getValue();

        if (selectedRole.equals("Alumno")) {
            // Consulta en la base de datos si el estudiante existe
            verificarUsuario("Alumno", emailValue, passwordValue, event);
        }
        else if (selectedRole.equals("Profesor")) {
            // Consulta en la base de datos si el profesor existe
            verificarUsuario("Profesor", emailValue, passwordValue, event);
        } else {
            System.out.println("Por favor, selecciona un rol.");
        }
    }

    @FXML
    protected void registrarse(ActionEvent event) {
        try {
            // Cargar el archivo FXML del nuevo frame
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("registro.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            // Crear un nuevo Stage
            Stage stage = new Stage();
            stage.setTitle("Nuevo Frame");
            stage.setScene(scene);
            stage.show();
            stage.setResizable(false);

            // Cerrar la ventana actual
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void verificarUsuario(String role, String email, String password, ActionEvent event) {
        String query = "SELECT * FROM " + role + " WHERE email = ? AND password = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Inicio de sesión exitoso
                showAlert("Correcto", "Inicio de sesión correcto", "Bienvenido");

                if (role.equals("Profesor")) {
                    // Recuperar datos del profesor
                    Profesor profesor = new Profesor(
                            rs.getInt("id_profesor"),
                            rs.getString("nombre"),
                            rs.getString("apellidos"),
                            rs.getString("email"),
                            rs.getString("password")
                    );
                    abrirInicioProfesor(profesor, event);
                } else if (role.equals("Alumno")) {
                    // Recuperar datos del alumno
                    Alumno alumno = new Alumno(
                            rs.getInt("id_alumno"),
                            rs.getString("nombre"),
                            rs.getString("apellidos"),
                            rs.getString("email"),
                            rs.getString("password")
                    );
                    abrirInicioAlumno(alumno, event);
                }
            } else {
                // Usuario no encontrado
                showAlert("Error", "Credenciales incorrectas", "El email o contraseña son incorrectos.");
                System.out.println("Error: Usuario o contraseña incorrectos.");
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar el usuario: " + e.getMessage());
        }
    }


    private void abrirInicioProfesor(Profesor profesor, ActionEvent event) {
        try {
            // Cargar el archivo FXML del panel de inicio del profesor
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("InicioProfesor.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            // Obtener el controlador del panel de inicio del profesor
            InicioProfesor controller = fxmlLoader.getController();
            controller.initialize(profesor); // Pasar el objeto Profesor al controlador de InicioProfesor

            // Crear un nuevo Stage
            Stage stage = new Stage();
            stage.setTitle("Panel de Profesor");
            stage.setScene(scene);
            stage.show();
            stage.setResizable(false);


            // Cerrar la ventana actual
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void abrirInicioAlumno(Alumno alumno, ActionEvent event) {
        try {
            // Cargar el archivo FXML del panel de inicio del profesor
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("InicioAlumno.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            // Obtener el controlador del panel de inicio del profesor
            InicioAlumno controller = fxmlLoader.getController();
            controller.initialize(alumno); // Pasar el objeto alumno al controlador de InicioAlumno

            // Crear un nuevo Stage
            Stage stage = new Stage();
            stage.setTitle("Panel de Alumno");
            stage.setScene(scene);
            stage.show();
            stage.setResizable(false);


            // Cerrar la ventana actual
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
