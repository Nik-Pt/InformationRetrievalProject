package SearchEngine.InformationRetreival;

public class Song {
    private String artist;
    private String title;
    private String album;
    private String date;
    private String lyrics;
    private String year;

    public Song(String artist,String title,String album,String date,String lyrics,String year){
        this.artist = artist;
        this.title = title;
        this.album = album;
        this.date = date;
        this.lyrics = lyrics;
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getDate() {
        return date;
    }

    public String getLyrics() {
        return lyrics;
    }

    public String getYear() {
        return year;
    }

    public String toString() {
        return "Song{" +
                "artist='" + artist + '\'' +
                ", title='" + title + '\'' +
                ", album='" + album + '\'' +
                ", date='" + date + '\'' +
                ", lyrics='" + lyrics + '\'' +
                ", year='" + year + '\'' +
                '}';
    }
}