package deedictionaryapplication.Controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ResourceBundle;

public class TranslateGui implements Initializable {
    @FXML
    private Label vietnameseLabel, englishLabel;
    @FXML
    private Button translateBtn;
    @FXML
    private TextArea sourceLangField, toLangField;
    private static final String CLIENT_ID = "FREE_TRIAL_ACCOUNT";
    private static final String CLIENT_SECRET = "PUBLIC_SECRET";
    private static final String ENDPOINT = "http://api.whatsmate.net/v1/translation/translate";
    private String sourceLanguage = "en";
    private String toLanguage = "vi";
    private boolean isToVietnameseLang = true;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        translateBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    handleOnClickTranslatebtn();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        sourceLangField.setOnKeyTyped(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (sourceLangField.getText().trim().isEmpty()) translateBtn.setDisable(true);
                else translateBtn.setDisable(false);
            }
        });

        translateBtn.setDisable(true);
        toLangField.setEditable(false);
    }

    @FXML
    private void handleOnClickTranslatebtn() throws IOException {
        String jsonPayload = new StringBuilder()
                .append("{")
                .append("\"fromLang\":\"")
                .append(sourceLanguage)
                .append("\",")
                .append("\"toLang\":\"")
                .append(toLanguage)
                .append("\",")
                .append("\"text\":\"")
                .append(sourceLangField.getText())
                .append("\"")
                .append("}")
                .toString();

        URL url = new URL(ENDPOINT);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("X-WM-CLIENT-ID", CLIENT_ID);
        conn.setRequestProperty("X-WM-CLIENT-SECRET", CLIENT_SECRET);
        conn.setRequestProperty("Content-Type", "application/json");

        OutputStream os = conn.getOutputStream();
        os.write(jsonPayload.getBytes());
        os.flush();
        os.close();

        int statusCode = conn.getResponseCode();
        BufferedReader br = new BufferedReader(new InputStreamReader(
                (statusCode == 200) ? conn.getInputStream() : conn.getErrorStream()
        ));
        String output;
        while ((output = br.readLine()) != null) {
            toLangField.setText(output);
        }
        conn.disconnect();
    }

    public void handleOnClickSwitchToggle(ActionEvent actionEvent) {
        sourceLangField.clear();
        toLangField.clear();
        if (!isToVietnameseLang) {
            englishLabel.setLayoutX(94);
            vietnameseLabel.setLayoutX(432);
            sourceLanguage = "en";
            toLanguage = "vi";
        } else {
            englishLabel.setLayoutX(432);
            vietnameseLabel.setLayoutX(94);
            sourceLanguage = "vi";
            toLanguage = "en";
        }
        isToVietnameseLang = !isToVietnameseLang;
    }
}