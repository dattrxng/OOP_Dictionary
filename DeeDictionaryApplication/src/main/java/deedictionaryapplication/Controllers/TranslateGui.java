package deedictionaryapplication.Controllers;

import deedictionaryapplication.DictionaryCommandline.TextToSpeech;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
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
    private static final String ENDPOINT = "https://api.whatsmate.net/v1/translation/translate";
    private String sourceLanguage = "en";
    private String toLanguage = "vi";
    private boolean isToVietnameseLang = true;
    private final TextToSpeech speech = new TextToSpeech();
    private String output;
    private String input;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        translateBtn.setOnAction(actionEvent -> {
            try {
                handleOnClickTranslatebtn();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        sourceLangField.setOnKeyTyped(keyEvent -> {
            this.input = sourceLangField.getText().trim();
            if (sourceLangField.getText().trim().isEmpty()) translateBtn.setDisable(true);
            else translateBtn.setDisable(false);
        });
        translateBtn.setDisable(true);
        toLangField.setEditable(false);
    }

    @FXML
    private void handleOnClickTranslatebtn() throws IOException {
        this.input = sourceLangField.getText().trim();
        System.out.println(input);
        String jsonPayload = "{" +
                "\"fromLang\":\"" +
                sourceLanguage +
                "\"," +
                "\"toLang\":\"" +
                toLanguage +
                "\"," +
                "\"text\":\"" +
                sourceLangField.getText() +
                "\"" +
                "}";

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
            this.output = output;
        }
        conn.disconnect();
    }

    public void handleOnClickSwitchToggle() {
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