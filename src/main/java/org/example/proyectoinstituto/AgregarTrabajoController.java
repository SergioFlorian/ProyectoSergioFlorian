package org.example.proyectoinstituto;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AgregarTrabajoController {

    @FXML
    private TextField nombreTrabajoField;

    @FXML
    private TextField descripcionTrabajoField;

    @FXML
    private TextField fechaEntregaField;

    private int idAsignatura;
    private int idProfesor;

    public void initialize(int idAsignatura, int idProfesor) {
        this.idAsignatura = idAsignatura;
        this.idProfesor = idProfesor;
    }

    @FXML
    public void guardarTrabajo() {
        String titulo = nombreTrabajoField.getText();
        String descripcion = descripcionTrabajoField.getText();
        String fechaEntrega = fechaEntregaField.getText();

        if (titulo.isEmpty() || descripcion.isEmpty() || fechaEntrega.isEmpty()) {
            System.out.println("Todos los campos son obligatorios.");
            return;
        }

        String query = "INSERT INTO Trabajos (titulo, descripcion, fecha_entrega, id_asignatura, id_profesor) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, titulo);
            stmt.setString(2, descripcion);
            stmt.setString(3, fechaEntrega);
            stmt.setInt(4, idAsignatura);
            stmt.setInt(5, idProfesor);

            stmt.executeUpdate();
            cerrarVentana();
        } catch (SQLException e) {
            System.out.println("Error al guardar el trabajo: " + e.getMessage());
        }
    }

    private void cerrarVentana() {
        Stage stage = (Stage) nombreTrabajoField.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void cancelar() {
        cerrarVentana();
    }
}
