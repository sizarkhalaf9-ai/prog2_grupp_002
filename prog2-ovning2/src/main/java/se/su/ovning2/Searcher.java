package se.su.ovning2;

import java.util.*;

public class Searcher implements SearchOperations {

  private Collection<Recording> recordings;

  private Set<String> numberOfArtists = new HashSet<>();
  private Set<String> genres = new HashSet<>();
  private Set<String> titles = new HashSet<>();

  private Map<String, Recording> recordingsByTitle = new HashMap<>();
  private Map<String, Set<Recording>> recordingsByArtist = new HashMap<>();

  private Set<Recording> recordingsSet = new HashSet<>();

  public Searcher(Collection<Recording> data) {

    recordings = data;

    for (Recording r : data) {

      numberOfArtists.add(r.getArtist());
      genres.addAll(r.getGenre());
      titles.add(r.getTitle());

      recordingsByTitle.put(r.getTitle(), r);

      recordingsByArtist
          .computeIfAbsent(r.getArtist(), k -> new HashSet<>())
          .add(r);

      recordingsSet.add(r);
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
    return titles.size();
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

    Set<Recording> result = new HashSet<>();

    for (Recording r : recordings) {
      if (r.getYear() >= year) {
        result.add(r);
      }
    }

    return Collections.unmodifiableSet(result);
  }

  @Override
  public SortedSet<Recording> getRecordingsByArtistOrderedByYearAsc(String artist) {

    SortedSet<Recording> result = new TreeSet<>(
        (a, b) -> {
          int cmp = Integer.compare(a.getYear(), b.getYear());
          if (cmp != 0)
            return cmp;
          return a.getTitle().compareTo(b.getTitle());
        });

    Set<Recording> artistRecordings = recordingsByArtist.get(artist);

    if (artistRecordings != null) {
      result.addAll(artistRecordings);
    }

    return Collections.unmodifiableSortedSet(result);
  }

  @Override
  public Collection<Recording> getRecordingsByGenre(String genre) {

    Set<Recording> result = new HashSet<>();

    for (Recording r : recordings) {
      if (r.getGenre().contains(genre)) {
        result.add(r);
      }
    }

    return Collections.unmodifiableSet(result);
  }

  @Override
  public Collection<Recording> getRecordingsByGenreAndYear(String genre, int yearFrom, int yearTo) {

    Set<Recording> result = new HashSet<>();

    for (Recording r : recordings) {
      if (r.getGenre().contains(genre)
          && r.getYear() >= yearFrom
          && r.getYear() <= yearTo) {
        result.add(r);
      }
    }

    return Collections.unmodifiableSet(result);
  }

  @Override
  public Collection<Recording> offerHasNewRecordings(Collection<Recording> offered) {

    Set<Recording> result = new HashSet<>();

    for (Recording r : offered) {
      if (!recordingsSet.contains(r)) {
        result.add(r);
      }
    }

    return Collections.unmodifiableSet(result);
  }
}