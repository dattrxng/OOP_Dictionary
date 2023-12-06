package deedictionaryapplication.Controllers;

import deedictionaryapplication.Alert.Alerts;
import deedictionaryapplication.DictionaryCommandline.DictionaryManagement;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.QuadCurve;
import javafx.scene.text.Text;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class HangmanGui {
    @FXML
    private Label score;
    @FXML
    private ImageView winIcon, loseIcon;
    @FXML
    private FlowPane keyboard;
    @FXML
    private Text realWord, winStatus, text;
    @FXML
    private Line turn1, turn3, turn4, turn51, turn52;
    @FXML
    private Circle turn2, eye1, eye2;
    @FXML
    private QuadCurve mouth;
    private int mistakes;
    private int correct;
    private String myWord;
    private List<String> myLetters;
    private List<String> answer;
    private final DictionaryManagement dictionaryManagement = new DictionaryManagement();
    private final Alerts alert = new Alerts();

    public HangmanGui() {
    }

    public void initialize() {
        Alert selectDifficulty = alert.alertConfirmation("Chọn độ dài từ", "Mức 1: từ 2-5 ký tự\nMức 2: từ 6-8 ký tự\nMức 3: từ 9-13 ký tự");
        ButtonType easy = new ButtonType("Mức 1");
        ButtonType medium = new ButtonType("Mức 2");
        ButtonType hard = new ButtonType("Mức 3");
        selectDifficulty.getButtonTypes().setAll(easy, medium, hard);
        Optional<ButtonType> result = selectDifficulty.showAndWait();

        if (result.isPresent()) {
            turn1.setVisible(false);
            turn2.setVisible(false);
            turn3.setVisible(false);
            turn4.setVisible(false);
            turn51.setVisible(false);
            turn52.setVisible(false);
            mouth.setVisible(false);
            eye1.setVisible(false);
            eye2.setVisible(false);
            winIcon.setVisible(false);
            loseIcon.setVisible(false);
            mistakes = 0;
            correct = 0;
            score.setText(String.valueOf(6));
            ArrayList<String> words = dictionaryManagement.getAllTargetWords();

            if (result.get() == easy) {
                myWord = dictionaryManagement.getRandomAlphabeticWordBySize(words, 2, 5);
            } else if (result.get() == medium) {
                myWord = dictionaryManagement.getRandomAlphabeticWordBySize(words, 6, 8);
            } else if (result.get() == hard) {
                myWord = dictionaryManagement.getRandomAlphabeticWordBySize(words, 9, 13);
            }
            System.out.println(myWord);
            myLetters = Arrays.asList(myWord.split(""));
            answer = Arrays.asList(new String[myLetters.size() * 2]);
            for (int i = 0; i < myLetters.size() * 2; i++) {
                if (i % 2 == 0) {
                    answer.set(i, "_");
                } else {
                    answer.set(i, " ");
                }
            }
            String res = String.join("", answer);
            text.setText(res);
            winStatus.setText("");
            realWord.setText("");
            keyboard.setDisable(false);
        }
    }

    public void onClick(ActionEvent event) {
        String letter = ((Button) event.getSource()).getText();
        ((Button) event.getSource()).setDisable(true);
        int[] correctCount = {correct};

        if (myLetters.contains(letter)) {
            List<Integer> indexes = getAllIndexes(myLetters, letter, correctCount);
            for (int letterIndex : indexes) {
                answer.set(letterIndex * 2, letter);
            }
            String res = String.join("", answer);
            text.setText(res);
            if (correctCount[0] == myWord.length()) {
                winStatus.setText("You Win!");
                winIcon.setVisible(true);
                keyboard.setDisable(true);
                playAudio("D:\\OOP (by Java)\\DeeDictionaryApplication\\src\\main\\resources\\Sound\\win.mp3");
            }
        } else {
            mistakes++;
            score.setText(String.valueOf(Integer.parseInt(score.getText()) - 1));
            if (mistakes == 1) turn1.setVisible(true);
            else if (mistakes == 2) turn2.setVisible(true);
            else if (mistakes == 3) turn3.setVisible(true);
            else if (mistakes == 4) {
                turn4.setVisible(true);
            } else if (mistakes == 5) {
                turn51.setVisible(true);
                turn52.setVisible(true);
            } else if (mistakes == 6) {
                mouth.setVisible(true);
                eye1.setVisible(true);
                eye2.setVisible(true);
                winStatus.setText("You Lose!");
                loseIcon.setVisible(true);
                realWord.setText(myWord);
                keyboard.setDisable(true);
                playAudio("D:\\OOP (by Java)\\DeeDictionaryApplication\\src\\main\\resources\\Sound\\lose.mp3");
            }
        }
        correct = correctCount[0];
    }

    private List<Integer> getAllIndexes(List<String> list, String value, int[] correctCount) {
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(value)) {
                indexes.add(i);
                correctCount[0]++;
            }
        }
        return indexes;
    }

    public void newGame() {
        for (int i = 0; i < 26; i++) {
            keyboard.getChildren().get(i).setDisable(false);
        }
        initialize();
    }

    private void playAudio(String audioPath) {
        Media media = new Media(Paths.get(audioPath).toUri().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnEndOfMedia(mediaPlayer::stop);
        mediaPlayer.play();
    }
}
