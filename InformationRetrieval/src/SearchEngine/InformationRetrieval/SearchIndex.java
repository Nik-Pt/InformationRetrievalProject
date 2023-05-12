package SearchEngine.InformationRetrieval;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
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
        analyzer = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String s) {
                Tokenizer tokenizer = new StandardTokenizer();
                TokenStream tokenStream = new LowerCaseFilter(tokenizer);
                tokenStream = new PorterStemFilter(tokenStream);
                return new TokenStreamComponents(tokenizer,tokenStream);

            }
        };
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

        QueryParser exactParser = new MultiFieldQueryParser(fields, analyzer);
        QueryParser wildcardParser = new MultiFieldQueryParser(fields, analyzer);
        wildcardParser.setAllowLeadingWildcard(true);

        Query exactQuery = exactParser.parse(query + "^2");
        Query wildcardQuery = wildcardParser.parse(query + "*");
        BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
        queryBuilder.add(exactQuery, BooleanClause.Occur.SHOULD);
        queryBuilder.add(wildcardQuery, BooleanClause.Occur.SHOULD);
        Query searchQuery = queryBuilder.build();
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
