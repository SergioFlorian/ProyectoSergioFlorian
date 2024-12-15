package org.example.proyectoinstituto;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.proyectoinstituto.dto.Alumno;
import org.example.proyectoinstituto.dto.Profesor;
import org.example.proyectoinstituto.dto.TrabajoTabla;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class CalificarTrabajoController {

    @FXML
    private ComboBox<Alumno> alumnoComboBox;
    @FXML
    private TextField calificacionField;

    private List<Alumno> alumnos;
    private TrabajoTabla trabajo;
    private int idAsignatura;
    private Profesor profesor;

    public void initialize(List<Alumno> alumnos, TrabajoTabla trabajo, int idAsignatura, Profesor profesor) {
        this.alumnos = alumnos;
        this.trabajo = trabajo;
        this.idAsignatura = idAsignatura;
        this.profesor = profesor;

        alumnoComboBox.getItems().setAll(alumnos);

        alumnoComboBox.setCellFactory(param -> new ListCell<Alumno>() {
            @Override
            protected void updateItem(Alumno alumno, boolean empty) {
                super.updateItem(alumno, empty);
                if (empty || alumno == null) {
                    setText(null);
                } else {
                    setText(alumno.getNombre() + " " + alumno.getApellidos());
                }
            }
        });

        // Mostrar solo el nombre en el ComboBox, pero usar el objeto Alumno internamente
        alumnoComboBox.setButtonCell(new ListCell<Alumno>() {
            @Override
            protected void updateItem(Alumno alumno, boolean empty) {
                super.updateItem(alumno, empty);
                if (empty || alumno == null) {
                    setText(null);
                } else {
                    setText(alumno.getNombre() + " " + alumno.getApellidos());
                }
            }
        });
    }

    @FXML
    private void calificar() {
        Alumno alumnoSeleccionado = alumnoComboBox.getValue();
        if (alumnoSeleccionado == null) {
            // Si no se ha seleccionado un alumno
            showAlert("Error", "Debe seleccionar un alumno.", Alert.AlertType.ERROR);
            return;
        }

        try {
            double calificacion = Double.parseDouble(calificacionField.getText());
            if (calificacion < 0 || calificacion > 10) {
                showAlert("Error", "La calificación debe estar entre 0 y 10.", Alert.AlertType.ERROR);
                return;
            }

            // Insertar o actualizar la calificación
            String query = "INSERT OR REPLACE INTO CalificacionesTrabajos (id_trabajo, id_alumno, calificacion) VALUES (?, ?, ?)";
            try (Connection conn = DatabaseConnection.connect();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setInt(1, trabajo.getIdTrabajo());
                stmt.setInt(2, alumnoSeleccionado.getId_alumno());
                stmt.setDouble(3, calificacion);

                stmt.executeUpdate();
                showAlert("Éxito", "Calificación guardada correctamente.", Alert.AlertType.INFORMATION);

                // Cerrar la ventana después de guardar
                Stage stage = (Stage) alumnoComboBox.getScene().getWindow();
                stage.close();

            } catch (SQLException e) {
                showAlert("Error", "Error al guardar la calificación.", Alert.AlertType.ERROR);
            }

        } catch (NumberFormatException e) {
            showAlert("Error", "La calificación debe ser un número.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void cerrar() {
        Stage stage = (Stage) alumnoComboBox.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
