package deedictionaryapplication.Controllers;

import deedictionaryapplication.DictionaryCommandline.DictionaryManagement;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.nio.file.Paths;
import java.util.ArrayList;

public class ScrambleGui {
    @FXML
    private ImageView winImg, loseImg;
    @FXML
    private Button next, submit, cancel;
    @FXML
    private Pane pane;
    @FXML
    private TextField answer;
    @FXML
    private Label notice, round, successAlert;
    private String randomWord;
    private final DictionaryManagement dictionaryManagement = new DictionaryManagement();
    private final ArrayList<String> words = dictionaryManagement.getAllTargetWords();
    private int i = 1;
    private static final int maxLevel = 15;

    public void initialize() {
        pane.setVisible(true);
        submit.setVisible(true);
        winImg.setVisible(false);
        loseImg.setVisible(false);
        answer.setEditable(true);
        successAlert.setVisible(false);
        next.setVisible(false);
        next.setText("NEXT");
        notice.setVisible(true);
        notice.setLayoutX(155);
        notice.setLayoutY(150);
        next.setLayoutX(555);
        next.setLayoutY(244);
        answer.setText("");

        if (i == maxLevel) {
            showEnd();
        }
        round.setText("ROUND  " + i);
        randomWord = dictionaryManagement.getRandomAlphabeticWordBySize(words, i, i);
        String sufferWord = dictionaryManagement.shuffleWord(randomWord);
        notice.setText("Guess the word: " + sufferWord);

        answer.setOnKeyTyped(event -> cancel.setVisible(!answer.getText().isEmpty()));

        cancel.setOnAction(event -> {
            answer.clear();
            cancel.setVisible(false);
        });

        System.out.println(randomWord);
    }

    private void showCorrect() {
        i++;
        notice.setVisible(false);
        submit.setVisible(false);
        answer.setEditable(false);
        successAlert.setVisible(true);
        playAudio("C:\\Users\\ACER\\Documents\\GitHub\\BTL\\DeeDictionaryApplication\\src\\main\\resources\\Sound\\correct.mp3");
    }

    private void showIncorrect() {
        i = 1;
        pane.setVisible(false);
        submit.setVisible(false);
        next.setText("REPLAY");
        notice.setVisible(true);
        notice.setText("The correct answer is: " + randomWord);
        notice.setLayoutX(180);
        notice.setLayoutY(300);
        next.setLayoutX(350);
        next.setLayoutY(350);
        loseImg.setVisible(true);
        playAudio("C:\\Users\\ACER\\Documents\\GitHub\\BTL\\DeeDictionaryApplication\\src\\main\\resources\\Sound\\lose.mp3");
    }

    private void showEnd() {
        pane.setVisible(false);
        submit.setVisible(false);
        notice.setVisible(false);
        next.setVisible(true);
        next.setText("REPLAY");
        next.setLayoutX(350);
        next.setLayoutY(350);
        winImg.setVisible(true);
        playAudio("C:\\Users\\ACER\\Documents\\GitHub\\BTL\\DeeDictionaryApplication\\src\\main\\resources\\Sound\\win.mp3");
        i = 1;
    }

    public void handleSubmitBtn() {
        String guessedWord = answer.getText();
        next.setVisible(true);

        if (guessedWord.equals(randomWord.toLowerCase()) || guessedWord.equals(randomWord)) {
            showCorrect();
        } else {
            showIncorrect();
        }
    }

    public void handleNextBtn() {
        initialize();
    }

    private void playAudio(String audioPath) {
        Media media = new Media(Paths.get(audioPath).toUri().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnEndOfMedia(mediaPlayer::stop);
        mediaPlayer.play();
    }
}

