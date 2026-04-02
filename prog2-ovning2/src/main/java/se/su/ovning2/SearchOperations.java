package se.su.ovning2;

import java.util.Collection;
import java.util.SortedSet;

interface SearchOperations {
  /**
   * Returnerar antalet unika artister
   *
   * <p>Exempel: numberOfArtists() => 0 numberOfArtists() => 100
   *
   * @return antalet unika artister (long)
   */
  long numberOfArtists();

  /**
   * Returnerar antalet genrer
   *
   * <p>Exempel: numberOfGenres() => 25
   *
   * @return antalet genrer (long)
   */
  long numberOfGenres();

  /**
   * Returnerar antalet unika titlar
   *
   * <p>Exempel: numberOfTitles => 500
   *
   * @return antalet unika titlar (long)
   */
  long numberOfTitles();

  /**
   * Kontrollerar om det finns en artist med det sökta namnet.
   *
   * <p>Exempel: doesArtistExist("Phoebe Bridgers") => false doesArtistExist("Miles Davis") => true
   *
   * @param name Artistens namn
   * @return true om artisten finns, false om inte
   */
  boolean doesArtistExist(String name);

  /**
   * Ger en omodifierbar samling med genrer.
   *
   * <p>Exempel: getGenres() => en samling med 19 genrer.
   *
   * @return en omodifierbar samling med genrer
   */
  Collection<String> getGenres();

  /**
   * Hämtar inspelning med den sökta titeln.
   *
   * <p>Exempel: getRecordingByName("Punisher") => null getRecordingByName("Giant Steps") =>
   * referens till Recording-objekt gär getTitle().equals("Giant Steps")
   *
   * @param title Inspelningens titel
   * @return ett Recodring-objekt med den sökta inspelningen om den hittades, null annars
   */
  Recording getRecordingByName(String title);

  /**
   * Hämtar en omodifierbar samling med inspelningar från och med det angivna året.
   *
   * <p>Exempel: getRecordingsAfter(2010) => samling med ett antal (kan vara 0) objekt
   *
   * @param year året som sökningen startar från (och inkluderar)
   * @return en omodifierbar samling med inspelningar
   */
  Collection<Recording> getRecordingsAfter(int year);

  /**
   * Hämtar en omodifierbar samling med inspelningar av artisten sorterade i stigande ordning på år.
   *
   * <p>Exempel: getRecordingsByArtistOrderedByYearAsc("John Coltrane") => samling med ett antal
   * (kan vara 0) objekt
   *
   * @param artist den sökta artisten
   * @return ett omodifierbar samling med inspelningar
   */
  SortedSet<Recording> getRecordingsByArtistOrderedByYearAsc(String artist);

  /**
   * Hämtar en omodifierbar samling med inspelningar i genren.
   *
   * <p>Exempel: getRecordingsByGenre("Jazz") => samling med ett antal (kan vara 0) objekt
   *
   * @param genre den sökta genren
   * @return ett omodifierbar samling med inspelningar
   */
  Collection<Recording> getRecordingsByGenre(String genre);

  /**
   * Hämtar en omodifierbar samling med inspelningar i genren gjorda mellan de angivna åren
   *
   * <p>Exempel: getRecordingsByGenreAndYear("Jazz", 1950, 1960) => samling med ett antal (kan vara
   * 0) objekt
   *
   * @param genre den efterfrågade genren
   * @param yearFrom första året i intervallet
   * @param yearTo sista året i intervallet
   * @return en omodifierbar samling
   */
  Collection<Recording> getRecordingsByGenreAndYear(String genre, int yearFrom, int yearTo);

  /**
   * Tar emot en samling och returnerar en ny samling som bara innehåller de Recordings som inte
   * redan fanns i databasen.
   *
   * @param offered En samling med inspelningar
   * @return en omodifierbar samling med de inspelningar som inte redan finns
   */
  Collection<Recording> offerHasNewRecordings(Collection<Recording> offered);

  /**
   * Hämtar en omodifierbar samling med inspelningar innan det angivna året.
   *
   * <p>Exempel: optionalGetRecordingsBefore(1960) => samling med ett antal (kan vara 0) objekt
   * daterade < 1960
   *
   * @param year året som sökningen löper till (och exkluderar)
   * @return ett omodifierbar samling med inspelningar
   */
  default Collection<Recording> optionalGetRecordingsBefore(int year) {
    return null;
  }

  /**
   * Hämtar en omodifierbar samling med inspelningar av den sökta artisten. Samlingen ska vara
   * sorterad på titel i stigande ordning A -> Z
   *
   * <p>Exempel: optionalGetRecordingsByArtistOrderedByTitleAsc(1960) => sorterad samling med ett
   * antal (kan vara 0) objekt
   *
   * @param artist den sökta artisten
   * @return ett omodifierbar samling med inspelningar sorterade på titel
   */
  default SortedSet<Recording> optionalGetRecordingsByArtistOrderedByTitleAsc(String artist) {
    return null;
  }

  /**
   * Hämtar en omodifierbar samling med inspelningar från det angivna året.
   *
   * <p>Exempel: optionalGetRecordingsFrom(1983) => samling med ett antal (kan vara 0) objekt
   *
   * @param year det sökta året
   * @return en omodifierbar samling med inspelningar
   */
  default Collection<Recording> optionalGetRecordingsFrom(int year) {
    return null;
  }
}
