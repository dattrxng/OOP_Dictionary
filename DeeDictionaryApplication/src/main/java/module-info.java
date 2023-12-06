module deedictionaryapplication.deedictionaryapplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires javafx.media;
    requires jsapi;

    opens deedictionaryapplication to javafx.fxml;
    exports deedictionaryapplication;
    exports deedictionaryapplication.Controllers;
    opens deedictionaryapplication.Controllers to javafx.fxml;
}