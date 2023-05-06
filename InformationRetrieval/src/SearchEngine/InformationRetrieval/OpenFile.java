package SearchEngine.InformationRetrieval;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class OpenFile{
    private String fileName;

    public OpenFile(String fileName, String delimiter) {
        this.fileName = fileName;
    }

    public ArrayList<Song> readCSV(){
        String line = "";
        String cvsSplitBy = ",";
        ArrayList<Song> songList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(cvsSplitBy);
                Song song = new Song(data[0], data[1], data[2], data[3], data[4],data[5]);
                songList.add(song);
            }
        }catch(IOException e) {
            System.out.println("File "+fileName+" was not found");
            System.out.println("or could not be opened.");
            System.exit(0);
        }
        return songList;
    }
}
