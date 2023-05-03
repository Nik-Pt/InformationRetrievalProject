package SearchEngine.InformationRetreival;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import SearchEngine.InformationRetreival.ReadCSV;
import SearchEngine.InformationRetreival.Song;

public class SearchIndex {

    public static void main(String[] args) {

        try {
            // Creating the index
            String indexPath = "index";
            Directory indexDir = FSDirectory.open(Paths.get(indexPath));
            IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
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
            String[] fields = {"artist", "title", "album", "date", "lyrics", "year"};
            QueryParser parser = new MultiFieldQueryParser(fields, new StandardAnalyzer());
            Query query = parser.parse("till");
            TopDocs results = searcher.search(query, 1000000000);
            ScoreDoc[] hits = results.scoreDocs;
            System.out.println("Found " + hits.length + " hits.");
            for (ScoreDoc scoreDoc : results.scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);
                System.out.println(doc.get("title") + " - " + doc.get("artist") + " - " + doc.get("album") + "score=" + scoreDoc.score);
                /*String lyrics = doc.get("lyrics");
                int snippetStart = lyrics.toLowerCase().indexOf("'till") - 20;
                if (snippetStart < 0) {
                    snippetStart = 0;
                }
                int snippetEnd = snippetStart + 50;
                if (snippetEnd > lyrics.length()) {
                    snippetEnd = lyrics.length();
                }
                String snippet = lyrics.substring(snippetStart, snippetEnd);
                System.out.println("  " + snippet + "...");*/
            }

        reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
