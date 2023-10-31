package deedictionaryapplication.DictionaryCommandline;

import deedictionaryapplication.DictionaryCommandline.Dictionary;
import deedictionaryapplication.DictionaryCommandline.Trie;
import deedictionaryapplication.DictionaryCommandline.Word;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.Comparator;
import java.util.List;

public class DictionaryManagement {
    private static final String DB_URL = "jdbc:mysql://localhost:3307/deedictionary";
    private static final String USER = "root";
    private static final String PASS = "tenladuc";

    private static Connection CONNECTION = null;

    private Trie trie = new Trie();

    public void getConnection() {
        try {
            CONNECTION = DriverManager.getConnection(DB_URL, USER, PASS); // Gán kết nối cho biến CONNECTION
            System.out.println("Connected to Database!!");
        } catch (SQLException e) {
            System.out.println("Connection failed!!");
            e.printStackTrace();
        }
    }

    public void getAllWords(Dictionary dictionary) {
        final String SQLQuery = "SELECT * FROM dictionary";
        try (PreparedStatement ps = CONNECTION.prepareStatement(SQLQuery);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                dictionary.add(new Word(rs.getString(2), rs.getString(3)));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int searchWord(Dictionary dictionary, String keyWord) {
        try {
            dictionary.sort(new Comparator<Word>() {
                @Override
                public int compare(Word o1, Word o2) {
                    return o1.getWord_target().compareTo(o2.getWord_target());
                }
            });
            int left = 0;
            int right = dictionary.size() - 1;
            while (left <= right) {
                int mid = left + (right - left) / 2;
                int res = dictionary.get(mid).getWord_target().compareTo(keyWord);
                if (res == 0) return mid;
                if (res <= 0) left = mid + 1;
                else right = mid - 1;
            }
        } catch (NullPointerException e) {
            System.out.println("Null Exception.");
        }
        return -1;
    }

    public ObservableList<String> lookupWord(Dictionary dictionary, String key) {
        ObservableList<String> list = FXCollections.observableArrayList();
        try {
            List<String> results = trie.autoComplete(key);
            if (results != null) {
                int length = Math.min(results.size(), 15);
                for (int i = 0; i < length; i++) list.add(results.get(i));
            }
        } catch (Exception e) {
            System.out.println("Something went wrong: " + e);
        }
        return list;
    }

    public void updateWord(Dictionary dictionary, int index, String meaning) {
        try {
            if (index >= 0 && index < dictionary.size()) {

                dictionary.get(index).setWord_explain(meaning);

                System.out.println("Word updated successfully.");
            } else {
                System.out.println("Invalid index.");
            }
        } catch (Exception e) {
            System.out.println("Something went wrong: " + e);
        }
    }

    public void deleteWord(Dictionary dictionary, int index, String path) {
        try {
            dictionary.remove(index);
            trie = new Trie();
            setTrie(dictionary);
        } catch (NullPointerException e) {
            System.out.println("Null Exception.");
        }
    }

    public void setTrie(Dictionary dictionary) {
        for (Word word : dictionary) {
            trie.insert(word.getWord_target());
        }

    }

}
