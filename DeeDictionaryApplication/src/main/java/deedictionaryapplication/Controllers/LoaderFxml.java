package deedictionaryapplication.Controllers;

import deedictionaryapplication.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.net.URL;

public class LoaderFxml {
    @FXML
    private Pane view;

    public Pane getScene(String sceneName) {
        try {
            URL sceneUrl = Main.class.getResource("/Views/" + sceneName + ".fxml");
            if (sceneUrl == null) {
                throw new java.io.FileNotFoundException("FXML file can't be found!");
            }
            view = new FXMLLoader().load(sceneUrl);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }
}
