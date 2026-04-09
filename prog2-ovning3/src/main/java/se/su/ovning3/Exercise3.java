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

	}

	public void exportRecordings(String fileName) {

		try {
			FileWriter fileWriter = new FileWriter(fileName);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			// printWriter.println(recording);
			// printWriter.println(new ArrayList<String>());
			StringBuilder sb = new StringBuilder("<recording>");
			System.out.println(sb);
			sb.append(" " + "<artist>John Coltrane</artist>");

			for (int i = 0; i < recordings.size(); i++) {
				sb.append("<recording>");
				sb.append(" " + "<artist>" + recordings.get(i).getArtist() + "</artist>");
				sb.append(" " + "<title>" + recordings.get(i).getTitle() + "</title>");
				sb.append(" " + "<year>" + recordings.get(i).getYear() + "</year>");

				for (int r = 0; r < recordings.get(i).getGenre().size(); r++) {
					// Inte klar med forloopen

				}

			}

			printWriter.close();
			fileWriter.close();
		} catch (FileNotFoundException e) {
			System.out.printf("%s not found%n", fileName);

		} catch (IOException e) {
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

				Recording recording = new Recording(parsad[0], parsad[1], Integer.parseInt(parsad[2]), genres);
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
