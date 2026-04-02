package se.su.ovning2;

import java.util.*;

public class Exercise2 {
  private static final int NUMBER_OF_ARTISTS = 289;
  private static final int NUMBER_OF_GENRES = 15;
  private static final int NUMBER_OF_RECORDINGS = 500;
  private static final int NUMBER_OF_JAZZ_RECORDINGS = 19;
  private static final int NUMBER_OF_RECORDINGS_AFTER_2010 = 2;
  private static final int NUMBER_OF_JAZZ_RECORDINGS_BETWEEN_1960_AND_1970 = 7;
  private static final int NUMBER_OF_NEW_RECORDINGS_IN_OFFER = 1;
  private static final int NUMBER_OF_RECORDINGS_BEFORE_1960 = 10;
  private static final int NUMBER_OF_RECORDINGS_BY_COLTRANE = 2;
  private static final int NUMBER_OF_RECORDINGS_FROM_1972 = 24;
  private static final String EXISTING_GENRE = "Jazz";
  private static final String MISSING_GENRE = "Grime";
  private static final String EXISTING_TITLE = "A Love Supreme";
  private static final String MISSING_TITLE = "After Hours";
  private static final String EXISTING_ARTIST = "John Coltrane";
  private static final String MISSING_ARTIST = "The Weeknd";
  private static final Recording EXISTING_RECORDING =
      new Recording(EXISTING_TITLE, EXISTING_ARTIST, 1965, "CD", Set.of(EXISTING_GENRE));
  private static final Recording MISSING_RECORDING =
      new Recording(MISSING_TITLE, MISSING_ARTIST, 2021, "LP", Set.of(MISSING_GENRE));
  private static final Set<Recording> OFFERED_RECORDINGS =
      Set.of(EXISTING_RECORDING, MISSING_RECORDING);

  public static void main(String[] args) {

    Searcher sut = new Searcher(new Data().getRecordings());

    long numberOfArtists = sut.numberOfArtists();
    if (numberOfArtists != NUMBER_OF_ARTISTS) {
      throw new AssertionError("numberOfArtists: wrong number of artists.");
    }

    long numberOfGenres = sut.numberOfGenres();
    if (numberOfGenres != NUMBER_OF_GENRES) {
      throw new AssertionError("numberOfGenres: wrong number of genres.");
    }

    long numberOfTitles = sut.numberOfTitles();
    if (numberOfTitles != NUMBER_OF_RECORDINGS) {
      throw new AssertionError("numberOfTitles: wrong number of titles.");
    }

    Collection<Recording> existingGenre = sut.getRecordingsByGenre(EXISTING_GENRE);
    if (existingGenre.size() != NUMBER_OF_JAZZ_RECORDINGS) {
      throw new AssertionError("getRecordingsByGenre: wrong number of recordings.");
    }

    boolean shouldBeTrue = sut.doesArtistExist(EXISTING_ARTIST);
    if (!shouldBeTrue) {
      throw new AssertionError("doesArtistExist: didn't find an existing artist.");
    }

    Collection<String> genres = sut.getGenres();
    if (genres.size() != NUMBER_OF_GENRES) {
      throw new AssertionError("getGenres: wrong number of genres.");
    }

    Recording recordingByName = sut.getRecordingByName(EXISTING_TITLE);
    if (recordingByName == null) {
      throw new AssertionError("getRecordingByName: existing record wasn't found.");
    }

    Collection<Recording> recordingsAfter = sut.getRecordingsAfter(2010);
    if (recordingsAfter.size() != NUMBER_OF_RECORDINGS_AFTER_2010) {
      throw new AssertionError("getRecordingsAfter: wrong number of recordings.");
    }

    SortedSet<Recording> recordingsByArtistOrderedByYearAsc =
        sut.getRecordingsByArtistOrderedByYearAsc(EXISTING_ARTIST);
    if (recordingsByArtistOrderedByYearAsc.size() != NUMBER_OF_RECORDINGS_BY_COLTRANE) {
      throw new AssertionError(
          "getRecordingsByArtistOrderedByYearAsc: wrong number of recordings.");
    }

    Collection<Recording> recordingsByGenre = sut.getRecordingsByGenre(EXISTING_GENRE);
    if (recordingsByGenre.size() != NUMBER_OF_JAZZ_RECORDINGS) {
      throw new AssertionError("getRecordingsByGenre: wrong number of recordings.");
    }

    Collection<Recording> recordingsByGenreAndYear =
        sut.getRecordingsByGenreAndYear(EXISTING_GENRE, 1960, 1970);
    if (recordingsByGenreAndYear.size() != NUMBER_OF_JAZZ_RECORDINGS_BETWEEN_1960_AND_1970) {
      throw new AssertionError("getRecordingsByGenreAndYear: wrong number of recordings.");
    }

    Collection<Recording> newRecordings = sut.offerHasNewRecordings(OFFERED_RECORDINGS);
    if (newRecordings.size() != NUMBER_OF_NEW_RECORDINGS_IN_OFFER) {
      throw new AssertionError("offerHasNewRecordings: wrong number of recordings.");
    }

    Collection<Recording> optionalGetRecordingsBefore = sut.optionalGetRecordingsBefore(1960);
    if (optionalGetRecordingsBefore != null) {
      if (optionalGetRecordingsBefore.size() != NUMBER_OF_RECORDINGS_BEFORE_1960) {
        throw new AssertionError("optionalGetRecordingsBefore: wrong number of recordings.");
      }
    }

    SortedSet<Recording> optionalGetRecordingsByArtistOrderedByTitleAsc =
        sut.optionalGetRecordingsByArtistOrderedByTitleAsc(EXISTING_ARTIST);
    if (optionalGetRecordingsByArtistOrderedByTitleAsc != null) {
      if (optionalGetRecordingsByArtistOrderedByTitleAsc.size()
          != NUMBER_OF_RECORDINGS_BY_COLTRANE) {
        throw new AssertionError(
            "optionalGetRecordingsByArtistOrderedByTitleAsc: wrong number of recordings.");
      }
    }

    Collection<Recording> optionalGetRecordingsFrom = sut.optionalGetRecordingsFrom(1972);
    if (optionalGetRecordingsFrom != null) {
      if (optionalGetRecordingsFrom.size() != NUMBER_OF_RECORDINGS_FROM_1972) {
        throw new AssertionError("optionalGetRecordingsFrom: wrong number of recordings.");
      }
    }
  }
}
