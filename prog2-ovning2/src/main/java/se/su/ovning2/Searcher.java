package se.su.ovning2;

import java.util.*;

public class Searcher implements SearchOperations {

  private Collection<Recording> recordings;
  private Map<String, TreeMap<Integer, Set<Recording>>> artistsMap = new HashMap<>();
  private Map<String, Set<Recording>> genresMap = new HashMap<>();
  private Map<String, TreeMap<Integer, Set<Recording>>> genresByYearMap = new HashMap<>();
  private Map<String, Recording> titlesMap = new HashMap<>();
  private TreeMap<Integer, Set<Recording>> yearsMap = new TreeMap<>();
  private Set<Recording> recordingsSet = new HashSet<>();

  //private Collection<Recording> emptySet = new HashSet<>();

  public Searcher(Collection<Recording> data) {
    this.recordings = data;

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
          genresMap.put(g, new HashSet<>());
        genresMap.get(g).add(d);

        if (!genresByYearMap.containsKey(g))
          genresByYearMap.put(g, new TreeMap<>());
        if (!genresByYearMap.get(g).containsKey(year))
          genresByYearMap.get(g).put(year, new HashSet<>());
        genresByYearMap.get(g).get(year).add(d);
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

  @Override //Är detta verkligen after och inte from?
  public Collection<Recording> getRecordingsAfter(int year) {
    SortedMap<Integer, Set<Recording>> recordingsMap = yearsMap.tailMap(year);
    Collection<Set<Recording>> recordingsSet = recordingsMap.values();
    Set<Recording> recordingsAfter = new HashSet<>();
    for (Set<Recording> rSet : recordingsSet) {
      recordingsAfter.addAll(rSet);
    }
    return Collections.unmodifiableSet(recordingsAfter);
  }

  @Override
  public SortedSet<Recording> getRecordingsByArtistOrderedByYearAsc(String artist) {
    Map<Integer, Set<Recording>> artistRecordings = artistsMap.get(artist);
    SortedSet<Recording> recordingsAfterYear = new TreeSet<>();
    for (Set<Recording> recordingSet : artistRecordings.values()) {
      recordingsAfterYear.addAll(recordingSet);
    }
    return recordingsAfterYear;
  }

  public class RecordingComparator implements Comparator<Recording> {
    public int compare(Recording r1, Recording r2) {
      return r1.getYear() - r2.getYear();
    }
  }

  @Override
  public Collection<Recording> getRecordingsByGenre(String genre) {
    if (!genresMap.keySet().contains(genre))
      return Collections.emptySet();

    return Collections.unmodifiableSet(genresMap.get(genre));
  }

  @Override
  public Collection<Recording> getRecordingsByGenreAndYear(String genre, int yearFrom, int yearTo) {
    SortedMap<Integer, Set<Recording>> recordingsOfGenre = genresByYearMap.get(genre).subMap(yearFrom, yearTo + 1);
    Set<Recording> recordingsByGenreAndYear = new HashSet<>();
    for (Set<Recording> recordingSet : recordingsOfGenre.values()) {
      recordingsByGenreAndYear.addAll(recordingSet);
    }
    return Collections.unmodifiableSet(recordingsByGenreAndYear);
  }

  @Override
  public Collection<Recording> offerHasNewRecordings(Collection<Recording> offered) {
    Set<Recording> keepers = new HashSet<>();
    for (Recording o : offered) {
      if (!recordingsSet.contains(o))
        keepers.add(o);
    }
    return Collections.unmodifiableSet(keepers);
  }
}
