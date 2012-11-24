package com.ventura.lyricsfinder.lyrics.provider;

import com.ventura.lyricsfinder.lyrics.Lyric;
import com.ventura.lyricsfinder.lyrics.LyricNotFoundException;

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
		return target.trim().replace(" ", "").toLowerCase();
	}

}
