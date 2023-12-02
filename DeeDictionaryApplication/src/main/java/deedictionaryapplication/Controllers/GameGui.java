package deedictionaryapplication.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class GameGui implements Initializable {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    private void openGameWindow(String title, String fxmlPath, String path) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.setTitle(title + " Game");
        stage.getIcons().add(new Image(path));
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();
    }

    public void handleHangmanBtn(ActionEvent actionEvent) throws IOException {
        openGameWindow("Hangman", "/Views/HangmanGui.fxml", "file:src/main/resources/Icons/icons8-hangman-game-64.png");
    }

    public void handleScrambleBtn(ActionEvent actionEvent) throws IOException {
        openGameWindow("Scramble", "/Views/ScrambleGui.fxml", "file:src/main/resources/Icons/icons8-scrambler-64.png");
    }
}
