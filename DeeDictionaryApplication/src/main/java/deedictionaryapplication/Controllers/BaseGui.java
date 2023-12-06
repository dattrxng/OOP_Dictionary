package deedictionaryapplication.Controllers;

public interface BaseGui {
    void setListDefault(int index);

    void refreshAfterDelete();

    void handleOnKeyTyped();

    void handleClickAWord();

    void handleClickSoundBtn();

    void handleClickDeleteBtn();

}
