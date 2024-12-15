package org.example.proyectoinstituto;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AsignaturasController {

    @FXML
    private CheckBox asignatura1;

    @FXML
    private CheckBox asignatura2;

    @FXML
    private CheckBox asignatura3;

    @FXML
    private CheckBox asignatura4;

    @FXML
    private CheckBox asignatura5;

    @FXML
    private CheckBox asignatura6;

    // Callback para pasar los datos seleccionados
    private Consumer<List<String>> onAsignaturasSeleccionadas;

    // Configurar el callback
    public void setOnAsignaturasSeleccionadas(Consumer<List<String>> callback) {
        this.onAsignaturasSeleccionadas = callback;
    }

    @FXML
    public void initialize() {
        // Seleccionar todas las asignaturas por defecto
        asignatura1.setSelected(true);
        asignatura2.setSelected(true);
        asignatura3.setSelected(true);
        asignatura4.setSelected(true);
        asignatura5.setSelected(true);
        asignatura6.setSelected(true);
    }

    @FXML
    protected void confirmarAsignaturas() {
        // Lista para almacenar las asignaturas seleccionadas
        List<String> asignaturasSeleccionadas = new ArrayList<>();

        // Verificar qué asignaturas están seleccionadas
        if (asignatura1.isSelected()) asignaturasSeleccionadas.add("Acceso a Datos");
        if (asignatura2.isSelected()) asignaturasSeleccionadas.add("Desarrollo de interfaces");
        if (asignatura3.isSelected()) asignaturasSeleccionadas.add("Programación multimedia y dispositivos móviles");
        if (asignatura4.isSelected()) asignaturasSeleccionadas.add("Programación de servicios y procesos");
        if (asignatura5.isSelected()) asignaturasSeleccionadas.add("Sistemas de gestión empresarial");
        if (asignatura6.isSelected()) asignaturasSeleccionadas.add("Empresa e iniciativa emprendedora");

        // Llamar al callback con la lista de asignaturas seleccionadas
        if (onAsignaturasSeleccionadas != null) {
            onAsignaturasSeleccionadas.accept(asignaturasSeleccionadas);
        }

        // Cerrar la ventana secundaria
        asignatura1.getScene().getWindow().hide();
    }
}
