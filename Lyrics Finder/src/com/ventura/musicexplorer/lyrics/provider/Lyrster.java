package com.ventura.musicexplorer.lyrics.provider;

import java.util.Locale;

import com.ventura.musicexplorer.entity.music.Lyrics;
import com.ventura.musicexplorer.lyrics.LyricNotFoundException;

public class Lyrster extends LyricProvider {

	@Override
	public String decodeLyricHtml(String html, Lyrics actualLyric)
			throws LyricNotFoundException {
		String lyric;
		String startingElement = "<div id=\"lyrics\">";
		String endingElement = "</div>";
		int indexOfStartingElement = html.indexOf(startingElement);

		int[] lyricsRange = new int[] {
				indexOfStartingElement + startingElement.length(),
				html.indexOf(endingElement, indexOfStartingElement) };
		html = html.trim();
		lyric = html.substring(lyricsRange[0], lyricsRange[1]);
		if (lyric
				.equalsIgnoreCase("We do not have the complete song's lyrics just yet. ")) {
			throw new LyricNotFoundException();
		}
		return lyric.trim();
	}

	@Override
	public String encodeToUrl(String target) {
		return target.trim().replace(' ', '-').toLowerCase(Locale.ENGLISH);
	}
}
