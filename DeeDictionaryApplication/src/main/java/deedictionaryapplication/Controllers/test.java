package deedictionaryapplication.Controllers;

import deedictionaryapplication.DictionaryCommandline.Dictionary;
import deedictionaryapplication.DictionaryCommandline.DictionaryManagement;

public class test {
    public static void main(String[] args) {
       DictionaryManagement database = new DictionaryManagement();

        database.getConnection();

        Dictionary dictionary = new Dictionary();
        database.getAllWords(dictionary);




    }
}
