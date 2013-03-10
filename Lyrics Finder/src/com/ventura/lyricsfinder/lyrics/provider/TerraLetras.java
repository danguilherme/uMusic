package com.ventura.lyricsfinder.lyrics.provider;

import com.ventura.lyricsfinder.lyrics.Lyric;
import com.ventura.lyricsfinder.lyrics.LyricNotFoundException;

public class TerraLetras extends LyricProvider {
	@Override
	public String decodeLyricHtml(String html, Lyric actualLyric)
			throws LyricNotFoundException {
		String lyric;
		String startingElement = "<p>";
		String endingElement = "</p>";

		String header = html.substring(html.indexOf("<h3>") + 4,
				html.indexOf("</h3>"));

		if (header.equalsIgnoreCase("Música não encontrada")) {
			throw new LyricNotFoundException();
		} else if (header.equalsIgnoreCase("Provável música")) {
			html = html.substring(html.indexOf(endingElement, 0)
					+ endingElement.length());
		}

		int indexOfStartingElement = html.indexOf(startingElement);
		int[] lyricsRange = new int[] {
				indexOfStartingElement + startingElement.length(),
				html.indexOf(endingElement, indexOfStartingElement) };
		html = html.trim();
		lyric = html.substring(lyricsRange[0], lyricsRange[1]).trim();
		return lyric;
	}

	@Override
	public String encodeToUrl(String target) {
		return target.trim().replace(' ', '+').replace("%26", "&")
				.toLowerCase();
	}
}
