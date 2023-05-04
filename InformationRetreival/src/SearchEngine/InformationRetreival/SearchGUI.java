package SearchEngine.InformationRetreival;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;
import org.apache.lucene.document.Document;

public class SearchGUI extends JFrame {
    private JPanel mainPanel;
    private JPanel searchPanel;
    private JPanel resultsPanel;
    private JTextField searchField;
    private JComboBox<String> fields;
    private JButton searchButton , loadMoreButton;
    private JTextArea resultsArea;
    private List<Document> results;
    private int resultsIndex = 0;
    private int resultsPerPage = 10;

    public SearchGUI(){
        super("Search Engine");
        mainPanel = new JPanel(new BorderLayout());
        searchPanel = new JPanel(new FlowLayout());
        resultsPanel = new JPanel(new BorderLayout());
        searchField = new JTextField(20);
        fields = new JComboBox<String>(new String[] {"artist", "title", "album", "date", "lyrics", "year"});
        searchButton = new JButton("Search");
        loadMoreButton = new JButton("Load More");
        resultsArea = new JTextArea(100,40);

        searchPanel.add(new JLabel("Search for:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("in field"));
        searchPanel.add(fields);
        searchPanel.add(searchButton);

        resultsPanel.add(loadMoreButton, BorderLayout.CENTER);
        resultsPanel.setVisible(true);

        mainPanel.add(searchPanel, BorderLayout.CENTER);
        mainPanel.add(resultsPanel, BorderLayout.CENTER);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchTerm = searchField.getText();
                String selectedField = (String)fields.getSelectedItem();
                
            }
        });



    }











    public static void main(String[] args){
        new SearchGUI();
    }
}
