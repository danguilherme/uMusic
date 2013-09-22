package com.ventura.umusic.lyrics.provider;

import java.util.Locale;

import com.ventura.umusic.entity.music.Lyrics;
import com.ventura.umusic.lyrics.LyricNotFoundException;

public class AzLyrics extends LyricProvider {

	@Override
	public String decodeLyricHtml(String html, Lyrics actualLyric)
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
