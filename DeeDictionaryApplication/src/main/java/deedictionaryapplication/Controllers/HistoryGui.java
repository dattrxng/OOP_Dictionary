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
    private Alerts alerts = new Alerts();
    ObservableList<String> list = FXCollections.observableArrayList();
    private Dictionary history = new Dictionary(), dictionary = new Dictionary();
    private DictionaryManagement dictionaryManagement = new DictionaryManagement();
    private int indexOfSelectedWord;
    private int firstIndexOfListFound = 0;
    private TextToSpeech speech = new TextToSpeech();

    private void setListDefault(int index) {
        list.clear();
        for (int i = history.size() - 1; i >= index; i--) list.add(history.get(i).getWord_target());
        listResults.setItems(list);
        if (history.size() > 0) {
            englishWord.setText(history.get(index).getWord_target());
            explanation.setText(history.get(index).getWord_explain());
        } else {
            englishWord.setText(""); // Xóa văn bản nếu danh sách trống
            explanation.setText("");
        }
    }

    private void refreshAfterDelete() {
        for (int i = 0; i < history.size(); i++)
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
    public void handleClickAWord(MouseEvent mouseEvent) {
        String selectedWord = (String) listResults.getSelectionModel().getSelectedItem();
        if (selectedWord != null) {
            indexOfSelectedWord = dictionaryManagement.searchWord(history, selectedWord);
            if (indexOfSelectedWord == -1) return;
            englishWord.setText(history.get(indexOfSelectedWord).getWord_target());
            explanation.setText(history.get(indexOfSelectedWord).getWord_explain());
            explanation.setVisible(true);
        }
    }

    public void handleClickSoundBtn(ActionEvent actionEvent) {
        speech.speak(history.get(indexOfSelectedWord).getWord_target());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //dictionaryManagement.getConnection();
        dictionaryManagement.getAllWordsInHistory(history);
        dictionaryManagement.setTrie(history);
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
        notAvailableAlert.setVisible(false);
    }

    public void handleClickDeleteBtn(ActionEvent actionEvent) {
        Alert alertWarning = alerts.alertWarning("Xóa từ", "Bạn có chắc chắn muốn xóa từ này khỏi lịch sử?");
        alertWarning.getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> option = alertWarning.showAndWait();
        if (option.get() == ButtonType.OK) {
            dictionaryManagement.deleteWordHistory(history, indexOfSelectedWord);
            alerts.showAlertInfo("Xóa từ", "Xóa thành công!");
            refreshAfterDelete();
        }
    }
}
