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

public class HistoryGui implements Initializable {
    @FXML
    private ListView listResults;
    @FXML
    private Label englishWord;
    @FXML
    private TextArea explanation;
    @FXML
    private TextField searchTerm;
    @FXML
    private Label notAvailableAlert;
    @FXML
    private Button cancelBtn;
    private final Alerts alerts = new Alerts();
    ObservableList<String> list = FXCollections.observableArrayList();
    private final Dictionary history = new Dictionary();
    private final Dictionary dictionary = new Dictionary();
    private final DictionaryManagement dictionaryManagement = new DictionaryManagement();
    private int indexOfSelectedWord;
    private int firstIndexOfListFound = 0;
    private final TextToSpeech speech = new TextToSpeech();

    private void setListDefault(int index) {
        list.clear();
        for (int i = history.size() - 1; i >= index; i--) list.add(history.get(i).getWord_target());
        listResults.setItems(list);
        englishWord.setText("");
        explanation.setText("");
    }

    private void refreshAfterDelete() {
        for (int i = 0; i < list.size(); i++)
            if (list.get(i).equals(englishWord.getText())) {
                list.remove(i);
                break;
            }
        listResults.setItems(list);
        explanation.setText("");
        englishWord.setText("");
    }

    @FXML
    private void handleOnKeyTyped() {
        list.clear();
        String searchKey = searchTerm.getText().trim();
        list = dictionaryManagement.lookupWord(history, searchKey);
        if (list.isEmpty()) {
            notAvailableAlert.setVisible(true);
            setListDefault(firstIndexOfListFound);
        } else {
            notAvailableAlert.setVisible(false);
            listResults.setItems(list);
            firstIndexOfListFound = dictionaryManagement.searchWord(history, list.get(0));
        }
    }
    public void handleClickAWord() {
        String selectedWord = (String) listResults.getSelectionModel().getSelectedItem();
        if (selectedWord != null) {
            indexOfSelectedWord = dictionaryManagement.searchWord(history, selectedWord);
            if (indexOfSelectedWord == -1) return;
            englishWord.setText(history.get(indexOfSelectedWord).getWord_target());
            explanation.setText(history.get(indexOfSelectedWord).getWord_explain());
            explanation.setVisible(true);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dictionaryManagement.getAllWordsInHistory(history);
        dictionaryManagement.setTrie(history);
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
        });
        explanation.setEditable(false);
        cancelBtn.setVisible(false);
        notAvailableAlert.setVisible(false);
    }

    public void handleClickSoundBtn() {
        if (!englishWord.getText().isEmpty()) {
            speech.speak(history.get(indexOfSelectedWord).getWord_target());
        } else {
            Alert alertWarning = alerts.alertWarning("Phát âm", "Bạn chưa chọn từ muốn phát âm!");
            Optional<ButtonType> option = alertWarning.showAndWait();
        }
    }

    public void handleClickDeleteBtn() {
        if (!englishWord.getText().isEmpty()) {
            Alert alertWarning = alerts.alertWarning("Xóa từ", "Bạn có chắc chắn muốn xóa từ này khỏi lịch sử?");
            alertWarning.getButtonTypes().add(ButtonType.CANCEL);
            Optional<ButtonType> option = alertWarning.showAndWait();
            if (option.get() == ButtonType.OK) {
                dictionaryManagement.deleteWordToHistory(history, indexOfSelectedWord);
                alerts.showAlertInfo("Xóa từ", "Xóa thành công!");
                refreshAfterDelete();
            }
        } else {
            Alert alertWarning = alerts.alertWarning("Xóa từ", "Bạn chưa chọn từ muốn xóa!");
            Optional<ButtonType> option = alertWarning.showAndWait();
        }
    }
}
