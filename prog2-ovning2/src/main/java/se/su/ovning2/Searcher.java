package se.su.ovning2;

import java.util.*;

public class Searcher implements SearchOperations {

  private Collection<Recording> recordings;
  
  public Searcher(Collection<Recording> data) {
    this.recordings = data;
  }

  @Override
  public long numberOfArtists() {
    Set<String> artists = new HashSet<>();
    for (Recording r : recordings) {
      artists.add(r.getArtist()); 
    }
    return artists.size();
  }

  @Override
public long numberOfGenres() {
    return this.getGenres().size();
}

  @Override
  public long numberOfTitles() {
    HashSet<String> titles = new HashSet<>();
    for (Recording r : recordings) { titles.add(r.getTitle()); }
    return titles.size();
  }

  @Override
  public boolean doesArtistExist(String name) {
    List<String> artists = new ArrayList<>();
    for (Recording r : recordings) { artists.add(r.getArtist()); }
    return artists.contains(name);
  }

  @Override
  public Collection<String> getGenres() {
    HashSet<Collection<String>> genres = new HashSet<>();
    for (Recording r : recordings) {
      genres.add(r.getGenre());
    }

    Set<String> mainGenres = new HashSet<>();    
    for (Collection<String> genre : genres) {
      List<String> list = new ArrayList<>(genre);
      mainGenres.add(list.get(0));
    }

    return mainGenres;
  }

  @Override
  public Recording getRecordingByName(String title) {
    for (Recording r : recordings) {
      List<String> titles = new ArrayList<>();
      titles.add(r.getTitle());
      if (title.equals(r.getTitle())) { return r; }
    }
    return null;
  }

  @Override
  public Collection<Recording> getRecordingsAfter(int year) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getRecordingsAfter'");
  }

  @Override
  public SortedSet<Recording> getRecordingsByArtistOrderedByYearAsc(String artist) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException(
        "Unimplemented method 'getRecordingsByArtistOrderedByYearAsc'");
  }

  @Override
  public Collection<Recording> getRecordingsByGenre(String genre) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getRecordingsByGenre'");
  }

  @Override
  public Collection<Recording> getRecordingsByGenreAndYear(String genre, int yearFrom, int yearTo) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getRecordingsByGenreAndYear'");
  }

  @Override
  public Collection<Recording> offerHasNewRecordings(Collection<Recording> offered) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'offerHasNewRecordings'");
  }
}
