package org.example.proyectoinstituto;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import org.example.proyectoinstituto.dto.TrabajoAlumno;
import org.example.proyectoinstituto.dto.Trabajos;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class CalificacionFinalTrabajos {

    @FXML
    private TableView<TrabajoAlumno> tableViewTrabajos;
    @FXML
    private TableColumn<TrabajoAlumno, String> colTrabajo;
    @FXML
    private TableColumn<TrabajoAlumno, Integer> colCalificacion;
    @FXML
    private TextField mediaField;
    @FXML
    private Button btnCalcularMedia;
    @FXML
    private Button btnConfirmarCalificacion;

    private List<TrabajoAlumno> trabajosAlumno;
    private int idAlumno;
    private int idAsignatura;

    // Inicialización de los datos en la ventana
    public void initialize(List<Trabajos> trabajos, List<Integer> calificaciones, int idAlumno, int idAsignatura) {
        this.idAlumno = idAlumno;
        this.idAsignatura = idAsignatura;

        // Crear la lista de trabajos y calificaciones
        trabajosAlumno = crearTrabajoAlumno(trabajos, calificaciones);

        colTrabajo.setCellValueFactory(new PropertyValueFactory<>("nombreTrabajo"));
        colCalificacion.setCellValueFactory(new PropertyValueFactory<>("calificacion"));

        tableViewTrabajos.setItems(FXCollections.observableArrayList(trabajosAlumno));

        // Lógica para calcular la media
        btnCalcularMedia.setOnAction(event -> calcularMedia());

        // Lógica para confirmar la calificación final
        btnConfirmarCalificacion.setOnAction(event -> confirmarCalificacion());
    }

    private List<TrabajoAlumno> crearTrabajoAlumno(List<Trabajos> trabajos, List<Integer> calificaciones) {
        // Crear una lista de objetos TrabajoAlumno con los datos de trabajos y calificaciones
        List<TrabajoAlumno> lista = new ArrayList<>();
        for (int i = 0; i < trabajos.size(); i++) {
            lista.add(new TrabajoAlumno(trabajos.get(i).getTitulo(), calificaciones.get(i)));
        }
        return lista;
    }

    private void calcularMedia() {
        int suma = 0;
        int cantidad = 0;
        for (TrabajoAlumno trabajo : trabajosAlumno) {
            suma += trabajo.getCalificacion();
            cantidad++;
        }

        if (cantidad > 0) {
            double media = (double) suma / cantidad;
            mediaField.setText(String.format("%.2f", media));
        }
    }

    private void confirmarCalificacion() {
        // Obtenemos la media calculada
        double calificacionFinal = Double.parseDouble(mediaField.getText());

        // Actualizamos la calificación en la tabla Matriculas
        if (actualizarCalificacionMatricula(idAlumno, idAsignatura, calificacionFinal)) {
            showAlert("Confirmación", "La calificación final se ha agregado correctamente.", "Calificación agregada.");
        } else {
            showAlert("Error", "Ocurrió un error al actualizar la calificación.", "No se pudo actualizar la calificación.");
        }
    }

    private boolean actualizarCalificacionMatricula(int idAlumno, int idAsignatura, double calificacion) {
        String query = "UPDATE Matriculas SET calificacion = ? WHERE id_alumno = ? AND id_asignatura = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, calificacion);
            stmt.setInt(2, idAlumno);
            stmt.setInt(3, idAsignatura);

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar la calificación en Matriculas: " + e.getMessage());
            return false;
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
