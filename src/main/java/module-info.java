module org.example.proyectoinstituto {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires java.mail;

    opens org.example.proyectoinstituto to javafx.fxml;
    opens org.example.proyectoinstituto.dto to javafx.base;

    exports org.example.proyectoinstituto;
}
