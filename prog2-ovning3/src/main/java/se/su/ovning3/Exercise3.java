package se.su.ovning3;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Exercise3 {

	private final List<Recording> recordings = new ArrayList<>();

	public static void main(String[] args) {
		Exercise3 exercise3 = new Exercise3();
		exercise3.importRecordings("recording_input.txt");
		exercise3.exportRecordings("recording_output.txt");

	}

	public void exportRecordings(String fileName) {

		try {
			PrintWriter writer = new PrintWriter(new FileWriter(fileName));
			StringBuilder sb = new StringBuilder();
			for (Recording r : recordings) {
				sb.append(String.format("<recording>%n  <artist>%s</artist>%n", r.getArtist()));
				sb.append(String.format("  <title>%s</title>%n", r.getTitle()));
				sb.append(String.format("  <year>%d</year>%n  <genres>%n", r.getYear()));
				for (String g : r.getGenre()) {
					sb.append(String.format("    <genre>%s</genre>%n", g));
				}
				sb.append(String.format("  </genres>%n</recording>%n"));
			}
			writer.print(sb);
			writer.close();
		} catch (IOException e) {
			System.out.print("Stack trace: ");
			e.printStackTrace();
		}
	}

	public void importRecordings(String fileName) {
		try {
			FileReader fileReader = new FileReader(fileName);
			BufferedReader reader = new BufferedReader(fileReader);
			String line;
			line = reader.readLine();
			while ((line = reader.readLine()) != null) {

				String[] parsad = line.split(";");

				line = reader.readLine();
				int numberOfgenres = Integer.parseInt(line);

				Set<String> genres = new HashSet<>();
				for (int i = 0; i < numberOfgenres; i++) {
					line = reader.readLine();
					genres.add(line);
				}

				Recording recording = new Recording(parsad[1], parsad[0], Integer.parseInt(parsad[2]), genres);
				recordings.add(recording);
			}
			fileReader.close();
			reader.close();
		} catch (FileNotFoundException e) {
			System.out.printf("%s not found%n", fileName);

		} catch (IOException e) {
			e.printStackTrace();

		}

	}

	public Map<Integer, Double> importSales(String fileName) {
		return null;
	}

	public List<Recording> getRecordings() {
		return Collections.unmodifiableList(recordings);
	}

	public void setRecordings(List<Recording> recordings) {
		this.recordings.clear();
		this.recordings.addAll(recordings);
	}
}
