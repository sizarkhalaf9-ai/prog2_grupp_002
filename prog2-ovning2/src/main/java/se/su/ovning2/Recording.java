package se.su.ovning2;

import java.util.Objects;
import java.util.Set;

public class Recording {

  private final String title;
  private final String artist;
  private final int year;
  private final String format;
  private final Set<String> genres;

  public Recording(String title, String artist, int year, String format, Set<String> genres) {
    this.title = title;
    this.artist = artist;
    this.year = year;
    this.format = format;
    this.genres = genres;
  }

  public String getTitle() {
    return title;
  }

  public String getArtist() {
    return artist;
  }

  public int getYear() {
    return year;
  }

  public String getFormat() {
    return format;
  }

  public Set<String> getGenres() {
    return genres;
  }

  /*
   * public boolean equals(Object o) {
   * if (this == o) return true;
   * if (!(o instanceof Recording)) return false;
   * Recording that = (Recording) o;
   * return year == that.year &&
   * Objects.equals(title, that.title) &&
   * Objects.equals(artist, that.artist);
   * }
   * 
   * 
   * public int hashCode() {
   * return Objects.hash(title, artist, year);
   * }
   */
}