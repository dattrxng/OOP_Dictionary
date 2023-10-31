package deedictionaryapplication.Controllers;

import deedictionaryapplication.DictionaryCommandline.Dictionary;
import deedictionaryapplication.DictionaryCommandline.DictionaryManagement;
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
import java.util.ResourceBundle;


public class SearchGui implements Initializable {
    @FXML
    private Pane headerOfExplanation;
    @FXML
    private TextField searchTerm;
    @FXML
    private Button cancelBtn;
    @FXML
    private Button saveBtn;
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
    private Dictionary dictionary = new Dictionary();
    private DictionaryManagement dictionaryManagement = new DictionaryManagement();

    ObservableList<String> list = FXCollections.observableArrayList();
    private int indexOfSelectedWord;
    private int firstIndexOfListFound = 0;

    private void setListDefault(int index) {
        list.clear();
        for (int i = index; i < index + 10; i++) list.add(dictionary.get(i).getWord_target());
        listResults.setItems(list);
        englishWord.setText(dictionary.get(index).getWord_target());
        explanation.setText(dictionary.get(index).getWord_explain());
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
            headerList.setText("Kết quả");
            listResults.setItems(list);
            firstIndexOfListFound = dictionaryManagement.searchWord(dictionary, list.get(0));
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dictionaryManagement.getConnection();
        dictionaryManagement.getAllWords(dictionary);
        System.out.println(dictionary.size());
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

    public void handleClickDeleteBtn(ActionEvent actionEvent) {
    }

    public void handleClickEditBtn(ActionEvent actionEvent) {
    }

    public void handleClickSoundBtn(ActionEvent actionEvent) {
    }

    public void handleClickSaveBtn(ActionEvent actionEvent) {
    }
}
