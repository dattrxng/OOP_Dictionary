package deedictionaryapplication.Controllers;

import deedictionaryapplication.Alert.Alerts;
import deedictionaryapplication.DictionaryCommandline.Dictionary;
import deedictionaryapplication.DictionaryCommandline.DictionaryManagement;
import deedictionaryapplication.DictionaryCommandline.TextToSpeech;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class FavouriteGui implements Initializable, BaseGui {
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
    private final Alerts alerts = new Alerts();
    private final Dictionary bookmark = new Dictionary();
    private final Dictionary dictionary = new Dictionary();
    ObservableList<String> list = FXCollections.observableArrayList();
    private final DictionaryManagement dictionaryManagement = new DictionaryManagement();
    private int indexOfSelectedWord;
    private int firstIndexOfListFound = 0;
    private final TextToSpeech speech = new TextToSpeech();

    @Override
    public void setListDefault(int index) {
        list.clear();
        for (int i = bookmark.size() - 1; i >= index; i--) list.add(bookmark.get(i).getWord_target());
        listResults.setItems(list);
        englishWord.setText("");
        explanation.setText("");
    }

    @Override
    public void refreshAfterDelete() {
        for (int i = 0; i < list.size(); i++)
            if (list.get(i).equals(englishWord.getText())) {
                list.remove(i);
                break;
            }
        listResults.setItems(list);
        explanation.setText("");
        englishWord.setText("");
    }

    @Override
    @FXML
    public void handleOnKeyTyped() {
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
    @FXML
    public void handleClickAWord() {
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dictionaryManagement.getAllWordsInBookmark(bookmark);
        dictionaryManagement.setTrie(bookmark);
        setListDefault(0);

        searchTerm.setOnKeyTyped(keyEvent -> {
            if (searchTerm.getText().isEmpty()) {
                cancelBtn.setVisible(false);
                setListDefault(0);
            } else {
                cancelBtn.setVisible(true);
                handleOnKeyTyped();
            }
        });
        cancelBtn.setOnAction(event -> {
            searchTerm.clear();
            notAvailableAlert.setVisible(false);
            cancelBtn.setVisible(false);
            setListDefault(0);
        });
        explanation.setEditable(false);
        cancelBtn.setVisible(false);
        saveBtn.setVisible(false);
        notAvailableAlert.setVisible(false);
    }

    @Override
    public void handleClickSoundBtn() {
        if (!englishWord.getText().isEmpty()) {
            speech.speak(bookmark.get(indexOfSelectedWord).getWord_target());
        } else {
            Alert alertWarning = alerts.alertWarning("Phát âm", "Bạn chưa chọn từ muốn phát âm!");
            Optional<ButtonType> option = alertWarning.showAndWait();
        }
    }

    public void handleClickEditBtn() {
        if (!englishWord.getText().isEmpty()) {
            explanation.setEditable(true);
            saveBtn.setVisible(true);
        } else {
            Alert alertWarning = alerts.alertWarning("Chỉnh sửa từ", "Bạn chưa chọn từ muốn chỉnh sửa!");
            Optional<ButtonType> option = alertWarning.showAndWait();
        }
    }

    @Override
    public void handleClickDeleteBtn() {
        if (!englishWord.getText().isEmpty()) {
            Alert alertWarning = alerts.alertWarning("Xóa từ", "Bạn có chắc chắn muốn xóa từ này khỏi danh sách đã lưu?");
            alertWarning.getButtonTypes().add(ButtonType.CANCEL);
            Optional<ButtonType> option = alertWarning.showAndWait();
            if (option.get() == ButtonType.OK) {
                dictionaryManagement.deleteWordToBookmark(bookmark, indexOfSelectedWord);
                refreshAfterDelete();
                alerts.showAlertInfo("Xóa từ", "Xóa thành công!");
            }
        } else {
            Alert alertWarning = alerts.alertWarning("Xóa từ", "Bạn chưa chọn từ muốn xóa!");
            Optional<ButtonType> option = alertWarning.showAndWait();
        }
    }

    public void handleClickSaveBtn() {
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
