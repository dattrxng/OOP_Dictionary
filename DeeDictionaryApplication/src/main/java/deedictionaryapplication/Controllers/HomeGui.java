package deedictionaryapplication.Controllers;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeGui implements Initializable {
    @FXML
    private Scene scene;
    @FXML
    private BorderPane mainPane;
    @FXML
    private VBox vbox;
    @FXML
    private Button BtnBurger;
    @FXML
    boolean vboxVisible = true;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    @FXML
    public void handleBtnSearch(ActionEvent actionEvent) throws IOException {
        LoaderFxml loaderFxml = new LoaderFxml();
        Pane view = loaderFxml.getScene("SearchGui");
        mainPane.setCenter(view);
    }

    public void handleBtnHome(ActionEvent actionEvent) {
        LoaderFxml loaderFxml = new LoaderFxml();
        Pane view = loaderFxml.getScene("HomeGui");
        mainPane.setLeft(null);
        mainPane.setTop(null);
        mainPane.setCenter(view);
    }

    public void handleBtnAdd(ActionEvent actionEvent) {
        LoaderFxml loaderFxml = new LoaderFxml();
        Pane view = loaderFxml.getScene("AddGui");
        mainPane.setCenter(view);
    }

    public void handleBtnTranslate(ActionEvent actionEvent) {
        LoaderFxml loaderFxml = new LoaderFxml();
        Pane view = loaderFxml.getScene("TranslateGui");
        mainPane.setCenter(view);
    }

    public void handleBtnFavourite(ActionEvent actionEvent) {
        LoaderFxml loaderFxml = new LoaderFxml();
        Pane view = loaderFxml.getScene("FavouriteGui");
        mainPane.setCenter(view);
    }

    public void handleBtnGame(ActionEvent actionEvent) {
    }

    public void handleBtnBurger(ActionEvent actionEvent) {
        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.seconds(0.5));
        slide.setNode(vbox);
        slide.setInterpolator(Interpolator.EASE_BOTH); // Thêm dòng này

        if (vboxVisible) {
            // Thụt vào (slide in)
            slide.setToX(-70);
            slide.play();
            vbox.setTranslateX(-70);
            slide.setOnFinished(e ->  {
                vbox.setVisible(false);
                vboxVisible = false;
            });
        } else {
            // Thụt ra (slide out)
            vbox.setVisible(true);

            vbox.setTranslateX(-70);
            slide.setToX(0);
            slide.play();
            slide.setOnFinished(e -> {
                vboxVisible = true;

            });
        }
    }


}
