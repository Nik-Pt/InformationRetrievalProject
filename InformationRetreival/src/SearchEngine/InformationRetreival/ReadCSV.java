package SearchEngine.InformationRetreival;
import java.io.*;
import java.util.*;

public class ReadCSV {

    private String folderPath = "../../ProjectFiles/csv";
    private File folder = new File(folderPath);
    private File[] fileList = folder.listFiles();
    //ArrayList<Song> allData = new ArrayList<Song>();

    public ArrayList<Song> toSongList() {
        ArrayList<Song> songList = new ArrayList<Song>();
        if (!folder.exists()) {
            System.out.println("Error: directory does not exist: " + folderPath);
            System.exit(0);
        }

        if (!folder.isDirectory()) {
            System.out.println("Error: not a directory: " + folderPath);
            System.exit(0);
        }

        for (File file : fileList) {
            if (file.isFile() && file.getName().endsWith(".csv")) {
                try {
                    BufferedReader csvReader = new BufferedReader(new FileReader(file));
                    String row;

                    // Skip the first row (header) of the file
                    csvReader.readLine();

                    while ((row = csvReader.readLine()) != null) {
                        String[] data = row.split(",");
                        String artist = data[1];
                        String title = data[2];
                        String album = data[3];
                        String date = data[4];
                        String year = data[5];
                        String lyrics = data[6];

                        Song song = new Song(artist, title, album, date, lyrics, year);
                        songList.add(song);

                    }
                    csvReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return songList;
    }
}

