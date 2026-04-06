package se.su.ovning2;

import java.util.*;

public class Searcher implements SearchOperations {

  private Collection<Recording> recordings;

  private Map<String, Set<Recording>> artistsMap = new HashMap<>();
  private Map<String, Set<Recording>> genresMap = new HashMap<>();
  private Map<String, Recording> titlesMap = new HashMap<>();
  private Map<Integer, Set<Recording>> yearsMap = new TreeMap<>();
  private Collection<Recording> emptySet = new HashSet<>();

  public Searcher(Collection<Recording> data) {
    this.recordings = data;
    
    for (Recording d : data) {
      String title = d.getTitle();
      String artist = d.getArtist();
      int year = d.getYear();
      Collection<String> genres = d.getGenre();

      if (!titlesMap.containsKey(title)) 
          titlesMap.put(title, d);
      
      if (!artistsMap.containsKey(artist)) {
        artistsMap.put(artist, new HashSet<>());
      }
      artistsMap.get(d.getArtist()).add(d);

      if (!yearsMap.containsKey(year)) {
        yearsMap.put(d.getYear(), new HashSet<>());
      }
      yearsMap.get(d.getYear()).add(d);
      
      for (String g : genres) {
        if (!genresMap.containsKey(g))
          genresMap.put(g, new HashSet<>());
        genresMap.get(g).add(d);
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

    return genresMap.keySet();
  }

  @Override
  public Recording getRecordingByName(String title) {
    
    return titlesMap.get(title);
  }

  @Override
/*  public Collection<Recording> getRecordingsAfter(int year) {
    SortedMap<Integer, ArrayList<Recording>> recordingsAfter = sortedTree.tailMap(year + 1);
    List<Recording> recordingsList = new ArrayList<>();
    for (Integer y : recordingsAfter.keySet()) {
      recordingsList.addAll(recordingsAfter.get(y));
    }
    return recordingsList;
  }*/

  @Override
  public SortedSet<Recording> getRecordingsByArtistOrderedByYearAsc(String artist) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException(
        "Unimplemented method 'getRecordingsByArtistOrderedByYearAsc'");
  }

  @Override
  public Collection<Recording> getRecordingsByGenre(String genre) {
    if (!genresMap.keySet().contains(genre))
      return emptySet;
    return genresMap.get(genre);
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
