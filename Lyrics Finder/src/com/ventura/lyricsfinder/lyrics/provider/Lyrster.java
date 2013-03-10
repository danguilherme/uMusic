package com.ventura.lyricsfinder.lyrics.provider;

import com.ventura.lyricsfinder.lyrics.Lyric;
import com.ventura.lyricsfinder.lyrics.LyricNotFoundException;

public class Lyrster extends LyricProvider {

	@Override
	public String decodeLyricHtml(String html, Lyric actualLyric)
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
		return target.trim().replace(' ', '-').toLowerCase();
	}
}
