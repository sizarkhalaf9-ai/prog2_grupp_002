package se.su.ovning3;

import java.util.Collection;
import java.util.Set;

public class Recording{
	private final int year;
	private final String artist;
	private final String title;
	private final Set<String> genre;

	public Recording(String title, String artist, int year, Set<String> genre) {
		this.title = title;
		this.year = year;
		this.artist = artist;
		this.genre = genre;
	}

	public String getArtist() {
		return artist;
	}

	public Collection<String> getGenre() {
		return genre;
	}

	public String getTitle() {
		return title;
	}

	public int getYear() {
		return year;
	}

	@Override
	public String toString() {
		return String.format("{ %s | %s | %d | %s }", artist, title, year, genre);
	}
}
