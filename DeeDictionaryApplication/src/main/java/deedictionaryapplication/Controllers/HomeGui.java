package deedictionaryapplication.Controllers;

import deedictionaryapplication.DictionaryCommandline.Dictionary;
import deedictionaryapplication.DictionaryCommandline.DictionaryManagement;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import javafx.stage.Stage;
import javafx.util.Duration;


import java.net.URL;
import java.util.ResourceBundle;

public class HomeGui implements Initializable {
    @FXML
    private Label topPane;
    @FXML
    private Button minimizeBtn, closeBtn, BtnBurger;
    @FXML
    private ToggleButton SearchMenu, AddMenu, TranslateMenu, FavouriteMenu, HistoryMenu, GameMenu;
    @FXML
    private Tooltip tooltip1, tooltip2, tooltip3, tooltip4, tooltip5, tooltip6, tooltip7;
    @FXML
    private BorderPane mainPane;
    @FXML
    private VBox vbox;
    @FXML
    boolean vboxVisible = true;
    private DictionaryManagement dictionaryManagement = new DictionaryManagement();
    private Dictionary dictionary, bookmark, history = new Dictionary();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Tooltip[] tooltips = new Tooltip[]{tooltip1, tooltip2, tooltip3, tooltip4, tooltip5, tooltip6, tooltip7};
        for (Tooltip tooltip : tooltips) {
            tooltip.setShowDelay(Duration.seconds(0.5));
        }
        vbox.setVisible(false);
        BtnBurger.setVisible(false);
        topPane.setVisible(false);

        dictionaryManagement.getConnection();
    }

    public void handleBtnHome(ActionEvent actionEvent) {
        LoaderFxml loaderFxml = new LoaderFxml();
        Pane view = loaderFxml.getScene("HomeGui");
        mainPane.setLeft(null);
        mainPane.setTop(null);
        mainPane.setCenter(view);
        BtnBurger.setVisible(false);
        vbox.setVisible(false);
    }

    public void addActive(ToggleButton[] toggleButton, ToggleButton display) {
        for (ToggleButton btn : toggleButton) {
            btn.getStyleClass().remove("active");
        }
        display.getStyleClass().add("active");
    }

    private void handleMenuButtonClick(Pane view, ToggleButton menuButton) {
        mainPane.setCenter(view);
        vbox.setVisible(true);
        BtnBurger.setVisible(true);
        topPane.setVisible(true);
        addActive(new ToggleButton[]{SearchMenu, AddMenu, TranslateMenu, FavouriteMenu, HistoryMenu, GameMenu}, menuButton);
    }

    @FXML
    public void handleBtnSearch(ActionEvent actionEvent) {
        LoaderFxml loaderFxml = new LoaderFxml();
        Pane view = loaderFxml.getScene("SearchGui");
        handleMenuButtonClick(view, SearchMenu);
    }

    @FXML
    public void handleBtnAdd(ActionEvent actionEvent) {
        LoaderFxml loaderFxml = new LoaderFxml();
        Pane view = loaderFxml.getScene("AddGui");
        handleMenuButtonClick(view, AddMenu);
    }

    @FXML
    public void handleBtnTranslate(ActionEvent actionEvent) {
        LoaderFxml loaderFxml = new LoaderFxml();
        Pane view = loaderFxml.getScene("TranslateGui");
        handleMenuButtonClick(view, TranslateMenu);
    }

    @FXML
    public void handleBtnFavourite(ActionEvent actionEvent) {
        LoaderFxml loaderFxml = new LoaderFxml();
        Pane view = loaderFxml.getScene("FavouriteGui");
        handleMenuButtonClick(view, FavouriteMenu);
    }

    @FXML
    public void handleBtnHistory(ActionEvent actionEvent) {
        LoaderFxml loaderFxml = new LoaderFxml();
        Pane view = loaderFxml.getScene("HistoryGui");
        handleMenuButtonClick(view, HistoryMenu);
    }

    @FXML
    public void handleBtnGame(ActionEvent actionEvent) {
        LoaderFxml loaderFxml = new LoaderFxml();
        Pane view = loaderFxml.getScene("GameGui");
        handleMenuButtonClick(view, GameMenu);
    }

    @FXML
    public void handleBtnBurger(ActionEvent actionEvent) {
        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.seconds(0.5));
        slide.setNode(vbox);
        slide.setInterpolator(Interpolator.EASE_BOTH);

        if (vboxVisible) {
            // Thụt vào (slide in)
            slide.setToX(-70);
            slide.play();
            vbox.setTranslateX(-70);
            slide.setOnFinished(e -> {
                vbox.setVisible(false);
                vboxVisible = false;
            });
        } else {
            // Thò ra (slide out)
            vbox.setVisible(true);
            vbox.setTranslateX(-70);
            slide.setToX(0);
            slide.play();
            slide.setOnFinished(e -> {
                vboxVisible = true;
            });
        }
    }

    @FXML
    public void handleCloseBtn(ActionEvent actionEvent) {
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleMinimizeBtn(ActionEvent actionEvent) {
        Stage stage = (Stage) minimizeBtn.getScene().getWindow();
        stage.setIconified(true);
    }

}
