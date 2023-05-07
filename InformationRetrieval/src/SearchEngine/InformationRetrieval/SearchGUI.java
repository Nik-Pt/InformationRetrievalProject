package SearchEngine.InformationRetrieval;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;

/*  SearchGUI class is responsible for creating the UI and getting the users input and selection of fields he wants for the search.
    The user enters the term he wants to search for in the .csv files and then the system after creating a SearchIndex object uses the search method to begin the search.
    Then after the search is complete the results are saved and displayed in the resultsArea 10 at a time along with a string in which the term appeared in the .csv files
    To make obvious where in this string the term was found I used a highlighter that specifically highlights the term.
    By pressing the Load More button the user can view 10 more results each time.
    Finally by pressing the Search History button the user can see previous searches that have been made(They are not available after closing the program).

    Note : I gave the user only the option of searching in these fields {"artist", "title", "album", "date", "lyrics", "year"} separately only because if the user enters for example "eminem 2016"
           then the program will report an error with 2016 in this line "int snippetStart = field.toLowerCase().indexOf(searchTerm) - 30;".
           If I fix it then the user will be able to search in any field but until then I will keep it as it is.

    Issues: 1)If the user enters a phrase that does not appear in any of the .csv files but the words appear separately then nothing will be highlighted.
            2)If the user enters a word,phrase using characters like [,],{,\,},?...ect then the program will report an error.
*/

public class SearchGUI extends JFrame {
    private JPanel mainPanel;
    private JPanel searchPanel;
    private JPanel resultsPanel;
    private JTextField searchField;
    private JComboBox<String> fields;
    private JButton searchButton , loadMoreButton,historyButton;
    private JTextArea resultsArea;
    private JScrollPane scrollPane;
    private List<Document> results;
    private int resultsIndex = 0;
    private int resultsPerPage = 10;
    private List<String> searchHistory = new ArrayList<>();

    public SearchGUI() {
        super("Search Engine");
        mainPanel = new JPanel(new BorderLayout());
        searchPanel = new JPanel(new FlowLayout());
        resultsPanel = new JPanel(new BorderLayout());
        searchField = new JTextField(20);
        fields = new JComboBox<String>(new String[]{"artist", "title", "album", "date", "lyrics", "year"});
        searchButton = new JButton("Search");
        loadMoreButton = new JButton("Load More");
        historyButton = new JButton("Search History");
        resultsArea = new JTextArea(35, 20);
        scrollPane = new JScrollPane(resultsArea);

        searchPanel.add(new JLabel("Search for:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("in field"));
        searchPanel.add(fields);
        searchPanel.add(searchButton);
        searchPanel.add(historyButton);

        resultsPanel.add(scrollPane, BorderLayout.CENTER);
        resultsPanel.add(loadMoreButton, BorderLayout.SOUTH);
        resultsPanel.setVisible(true);

        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(resultsPanel, BorderLayout.CENTER);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchTerm = searchField.getText();
                String selectedField = (String) fields.getSelectedItem();
                String[] fields = selectedField.split(",");
                try {
                    SearchIndex searcher = new SearchIndex();
                    results = searcher.search(searchTerm, fields);
                    resultsIndex = 0;
                    displayResults();
                    searchHistory.add(searchTerm);
                } catch (IOException | ParseException exception) {
                    exception.printStackTrace();
                }
            }
        });

        loadMoreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resultsIndex += resultsPerPage;
                displayResults();
            }
        });

        historyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resultsArea.setText("");
                resultsArea.append("Search History:\n\n");
                for (String searchTerm : searchHistory){
                    resultsArea.append(searchTerm + "\n");
                }
            }
        });

        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void displayResults(){
        resultsArea.setText("");
        Highlighter highlighter = resultsArea.getHighlighter();
        for(int i = resultsIndex; i < Math.min(resultsIndex + resultsPerPage , results.size()); i++) {
            Document doc = results.get(i);
            String selectedField = (String) fields.getSelectedItem();
            String field = doc.get(selectedField);
            String searchTerm = searchField.getText().toLowerCase();
            int snippetStart = field.toLowerCase().indexOf(searchTerm) - 30;
            if (snippetStart < 0) {
                snippetStart = 0;
            }
            int snippetEnd = snippetStart + 50;
            if (snippetEnd > field.length()) {
                snippetEnd = field.length();
            }
            String snippet = field.substring(snippetStart, snippetEnd);
            resultsArea.append(doc.get("title") + " - " + doc.get("artist") + " - " + doc.get("year") + " - " + doc.get("date") +"\n" + "  ..." + snippet + "..." + "\n\n");
            String text = resultsArea.getText();
            int index = 0;
            while (index >= 0) {
                index = text.toLowerCase().indexOf(searchTerm, index);
                if (index >= 0) {
                    try {
                        highlighter.addHighlight(index, index + searchTerm.length(), DefaultHighlighter.DefaultPainter);
                        index += searchTerm.length();
                    } catch (BadLocationException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        if(resultsIndex + resultsPerPage < results.size()){
            loadMoreButton.setEnabled(true);
            resultsPanel.setVisible(true);
        }else{
            loadMoreButton.setEnabled(false);
            resultsPanel.setVisible(true);
        }
    }

    public static void main(String[] args){
        new SearchGUI();
    }
}
