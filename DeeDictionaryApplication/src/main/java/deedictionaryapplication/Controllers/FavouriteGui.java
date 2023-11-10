package deedictionaryapplication.Controllers;

import deedictionaryapplication.Alert.Alerts;
import deedictionaryapplication.DictionaryCommandline.Dictionary;
import deedictionaryapplication.DictionaryCommandline.DictionaryManagement;
import deedictionaryapplication.DictionaryCommandline.TextToSpeech;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class FavouriteGui implements Initializable {
    @FXML
    private ListView listResults;
    @FXML
    private Label englishWord;
    @FXML
    private Button cancelBtn, saveBtn;
    @FXML
    private TextArea explanation;
    @FXML
    private TextField searchTerm;
    @FXML
    private Label notAvailableAlert;
    private Alerts alerts = new Alerts();
    private Dictionary bookmark = new Dictionary(), dictionary = new Dictionary();
    ObservableList<String> list = FXCollections.observableArrayList();
    private DictionaryManagement dictionaryManagement = new DictionaryManagement();
    private int indexOfSelectedWord;
    private int firstIndexOfListFound = 0;
    private TextToSpeech speech = new TextToSpeech();

    private void setListDefault(int index) {
        list.clear();
        for (int i = bookmark.size() - 1; i >= index; i--) list.add(bookmark.get(i).getWord_target());
        listResults.setItems(list);
        if (bookmark.size() > 0) {
            englishWord.setText(bookmark.get(index).getWord_target());
            explanation.setText(bookmark.get(index).getWord_explain());
        } else {
            englishWord.setText(""); // Xóa văn bản nếu danh sách trống
            explanation.setText("");
        }
    }

    private void refreshAfterDelete() {
        for (int i = 0; i < list.size(); i++)
            if (list.get(i).equals(englishWord.getText())) {
                list.remove(i);
                break;
            }
        listResults.setItems(list);
        setListDefault(0);
        explanation.setText("");
        englishWord.setText("");
    }

    @FXML
    private void handleOnKeyTyped() {
        list.clear();
        String searchKey = searchTerm.getText().trim();
        list = dictionaryManagement.lookupWord(bookmark, searchKey);
        if (list.isEmpty()) {
            notAvailableAlert.setVisible(true);
            setListDefault(firstIndexOfListFound);
        } else {
            notAvailableAlert.setVisible(false);
            listResults.setItems(list);
            firstIndexOfListFound = dictionaryManagement.searchWord(bookmark, list.get(0));
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //dictionaryManagement.getConnection();
        dictionaryManagement.getAllWordsInBookmark(bookmark);
        dictionaryManagement.setTrie(bookmark);
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
                //setListDefault(0);
            }
        });
        explanation.setEditable(false);
        cancelBtn.setVisible(false);
        saveBtn.setVisible(false);
        notAvailableAlert.setVisible(false);
    }

    @FXML
    private void handleClickAWord(MouseEvent arg0) {
        String selectedWord = (String) listResults.getSelectionModel().getSelectedItem();
        if (selectedWord != null) {
            indexOfSelectedWord = dictionaryManagement.searchWord(bookmark, selectedWord);
            if (indexOfSelectedWord == -1) return;
            englishWord.setText(bookmark.get(indexOfSelectedWord).getWord_target());
            explanation.setText(bookmark.get(indexOfSelectedWord).getWord_explain());
            explanation.setVisible(true);
            explanation.setEditable(false);
            saveBtn.setVisible(false);
        }
    }

    public void handleClickSoundBtn(ActionEvent actionEvent) {
        speech.speak(bookmark.get(indexOfSelectedWord).getWord_target());
    }

    public void handleClickEditBtn(ActionEvent actionEvent) {
        explanation.setEditable(true);
        saveBtn.setVisible(true);
    }

    public void handleClickFavouriteBtn(ActionEvent actionEvent) {
        Alert alertWarning = alerts.alertWarning("Xóa từ", "Bạn có chắc chắn muốn xóa từ này khỏi danh sách đã lưu?");
        alertWarning.getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> option = alertWarning.showAndWait();
        if (option.get() == ButtonType.OK) {
            dictionaryManagement.deleteWordFavourite(bookmark, indexOfSelectedWord);
            refreshAfterDelete();
            alerts.showAlertInfo("Xóa từ", "Xóa thành công!");
        }
    }

    public void handleClickSaveBtn(ActionEvent actionEvent) {
        Alert alertConfirmation = alerts.alertConfirmation("Cập nhật", "Bạn có chắc chắn muốn cập nhật nghĩa của từ này?");
        Optional<ButtonType> option = alertConfirmation.showAndWait();
        if (option.get() == ButtonType.OK) {
            dictionaryManagement.updateWord(bookmark, indexOfSelectedWord, explanation.getText());
            dictionaryManagement.updateWord(dictionary, indexOfSelectedWord, explanation.getText());
            alerts.showAlertInfo("Cập nhật", "Cập nhập thành công!");
        }
        saveBtn.setVisible(false);
        explanation.setEditable(false);
    }
}
