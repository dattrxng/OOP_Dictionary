package deedictionaryapplication.DictionaryCommandline;

import deedictionaryapplication.DictionaryCommandline.Dictionary;
import deedictionaryapplication.DictionaryCommandline.Trie;
import deedictionaryapplication.DictionaryCommandline.Word;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.*;
import java.util.Comparator;
import java.util.List;

public class DictionaryManagement {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/dictionary";
    private static final String USER = "root";
    private static final String PASS = "27092004";

    private static Connection CONNECTION = null;

    private Trie trie = new Trie();

    public void getConnection() {
        try {
            CONNECTION = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected to Database!!");
        } catch (SQLException e) {
            System.out.println("Connection failed!!");
            e.printStackTrace();
        }
    }

    public static String formatText(String inputText) {
        String result = inputText.replaceAll("<br\\s*/?>", System.lineSeparator());
        result = result.replaceAll("<[^>]+>", "");
        return result;
    }

    public void getAllWords(Dictionary dictionary) {
        final String SQLQuery = "SELECT * FROM dictionary";
        try (PreparedStatement ps = CONNECTION.prepareStatement(SQLQuery);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String format =formatText(rs.getString(3));
                dictionary.add(new Word(rs.getString(2), format));
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

//    public void deleteWordInSQL(Dictionary dictionary, int index) {
//        if (index >= 0 && index < dictionary.size()) {
//            try {
//
//                String deleteSQL = "DELETE FROM dictionary WHERE target = ?";
//                PreparedStatement preparedStatement = CONNECTION.prepareStatement(deleteSQL);
//                String target = dictionary.get(index).getWord_target();
//                preparedStatement.setString(1, target);
//
//                int rowsAffected = preparedStatement.executeUpdate();
//
//                if (rowsAffected > 0) {
//                    System.out.println("Word deleted successfully to MySQL.");
//                } else {
//                    System.out.println("Failed to delete the word to MySQL.");
//                }
//
//                CONNECTION.close();
//            } catch (SQLException e) {
//                System.out.println("SQL error: " + e.getMessage());
//            }
//        } else {
//            System.out.println("Invalid index.");
//        }
//    }

    public void updateWordInSQL(Dictionary dictionary, int index, String meaning) {
        if (index >= 0 && index < dictionary.size()) {
            try {

                String updateSQL = "UPDATE dictionary SET definition = ? WHERE target = ?";
                PreparedStatement preparedStatement = CONNECTION.prepareStatement(updateSQL);
                preparedStatement.setString(1, meaning);
                String target = dictionary.get(index).getWord_target();
                preparedStatement.setString(2, target);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Word updated successfully to MySQL.");
                } else {
                    System.out.println("Failed to update the word to MySQL.");
                }
            } catch (SQLException e) {
                System.out.println("SQL error: " + e.getMessage());
            }
        } else {
            System.out.println("Invalid index.");
        }
    }


    public void updateWord(Dictionary dictionary, int index, String meaning) {
        try {
            if (index >= 0 && index < dictionary.size()) {

                dictionary.get(index).setWord_explain(meaning);
                updateWordInSQL(dictionary,index,meaning);

                System.out.println("Word updated successfully.");
            } else {
                System.out.println("Invalid index.");
            }
        } catch (Exception e) {
            System.out.println("Something went wrong: " + e);
        }
    }
    public void deleteWordInSQL(Dictionary dictionary, int index) {
        if (index >= 0 && index < dictionary.size()) {
            try {
                String deleteSQL = "DELETE FROM dictionary WHERE target = ?";
                PreparedStatement preparedStatement = CONNECTION.prepareStatement(deleteSQL);

                String target = dictionary.get(index).getWord_target();
                preparedStatement.setString(1, target);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Word deleted successfully from MySQL.");
                } else {
                    System.out.println("Failed to delete the word from MySQL.");
                }

            } catch (SQLException e) {
                System.out.println("SQL error: " + e.getMessage());
            }
        } else {
            System.out.println("Invalid index.");
        }
    }

    public void deleteWord(Dictionary dictionary, int index) {
        try {
            dictionary.remove(index);
            deleteWordInSQL(dictionary, index);
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
    public void addWord(Dictionary dictionary, String target, String meaning) {
        try {
            // Thêm từ mới vào cơ sở dữ liệu
            String insertSQL = "INSERT INTO dictionary (target, definition) VALUES (?, ?)";
            PreparedStatement preparedStatement = CONNECTION.prepareStatement(insertSQL);
            preparedStatement.setString(1, target);
            preparedStatement.setString(2, meaning);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Nếu thêm từ mới thành công, thêm nó vào từ điển và cập nhật cây trie
                dictionary.add(new Word(target, meaning));
                trie.insert(target);

                System.out.println("Word added successfully to MySQL and dictionary.");
            } else {
                System.out.println("Failed to add the word to MySQL.");
            }
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
        }
    }

    public void setTimeout(Runnable runnable, int delay) {
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            } catch (Exception e) {
                System.err.println(e);
            }
        }).start();
    }

    public void exportToFile(Dictionary dictionary, String DB_URL) {
        try {
            FileWriter fileWriter = new FileWriter(DB_URL);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            for (Word word : dictionary) {
                bufferedWriter.write("|" + word.getWord_target() + "\n" + word.getWord_explain());
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (Exception e) {
            System.out.println("Something went wrong: " + e);
        }
    }


}
