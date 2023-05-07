package SearchEngine.InformationRetrieval;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.*;

import org.apache.lucene.analysis.Analyzer;
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

/*  SearchIndex class is responsible for the search and retrieval of a word,number or phrase that the user wants to find in the .csv files.
    This class contains one method(could have made one for the index creation and one for the search) that creates a folder named index.
    We check if the folder already exist and if so then it is deleted to prevent duplicate results when searching for something.
    A new ReadCSV object is created and then the method toSongList is called in order to read all the data from the .csv files.
    Then the song is saved in a Document so that Lucene can begin the search.
    A MultiQueryParser is being used because the search can be done in all the fields using the StandardAnalyzer(I wanted to use the Snowball analyzer,but it is not included this version of Lucene).
    During the for loop when the word,number,phrase is found in a Document then it is saved in a List<Document> so that it can be displayed later on.
*/

public class SearchIndex {

    public List<Document> search(String query, String[] fields) throws IOException, ParseException {

        String indexPath = "index";
        Directory indexDir = FSDirectory.open(Paths.get(indexPath));
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        boolean indexExists = DirectoryReader.indexExists(indexDir);

        if(indexExists){
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        }

        IndexWriter writer = new IndexWriter(indexDir,config);

        ReadCSV reader = new ReadCSV();
        ArrayList<Song> songList = reader.toSongList();
        for(Song song : songList){
            Document doc = new Document();
            doc.add(new TextField("artist",song.getArtist(),Field.Store.YES));
            doc.add(new TextField("title",song.getTitle(),Field.Store.YES));
            doc.add(new TextField("album",song.getAlbum(),Field.Store.YES));
            doc.add(new TextField("date",song.getDate(),Field.Store.YES));
            doc.add(new TextField("lyrics",song.getLyrics(),Field.Store.YES));
            doc.add(new TextField("year",song.getYear(),Field.Store.YES));
            writer.addDocument(doc);
        }
        writer.close();

        DirectoryReader dirReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
        IndexSearcher searcher = new IndexSearcher((dirReader));

        //Querying the index and begin the search in all the fields above
        QueryParser parser = new MultiFieldQueryParser(fields,analyzer);
        Query searchQuery = parser.parse(query+"*");
        TopDocs results = searcher.search(searchQuery,1000000000);
        ScoreDoc[] hits = results.scoreDocs;

        System.out.println("Found " + hits.length + " hits.");

        List<Document> result = new ArrayList<>();
        for(ScoreDoc scoreDoc : results.scoreDocs){
            Document doc = searcher.doc(scoreDoc.doc);
            result.add(doc);
        }

        dirReader.close();
        return result;
    }
}
