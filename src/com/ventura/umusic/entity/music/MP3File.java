package com.ventura.umusic.entity.music;

import java.io.IOException;
import java.io.Serializable;

import org.farng.mp3.TagException;
import org.farng.mp3.id3.AbstractID3v2Frame;
import org.farng.mp3.id3.FrameBodyUSLT;

import android.util.Log;

public class MP3File implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String TAG = getClass().getName();

	// Identifier + null + language + null
	private static final String FRAME_ID_UNSYNC_LYRICS = "USLT" + ((char) 0)
			+ "XXX" + ((char) 0);

	private org.farng.mp3.MP3File mp3File;
	/**
	 * Saves the state of the track when it was first loaded
	 */
	private Track originalTrack;
	private Track track;

	public MP3File(org.farng.mp3.MP3File file) {
		this.mp3File = file;
		loadFileTags();
	}

	public MP3File(org.farng.mp3.MP3File mp3File, Track fileTrack) {
		this(mp3File);

		this.track = fileTrack;
		this.originalTrack = fileTrack;
	}

	public boolean isModified() {
		return track.equals(originalTrack);
	}

	private void loadFileTags() {
		loadTrack();
	}

	private void loadTrack() {
		if (track == null)
			track = new Track(getSongTitle(), getTrackPosition(), "0");
		track.setTitle(getSongTitle());
		track.setPosition(getTrackPosition());

		track.setArtistName(getArtist());
		track.setAlbumTitle(getAlbumTitle());
		track.setLyrics(getLyrics());

		originalTrack = track;
	}

	public Track getTrack() {
		return track;
	}

	public org.farng.mp3.MP3File getMp3File() {
		return mp3File;
	}

	public void save() throws IOException, TagException {
			mp3File.save();
	}

	// Get track info from tags
	public String getArtist() {
		String item = "";
		try {
			if (mp3File.hasID3v1Tag()) {
				item = mp3File.getID3v1Tag().getArtist();
				if (item != null && item != "")
					return item;
			}
		} catch (Exception e) {
			Log.e(TAG, "Error when reading artist from ID3v1 tag.", e);
		}

		try {
			if (mp3File.hasID3v1Tag()) {
				item = mp3File.getID3v1Tag().getLeadArtist();
				if (item != null && item != "")
					return item;
			}
		} catch (Exception e) {
			Log.e(TAG, "Error when reading artist from ID3v1 tag.", e);
		}

		try {
			if (mp3File.hasID3v2Tag()) {
				item = mp3File.getID3v2Tag().getLeadArtist();
				if (item != null && item != "")
					return item;
			}
		} catch (Exception e) {
			Log.e(TAG, "Error when reading artist from ID3v2 tag.", e);
		}

		return item;
	}

	public void setArtist(String artist) {
		try {
			if (mp3File.hasID3v1Tag())
				mp3File.getID3v1Tag().setArtist(artist);
		} catch (Exception e) {
			Log.e(TAG, "Error when setting artist from ID3v1 tag.", e);
		}

		try {
			if (mp3File.hasID3v1Tag())
				mp3File.getID3v1Tag().setLeadArtist(artist);
		} catch (Exception e) {
			Log.e(TAG, "Error when setting lead artist from ID3v1 tag.", e);
		}

		try {
			if (mp3File.hasID3v2Tag())
				mp3File.getID3v2Tag().setLeadArtist(artist);
		} catch (Exception e) {
			Log.e(TAG, "Error when setting lead artist from ID3v2 tag.", e);
		}
	}

	public String getAlbumTitle() {
		String item = "";

		try {
			if (mp3File.hasID3v1Tag()) {
				item = mp3File.getID3v1Tag().getAlbum();
				if (item != null && item != "")
					return item;
			}
		} catch (Exception e) {
			Log.e(TAG, "Error when reading album from ID3v1 tag.", e);
		}

		try {
			if (mp3File.hasID3v2Tag()) {
				item = mp3File.getID3v2Tag().getAlbumTitle();
				if (item != null && item != "")
					return item;
			}
		} catch (Exception e) {
			Log.e(TAG, "Error when reading album from ID3v2 tag.", e);
		}

		return item;
	}

	public void setAlbumTitle(String albumTitle) {
		try {
			if (mp3File.hasID3v1Tag())
				mp3File.getID3v1Tag().setAlbum(albumTitle);
		} catch (Exception e) {
			Log.e(TAG, "Error when setting artist from ID3v1 tag.", e);
		}

		try {
			if (mp3File.hasID3v1Tag())
				mp3File.getID3v1Tag().setAlbumTitle(albumTitle);
		} catch (Exception e) {
			Log.e(TAG, "Error when setting lead artist from ID3v1 tag.", e);
		}

		try {
			if (mp3File.hasID3v2Tag())
				mp3File.getID3v2Tag().setAlbumTitle(albumTitle);
		} catch (Exception e) {
			Log.e(TAG, "Error when setting lead artist from ID3v2 tag.", e);
		}
	}

	public String getSongTitle() {
		String item = "";

		try {
			if (mp3File.hasID3v1Tag()) {
				item = mp3File.getID3v1Tag().getTitle();
				if (item != null && item != "")
					return item;
			}
		} catch (Exception e) {
			Log.e(TAG, "Error when reading title from ID3v1 tag.", e);
		}

		try {
			if (mp3File.hasID3v2Tag()) {
				item = mp3File.getID3v2Tag().getSongTitle();
				if (item != null && item != "")
					return item;
			}
		} catch (Exception e) {
			Log.e(TAG, "Error when reading title from ID3v2 tag.", e);
		}

		return item;
	}

	public void setSongTitle(String songTitle) {
		try {
			if (mp3File.hasID3v1Tag())
				mp3File.getID3v1Tag().setTitle(songTitle);
		} catch (Exception e) {
			Log.e(TAG, "Error when setting title from ID3v1 tag.", e);
		}

		try {
			if (mp3File.hasID3v1Tag())
				mp3File.getID3v1Tag().setSongTitle(songTitle);
		} catch (Exception e) {
			Log.e(TAG, "Error when setting song title artist from ID3v1 tag.", e);
		}

		try {
			if (mp3File.hasID3v2Tag())
				mp3File.getID3v2Tag().setSongTitle(songTitle);
		} catch (Exception e) {
			Log.e(TAG, "Error when setting song title from ID3v2 tag.", e);
		}
	}

	public String getTrackPosition() {
		String item = "";
		try {
			if (mp3File.hasID3v1Tag()) {
				item = mp3File.getID3v1Tag().getTrackNumberOnAlbum();
				if (item != null && item != "")
					return item;
			}
		} catch (Exception e) {
			Log.e(TAG, "Error when reading track position from ID3v1 tag.", e);
		}

		try {
			if (mp3File.hasID3v2Tag()) {
				item = mp3File.getID3v2Tag().getTrackNumberOnAlbum();
				if (item != null && item != "")
					return item;
			}
		} catch (Exception e) {
			Log.e(TAG, "Error when reading track position from ID3v2 tag.", e);
		}

		return item;
	}

	public void setTrackPosition(String position) {
		try {
			if (mp3File.hasID3v1Tag())
				mp3File.getID3v1Tag().setTrackNumberOnAlbum(position);
		} catch (Exception e) {
			Log.e(TAG, "Error when setting track number on album from ID3v1 tag.", e);
		}

		try {
			if (mp3File.hasID3v2Tag())
				mp3File.getID3v2Tag().setTrackNumberOnAlbum(position);
		} catch (Exception e) {
			Log.e(TAG, "Error when setting track number on album from ID3v2 tag.", e);
		}
	}

	public String getComposer() {
		String item = "";

		try {
			if (mp3File.hasID3v1Tag()) {
				item = mp3File.getID3v1Tag().getAuthorComposer();
				if (item != null && item != "")
					return item;
			}
		} catch (Exception e) {
			Log.e(TAG, "Error when reading composer from ID3v1 tag.", e);
		}

		try {
			if (mp3File.hasID3v2Tag()) {
				item = mp3File.getID3v2Tag().getAuthorComposer();
				if (item != null && item != "")
					return item;
			}
		} catch (Exception e) {
			Log.e(TAG, "Error when reading composer from ID3v2 tag.", e);
		}

		return item;
	}

	public void setComposer(String composer) {
		try {
			if (mp3File.hasID3v1Tag())
				mp3File.getID3v1Tag().setAuthorComposer(composer);
		} catch (Exception e) {
			Log.e(TAG, "Error when setting author composer from ID3v1 tag.", e);
		}

		try {
			if (mp3File.hasID3v2Tag())
				mp3File.getID3v2Tag().setAuthorComposer(composer);
		} catch (Exception e) {
			Log.e(TAG, "Error when setting author composer from ID3v2 tag.", e);
		}
	}

	public String getSongComment() {
		String item = "";

		try {
			if (mp3File.hasID3v1Tag()) {
				item = mp3File.getID3v1Tag().getComment();
				if (item != null && item != "")
					return item;
			}
		} catch (Exception e) {
			Log.e(TAG, "Error when reading comment from ID3v1 tag.", e);
		}

		try {
			if (mp3File.hasID3v1Tag()) {
				item = mp3File.getID3v1Tag().getSongComment();
				if (item != null && item != "")
					return item;
			}
		} catch (Exception e) {
			Log.e(TAG, "Error when reading comment from ID3v1 tag.", e);
		}

		try {
			if (mp3File.hasID3v2Tag()) {
				item = mp3File.getID3v2Tag().getSongComment();
				if (item != null && item != "")
					return item;
			}
		} catch (Exception e) {
			Log.e(TAG, "Error when reading comment from ID3v2 tag.", e);
		}

		return item;
	}

	public void setSongComment(String comment) {
		try {
			if (mp3File.hasID3v1Tag())
				mp3File.getID3v1Tag().setComment(comment);
		} catch (Exception e) {
			Log.e(TAG, "Error when setting comment from ID3v1 tag.", e);
		}

		try {
			if (mp3File.hasID3v1Tag())
				mp3File.getID3v1Tag().setSongComment(comment);
		} catch (Exception e) {
			Log.e(TAG, "Error when setting song comment from ID3v1 tag.", e);
		}

		try {
			if (mp3File.hasID3v2Tag())
				mp3File.getID3v2Tag().setSongComment(comment);
		} catch (Exception e) {
			Log.e(TAG, "Error when setting song comment from ID3v2 tag.", e);
		}
	}

	public byte getSongGenre() {
		byte item = -1;
		try {
			if (mp3File.hasID3v1Tag()) {
				item = mp3File.getID3v1Tag().getGenre();
				return item;
			}
		} catch (Exception e) {
			Log.e(TAG, "Error when reading genre from ID3v1 tag.", e);
		}

		try {
			if (mp3File.hasID3v1Tag()) {
				item = Byte.valueOf(mp3File.getID3v1Tag().getSongGenre());
				return item;
			}
		} catch (Exception e) {
			Log.e(TAG, "Error when reading genre from ID3v1 tag.");
		}

		try {
			if (mp3File.hasID3v2Tag()) {
				item = Byte.valueOf(mp3File.getID3v1Tag().getSongGenre());
				return item;
			}
		} catch (Exception e) {
			Log.e(TAG, "Error when reading genre from ID3v2 tag.", e);
		}

		return item;
	}

	public void setSongGenre(byte genre) {
		try {
			if (mp3File.hasID3v1Tag())
				mp3File.getID3v1Tag().setGenre(genre);
		} catch (Exception e) {
			Log.e(TAG, "Error when setting genre from ID3v1 tag.", e);
		}

		try {
			if (mp3File.hasID3v1Tag())
				mp3File.getID3v1Tag().setSongGenre(String.valueOf(genre));
		} catch (Exception e) {
			Log.e(TAG, "Error when setting song genre from ID3v1 tag.", e);
		}

		try {
			if (mp3File.hasID3v2Tag())
				mp3File.getID3v2Tag().setSongGenre(String.valueOf(genre));
		} catch (Exception e) {
			Log.e(TAG, "Error when setting song genre from ID3v2 tag.", e);
		}
	}

	public String getYearReleased() {
		String item = "";

		try {
			if (mp3File.hasID3v1Tag()) {
				item = mp3File.getID3v1Tag().getYear();
				if (item != null && item != "")
					return item;
			}
		} catch (Exception e) {
			Log.e(TAG, "Error when reading year from ID3v1 tag.", e);
		}

		try {
			if (mp3File.hasID3v1Tag()) {
				item = mp3File.getID3v1Tag().getYearReleased();
				if (item != null && item != "")
					return item;
			}
		} catch (Exception e) {
			Log.e(TAG, "Error when reading year released from ID3v1 tag.", e);
		}

		try {
			if (mp3File.hasID3v2Tag()) {
				item = mp3File.getID3v2Tag().getYearReleased();
				if (item != null && item != "")
					return item;
			}
		} catch (Exception e) {
			Log.e(TAG, "Error when reading year released from ID3v2 tag.", e);
		}

		return item;
	}

	public void setYearReleased(String year) {
		try {
			if (mp3File.hasID3v1Tag())
				mp3File.getID3v1Tag().setYear(year);
		} catch (Exception e) {
			Log.e(TAG, "Error when setting year from ID3v1 tag.", e);
		}

		try {
			if (mp3File.hasID3v1Tag())
				mp3File.getID3v1Tag().setYearReleased(year);
		} catch (Exception e) {
			Log.e(TAG, "Error when setting year released from ID3v1 tag.", e);
		}

		try {
			if (mp3File.hasID3v2Tag())
				mp3File.getID3v2Tag().setYearReleased(year);
		} catch (Exception e) {
			Log.e(TAG, "Error when setting year released from ID3v2 tag.", e);
		}
	}

	public String getLyrics() {
		String item = "";
		try {
			if (mp3File.hasID3v2Tag()) {
				item = mp3File.getID3v2Tag().getSongLyric();
				if (item != null && item != "")
					return item;
			}
		} catch (Exception e) {
			Log.e(TAG, "Error when reading lyrics from ID3v2 tag.", e);
		}

		try {
			if (mp3File.hasID3v2Tag()) {
				AbstractID3v2Frame lyricsTagFrame = mp3File.getID3v2Tag()
						.getFrame(FRAME_ID_UNSYNC_LYRICS);
				if (lyricsTagFrame != null) {
					item = ((FrameBodyUSLT) lyricsTagFrame.getBody())
							.getLyric();
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "Error when reading lyrics from ID3v2 tag.", e);
		}

		try {
			if (mp3File.hasLyrics3Tag()) {
				item = mp3File.getLyrics3Tag().getSongLyric();
				if (item != null && item != "")
					return item;
			}
		} catch (Exception e) {
			Log.e(TAG, "Error when reading lyrics from Lyrics3 tag.", e);
		}

		return item;
	}

	public void setLyrics(String lyrics) {
		try {
			if (mp3File.hasLyrics3Tag())
				mp3File.getLyrics3Tag().setSongLyric(lyrics);
		} catch (Exception e) {
			Log.e(TAG, "Error when setting lyric from Lyrics3 tag.", e);
		}

		try {
			if (mp3File.hasID3v2Tag())
				mp3File.getID3v2Tag().setSongLyric(lyrics);
		} catch (Exception e) {
			Log.e(TAG, "Error when setting song lyric from ID3v2 tag.", e);
		}

		try {
			if (mp3File.hasID3v2Tag()) {
				AbstractID3v2Frame lyricsTagFrame = mp3File.getID3v2Tag()
						.getFrame(FRAME_ID_UNSYNC_LYRICS);
				if (lyricsTagFrame != null)
					((FrameBodyUSLT) lyricsTagFrame.getBody()).setLyric(lyrics);
			}
		} catch (Exception e) {
			Log.e(TAG, "Error when setting lyrcs on body of ID3v2 tag.", e);
		}
	}
}
