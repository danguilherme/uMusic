package com.ventura.lyricsfinder.lyrdb;

public enum QueryType {
	/**
	 * Query represents the exact name of the song name or the exact beginning
	 * of the song name. Matched results will be returned.
	 */
	TrackName,
	/**
	 * Query represents the exact name of the artist/band or the exact
	 * begginning of the artist. If your query is "Elton John", lyrics for
	 * "Elton John", "Elton John feat. artist", "Elton John with artist" will be
	 * returned, for example.
	 */
	Artist,
	/**
	 * Query must be "artist|trackname". "|" is a vertical bar character and
	 * there aren't spaces around the vertical bar. Artist and Trackname must be
	 * supplied in the specified order or no results will be matched
	 */
	Match,
	/**
	 * Query represent part of the song lyrics. Query may be separate words, a
	 * full verse... A maximum of 50 lyrics will be returned, ordered by
	 * relevance, being the first the one with more relevance.
	 */
	InLyrics,
	/**
	 * Query represent a flexible string with enough information (trackname
	 * and/or artist, part of them) to identify the lyrics. Examples:
	 * "Simply Red - Sunrise", "Sunrise - Simply Red",
	 * "Sunrise (Home) - Simply Red", "Simply Red - Sunrise (feat. artist)". A
	 * maximum of 50 lyrics will be returned, ordered by relevance. "Fullt" is
	 * certainly a great way of matching results when metadata from MP3 is not
	 * very accurate, or you have the song title in a non-standard format. Note
	 * that in the query string should be enough information (the keywords order
	 * doesn't matter, or if there are more keyword than needed).
	 */
	FullT
}
