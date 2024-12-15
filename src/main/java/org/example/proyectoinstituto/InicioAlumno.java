package org.example.proyectoinstituto;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.proyectoinstituto.dto.Alumno;
import org.example.proyectoinstituto.dto.AsignaturaRow;
import org.example.proyectoinstituto.dto.TrabajoRow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.example.proyectoinstituto.EmailSender.enviarCorreoPersonalAlumno;

public class InicioAlumno {

    @FXML
    private Label nombre;
    @FXML
    private AnchorPane inicioAlumnoPane, asignaturasAlumnoPane, trabajosAlumnoPane, correoAlumnoPane, perfilAlumnoPane;
    @FXML
    private TableView<AsignaturaRow> tablaAsignaturas;
    @FXML
    private TableColumn<AsignaturaRow, String> colNombreAsignatura;
    @FXML
    private TableColumn<AsignaturaRow, String> colDescripcion;
    @FXML
    private TableColumn<AsignaturaRow, String> colProfesor;
    @FXML
    private TableColumn<AsignaturaRow, Double> colCalificacion;
    @FXML
    private TableColumn<AsignaturaRow, Integer> colFaltas;

    @FXML
    private ComboBox<String> comboAsignaturas;
    @FXML
    private TableView<TrabajoRow> tablaTrabajos;
    @FXML
    private TableColumn<TrabajoRow, String> colTituloTrabajo;
    @FXML
    private TableColumn<TrabajoRow, String> colDescripcionTrabajo;
    @FXML
    private TableColumn<TrabajoRow, String> colFechaEntrega;
    @FXML
    private TableColumn<TrabajoRow, Double> colCalificacionTrabajo;

    @FXML
    private ComboBox<String> comboCorreosProfesores;
    @FXML
    private TextArea mensajeTextArea;
    @FXML
    private TextField fieldNombreProfe, fieldApellidosProfe, fieldEmailProfe, fieldPasswordProfe;
    @FXML
    private Button editarDatosBtn, aceptarCambiosBtn;


    private Alumno alumno;

    public void initialize(Alumno alumno) {
        this.alumno = alumno;
        nombre.setText(alumno.getNombre());
    }

    @FXML
    public void mostrarInicio() {
        inicioAlumnoPane.setVisible(true);
        asignaturasAlumnoPane.setVisible(false);
        trabajosAlumnoPane.setVisible(false);
        correoAlumnoPane.setVisible(false);
        perfilAlumnoPane.setVisible(false);
    }

    @FXML
    public void mostrarPerfil() {
        inicioAlumnoPane.setVisible(false);
        asignaturasAlumnoPane.setVisible(false);
        trabajosAlumnoPane.setVisible(false);
        correoAlumnoPane.setVisible(false);
        perfilAlumnoPane.setVisible(true);

        cargarDatosPerfil();
    }


    @FXML
    public void mostrarTrabajos() {
        inicioAlumnoPane.setVisible(false);
        asignaturasAlumnoPane.setVisible(false);
        trabajosAlumnoPane.setVisible(true);
        correoAlumnoPane.setVisible(false);
        perfilAlumnoPane.setVisible(false);

        inicializarTablaTrabajos();
        cargarComboAsignaturas();
    }

    @FXML
    public void mostrarCorreo() {
        inicioAlumnoPane.setVisible(false);
        asignaturasAlumnoPane.setVisible(false);
        trabajosAlumnoPane.setVisible(false);
        correoAlumnoPane.setVisible(true);
        perfilAlumnoPane.setVisible(false);

        cargarCorreosProfesores();
    }

    @FXML
    public void mostrarAsignaturas() {
        inicioAlumnoPane.setVisible(false);
        asignaturasAlumnoPane.setVisible(true);
        trabajosAlumnoPane.setVisible(false);
        correoAlumnoPane.setVisible(false);
        perfilAlumnoPane.setVisible(false);

        inicializarTablaAsignaturas();
        cargarDatosAsignaturas();
    }

    @FXML
    public void cambiarAsignatura() {
        String asignaturaSeleccionada = comboAsignaturas.getValue();
        if (asignaturaSeleccionada != null) {
            cargarTrabajos(asignaturaSeleccionada);
        }
    }

    private void inicializarTablaAsignaturas() {
        colNombreAsignatura.setCellValueFactory(new PropertyValueFactory<>("nombreAsignatura"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colProfesor.setCellValueFactory(new PropertyValueFactory<>("profesor"));
        colCalificacion.setCellValueFactory(new PropertyValueFactory<>("calificacion"));
        colFaltas.setCellValueFactory(new PropertyValueFactory<>("faltas"));
    }

    private void inicializarTablaTrabajos() {
        colTituloTrabajo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colDescripcionTrabajo.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colFechaEntrega.setCellValueFactory(new PropertyValueFactory<>("fechaEntrega"));
        colCalificacionTrabajo.setCellValueFactory(new PropertyValueFactory<>("calificacion"));
    }

    private void cargarComboAsignaturas() {
        List<String> asignaturas = new ArrayList<>();
        String query = "SELECT nombre_asignatura FROM Asignaturas a JOIN Matriculas m ON a.id_asignatura = m.id_asignatura WHERE m.id_alumno = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, alumno.getId_alumno());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                asignaturas.add(rs.getString("nombre_asignatura"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        comboAsignaturas.setItems(FXCollections.observableArrayList(asignaturas));
        comboAsignaturas.setOnAction(e -> cargarTrabajos(comboAsignaturas.getValue()));
    }

    private void cargarTrabajos(String asignaturaSeleccionada) {
        List<TrabajoRow> trabajos = new ArrayList<>();
        String query = """
        SELECT t.titulo, t.descripcion, t.fecha_entrega, ct.calificacion
        FROM Trabajos t
        JOIN Asignaturas a ON t.id_asignatura = a.id_asignatura
        LEFT JOIN CalificacionesTrabajos ct ON t.id_trabajo = ct.id_trabajo AND ct.id_alumno = ?
        WHERE a.nombre_asignatura = ? AND t.id_asignatura IN (
            SELECT id_asignatura FROM Matriculas WHERE id_alumno = ?
        );
        """;

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, alumno.getId_alumno());
            stmt.setString(2, asignaturaSeleccionada);
            stmt.setInt(3, alumno.getId_alumno());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                trabajos.add(new TrabajoRow(
                        rs.getString("titulo"),
                        rs.getString("descripcion"),
                        rs.getString("fecha_entrega"),
                        rs.getDouble("calificacion")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        tablaTrabajos.getItems().setAll(trabajos);
    }


    private void cargarDatosAsignaturas() {
        List<AsignaturaRow> rows = new ArrayList<>();
        String query = """
        SELECT a.nombre_asignatura, a.descripcion, p.nombre || ' ' || p.apellidos AS profesor,
               m.calificacion, m.faltas
        FROM Matriculas m
        JOIN Asignaturas a ON m.id_asignatura = a.id_asignatura
        JOIN Profesor p ON a.id_profesor = p.id_profesor
        WHERE m.id_alumno = ?;
        """;

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, alumno.getId_alumno());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                rows.add(new AsignaturaRow(
                        rs.getString("nombre_asignatura"),
                        rs.getString("descripcion"),
                        rs.getString("profesor"),
                        rs.getDouble("calificacion"),
                        rs.getInt("faltas")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        tablaAsignaturas.getItems().setAll(rows);
    }

    private void cerrarVentana() {
        Stage stage = (Stage) nombre.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void cerrarSesion() {
        cerrarVentana();
        RegistroController.abrirLogin();
    }

    @FXML
    private void cargarCorreosProfesores() {
        List<String> correosProfesores = new ArrayList<>();
        String query = """
        SELECT DISTINCT p.email
        FROM Profesor p
        JOIN Asignaturas a ON p.id_profesor = a.id_profesor
        JOIN Matriculas m ON a.id_asignatura = m.id_asignatura
        WHERE m.id_alumno = ?;
        """;

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, alumno.getId_alumno());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                correosProfesores.add(rs.getString("email"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        comboCorreosProfesores.setItems(FXCollections.observableArrayList(correosProfesores));
    }

    @FXML
    private void enviarCorreo() {
        String correoSeleccionado = comboCorreosProfesores.getValue();
        String mensaje = mensajeTextArea.getText();

        if (correoSeleccionado == null || mensaje.isBlank()) {
            mostrarAlerta("Error", "Por favor selecciona un profesor y escribe un mensaje.");
            return;
        }

        enviarCorreoPersonalAlumno(correoSeleccionado, alumno.getEmail(), mensaje);

        mostrarAlerta("Éxito", "El correo ha sido enviado correctamente.");
        mensajeTextArea.clear();
        comboCorreosProfesores.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void cargarDatosPerfil() {
        String query = "SELECT nombre, apellidos, email, password FROM Alumno WHERE id_alumno = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, alumno.getId_alumno());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Asignar los valores de la base de datos a los TextField
                fieldNombreProfe.setText(rs.getString("nombre"));
                fieldApellidosProfe.setText(rs.getString("apellidos"));
                fieldEmailProfe.setText(rs.getString("email"));
                fieldPasswordProfe.setText(rs.getString("password"));
            } else {
                // En caso de no encontrar al alumno
                mostrarAlerta("Error", "No se encontraron los datos del alumno.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error al cargar los datos del perfil.");
        }
    }


    @FXML
    private void editarDatosPerfil() {
        // Habilitar los TextField para que el alumno pueda editar los datos
        fieldNombreProfe.setDisable(false);
        fieldApellidosProfe.setDisable(false);
        fieldEmailProfe.setDisable(false);
        fieldPasswordProfe.setDisable(false);

        // Mostrar el botón "Aceptar cambios"
        aceptarCambiosBtn.setVisible(true);
    }


    @FXML
    private void aceptarCambios() {
        String nuevoNombre = fieldNombreProfe.getText();
        String nuevosApellidos = fieldApellidosProfe.getText();
        String nuevoEmail = fieldEmailProfe.getText();
        String nuevaContrasena = fieldPasswordProfe.getText();

        String query = "UPDATE Alumno SET nombre = ?, apellidos = ?, email = ?, password = ? WHERE id_alumno = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nuevoNombre);
            stmt.setString(2, nuevosApellidos);
            stmt.setString(3, nuevoEmail);
            stmt.setString(4, nuevaContrasena);
            stmt.setInt(5, alumno.getId_alumno());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                mostrarAlerta("Éxito", "Los cambios se han guardado correctamente.");
                // Deshabilitar los TextField después de guardar
                fieldNombreProfe.setDisable(true);
                fieldApellidosProfe.setDisable(true);
                fieldEmailProfe.setDisable(true);
                fieldPasswordProfe.setDisable(true);
                aceptarCambiosBtn.setVisible(false);
            } else {
                mostrarAlerta("Error", "No se pudieron guardar los cambios.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error al guardar los cambios.");
        }
    }

}
