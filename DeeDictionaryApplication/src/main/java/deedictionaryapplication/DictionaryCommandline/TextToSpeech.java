package deedictionaryapplication.DictionaryCommandline;



import java.util.Locale;
import java.util.Scanner;

import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;

public class TextToSpeech {
    private static final String VOICES_KEY = "freetts.voices";
    private static final String VOICE_VALUE = "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory";
    private static final String CENTRAL_DATA = "com.sun.speech.freetts.jsapi.FreeTTSEngineCentral";
    private Synthesizer synthesizer;

    public TextToSpeech() {
        try {
            System.setProperty(VOICES_KEY, VOICE_VALUE);
            Central.registerEngineCentral(CENTRAL_DATA);
            synthesizer = Central.createSynthesizer(new SynthesizerModeDesc(Locale.US));
            synthesizer.allocate();
            synthesizer.resume();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void speak(String data) {
        if (synthesizer != null) {
            try {
                synthesizer.speakPlainText(data, null);
                synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void close() throws EngineException {
        if (synthesizer != null) {
            synthesizer.deallocate();
        }
    }
}
