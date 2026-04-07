package se.su.ovning2;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.SortedSet;
import java.util.Set;

public class Searcher implements SearchOperations {
  private Collection<Recording> recordings;
  private Set<String> numberOfArtists = new HashSet<>();
  private Set<String> genres = new HashSet<>();
  private Set<String> title = new HashSet<>();
  private Map<String, Recording> recordingsByTitle = new HashMap<>();
  private Map<String, Set<Recording>> recordingsByArtist = new HashMap<>();

  public Searcher(Collection<Recording> data) {

    recordings = data;

    for (Recording r : data) {
      numberOfArtists.add(r.getArtist());
      genres.addAll(r.getGenre());
      title.add(r.getTitle());

      recordingsByTitle.put(r.getTitle(), r);

      recordingsByArtist
          .computeIfAbsent(r.getArtist(), k -> new HashSet<>())
          .add(r);
    }
  }

  @Override
  public long numberOfArtists() {
    return numberOfArtists.size();
  }

  @Override
  public long numberOfGenres() {
    return genres.size();
  }

  @Override
  public long numberOfTitles() {
    return title.size();
  }

  @Override
  public boolean doesArtistExist(String name) {
    return numberOfArtists.contains(name);
  }

  @Override
  public Collection<String> getGenres() {
    return Collections.unmodifiableSet(genres);
  }

  @Override
  public Recording getRecordingByName(String title) {
    return recordingsByTitle.get(title);

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