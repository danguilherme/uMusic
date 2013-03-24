package com.ventura.musicexplorer.lyrics.provider;

import java.util.Locale;

import com.ventura.musicexplorer.lyrics.Lyric;
import com.ventura.musicexplorer.lyrics.LyricNotFoundException;

public class AzLyrics extends LyricProvider {

	@Override
	public String decodeLyricHtml(String html, Lyric actualLyric)
			throws LyricNotFoundException {
		String lyric;
		String startingElement = "<!-- start of lyrics -->";
		String endingElement = "<!-- end of lyrics -->";
		int indexOfStartingElement = html.indexOf(startingElement);

		int[] lyricsRange = new int[] {
				indexOfStartingElement + startingElement.length(),
				html.indexOf(endingElement, indexOfStartingElement) };

		if (indexOfStartingElement < 0) {
			throw new LyricNotFoundException();
		}

		html = html.trim();
		lyric = html.substring(lyricsRange[0], lyricsRange[1]);

		return lyric.replace("<i>", "").replace("</i>", "").trim();
	}

	@Override
	public String encodeToUrl(String target) {
		return target.trim().replace(" ", "").toLowerCase(Locale.ENGLISH);
	}

}
