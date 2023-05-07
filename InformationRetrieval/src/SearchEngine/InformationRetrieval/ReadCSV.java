package  SearchEngine.InformationRetrieval;

import java.io.*;
import java.util.*;

/* ReadCSV class contains the path of the .csv files.
   The method toSongList() creates a new ArrayList that can store Song objects. Specifically it checks if the file or directory is valid and then using a BufferedReader reads each line except
   the first one and saves it as a Song object in the ArrayList
*/

public class ReadCSV{

    private String folderPath = "csv";
    private File folder = new File(folderPath);
    private File[] fileList = folder.listFiles();

    public  ArrayList<Song> toSongList(){
        ArrayList<Song> songList = new ArrayList<Song>();
        if(!folder.exists()){
            System.out.println("Error: directory does not exist: " + folderPath);
            System.exit(0);
        }
        if(!folder.isDirectory()){
            System.out.println("Error: not a directory: " + folderPath);
            System.exit(0);
        }

        for(File file : fileList){
            if(file.isFile() && file.getName().endsWith(".csv")){
                try{
                    BufferedReader csvReader = new BufferedReader(new FileReader(file));
                    String line;
                    csvReader.readLine(); //Skips the first line because of the header in each .csv file
                    while((line = csvReader.readLine()) != null){
                        String[] songdata = line.split(",");
                        String artist = songdata[1];
                        String title = songdata[2];
                        String album = songdata[3];
                        String year = songdata[4];
                        String date = songdata[5];
                        String lyrics = songdata[6];
                        Song song = new Song(artist,title,album,date,lyrics,year);
                        songList.add(song);
                    }
                    csvReader.close();
                }catch(IOException exception){
                    exception.printStackTrace();
                }
            }
        }
        return songList;
    }
}