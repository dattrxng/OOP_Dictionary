package deedictionaryapplication.DictionaryCommandline;

import java.util.Objects;

public class Word {
    private String word_target;
    private String word_explain;

    public Word(String word_target, String word_explain) {
        this.word_target = word_target;
        this.word_explain = word_explain;
    }

    public Word() {
        this("", "");
    }

    public String getWord_explain() {
        return word_explain;
    }

    public String getWord_target() {
        return word_target;
    }

    public void setWord_explain(String word_explain) {
        this.word_explain = word_explain;
    }

    public void setWord_target(String word_target) {
        this.word_target = word_target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Word word = (Word) o;
        return Objects.equals(word_target, word.word_target) && Objects.equals(word_explain, word.word_explain);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word_target, word_explain);
    }

    @Override
    public String toString() {
        return "Word[" + "word_target=" + word_target + ", word_explain=" + word_explain + ']';
    }
}
