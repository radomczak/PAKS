module PAKSProject {
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;

    exports Client to javafx.graphics;
    opens Controller to javafx.fxml, javafx.base;
}