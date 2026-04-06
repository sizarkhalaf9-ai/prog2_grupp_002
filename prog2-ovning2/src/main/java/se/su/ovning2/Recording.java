package se.su.ovning2;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

public class Recording {
  private final int year;
  private final String artist;
  private final String title;
  private final String type;
  private final Set<String> genre;

  public Recording(String title, String artist, int year, String type, Set<String> genre) {
    this.title = title;
    this.year = year;
    this.artist = artist;
    this.type = type;
    this.genre = genre;
  }

  public String getArtist() {
    return artist;
  }

  public Collection<String> getGenre() {
    return genre;
  }

  public String getTitle() {
    return title;
  }

  public String getType() {
    return type;
  }

  public int getYear() {
    return year;
  }

  /*public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Recording)) return false;
    Recording that = (Recording) o;
    return year == that.year &&
    Objects.equals(title, that.title) &&
    Objects.equals(artist, that.artist);
  }*/
  
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof Recording) {
      Recording rec = (Recording) o;
      return this.getYear() == rec.getYear() && 
      this.getTitle().equals(rec.getTitle()) && 
      this.getArtist().equals(rec.getArtist());
    }
    return false;
  }
  
public static void main(String[] args) {
  Set<String> dylanGenres = new HashSet<>();
    dylanGenres.add("Rock");
    dylanGenres.add("Folk");
    dylanGenres.add("World");
    dylanGenres.add("Country");
  Recording r1 = new Recording("Bringing It All Back Home", "Bob Dylan", 1, "CD", dylanGenres);
  System.out.println(r1.hashCode());
}

  public int hashCode() {
    return Objects.hash(title, artist, year);
  }

  @Override
  public String toString() {
    return String.format("{ %s | %s | %s | %d | %s }", artist, title, genre, year, type);
  }
}