package deedictionaryapplication.Controllers;

import deedictionaryapplication.DictionaryCommandline.DictionaryManagement;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.util.ArrayList;
import java.util.Random;

public class ScrambleGui {
    @FXML
    private Button letter1, letter2, letter3, letter4;
    @FXML
    private Label wordLabel, falseAlert;
    @FXML
    private TextArea keyList;
    private final DictionaryManagement dictionaryManagement = new DictionaryManagement();
    ArrayList<String> words = dictionaryManagement.getAllTargetWords();

    public void initialize() {
        setLetters();
        falseAlert.setVisible(false);
    }

    public void setLetters() {
        Random random = new Random();
        letter1.setText(String.valueOf((char) ('A' + random.nextInt(7))));
        letter2.setText(String.valueOf((char) ('H' + random.nextInt(7))));
        letter3.setText(String.valueOf((char) ('O' + random.nextInt(7))));
        letter4.setText(String.valueOf((char) ('V' + random.nextInt(5))));
    }

    public void handleLetterButtonClicked(ActionEvent actionEvent) {
        String letter = ((Button) actionEvent.getSource()).getText();
        wordLabel.setText(wordLabel.getText() + letter);
    }

    public void handleConfirmButtonClicked() {
        if (words.contains(wordLabel.getText().toLowerCase())) {
            keyList.setText(keyList.getText() + "\n" + wordLabel.getText());
            System.out.println(wordLabel.getText().toLowerCase());
        } else {
            falseAlert.setVisible(true);
        }
    }
    public void handleClearBtn() {
        wordLabel.setText("");
        falseAlert.setVisible(false);
    }
}
