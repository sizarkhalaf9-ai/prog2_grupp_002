package se.su.ovning3;

import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class Exercise3Test {
    public static final String SHOULD_THROW = "Metoden borde ha orsakat FileNotFoundException.";
    private static final String GENRE_ALTERNATIVE_ROCK = "Alternative Rock";
    private static final String GENRE_BLUES_ROCK = "Blues Rock";
    private static final String GENRE_GARAGE_ROCK = "Garage Rock";
    private static final String ARTIST_THE_CURE = "The Cure";
    private static final String ARTIST_THE_WHITE_STRIPES = "The White Stripes";
    private static final String ARTIST_JOHN_COLTRANE = "John Coltrane";
    private static final String ARTIST_PHOEBE_BRIDGERS = "Phoebe Bridgers";
    private static final String ARTIST_TOM_MISCH_YUSSEF_DAYES = "Tom Misch & Yussef Dayes";
    private static final String TITLE_BOYS_DON_T_CRY = "Boys Don't Cry";
    private static final String TITLE_WHITE_BLOOD_CELLS = "White Blood Cells";
    private static final String GENRE_ROCK = "Rock";
    private static final String GENRE_NEW_WAVE = "New Wave";
    private static final String GENRE_POST_PUNK = "Post-Punk";
    private static final String GENRE_INDIE_ROCK = "Indie Rock";
    private static final String GENRE_SOUL = "Soul";
    private static final String GENRE_JAZZ = "Jazz";
    private static final String TITLE_A_LOVE_SUPREME = "A Love Supreme";
    private static final String TITLE_BITCHES_BREW = "Bitches Brew";
    private static final String TITLE_GIANT_STEPS = "Giant Steps";
    private static final String TITLE_KIND_OF_BLUE = "Kind of Blue";
    private static final String TITLE_PUNISHER = "Punisher";
    private static final String TITLE_SKETCHES_OF_SPAIN = "Sketches of Spain";
    private static final String TITLE_WHAT_KINDA_MUSIC = "What Kinda Music";
    private static final Recording PUNISHER = new Recording(TITLE_PUNISHER, ARTIST_PHOEBE_BRIDGERS, 2021, Set.of(GENRE_JAZZ));
    private static final Recording WKM = new Recording(TITLE_WHAT_KINDA_MUSIC, ARTIST_TOM_MISCH_YUSSEF_DAYES, 2021, Set.of(GENRE_SOUL));
    private static final Recording GS = new Recording(TITLE_GIANT_STEPS, ARTIST_JOHN_COLTRANE, 1959, Set.of(GENRE_JAZZ));
    private static final Recording LS = new Recording(TITLE_A_LOVE_SUPREME, ARTIST_JOHN_COLTRANE, 1965, Set.of(GENRE_JAZZ));
    private static final Recording BDC = new Recording(TITLE_BOYS_DON_T_CRY, ARTIST_THE_CURE, 1979, Set.of(GENRE_ROCK, GENRE_NEW_WAVE, GENRE_POST_PUNK));
    private static final Recording WBC = new Recording(TITLE_WHITE_BLOOD_CELLS, ARTIST_THE_WHITE_STRIPES, 2001, Set.of(GENRE_ROCK, GENRE_INDIE_ROCK, GENRE_ALTERNATIVE_ROCK, GENRE_BLUES_ROCK, GENRE_GARAGE_ROCK));
    public static final List<Recording> DEFAULT_RECORDINGS = List.of(
            LS, BDC, GS, PUNISHER, WBC, WKM
    );
    private static final String RECORD_IMPORT_FILE = "recording_import.text";
    private static final String RECORD_EXPORT_FILE = "recording_export.text";
    private static final String BINARY_SALES_FILE = "sales_import.bin";
    private static final String BINARY_SALES_FILE_RANDOM = "sales_import_random.bin";
    private static final String INVALID_FILE_NAME = "dskjgfdlkgj";
    private static final String UPDATE_DATE = "2023-04-10";
    private final List<Recording> recordings = new ArrayList<>();

    private Exercise3 exercise3;

    private static LocalDate between(LocalDate startInclusive, LocalDate endExclusive) {
        long startEpochDay = startInclusive.toEpochDay();
        long endEpochDay = endExclusive.toEpochDay();
        long randomDay = ThreadLocalRandom
                .current()
                .nextLong(startEpochDay, endEpochDay);

        return LocalDate.ofEpochDay(randomDay);
    }

    @Test
    @Order(0)
    @DisplayName("Information")
    void __version() {
        System.out.println("Test uppdaterat " + UPDATE_DATE);
    }

    @Test
    @Order(10)
    @DisplayName("Testar att importera binär försäljningsdata (ej slumpad).")
    void binarySalesDataImportFixed() {

        Map<Integer, Double> data = Map.of(
                202301, 20.0
                , 202302, 12.4
                , 202303, 40.0
                , 202304, 33.2
                , 202305, 70.0
        );
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(BINARY_SALES_FILE))) {

            out.writeInt(10);

            out.writeInt(2023);
            out.writeInt(1);
            out.writeInt(1);
            out.writeDouble(10.5);

            out.writeInt(2023);
            out.writeInt(2);
            out.writeInt(2);
            out.writeDouble(10.4);

            out.writeInt(2023);
            out.writeInt(3);
            out.writeInt(3);
            out.writeDouble(40.0);

            out.writeInt(2023);
            out.writeInt(4);
            out.writeInt(4);
            out.writeDouble(10.5);

            out.writeInt(2023);
            out.writeInt(5);
            out.writeInt(5);
            out.writeDouble(35.0);

            out.writeInt(2023);
            out.writeInt(1);
            out.writeInt(6);
            out.writeDouble(9.5);

            out.writeInt(2023);
            out.writeInt(2);
            out.writeInt(7);
            out.writeDouble(2.0);

            out.writeInt(2023);
            out.writeInt(5);
            out.writeInt(8);
            out.writeDouble(20.0);

            out.writeInt(2023);
            out.writeInt(4);
            out.writeInt(9);
            out.writeDouble(22.7);

            out.writeInt(2023);
            out.writeInt(5);
            out.writeInt(10);
            out.writeDouble(15.0);
        } catch (IOException e) {
            System.err.println("Fel:" + e.getMessage());
        }

        TreeMap<Integer, Double> actualMap = new TreeMap<>(exercise3.importSales(BINARY_SALES_FILE));
        TreeMap<Integer, Double> expectedMap = new TreeMap<>(data);

        assertEquals(expectedMap, actualMap, "Avbildningen innehåller inte rätt data.\n");
    }

    @Test
    @Order(11)
    @DisplayName("Testar att importera binär försäljningsdata (slumpad).")
    void binarySalesDataImportRandom() throws FileNotFoundException {

        Random r = new Random();

        var length = r.nextInt(100) + 10;
        LocalDate start = LocalDate.of(2023, Month.JANUARY, 1);
        LocalDate end = LocalDate.now();
        HashMap<Integer, Double> data = new HashMap<>();
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(BINARY_SALES_FILE_RANDOM))) {
            out.writeInt(length);
            for (int i = 0; i < length; i++) {
                LocalDate random = between(start, end);
                out.writeInt(random.getYear());
                out.writeInt(random.getMonthValue());
                out.writeInt(random.getDayOfMonth());
                double value = r.nextDouble() * 100;
                out.writeDouble(value);
                int key = random.getYear() * 100 + random.getMonthValue();
                var val = data.getOrDefault(key, 0.0);
                data.put(key, val + value);
            }
        } catch (IOException e) {
            System.err.println("Fel:" + e.getMessage());
        }

        Map<Integer, Double> actualMap = exercise3.importSales(BINARY_SALES_FILE_RANDOM);
        for (Integer key : data.keySet()) {
            double expected = data.get(key);
            assertTrue(actualMap.containsKey(key), "Avbildningen innehåller inte en nyckel som förväntades.");
            assertEquals(Math.round(expected), Math.round(actualMap.get(key)));
        }
    }

    private void createImportRecordFile() {
        Charset charset = StandardCharsets.US_ASCII;
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(Path.of(RECORD_IMPORT_FILE), charset))) {
            pw.printf("%d%n", recordings.size());
            for (Recording recording : recordings) {
                pw.printf("%s;%s;%d%n", recording.getArtist(), recording.getTitle(), recording.getYear());
                pw.printf("%d%n", recording.getGenre().size());
                for (String genre : recording.getGenre()) {
                    pw.println(genre);
                }
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
    }

    @Test
    @Order(30)
    @DisplayName("Testar exportRecordings.")
    void exportRecording() {

        recordings.clear();
        recordings.addAll(DEFAULT_RECORDINGS);
        exercise3.setRecordings(DEFAULT_RECORDINGS);

        assertEquals(recordings, exercise3.getRecordings(), "Före import: listan innehåller inte rätt saker.");
        exercise3.exportRecordings(RECORD_EXPORT_FILE);

        StringBuilder sb = new StringBuilder();
        for (Recording recording : recordings) {
            sb.append(String.format("<recording>%n"));
            sb.append(String.format("<artist>%s</artist>%n", recording.getArtist()));
            sb.append(String.format("<title>%s</title>%n", recording.getTitle()));
            sb.append(String.format("<year>%d</year>%n", recording.getYear()));
            sb.append(String.format("<genres>%n"));
            for (String genre : recording.getGenre()) {
                sb.append(String.format("<genre>%s</genre>%n", genre));
            }
            sb.append(String.format("</genres>%n"));
            sb.append(String.format("</recording>%n"));
        }

        try {

            var expected = Arrays.asList(sb.toString().split("\n"));

            var actual = Files.readString(Path.of(RECORD_EXPORT_FILE));

            var ok = expected
                    .stream()
                    .allMatch(actual::contains);
            if (!ok) {
                var missingRows = expected
                        .stream()
                        .filter(s -> !actual.contains(s.trim()))
                        .collect(Collectors.joining("\n"));

                var message = String.format("Följande rader hittades inte:%n%s", missingRows);
                fail(String.format("Någonting verkar saknas i den exporterade filen.%n%s%n", message));
            }
        } catch (java.nio.file.NoSuchFileException f) {
            fail("Ett undantag inträffade...\nTestet kunde inte läsa filen - kolla att den är sparad på med rätt namn.");
        } catch (IOException e) {
            fail("Ett undantag inträffade...:\n" + e);
            e.printStackTrace();
        }
    }

    @Test
    @Order(10)
    @DisplayName("Testar importRecordings (ej slumpad).")
    void importRecordingFixedData() {

        recordings.clear();
        recordings.addAll(DEFAULT_RECORDINGS);
        exercise3.setRecordings(DEFAULT_RECORDINGS);

        createImportRecordFile();

        testImportRecordings();

        exercise3.exportRecordings(RECORD_EXPORT_FILE);
    }

    @Test
    @Order(11)
    @DisplayName("Testar importRecordings (slumpad).")
    void importRecordingRandomData() {
        Random random = new Random();
        var randomRecordings = new ArrayList<Recording>();

        for (int i = 0; i < 1000; i++) {
            randomRecordings.add(new Recording("title" + i, "artist" + random.nextInt(50), 2021, Set.of("genre1", "genre2")));
        }

        recordings.clear();
        recordings.addAll(randomRecordings);
        exercise3.setRecordings(randomRecordings);

        createImportRecordFile();

        testImportRecordings();
        exercise3.exportRecordings(RECORD_EXPORT_FILE);
    }


    @BeforeEach
    void setUp() {
        exercise3 = new Exercise3();
    }

    private void testImportRecordings() {
        exercise3.setRecordings(Collections.emptyList());
        assertEquals(0, exercise3.getRecordings().size(), "Före import: listan är inte tom.");
        exercise3.importRecordings(RECORD_IMPORT_FILE);
        assertEquals(recordings.size(), exercise3.getRecordings().size(), "Efter import: listan innehåller inte rätt antal objekt.");
    }
}
