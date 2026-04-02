package se.su.ovning2;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumingThat;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.time.Duration;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Exercise2Test {
  public static final String ARTIST_JOHN_COLTRANE = "John Coltrane";
  public static final String ARTIST_PHOEBE_BRIDGERS = "Phoebe Bridgers";
  public static final String ARTIST_TOM_MISCH_YUSSEF_DAYES = "Tom Misch & Yussef Dayes";
  public static final String GENRE_JAZZ = "Jazz";
  public static final String TITLE_A_LOVE_SUPREME = "A Love Supreme";
  public static final String TITLE_BITCHES_BREW = "Bitches Brew";
  public static final String TITLE_GIANT_STEPS = "Giant Steps";
  public static final String TITLE_KIND_OF_BLUE = "Kind of Blue";
  public static final String TITLE_PUNISHER = "Punisher";
  public static final String TITLE_SKETCHES_OF_SPAIN = "Sketches of Spain";
  public static final String TITLE_WHAT_KINDA_MUSIC = "What Kinda Music";
  public static final String TYPE_CD = "CD";
  public static final String TYPE_LP = "LP";
  public static final Recording PUNISHER =
      new Recording(TITLE_PUNISHER, ARTIST_PHOEBE_BRIDGERS, 2021, TYPE_CD, Set.of(GENRE_JAZZ));
  public static final Recording WKM =
      new Recording(
          TITLE_WHAT_KINDA_MUSIC, ARTIST_TOM_MISCH_YUSSEF_DAYES, 2021, TYPE_LP, Set.of("Soul"));

  private static final Set<String> VALUE_TYPES =
      Set.of(
          "java.util.HashSet",
          "java.util.Collection",
          "java.util.Set",
          "java.util.SortedMap",
          "java.util.TreeSet",
          "java.util.Map",
          "java.util.TreeMap",
          "java.util.SortedSet",
          "Recording");
  private static final Set<String> KEY_TYPES = Set.of("java.lang.String", "java.lang.Integer");

  private static final int TEST_SIZE = 1000000;
  private static final Collection<Recording> data1 = generateRandomData();
  private static final Collection<Recording> data2 = generateRandomData();
  private final SearchOperations sut = new Searcher(new Data().getRecordings());

  private static boolean isMapWithCorrectTypes(Field field) {
    ParameterizedType pt = (ParameterizedType) field.getGenericType();
    var t1 = pt.getActualTypeArguments()[0].getTypeName();
    var t2 = pt.getActualTypeArguments()[1].getTypeName();

    if (t2.contains("<")) t2 = t2.substring(0, t2.indexOf('<'));
    if (t2.contains("Recording")) t2 = "Recording";
    return KEY_TYPES.contains(t1) && VALUE_TYPES.contains(t2);
  }

  private static Collection<Recording> generateRandomData() {
    Collection<Recording> data = new LinkedList<>();
    Random random = new Random();

    for (int i = 0; i < TEST_SIZE; i++) {
      data.add(
          new Recording(
              "Title" + random.nextInt(TEST_SIZE),
              "Artist" + random.nextInt(TEST_SIZE / 10),
              1900 + random.nextInt(122),
              random.nextBoolean() ? "LP" : "CD",
              Set.of("Jazz")));
    }
    return data;
  }

  @Test
  @Order(0)
  @DisplayName("Information")
  void __version() {
    System.out.println("Test version 2.0 (2023-04-02)");
  }

  @Test
  @DisplayName("Hastighetstest #1: skapande av Searcher.")
  @Order(1)
  void _speedTest1() {
    assertTimeoutPreemptively(
        Duration.ofSeconds(10),
        () -> {
          var sut = new Searcher(data1);
          sut.getGenres();
        },
        "Timeout... klassen Searcher kunde inte instansieras tillräckligt snabbt, vilket tyder på"
            + " att någon del använder datastrukturer fel.");
  }

  @Test
  @DisplayName("Hastighetstest #2: användning av metoder.")
  @Order(1000)
  void _speedTest2() {

    var sut = new Searcher(data1);
    Recording next = data1.iterator().next();

    assertTimeoutPreemptively(
        Duration.ofSeconds(5),
        () -> {
          sut.offerHasNewRecordings(data2);
        },
        "Timeout... testerna kunde inte utföra metoden offerHasNewRecordings tillräckligt snabbt,"
            + " vilket tyder på att någon del använder datastrukturer fel.");

    assertTimeoutPreemptively(
        Duration.ofSeconds(5),
        () -> {
          sut.doesArtistExist(next.getArtist());
          sut.getGenres();
          sut.getRecordingByName(next.getTitle());
          sut.getRecordingsAfter(next.getYear());
          sut.getRecordingsByArtistOrderedByYearAsc(next.getArtist());
          sut.getRecordingsByGenre("Jazz");
          sut.getRecordingsByGenreAndYear("Jazz", 1950, 2000);
        },
        "Timeout... testerna kunde inte utföra metoderna (förutom offerHasNewRecordings &"
            + " numberOf*) tillräckligt snabbt, vilket tyder på att någon del använder"
            + " datastrukturer fel.");

    assertTimeoutPreemptively(
        Duration.ofSeconds(5),
        () -> {
          sut.numberOfArtists();
          sut.numberOfGenres();
          sut.numberOfTitles();
        },
        "Timeout... testerna kunde inte utföra metoderna numberOf* tillräckligt snabbt, vilket"
            + " tyder på att någon del använder datastrukturer fel.");
  }

  @Test
  @Order(2)
  @DisplayName("Testar att klassen innehåller minst två av de nödvändiga samlingarna.")
  void testClassMembers() {

    long countOfApprovedMaps =
        Arrays.stream(sut.getClass().getDeclaredFields())
            .filter(field -> Map.class.isAssignableFrom(field.getType()))
            .filter(Exercise2Test::isMapWithCorrectTypes)
            .count();

    assertTrue(
        countOfApprovedMaps >= 2,
        "Klassen innehåller inte minst två avbildningar med Recording eller samling av Recording"
            + " som värde.");
  }

  @ParameterizedTest
  @Order(10)
  @DisplayName("Testar att artister som ska finnas finns.")
  @CsvSource({"John Coltrane, Miles Davis, Bruce Springsteen"})
  void testDoesArtistExistExistingArtist(String artist) {
    var msg = String.format("Hittade inte artisten %s som skulle finnas.", artist);
    assertTrue(sut.doesArtistExist(artist), msg);
  }

  @ParameterizedTest
  @Order(10)
  @DisplayName("Testar att artister som inte ska finnas inte finns.")
  @CsvSource({"Miley Cyrus, Phoebe Bridgers"})
  void testDoesArtistExistNonExistingArtist(String artist) {
    var msg = String.format("Hittade artisten %s som inte skulle finnas.", artist);
    assertFalse(sut.doesArtistExist(artist), msg);
  }

  @Test
  @Order(20)
  @DisplayName("Testar att getGenres ger rätt resultat.")
  void testGetGenres() {
    var result = sut.getGenres();
    assertEquals(15, result.size(), "Fel antal genrer.");

    Collection<String> expected =
        Set.of(
            "Rock",
            "Stage & Screen",
            "Folk",
            "Electronic",
            "World",
            "Blues",
            "Funk",
            "Pop",
            "Jazz",
            "Soul",
            "Latin",
            "Country",
            "Reggae",
            "Classical",
            "Hip Hop");
    assertTrue(result.containsAll(expected));
  }

  @Test
  @Order(30)
  @DisplayName("Testar att getRecordingByName ger rätt svar för skiva som finns.")
  void testGetRecordingByNameWhenRecordingExists() {
    var recording = sut.getRecordingByName(TITLE_GIANT_STEPS);
    assertEquals(TITLE_GIANT_STEPS, recording.getTitle());
  }

  @Test
  @Order(40)
  @DisplayName("Testar att getRecordingByName ger rätt svar för skiva som saknas.")
  void testGetRecordingByNameWhenRecordingIsMissing() {
    var recording = sut.getRecordingByName("Minor Steps");
    assertEquals(recording, null);
  }

  @ParameterizedTest
  @Order(50)
  @DisplayName("Testar att getRecordingsAfter ger rätt svar för olika år.")
  @CsvSource({"1983,178", "1950,500", "1972,338", "2010, 2", "2011,1", "2012, 0"})
  void testGetRecordingsAfter(int year, int count) {
    assertEquals(count, sut.getRecordingsAfter(year).size());
  }

  @Test
  @Order(60)
  @DisplayName("Testar att getRecordingsByArtistOrderedByYearAsc ger rätt svar.")
  void testGetRecordingsByArtistOrderedByYearAsc() {
    var recordings = sut.getRecordingsByArtistOrderedByYearAsc(ARTIST_JOHN_COLTRANE);
    var expected = Set.of(TITLE_GIANT_STEPS, TITLE_A_LOVE_SUPREME);
    var msg =
        String.format(
            "Letade efter titlarna %s i samlingen %n%s%n men hittade inte alla titlar.%n",
            expected, recordings);
    assertTrue(recordings.stream().map(Recording::getTitle).allMatch(expected::contains), msg);

    assertEquals(2, recordings.size(), "Fel antal objekt i samlingen.");
    var iter = recordings.iterator();
    var first = iter.next();
    var second = iter.next();
    assertAll(
        () -> {
          assertEquals(TITLE_GIANT_STEPS, first.getTitle());
          assertEquals(TITLE_A_LOVE_SUPREME, second.getTitle());
        });
  }

  @ParameterizedTest
  @Order(70)
  @DisplayName("Testar att getRecordingsByGenre ger rätt svar för olika genrer.")
  @CsvSource({"Jazz, 19", "Rock,351", "Pop,54", "Metal, 0"})
  void testGetRecordingsByGenre(String genre, int expected) {
    var actual = sut.getRecordingsByGenre(genre).size();
    assertEquals(
        expected,
        actual,
        "Genren " + genre + " förväntades ha " + expected + " skivor men hade " + actual + ".");
  }

  @ParameterizedTest
  @Order(80)
  @DisplayName("Testar att getRecordingsByGenreAndYear ger rätt svar för olika kombinationer.")
  @CsvSource({
    "Jazz, 1950, 1960, 7",
    "Pop, 1990, 2000, 9",
    "Rock, 1970, 1980, 150",
    "Country, 2010, 2021, 0"
  })
  void testGetRecordingsByGenreAndYear(String genre, int from, int to, int expected) {
    var actual = sut.getRecordingsByGenreAndYear(genre, from, to).size();
    assertEquals(
        expected,
        actual,
        "Genren " + genre + " förväntades ha " + expected + " skivor men hade " + actual + ".");
  }

  @Test
  @Order(90)
  @DisplayName("Testar att numberOfArtists ger rätt svar.")
  void testNumberOfArtists() {
    var actual = sut.numberOfArtists();
    assertEquals(289, actual, "Fel antal artister.");
  }

  @Test
  @Order(100)
  @DisplayName("Testar att numberOfGenres ger rätt svar.")
  void testNumberOfGenres() {
    var count = sut.numberOfGenres();
    assertEquals(15, count, "Fel antal genres.");
  }

  @Test
  @Order(110)
  @DisplayName("Testar att numberOfTitles ger rätt svar.")
  void testNumberOfTitles() {
    var count = sut.numberOfTitles();
    assertEquals(500, count, "Fel antal artister.");
  }

  @Test
  @Order(120)
  @DisplayName("Testar att offerHasNewRecordings ger rätt svar.")
  void testOfferHasNewRecordings() {
    var punisher = PUNISHER;
    var gs = sut.getRecordingByName(TITLE_GIANT_STEPS);

    var offered =
        List.of(
            new Recording(
                TITLE_GIANT_STEPS, ARTIST_JOHN_COLTRANE, 1959, TYPE_LP, Set.of(GENRE_JAZZ)),
            punisher);

    var recordings = sut.offerHasNewRecordings(offered);

    assertEquals(1, recordings.size());
    assertTrue(recordings.contains(punisher));
    assertFalse(recordings.contains(gs));
  }

  @Test
  @Order(130)
  @DisplayName(
      "Testar metoden optionalGetRecordingsByArtistOrderedByTitleAsc om den är implemeterad.")
  void testOptionalGetRecordingsByArtistOrderedByTitleAsc() {
    var result = sut.optionalGetRecordingsByArtistOrderedByTitleAsc("Miles Davis");

    assumingThat(
        result != null,
        () -> {
          assert result != null;
          assertEquals(3, result.size(), "Fel antal objekt i samlingen.");

          var iter = result.iterator();
          var first = iter.next().getTitle(); // bitches brew
          var second = iter.next().getTitle(); // kind of blue
          var third = iter.next().getTitle(); // sketches
          var actual = Set.of(first, second, third);
          var expected = Set.of(TITLE_BITCHES_BREW, TITLE_KIND_OF_BLUE, TITLE_SKETCHES_OF_SPAIN);

          assertEquals(expected, actual, "Fel objekt i samlingen.");
        });
  }

  @Test
  @Order(140)
  @DisplayName("Testar metoden optionalGetRecordingsFrom om den är implemeterad.")
  void testOptionalGetRecordingsFrom() {
    var result = sut.optionalGetRecordingsFrom(1960);

    assumingThat(
        result != null,
        () -> {
          assert result != null;
          assertEquals(3, result.size(), "Fel antal objekt i samlingen.");

          var iter = result.iterator();
          var first = iter.next().getTitle(); // bitches brew
          var second = iter.next().getTitle(); // kind of blue
          var third = iter.next().getTitle(); // sketches
          var actual = Set.of(first, second, third);
          var expected =
              Set.of("Muddy Waters at Newport 1960", "At Last!", TITLE_SKETCHES_OF_SPAIN);
          assertEquals(expected, actual, "Fel objekt i samlingen.");
        });
  }

  @ParameterizedTest
  @Order(150)
  @DisplayName("Testar metoden optionalGetRecordingsFrom om den är implementerad.")
  @CsvSource({"1983,6", "1950,0", "1960,3", "1970, 26"})
  void testOptionalGetRecordingsFrom(int year, int expected) {
    var result = sut.optionalGetRecordingsFrom(year);
    assumingThat(
        result != null,
        () -> {
          var msg =
              String.format(
                  "Det förväntades finnas %d skivor från %d men det fanns %d.",
                  expected, year, result.size());
          assertEquals(expected, result.size(), msg);
        });
  }

  @ParameterizedTest
  @Order(160)
  @DisplayName("Testar metoden optionalRecordingsBefore om den är implementerad.")
  @CsvSource({"1950,0", "1960,10", "1970, 115", "1983,322"})
  void testOptionalRecordingsBefore(int year, int expected) {
    var result = sut.optionalGetRecordingsBefore(year);
    assumingThat(
        result != null,
        () -> {
          assert result != null;
          var msg =
              String.format(
                  "Det förväntades finnas %d skivor från %d men det fanns %d.",
                  expected, year, result.size());
          assertEquals(expected, result.size(), msg);
        });
  }

  @Test
  @Order(125)
  @DisplayName("Testar att resultat inte går att ändra.")
  void testUnmodifiableResults() {

    var message = "Metoden %s returnerade inte en omodifierbar samling.%n";

    assertThrows(
        UnsupportedOperationException.class,
        () -> sut.offerHasNewRecordings(Collections.emptyList()).add(null),
        String.format(message, "offerHasNewRecordings"));
    assertThrows(
        UnsupportedOperationException.class,
        () -> sut.getRecordingsAfter(1980).add(null),
        String.format(message, "getRecordingsAfter"));
    assertThrows(
        UnsupportedOperationException.class,
        () -> sut.getRecordingsByArtistOrderedByYearAsc(ARTIST_JOHN_COLTRANE).add(null),
        String.format(message, "getRecordingsByArtistOrderedByYearAsc"));
    assertThrows(
        UnsupportedOperationException.class,
        () -> sut.getRecordingsByGenre(GENRE_JAZZ).add(null),
        String.format(message, "getRecordingsByGenre"));
    assertThrows(
        UnsupportedOperationException.class,
        () -> sut.getRecordingsByGenreAndYear(GENRE_JAZZ, 1960, 1965).add(null),
        String.format(message, "getRecordingsByGenreAndYear"));

    assumingThat(
        sut.optionalGetRecordingsByArtistOrderedByTitleAsc(ARTIST_JOHN_COLTRANE) != null,
        () ->
            assertThrows(
                UnsupportedOperationException.class,
                () ->
                    sut.optionalGetRecordingsByArtistOrderedByTitleAsc(ARTIST_JOHN_COLTRANE)
                        .add(WKM),
                String.format(message, "optionalGetRecordingsByArtistOrderedByTitleAsc")));

    assumingThat(
        sut.optionalGetRecordingsBefore(1970) != null,
        () ->
            assertThrows(
                UnsupportedOperationException.class,
                () -> sut.optionalGetRecordingsBefore(1970).add(null),
                String.format(message, "optionalGetRecordingsBefore")));

    assumingThat(
        sut.optionalGetRecordingsFrom(1970) != null,
        () ->
            assertThrows(
                UnsupportedOperationException.class,
                () -> sut.optionalGetRecordingsFrom(1970).add(null),
                String.format(message, "optionalGetRecordingsFrom")));
  }
}
