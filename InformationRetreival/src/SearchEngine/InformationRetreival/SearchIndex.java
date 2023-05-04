package SearchEngine.InformationRetreival;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.*;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class SearchIndex {

    public List<Document> search(String query, String[]fields) throws IOException, ParseException {

        // Creating the index
        String indexPath = "index";
        Directory indexDir = FSDirectory.open(Paths.get(indexPath));
        CharArraySet stopWords = EnglishAnalyzer.getDefaultStopSet();
        StandardAnalyzer analyzer = new StandardAnalyzer(stopWords);
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        boolean isIndexExists = DirectoryReader.indexExists(indexDir);

        if (isIndexExists) {
            System.out.println("Index directory already exists, rebuilding index...");
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        } else {
            System.out.println("Creating new index...");
        }
        IndexWriter writer = new IndexWriter(indexDir, config);

        // Reading data from CSV and adding to the index
        ReadCSV csvReader = new ReadCSV();
        ArrayList<Song> songs = csvReader.toSongList();
        for (Song song : songs) {
            Document doc = new Document();
            doc.add(new TextField("artist", song.getArtist(), Field.Store.YES));
            doc.add(new TextField("title", song.getTitle(), Field.Store.YES));
            doc.add(new TextField("album", song.getAlbum(), Field.Store.YES));
            doc.add(new TextField("date", song.getDate(), Field.Store.YES));
            doc.add(new TextField("lyrics", song.getLyrics(), Field.Store.YES));
            doc.add(new TextField("year", song.getYear(), Field.Store.YES));
            writer.addDocument(doc);
        }
        writer.close();

        // Searching the index
        DirectoryReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
        IndexSearcher searcher = new IndexSearcher(reader);

        // Querying the index
        //String[] fields = {"artist", "title", "album", "date", "lyrics", "year"};
            /*Scanner input = new Scanner(System.in);
            System.out.print("Enter search query: ");
            String queryString = input.nextLine();
            System.out.print("Enter field to search in (artist, title, album, date, lyrics, year): ");
            String field = input.nextLine().toLowerCase();
            while (!field.equals("artist") && !field.equals("title") && !field.equals("album") && !field.equals("date") && !field.equals("lyrics") && !field.equals("year")) {
                System.out.print("Invalid field. Enter a valid field: ");
                field = input.nextLine().toLowerCase();
            }*/

        QueryParser parser = new MultiFieldQueryParser(fields, analyzer);
        Query searchQuery = parser.parse(query);
        TopDocs results = searcher.search(searchQuery, 1000000000);
        ScoreDoc[] hits = results.scoreDocs;
        System.out.println("Found " + hits.length + " hits.");
        List<Document> result = new ArrayList<>();
        for (ScoreDoc scoreDoc : results.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            System.out.println(doc.get("title") + " - " + doc.get("artist") + " - " + doc.get("album") + "score=" + scoreDoc.score);
            result.add(doc);
        }
        reader.close();
        return result;
    }
}
