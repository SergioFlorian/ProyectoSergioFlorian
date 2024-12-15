package org.example.proyectoinstituto;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.proyectoinstituto.dto.TrabajoTabla;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EditarTrabajoController {

    @FXML
    private TextField nombreTrabajoField;

    @FXML
    private TextField descripcionTrabajoField;

    @FXML
    private TextField fechaEntregaField;

    private TrabajoTabla trabajo;

    public void initialize(TrabajoTabla trabajo) {
        this.trabajo = trabajo;

        // Rellenar los campos con los datos del trabajo
        nombreTrabajoField.setText(trabajo.getTitulo());
        descripcionTrabajoField.setText(trabajo.getDescripcion());
        fechaEntregaField.setText(trabajo.getFechaEntrega());
    }

    @FXML
    public void guardarCambios() {
        String nombre = nombreTrabajoField.getText();
        String descripcion = descripcionTrabajoField.getText();
        String fechaEntrega = fechaEntregaField.getText();

        if (nombre.isEmpty() || descripcion.isEmpty() || fechaEntrega.isEmpty()) {
            System.out.println("Todos los campos son obligatorios.");
            return;
        }

        String query = "UPDATE Trabajos SET titulo = ?, descripcion = ?, fecha_entrega = ? WHERE id_trabajo = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nombre);
            stmt.setString(2, descripcion);
            stmt.setString(3, fechaEntrega);
            stmt.setInt(4, trabajo.getIdTrabajo());

            stmt.executeUpdate();
            cerrarVentana();
        } catch (SQLException e) {
            System.out.println("Error al actualizar el trabajo: " + e.getMessage());
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
