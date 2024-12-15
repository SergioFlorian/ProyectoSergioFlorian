package org.example.proyectoinstituto;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.example.proyectoinstituto.dto.Alumno;
import org.example.proyectoinstituto.dto.Profesor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class AgregarFaltaController {

    @FXML
    private TextArea comentarioField;
    @FXML
    private DatePicker fechaFaltaPicker;  // DatePicker para seleccionar la fecha de la falta

    private Alumno alumno;
    private Profesor profesor;
    private int idAsignatura;

    public void initialize(Alumno alumno, Profesor profesor, int idAsignatura) {
        this.alumno = alumno;
        this.profesor = profesor;
        this.idAsignatura = idAsignatura;
    }

    @FXML
    private void handleAddFalta() {
        String comentario = comentarioField.getText();
        LocalDate fechaFalta = fechaFaltaPicker.getValue();

        if (fechaFalta == null || comentario.isEmpty()) {
            mostrarAlerta(false);
            return;
        }

        // Lógica para añadir la falta en la base de datos
        String query = "UPDATE Matriculas SET faltas = faltas + 1 WHERE id_alumno = ? AND id_asignatura = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, alumno.getId_alumno());
            stmt.setInt(2, idAsignatura);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error al añadir falta: " + e.getMessage());
        }

        // Enviar el correo al alumno notificando la falta
        EmailSender.enviarFalta(alumno.getEmail(), alumno.getNombre(), profesor.getNombre(), obtenerNombreAsignatura(idAsignatura), comentario, fechaFalta);

        // Mostrar alerta de éxito
        mostrarAlerta(true);

        // Cerrar la ventana
        Stage stage = (Stage) comentarioField.getScene().getWindow();
        stage.close();
    }

    private String obtenerNombreAsignatura(int idAsignatura) {
        // Lógica para obtener el nombre de la asignatura a partir del idAsignatura
        String nombreAsignatura = "";
        String query = "SELECT nombre_asignatura FROM Asignaturas WHERE id_asignatura = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idAsignatura);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                nombreAsignatura = rs.getString("nombre_asignatura");
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener nombre de asignatura: " + e.getMessage());
        }

        return nombreAsignatura;
    }

    // Método para mostrar la alerta
    private void mostrarAlerta(boolean exito) {
        Alert alert = new Alert(exito ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle(exito ? "Falta Registrada" : "Error al Registrar Falta");
        alert.setHeaderText(exito ? "¡Falta Registrada!" : "Error al Registrar Falta");
        alert.setContentText(exito ? "Se ha enviado un correo al alumno con la notificación de la falta." : "Hubo un error al registrar la falta y enviar el correo.");

        alert.showAndWait();
    }
}
