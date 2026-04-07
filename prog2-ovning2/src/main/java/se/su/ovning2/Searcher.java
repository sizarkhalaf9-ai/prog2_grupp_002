package se.su.ovning2;

import java.util.*;

public class Searcher implements SearchOperations {

  private Map<String, TreeMap<Integer, Set<Recording>>> artistsMap = new HashMap<>();
  private Map<String, TreeMap<Integer, Set<Recording>>> genresMap = new HashMap<>();
  private Map<String, Recording> titlesMap = new HashMap<>();
  private TreeMap<Integer, Set<Recording>> yearsMap = new TreeMap<>();
  private Set<Recording> recordingsSet = new HashSet<>();

  // private Collection<Recording> emptySet = new HashSet<>();

  public Searcher(Collection<Recording> data) {
    recordingsSet.addAll(data);

    for (Recording d : data) {
      String title = d.getTitle();
      String artist = d.getArtist();
      int year = d.getYear();
      Collection<String> genres = d.getGenre();

      if (!titlesMap.containsKey(title))
        titlesMap.put(title, d);

      if (!artistsMap.containsKey(artist))
        artistsMap.put(artist, new TreeMap<>());
      if (!artistsMap.get(artist).containsKey(year))
        artistsMap.get(artist).put(year, new HashSet<>());
      artistsMap.get(artist).get(year).add(d);

      if (!yearsMap.containsKey(year))
        yearsMap.put(year, new HashSet<>());
      yearsMap.get(year).add(d);

      for (String g : genres) {
        if (!genresMap.containsKey(g))
          genresMap.put(g, new TreeMap<>());
        if (!genresMap.get(g).containsKey(year))
          genresMap.get(g).put(year, new HashSet<>());
        genresMap.get(g).get(year).add(d);
      }
    }
  }

  @Override
  public long numberOfArtists() {

    return artistsMap.keySet().size();
  }

  @Override
  public long numberOfGenres() {

    return genresMap.keySet().size();
  }

  @Override
  public long numberOfTitles() {

    return titlesMap.keySet().size();
  }

  @Override
  public boolean doesArtistExist(String name) {

    return artistsMap.keySet().contains(name);
  }

  public static void main(String[] args) {
    Data data = new Data();

    for (Recording r : data.getRecordings()) {
      System.out.println(r.getGenre() + ", ");
    }
  }

  @Override
  public Collection<String> getGenres() {

    return Collections.unmodifiableSet(genresMap.keySet());
  }

  @Override
  public Recording getRecordingByName(String title) {

    return titlesMap.get(title);
  }

  @Override // Är detta verkligen after och inte from?
  @Override
  public Collection<Recording> getRecordingsAfter(int year) {
    SortedMap<Integer, Set<Recording>> recordingsMap = yearsMap.tailMap(year);
    Collection<Set<Recording>> recordingsSet = recordingsMap.values();
    Set<Recording> recordingsAfter = new HashSet<>();
    for (Set<Recording> recordingSet : recordingsSet) {
      recordingsAfter.addAll(recordingSet);
    }
    return Collections.unmodifiableSet(recordingsAfter);
  }

  @Override
  public SortedSet<Recording> getRecordingsByArtistOrderedByYearAsc(String artist) {
    Map<Integer, Set<Recording>> artistRecordings = artistsMap.get(artist);
    SortedSet<Recording> recordingsAfterYear = new TreeSet<>(new YearComparator());
    for (Set<Recording> recordingSet : artistRecordings.values()) 
      recordingsAfterYear.addAll(recordingSet);
    
    return Collections.unmodifiableSortedSet(recordingsAfterYear);
  }

  private class YearComparator implements Comparator<Recording> {
    public int compare(Recording r1, Recording r2) {

      return r1.getYear() - r2.getYear();
    }
  }

  @Override
  public Collection<Recording> getRecordingsByGenre(String genre) {
    if (!genresMap.keySet().contains(genre))
      return Collections.emptySet();

    Set<Recording> recordingsByGenre = new HashSet<>();
    for (Set<Recording> recordingsSet : genresMap.get(genre).values()) 
      recordingsByGenre.addAll(recordingsSet);
    
    return Collections.unmodifiableSet(recordingsByGenre);
  }

  @Override
  public Collection<Recording> getRecordingsByGenreAndYear(String genre, int yearFrom, int yearTo) {
    SortedMap<Integer, Set<Recording>> recordingsOfGenre = genresMap.get(genre).subMap(yearFrom, yearTo + 1);
    Set<Recording> recordingsByGenreAndYear = new HashSet<>();
    for (Set<Recording> recordingSet : recordingsOfGenre.values()) 
      recordingsByGenreAndYear.addAll(recordingSet);
    
    return Collections.unmodifiableSet(recordingsByGenreAndYear);
  }

  @Override
  public Collection<Recording> offerHasNewRecordings(Collection<Recording> offered) {
    Set<Recording> keepers = new HashSet<>();
    for (Recording o : offered) 
      if (!recordingsSet.contains(o))
        keepers.add(o);
    
    return Collections.unmodifiableSet(keepers);
  }

  public Collection<Recording> optionalGetRecordingsBefore(int year) {
    Set<Recording> recordingsBeforeYear = new HashSet<>();
    for (Set<Recording> recording : yearsMap.headMap(year).values()) 
      recordingsBeforeYear.addAll(recording);

    return Collections.unmodifiableSet(recordingsBeforeYear);
  }

  public SortedSet<Recording> optionalGetRecordingsByArtistOrderedByTitleAsc(String artist) {
    SortedSet<Recording> artistRecordings = new TreeSet<>(new TitleComparator());
    for (Set<Recording> recordingsSet : artistsMap.get(artist).values()) 
      artistRecordings.addAll(recordingsSet);
    
    return Collections.unmodifiableSortedSet(artistRecordings);
  }

  private class TitleComparator implements Comparator<Recording> {
    public int compare(Recording r1, Recording r2) {

      return r1.getTitle().compareTo(r2.getTitle());
    }
  }

  public Collection<Recording> optionalGetRecordingsFrom(int year) {
    if(!yearsMap.containsKey(year))
      return Collections.emptySet();

    return Collections.unmodifiableSet(yearsMap.get(year));
  }
}
