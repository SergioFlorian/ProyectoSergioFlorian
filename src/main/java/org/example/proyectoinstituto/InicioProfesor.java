package org.example.proyectoinstituto;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.proyectoinstituto.dto.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.example.proyectoinstituto.EmailSender.enviarCorreoPersonal;

public class InicioProfesor {

    @FXML
    private Label nombre, textoTrabajosAsignatura;
    @FXML
    private TextField fieldNombreProfe, fieldApellidosProfe, fieldEmailProfe, fieldPasswordProfe,fieldAsignaturasProfe ;
    @FXML
    private TextArea descripcionCorreoAlumnos;
    @FXML
    private AnchorPane inicioPane, perfilPane, trabajosPane, correoPane;
    @FXML
    private ChoiceBox<String> choiceAsignaturas;
    @FXML
    private ComboBox<String> alumnosCorreo;
    @FXML
    private TableView<AlumnoTabla> tableView;
    @FXML
    private TableView<TrabajoTabla> trabajosTableView;
    @FXML
    private TableColumn<AlumnoTabla, String> nombreColumn, apellidosColumn;
    @FXML
    private TableColumn<AlumnoTabla, Integer> calificacionColumn, faltasColumn;
    @FXML
    private TableColumn<TrabajoTabla, String> trabajoNombreColumn, trabajoDescripcionColumn, trabajoFechaColumn;
    @FXML
    private Button ponerFaltaButton, aceptarCambiosBtn;

    private ObservableList<TrabajoTabla> trabajosList;
    private Profesor profesor; // Almacenar el objeto Profesor

    @FXML
    public void mostrarInicio() {
        inicioPane.setVisible(true);
        perfilPane.setVisible(false);
        trabajosPane.setVisible(false);
        correoPane.setVisible(false);
    }

    @FXML
    public void mostrarPerfil() {
        inicioPane.setVisible(false);
        perfilPane.setVisible(true);
        trabajosPane.setVisible(false);
        correoPane.setVisible(false);
    }

    @FXML
    public void mostrarTrabajos() {
        inicioPane.setVisible(false);
        perfilPane.setVisible(false);
        trabajosPane.setVisible(true);
        correoPane.setVisible(false);
        cargarTrabajos();
    }

    @FXML
    public void mostrarCorreo() {
        inicioPane.setVisible(false);
        perfilPane.setVisible(false);
        trabajosPane.setVisible(false);
        correoPane.setVisible(true);

        List<Alumno> alumnos = obtenerAlumnosPorProfesor(profesor.getId_profesor());
        for (Alumno alumno : alumnos) { alumnosCorreo.getItems().add(alumno.getEmail()); }
    }

    private void cerrarVentana() {
        Stage stage = (Stage) nombre.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void cerrarSesion(){
        cerrarVentana();
        RegistroController.abrirLogin();
    }

    public void initialize(Profesor profesor) {
        this.profesor = profesor; // Guardar el objeto Profesor
        // Mostrar información del profesor
        nombre.setText(profesor.getNombre());

        cargarDatosPerfil(profesor);

        // Consultar asignaturas del profesor y mostrarlas
        List<Asignaturas> asignaturas = obtenerAsignaturasProfesor(profesor.getId_profesor());
        for (Asignaturas asignatura : asignaturas) {
            choiceAsignaturas.getItems().add(asignatura.getNombre_asignatura());
        }
        if (!asignaturas.isEmpty()) {
            choiceAsignaturas.setValue(asignaturas.get(0).getNombre_asignatura());
            cargarAlumnos(asignaturas.get(0).getId_asignatura());
            textoTrabajosAsignatura.setText("TRABAJOS DE: " + choiceAsignaturas.getValue());
        }

        // Configurar acción para el botón "Poner Falta"
        ponerFaltaButton.setOnAction(event -> abrirVentanaFalta());

        // Configurar las columnas de la tabla
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        apellidosColumn.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        calificacionColumn.setCellValueFactory(new PropertyValueFactory<>("calificacion"));
        faltasColumn.setCellValueFactory(new PropertyValueFactory<>("faltas"));

        // Agregar ChangeListener al ChoiceBox
        choiceAsignaturas.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                int idAsignatura = obtenerIdAsignatura(newValue);
                textoTrabajosAsignatura.setText("TRABAJOS DE: " + choiceAsignaturas.getValue());
                cargarTrabajos(); // Llama a cargarTrabajos para actualizar la tabla
                // Refrescar la tabla después de añadir la calificacion final
                cargarAlumnos(obtenerIdAsignatura(choiceAsignaturas.getValue()));
            }
        });

    }

    private void cargarDatosPerfil(Profesor profesor) {
        // Asignar los datos del profesor a los campos correspondientes
        fieldNombreProfe.setText(profesor.getNombre());
        fieldApellidosProfe.setText(profesor.getApellidos());
        fieldEmailProfe.setText(profesor.getEmail());
        fieldPasswordProfe.setText(profesor.getPassword());

        // Obtener las asignaturas del profesor
        List<Asignaturas> asignaturas = obtenerAsignaturasProfesor(profesor.getId_profesor());

        // Crear un StringBuilder para almacenar las asignaturas
        StringBuilder textoAsignaturas = new StringBuilder();

        // Añadir las asignaturas al StringBuilder
        for (Asignaturas asignatura : asignaturas) {
            textoAsignaturas.append(asignatura.getNombre_asignatura()).append(", ");
        }

        // Eliminar la última coma y espacio sobrante, si los hay
        if (textoAsignaturas.length() > 0) {
            textoAsignaturas.setLength(textoAsignaturas.length() - 2); // Elimina la última coma y espacio
        }

        // Mostrar el texto de asignaturas en el campo correspondiente
        fieldAsignaturasProfe.setText(textoAsignaturas.toString());
    }

    @FXML
    private void enviarCorreoAlumno() {
        String correoAlumno = alumnosCorreo.getValue();
        String mensaje = descripcionCorreoAlumnos.getText();

        enviarCorreoPersonal(correoAlumno, correoAlumno, profesor.getNombre(), mensaje);
        showAlert("Éxito", "Se ha enviado el correo al alumno.", String.valueOf(Alert.AlertType.INFORMATION));

        descripcionCorreoAlumnos.clear();
        alumnosCorreo.getSelectionModel().clearSelection();
    }

    // Método para habilitar la edición de los campos
    @FXML
    private void editarDatosPerfil() {
        // Habilitar los TextField para permitir la edición
        fieldNombreProfe.setDisable(false);
        fieldApellidosProfe.setDisable(false);
        fieldEmailProfe.setDisable(false);
        fieldPasswordProfe.setDisable(false);

        // Mostrar el botón de aceptar cambios
        aceptarCambiosBtn.setVisible(true);
    }

    @FXML
    private void aceptarCambios() {
        // Obtener los datos ingresados en los campos de texto
        String nombre = fieldNombreProfe.getText();
        String apellidos = fieldApellidosProfe.getText();
        String email = fieldEmailProfe.getText();
        String password = fieldPasswordProfe.getText();
        int id_profesor = profesor.getId_profesor();

        // Consulta SQL para actualizar los datos del profesor
        String sql = "UPDATE profesor SET nombre = ?, apellidos = ?, email = ?, password = ? WHERE id_profesor = ?";

        try (Connection conn = DatabaseConnection.connect(); // Suponiendo que tienes este método de conexión
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Establecer los parámetros en la consulta SQL
            pstmt.setString(1, nombre);
            pstmt.setString(2, apellidos);
            pstmt.setString(3, email);
            pstmt.setString(4, password);
            pstmt.setInt(5, id_profesor);

            // Ejecutar la actualización
            int filasAfectadas = pstmt.executeUpdate();

            // Verificar si la actualización fue exitosa
            if (filasAfectadas > 0) {
                // Si se actualizó correctamente, mostrar una alerta de éxito
                showAlert("Éxito", "Los datos se actualizaron correctamente.", String.valueOf(Alert.AlertType.INFORMATION));

                // Deshabilitar los TextField después de guardar los cambios
                fieldNombreProfe.setDisable(true);
                fieldApellidosProfe.setDisable(true);
                fieldEmailProfe.setDisable(true);
                fieldPasswordProfe.setDisable(true);

                // Ocultar el botón de aceptar cambios
                aceptarCambiosBtn.setVisible(false);
            } else {
                // Si no se actualizó ninguna fila, mostrar una alerta de error
                showAlert("Error", "No se pudo actualizar los datos.", String.valueOf(Alert.AlertType.ERROR));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // En caso de error, mostrar una alerta de error
            showAlert("Error", "Hubo un problema al intentar actualizar los datos.", String.valueOf(Alert.AlertType.ERROR));
        }
    }

    private List<Asignaturas> obtenerAsignaturasProfesor(int idProfesor) {
        List<Asignaturas> asignaturasList = new ArrayList<>();
        String query = "SELECT * FROM Asignaturas WHERE id_profesor = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idProfesor);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Asignaturas asignatura = new Asignaturas(
                        rs.getInt("id_asignatura"),
                        rs.getString("nombre_asignatura"),
                        rs.getString("descripcion"),
                        rs.getInt("id_profesor")
                );
                asignaturasList.add(asignatura);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener asignaturas: " + e.getMessage());
        }

        return asignaturasList;
    }

    private int obtenerIdAsignatura(String nombreAsignatura) {
        int idAsignatura = -1;
        String query = "SELECT id_asignatura FROM Asignaturas WHERE nombre_asignatura = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nombreAsignatura);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                idAsignatura = rs.getInt("id_asignatura");
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener ID de la asignatura: " + e.getMessage());
        }

        return idAsignatura;
    }

    private List<Alumno> obtenerAlumnosPorProfesor(int idProfesor) {
        List<Alumno> alumnos = new ArrayList<>();
        String query = "SELECT DISTINCT Alumno.id_alumno, Alumno.nombre, Alumno.apellidos, Alumno.email, Alumno.password " +
                "FROM Alumno " +
                "JOIN Matriculas ON Alumno.id_alumno = Matriculas.id_alumno " +
                "JOIN Asignaturas ON Matriculas.id_asignatura = Asignaturas.id_asignatura " +
                "WHERE Asignaturas.id_profesor = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idProfesor);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Alumno alumno = new Alumno(
                        rs.getInt("id_alumno"),
                        rs.getString("nombre"),
                        rs.getString("apellidos"),
                        rs.getString("email"),
                        rs.getString("password")
                );
                alumnos.add(alumno);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener alumnos por profesor: " + e.getMessage());
        }

        return alumnos;
    }


    private void cargarAlumnos(int idAsignatura) {
        ObservableList<AlumnoTabla> estudiantes = FXCollections.observableArrayList();
        String query = "SELECT Alumno.id_alumno, Alumno.nombre, Alumno.apellidos, Matriculas.calificacion, Matriculas.faltas " +
                "FROM Alumno " +
                "JOIN Matriculas ON Alumno.id_alumno = Matriculas.id_alumno " +
                "WHERE Matriculas.id_asignatura = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idAsignatura);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                AlumnoTabla estudiante = new AlumnoTabla(
                        rs.getInt("id_alumno"),
                        rs.getString("nombre"),
                        rs.getString("apellidos"),
                        rs.getInt("calificacion"),
                        rs.getInt("faltas")
                );
                estudiantes.add(estudiante);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener estudiantes: " + e.getMessage());
        }

        tableView.setItems(estudiantes);
    }

    private void abrirVentanaFalta() {
        AlumnoTabla selectedAlumno = tableView.getSelectionModel().getSelectedItem();

        if (selectedAlumno != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/proyectoinstituto/AgregarFalta.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene(loader.load()));
                stage.setTitle("Añadir Falta");
                stage.initModality(Modality.APPLICATION_MODAL);

                AgregarFaltaController controller = loader.getController();
                // Suponiendo que tienes un método para obtener el objeto Alumno a partir de selectedAlumno
                Alumno alumno = obtenerAlumnoPorId(selectedAlumno.getId_alumno());
                controller.initialize(alumno, profesor, obtenerIdAsignatura(choiceAsignaturas.getValue()));

                stage.showAndWait();

                // Refrescar la tabla después de añadir la falta
                cargarAlumnos(obtenerIdAsignatura(choiceAsignaturas.getValue()));

            } catch (IOException e) {
                System.out.println("Error al abrir la ventana de añadir falta: " + e.getMessage());
            }
        } else {
            System.out.println("Por favor, seleccione un alumno.");
        }
    }

    private Alumno obtenerAlumnoPorId(int idAlumno) {
        // Implementa la lógica para obtener un objeto Alumno a partir del idAlumno
        Alumno alumno = null;
        String query = "SELECT * FROM Alumno WHERE id_alumno = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idAlumno);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                alumno = new Alumno(rs.getInt("id_alumno"),
                        rs.getString("nombre"),
                        rs.getString("apellidos"),
                        rs.getString("email"),
                        rs.getString("password"));
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener alumno: " + e.getMessage());
        }

        return alumno;
    }

    private void cargarTrabajos() {
        trabajosList = FXCollections.observableArrayList();
        String query = "SELECT * FROM Trabajos WHERE id_asignatura = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, obtenerIdAsignatura(choiceAsignaturas.getValue())); // Obtiene el ID de la asignatura seleccionada
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                TrabajoTabla trabajo = new TrabajoTabla(
                        rs.getInt("id_trabajo"),
                        rs.getString("titulo"),
                        rs.getString("descripcion"),
                        rs.getString("fecha_entrega")
                );
                trabajosList.add(trabajo); // Añade el trabajo a la lista
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar trabajos: " + e.getMessage());
        }

        trabajoNombreColumn.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        trabajoDescripcionColumn.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        trabajoFechaColumn.setCellValueFactory(new PropertyValueFactory<>("fechaEntrega"));

        trabajosTableView.setItems(trabajosList); // Asocia la lista de trabajos a la tabla
    }


    @FXML
    private void añadirTrabajo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/proyectoinstituto/AgregarTrabajo.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Añadir Trabajo");
            stage.initModality(Modality.APPLICATION_MODAL);

            AgregarTrabajoController controller = loader.getController();
            controller.initialize(obtenerIdAsignatura(choiceAsignaturas.getValue()), profesor.getId_profesor());

            stage.showAndWait();
            cargarTrabajos();

        } catch (IOException e) {
            System.out.println("Error al abrir la ventana de añadir trabajo: " + e.getMessage());
        }
    }

    @FXML
    private void editarTrabajo() {
        TrabajoTabla selectedTrabajo = trabajosTableView.getSelectionModel().getSelectedItem();

        if (selectedTrabajo != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/proyectoinstituto/EditarTrabajo.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene(loader.load()));
                stage.setTitle("Editar Trabajo");
                stage.initModality(Modality.APPLICATION_MODAL);

                EditarTrabajoController controller = loader.getController();
                controller.initialize(selectedTrabajo);

                stage.showAndWait();
                cargarTrabajos();

            } catch (IOException e) {
                System.out.println("Error al abrir la ventana de editar trabajo: " + e.getMessage());
            }
        } else {
            System.out.println("Por favor, seleccione un trabajo.");
        }
    }

    @FXML
    private void borrarTrabajo() {
        TrabajoTabla selectedTrabajo = trabajosTableView.getSelectionModel().getSelectedItem();

        if (selectedTrabajo != null) {
            String query = "DELETE FROM Trabajos WHERE id_trabajo = ?";

            try (Connection conn = DatabaseConnection.connect();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setInt(1, selectedTrabajo.getIdTrabajo());
                int filasEliminadas = stmt.executeUpdate();

                if (filasEliminadas > 0) {
                    System.out.println("Trabajo eliminado correctamente.");
                    cargarTrabajos();
                }

            } catch (SQLException e) {
                System.out.println("Error al eliminar trabajo: " + e.getMessage());
            }
        } else {
            System.out.println("Por favor, seleccione un trabajo.");
        }
    }

    private List<Alumno> obtenerAlumnosAsignatura(int idAsignatura) {
        List<Alumno> alumnos = new ArrayList<>();
        String query = "SELECT Alumno.id_alumno, Alumno.nombre, Alumno.apellidos " +
                "FROM Alumno " +
                "JOIN Matriculas ON Alumno.id_alumno = Matriculas.id_alumno " +
                "WHERE Matriculas.id_asignatura = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idAsignatura);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Alumno alumno = new Alumno(
                        rs.getInt("id_alumno"),
                        rs.getString("nombre"),
                        rs.getString("apellidos")
                );
                alumnos.add(alumno);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener los alumnos: " + e.getMessage());
        }

        return alumnos;
    }

    public void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void ponerCalificacion() {
        TrabajoTabla selectedTrabajo = trabajosTableView.getSelectionModel().getSelectedItem();

        if (selectedTrabajo != null) {
            // Obtener la lista de alumnos para esta asignatura
            List<Alumno> alumnos = obtenerAlumnosAsignatura(obtenerIdAsignatura(choiceAsignaturas.getValue()));

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/proyectoinstituto/CalificacionTrabajo.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene(loader.load()));
                stage.setTitle("Calificar Trabajo");
                stage.initModality(Modality.APPLICATION_MODAL);

                CalificarTrabajoController controller = loader.getController();
                controller.initialize(alumnos, selectedTrabajo, obtenerIdAsignatura(choiceAsignaturas.getValue()), profesor);

                stage.showAndWait();
            } catch (IOException e) {
                System.out.println("Error al abrir la ventana de calificación: " + e.getMessage());
            }
        } else {
            showAlert("Error", "Por favor, seleccione un trabajo.", String.valueOf(Alert.AlertType.ERROR));
        }
    }

    private List<Trabajos> obtenerTrabajosAlumno(int idAlumno, int idAsignatura) {
        List<Trabajos> trabajos = new ArrayList<>();
        String query = "SELECT t.id_trabajo, t.titulo, t.descripcion, t.fecha_entrega " +
                "FROM Trabajos t " +
                "JOIN CalificacionesTrabajos e ON t.id_trabajo = e.id_trabajo " +
                "WHERE e.id_alumno = ? AND t.id_asignatura = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idAlumno);
            stmt.setInt(2, idAsignatura);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Trabajos trabajo = new Trabajos(
                        rs.getInt("id_trabajo"),
                        rs.getString("titulo"),
                        rs.getString("descripcion"),
                        rs.getString("fecha_entrega")
                );
                trabajos.add(trabajo);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener los trabajos del alumno: " + e.getMessage());
        }

        return trabajos;
    }

    private List<Integer> obtenerCalificaciones(List<Trabajos> trabajos, int idAlumno) {
        List<Integer> calificaciones = new ArrayList<>();
        String query = "SELECT calificacion FROM CalificacionesTrabajos WHERE id_alumno = ? AND id_trabajo = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            for (Trabajos trabajo : trabajos) {
                stmt.setInt(1, idAlumno);
                stmt.setInt(2, trabajo.getId_trabajo());
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    calificaciones.add(rs.getInt("calificacion"));
                } else {
                    calificaciones.add(0); // Si no hay calificación, se asume un 0
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener las calificaciones: " + e.getMessage());
        }

        return calificaciones;
    }

    @FXML
    private void ponerCalificacionFinal() {
        AlumnoTabla selectedAlumno = tableView.getSelectionModel().getSelectedItem();
        String selectedAsignatura = choiceAsignaturas.getSelectionModel().getSelectedItem();

        if (selectedAlumno != null && selectedAsignatura != null) {
            // Obtener los trabajos y las calificaciones del alumno para la asignatura seleccionada
            List<Trabajos> trabajos = obtenerTrabajosAlumno(selectedAlumno.getId_alumno(), obtenerIdAsignatura(selectedAsignatura));
            List<Integer> calificaciones = obtenerCalificaciones(trabajos, selectedAlumno.getId_alumno());

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/proyectoinstituto/CalificacionFinalTrabajos.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene(loader.load()));
                stage.setTitle("Calificar Trabajo");
                stage.initModality(Modality.APPLICATION_MODAL);

                CalificacionFinalTrabajos controller = loader.getController();
                controller.initialize(trabajos, calificaciones, selectedAlumno.getId_alumno(), obtenerIdAsignatura(selectedAsignatura));

                stage.showAndWait();
                // Refrescar la tabla después de añadir la calificacion final
                cargarAlumnos(obtenerIdAsignatura(choiceAsignaturas.getValue()));
            } catch (IOException e) {
                System.out.println("Error al abrir la ventana de calificación: " + e.getMessage());
            }
        } else {
            showAlert("Error", "Por favor, seleccione un alumno y una asignatura.", "No se ha seleccionado ningún alumno o asignatura.");
        }
    }


}
