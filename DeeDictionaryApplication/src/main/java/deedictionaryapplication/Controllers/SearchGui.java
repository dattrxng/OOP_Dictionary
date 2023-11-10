package deedictionaryapplication.Controllers;

import deedictionaryapplication.Alert.Alerts;
import deedictionaryapplication.DictionaryCommandline.Dictionary;
import deedictionaryapplication.DictionaryCommandline.DictionaryManagement;
import deedictionaryapplication.DictionaryCommandline.TextToSpeech;
import deedictionaryapplication.DictionaryCommandline.Word;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;


public class SearchGui implements Initializable {
    @FXML
    private Pane headerOfExplanation;
    @FXML
    private TextField searchTerm;
    @FXML
    private Button cancelBtn, saveBtn;
    @FXML
    private Label notAvailableAlert;
    @FXML
    private TextArea explanation;
    @FXML
    private Label englishWord;
    @FXML
    private Label headerList;
    @FXML
    private ListView<String> listResults;
    private Alerts alerts = new Alerts();
    private Dictionary dictionary = new Dictionary(), bookmark = new Dictionary(), history = new Dictionary();
    private DictionaryManagement dictionaryManagement = new DictionaryManagement();
    private TextToSpeech speech = new TextToSpeech();

    ObservableList<String> list = FXCollections.observableArrayList();
    private int indexOfSelectedWord;
    private int firstIndexOfListFound = 0;

    private void setListDefault(int index) {
        list.clear();
        for (int i = index; i < index + 20; i++) list.add(dictionary.get(i).getWord_target());
        listResults.setItems(list);
        englishWord.setText(dictionary.get(index).getWord_target());
        explanation.setText(dictionary.get(index).getWord_explain());
    }

    private void refreshAfterDelete() {
        for (int i = 0; i < list.size(); i++)
            if (list.get(i).equals(englishWord.getText())) {
                list.remove(i);
                break;
            }
        listResults.setItems(list);
        headerOfExplanation.setVisible(false);
        explanation.setVisible(false);
        explanation.setText("");
        englishWord.setText("");
    }

    @FXML
    private void handleOnKeyTyped() {
        list.clear();
        String searchKey = searchTerm.getText().trim();
        list = dictionaryManagement.lookupWord(dictionary, searchKey);
        if (list.isEmpty()) {
            notAvailableAlert.setVisible(true);
            setListDefault(firstIndexOfListFound);
        } else {
            notAvailableAlert.setVisible(false);
            headerList.setText("Kết quả liên quan");
            listResults.setItems(list);
            firstIndexOfListFound = dictionaryManagement.searchWord(dictionary, list.get(0));
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dictionaryManagement.getAllWords(dictionary);
        dictionaryManagement.getAllWordsInBookmark(bookmark);
        dictionaryManagement.getAllWordsInHistory(history);
        dictionaryManagement.setTrie(dictionary);
        setListDefault(0);

        searchTerm.setOnKeyTyped(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (searchTerm.getText().isEmpty()) {
                    cancelBtn.setVisible(false);
                    setListDefault(0);
                } else {
                    cancelBtn.setVisible(true);
                    handleOnKeyTyped();
                }
            }
        });
        cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                searchTerm.clear();
                notAvailableAlert.setVisible(false);
                cancelBtn.setVisible(false);
                setListDefault(0);
            }
        });

        explanation.setEditable(false);
        saveBtn.setVisible(false);
        cancelBtn.setVisible(false);
        notAvailableAlert.setVisible(false);
    }

    @FXML
    private void handleClickAWord(MouseEvent arg0) {
        String selectedWord = listResults.getSelectionModel().getSelectedItem();
        if (selectedWord != null) {
            indexOfSelectedWord = dictionaryManagement.searchWord(dictionary, selectedWord);
            if (indexOfSelectedWord == -1) return;
            englishWord.setText(dictionary.get(indexOfSelectedWord).getWord_target());
            explanation.setText(dictionary.get(indexOfSelectedWord).getWord_explain());
            int indexOfWord = dictionaryManagement.searchWord(history, englishWord.getText());
            if (indexOfWord >= 0) {
                dictionaryManagement.deleteWordHistory(history, indexOfWord);
            }
            dictionaryManagement.addWordInHistory(history, englishWord.getText(), explanation.getText());
            headerOfExplanation.setVisible(true);
            explanation.setVisible(true);
            explanation.setEditable(false);
            saveBtn.setVisible(false);
        }
    }

    public void handleClickSoundBtn(ActionEvent actionEvent) {
        speech.speak(dictionary.get(indexOfSelectedWord).getWord_target());
    }

    public void handleClickDeleteBtn(ActionEvent actionEvent) {
        Alert alertWarning = alerts.alertWarning("Xóa từ", "Bạn có chắc chắn muốn xóa từ này?");
        alertWarning.getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> option = alertWarning.showAndWait();
        if (option.get() == ButtonType.OK) {
            dictionaryManagement.deleteWord(dictionary, indexOfSelectedWord);
            dictionaryManagement.deleteWordFavourite(bookmark, indexOfSelectedWord);
            refreshAfterDelete();
            alerts.showAlertInfo("Xóa từ", "Xóa thành công!");
        }
    }

    public void handleClickEditBtn(ActionEvent actionEvent) {
        explanation.setEditable(true);
        saveBtn.setVisible(true);
    }

    public void handleClickSaveBtn(ActionEvent actionEvent) {
        Alert alertConfirmation = alerts.alertConfirmation("Cập nhật", "Bạn có chắc chắn muốn cập nhật nghĩa của từ này?");
        Optional<ButtonType> option = alertConfirmation.showAndWait();
        if (option.get() == ButtonType.OK) {
            dictionaryManagement.updateWord(dictionary, indexOfSelectedWord, explanation.getText());
            alerts.showAlertInfo("Cập nhật", "Cập nhập thành công!");
        }
        saveBtn.setVisible(false);
        explanation.setEditable(false);
    }

    public void handleClickFavouriteBtn(ActionEvent actionEvent) {
        Alert alertConfirmation = alerts.alertConfirmation("Đã lưu", "Bạn có chắc chắn muốn lưu từ này?");
        Optional<ButtonType> option = alertConfirmation.showAndWait();
        String newEnglishWord = englishWord.getText();
        String newMeaning = explanation.getText();
        int indexOfWord = dictionaryManagement.searchWord(bookmark, newEnglishWord);
        if (indexOfWord >= 0) {
            // Từ đã tồn tại trong danh sách yêu thích
            Alert alertWarning = alerts.alertWarning("Đã lưu", "Từ này đã được lưu trước đó!");
            Optional<ButtonType> option1 = alertWarning.showAndWait();
        } else {
            // Từ chưa tồn tại trong danh sách yêu thích
            Word word = new Word(newEnglishWord, newMeaning);
            dictionaryManagement.addWordInBookmark(bookmark, newEnglishWord, newMeaning);
            alerts.showAlertInfo("Đã lưu", "Từ đã được lưu!");
        }
    }

}