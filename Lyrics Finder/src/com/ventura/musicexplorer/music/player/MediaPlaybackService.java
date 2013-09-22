package com.ventura.musicexplorer.music.player;

import java.lang.ref.WeakReference;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.MediaStore;
import android.util.Log;

import com.ventura.musicexplorer.entity.music.Track;
import com.ventura.musicexplorer.music.IMediaPlaybackService;

/**
 * Provides "background" audio playback capabilities, allowing the user to
 * switch between activities without stopping playback.
 */
public class MediaPlaybackService extends Service {
	private MediaPlayerHandler mMediaplayerHandler = new MediaPlayerHandler();

	private AudioManager mAudioManager;
	private SharedPreferences mPreferences;

	/* changes events */
	private final String QUEUE_CHANGED = "queue_changed";
	private final String META_CHANGED = "meta_changed";

	/**
	 * Called when the external media is about to be removed or inserted. This
	 * object is set at the {@link #registerExternalStorageListener()} function.
	 */
	private BroadcastReceiver mUnmountReceiver;

	private WakeLock mWakeLock;

	/**
	 * The songs player instance
	 */
	private MultiPlayer mPlayer;
	private Song mCurrentSong;
	private Song mNextSong;

	private Cursor mCursor;
	String[] mCursorCols = new String[] {
			"audio._id AS _id", // index must match IDCOLIDX below
			MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
			MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
			MediaStore.Audio.Media.MIME_TYPE, MediaStore.Audio.Media.ALBUM_ID,
			MediaStore.Audio.Media.ARTIST_ID };

	private int mServiceStartId;
	private final static int IDCOLIDX = 0;

	@Override
	public void onCreate() {
		super.onCreate();

		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mPreferences = getSharedPreferences("Music", 0);

		// Needs to be done in this thread, since otherwise
		// ApplicationContext.getPowerManager() crashes.
		mPlayer = new MultiPlayer(this);
		mPlayer.setHandler(mMediaplayerHandler);

		IntentFilter commandFilter = new IntentFilter();
		commandFilter.addAction("service");
		commandFilter.addAction("toggle_pause");
		commandFilter.addAction("pause");
		commandFilter.addAction("next");
		commandFilter.addAction("prev");
		registerReceiver(mIntentReceiver, commandFilter);

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this
				.getClass().getName());
		mWakeLock.setReferenceCounted(false);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mServiceStartId = startId;

		return START_STICKY;
	}

	/**
	 * The service was called to work
	 */
	private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			/*
			 * String action = intent.getAction(); String cmd =
			 * intent.getStringExtra("command");
			 * 
			 * if (CMDNEXT.equals(cmd) || NEXT_ACTION.equals(action)) {
			 * gotoNext(true); } else if (CMDPREVIOUS.equals(cmd) ||
			 * PREVIOUS_ACTION.equals(action)) { prev(); } else if
			 * (CMDTOGGLEPAUSE.equals(cmd) || TOGGLEPAUSE_ACTION.equals(action))
			 * { if (isPlaying()) { pause(); mPausedByTransientLossOfFocus =
			 * false; } else { play(); } } else if (CMDPAUSE.equals(cmd) ||
			 * PAUSE_ACTION.equals(action)) { pause();
			 * mPausedByTransientLossOfFocus = false; } else if
			 * (CMDPLAY.equals(cmd)) { play(); } else if (CMDSTOP.equals(cmd)) {
			 * pause(); mPausedByTransientLossOfFocus = false; seek(0); } else
			 * if (MediaAppWidgetProvider.CMDAPPWIDGETUPDATE.equals(cmd)) { //
			 * Someone asked us to refresh a set of specific widgets, probably
			 * // because they were just added. int[] appWidgetIds =
			 * intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
			 * mAppWidgetProvider.performUpdate(MediaPlaybackService.this,
			 * appWidgetIds); }
			 */
		}
	};

	/**
	 * Opens the specified file and readies it for playback.
	 * 
	 * @param path
	 *            The full path of the file to be opened.
	 */
	public boolean open(String path) {
		synchronized (this) {
			if (path == null) {
				return false;
			}

			// if mCursor is null, try to associate path with a database cursor
			if (mCursor == null) {

				ContentResolver resolver = getContentResolver();
				Uri uri;
				String where;
				String selectionArgs[];
				if (path.startsWith("content://media/")) {
					uri = Uri.parse(path);
					where = null;
					selectionArgs = null;
				} else {
					uri = MediaStore.Audio.Media.getContentUriForPath(path);
					where = MediaStore.Audio.Media.DATA + "=?";
					selectionArgs = new String[] { path };
				}

				try {
					mCursor = resolver.query(uri, mCursorCols, where,
							selectionArgs, null);
					if (mCursor != null) {
						if (mCursor.getCount() == 0) {
							mCursor.close();
							mCursor = null;
						} else {
							mCursor.moveToNext();

							mCurrentSong = new Song(mCursor.getLong(IDCOLIDX),
									path);
						}
					}
				} catch (UnsupportedOperationException ex) {
				}
			}

			mPlayer.setDataSource(path);
			if (mPlayer.isInitialized()) {
				return true;
			}
			stop();
			return false;
		}
	}

	/**
	 * Replaces the current playlist with a new list, and prepares for starting
	 * playback at the specified position in the list, or a random position if
	 * the specified position is 0.
	 * 
	 * @param list
	 *            The new list of tracks.
	 */
	/*
	 * public void open(long[] list, int position) { synchronized (this) { long
	 * oldId = getAudioId(); int listlength = list.length; boolean newlist =
	 * true; if (mPlayListLen == listlength) { // possible fast path: list might
	 * be the same newlist = false; for (int i = 0; i < listlength; i++) { if
	 * (list[i] != mPlayList[i]) { newlist = true; break; } } } if (newlist) {
	 * // addToPlayList(list, -1); notifyChange(QUEUE_CHANGED); } int oldpos =
	 * mPlayPos; if (position >= 0) { // mPlayPos = position; } else { //
	 * mPlayPos = mRand.nextInt(mPlayListLen); } // mHistory.clear();
	 * 
	 * saveBookmarkIfNeeded(); openCurrentAndNext(); if (oldId != getAudioId())
	 * { notifyChange(META_CHANGED); } } }
	 */

	/**
	 * Starts playback of a previously opened file.
	 */
	public void play() {
		if (mPlayer.isInitialized()) {
			// if we are at the end of the song, go to the next song first
			long duration = mPlayer.getDuration();
			if (isRepeating() && duration > 2000
					&& mPlayer.getCurrentPosition() >= duration - 2000) {
				// gotoNext(true);
			}

			mPlayer.start();
		}
	}

	public void prev() {
		// TODO: Implement method
	}

	public void next() {
		// TODO: Implement method
	}

	private boolean isPlaying() {
		return mPlayer.isPlaying();
	}

	private void pause() {
		mPlayer.pause();
	}

	/**
	 * Stops playback.
	 */
	private void stop() {
		if (mPlayer.isInitialized()) {
			mPlayer.stop();
		}

		if (mCursor != null) {
			mCursor.close();
			mCursor = null;
		}

		stopForeground(false);
	}

	public Song getCurrentSong() {
		return mCurrentSong;
	}

	public Track getCurrentTrack() {
		return mCurrentSong.getTrack();
	}

	final String PREF_IS_SHUFFLE = this.getClass().getName()
			+ ".PREF_IS_SHUFFLE";
	final String PREF_IS_REPEAT = this.getClass().getName() + ".PREF_IS_REPEAT";

	public void setShuffle(boolean shuffle) {
		Editor editor = mPreferences.edit();
		editor.putBoolean(PREF_IS_SHUFFLE, shuffle);
		editor.commit();
	}

	public boolean isShuffling() {
		return mPreferences.getBoolean(PREF_IS_SHUFFLE, false);
	}

	public void setRepeat(boolean repeat) {
		Editor editor = mPreferences.edit();
		editor.putBoolean(PREF_IS_REPEAT, repeat);
		editor.commit();
	}

	public boolean isRepeating() {
		return mPreferences.getBoolean(PREF_IS_REPEAT, false);
	}

	public long getDuration() {
		return this.mPlayer.getDuration();
	}

	public long getCurrentPosition() {
		return this.mPlayer.getCurrentPosition();
	}

	private void setNextTrack() {
		// mNextPlayPosition = getNextPosition(false);
		// if (mNextPlayPos >= 0) {
		// long id = mPlayList[mNextPlayPos];
		// mPlayer.setNextDataSource(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
		// + "/" + id);
		// }
		mNextSong = new Song(0, null);
		mPlayer.setNextDataSource(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
				+ "/" + mNextSong.getId());
	}

	private long getAudioId() {
		synchronized (this) {
			if (mCurrentSong != null && mPlayer.isInitialized()) {
				return mCurrentSong.getId();
			}
		}
		return -1;
	}

	/**
	 * Gets a cursor positioned in the line of the song with the specified id.
	 * 
	 * @param lid
	 *            The song id
	 * @return A cursor already positioned at the song's line, or `null` if it
	 *         was not found
	 */
	private Cursor getCursorForId(long lid) {
		String id = String.valueOf(lid);

		Cursor c = getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mCursorCols,
				"_id=" + id, null, null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}

	/*
	 * By making this a static class with a WeakReference to the Service, we
	 * ensure that the Service can be GCd even when the system process still has
	 * a remote reference to the stub.
	 */
	static class ServiceStub extends IMediaPlaybackService.Stub {
		WeakReference<MediaPlaybackService> mService;

		ServiceStub(MediaPlaybackService service) {
			mService = new WeakReference<MediaPlaybackService>(service);
		}

		public void openFile(String path) {
			mService.get().open(path);
		}

		public void open(long[] list, int position) {
			// mService.get().open(list, position);
		}

		public int getQueuePosition() {
			// return mService.get().getQueuePosition();
			return 0;
		}

		public void setQueuePosition(int index) {
			// mService.get().setQueuePosition(index);
		}

		public boolean isPlaying() {
			return mService.get().isPlaying();
		}

		public void stop() {
			mService.get().stop();
		}

		public void pause() {
			mService.get().pause();
		}

		public void play() {
			mService.get().play();
		}

		public void prev() {
			mService.get().prev();
		}

		public void next() {
			mService.get().next();
		}

		public String getTrackName() {
			return mService.get().getCurrentTrack().getTitle();
		}

		public String getAlbumName() {
			// return mService.get().getAlbumName();
			return "Album Name";
		}

		public long getAlbumId() {
			// return mService.get().getAlbumId();
			return 1;
		}

		public String getArtistName() {
			return mService.get().getCurrentTrack().getArtist().getName();
		}

		public long getArtistId() {
			return mService.get().getCurrentTrack().getArtist().getId();
		}

		public void enqueue(long[] list, int action) {
			// mService.get().enqueue(list, action);
		}

		public long[] getQueue() {
			// return mService.get().getQueue();
			return new long[] { 1, 2 };
		}

		public void moveQueueItem(int from, int to) {
			// mService.get().moveQueueItem(from, to);
		}

		public String getPath() {
			return mService.get().getCurrentSongPath();
		}

		public long getAudioId() {
			return mService.get().getAudioId();
		}

		public long position() {
			return mService.get().getCurrentPosition();
		}

		public long duration() {
			return mService.get().getDuration();
		}

		public long seek(long pos) {
			// return mService.get().seek(pos);
			return 0;
		}

		public void setShuffleMode(boolean isShuffle) {
			mService.get().setShuffle(isShuffle);
		}

		public boolean getShuffleMode() {
			return mService.get().isShuffling();
		}

		public void setRepeatMode(boolean isRepeat) {
			mService.get().setRepeat(isRepeat);
		}

		public boolean getRepeatMode() {
			return mService.get().isRepeating();
		}

		public int removeTracks(int first, int last) {
			// return mService.get().removeTracks(first, last);
			return 0;
		}

		public int removeTrack(long id) {
			// return mService.get().removeTrack(id);
			return 0;
		}

		public int getMediaMountedCount() {
			// return mService.get().getMediaMountedCount();
			return 1;
		}

		public int getAudioSessionId() {
			// return mService.get().getAudioSessionId();
			return -1;
		}
	}

	private final IBinder mBinder = new ServiceStub(this);

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(this.getClass().getName(), "BIND");
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.d(this.getClass().getName(), "UNBIND");
		return true;
	}

	@Override
	public void onDestroy() {
		Log.d(this.getClass().getName(), "DESTROY");
		unregisterReceiver(mIntentReceiver);
		super.onDestroy();
	}

	public String getCurrentSongPath() {
		if (mCurrentSong != null && mPlayer.isInitialized()) {
			return mCurrentSong.getPath();
		}
		return null;
	}

	/**
	 * Notify the change-receivers that something has changed. The intent that
	 * is sent contains the following data for the currently playing track: "id"
	 * - Integer: the database row ID "artist" - String: the name of the artist
	 * "album" - String: the name of the album "track" - String: the name of the
	 * track The intent has an action that is one of
	 * "com.android.music.metachanged" "com.android.music.queuechanged",
	 * "com.android.music.playbackcomplete" "com.android.music.playstatechanged"
	 * respectively indicating that a new track has started playing, that the
	 * playback queue has changed, that playback has stopped because the last
	 * file in the list has been played, or that the play-state changed
	 * (paused/resumed).
	 */
	private void notifyChange(String what) {

		Intent i = new Intent(what);
		i.putExtra("id", Long.valueOf(getAudioId()));
		// i.putExtra("artist", getArtistName());
		// i.putExtra("album", getAlbumName());
		// i.putExtra("track", getTrackName());
		i.putExtra("playing", isPlaying());
		sendStickyBroadcast(i);
	}
}