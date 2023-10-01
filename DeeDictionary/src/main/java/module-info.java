module main.deedictionary {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;

    opens main.deedictionary to javafx.fxml;
    exports main.deedictionary;
}