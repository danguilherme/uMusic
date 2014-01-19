package com.ventura.umusic.ui.music;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.ventura.umusic.R;
import com.ventura.umusic.entity.music.Track;
import com.ventura.umusic.music.MusicPlayer;
import com.ventura.umusic.music.TracksManager;
import com.ventura.umusic.ui.BaseActivity;

@EActivity(R.layout.activity_music_player)
public class MusicPlayerActivity extends BaseActivity {

	@ViewById(R.id.btn_play)
	protected Button btnPlay;

	@ViewById(R.id.btn_pause)
	protected Button btnPause;

	@ViewById(R.id.btn_stop)
	protected Button btnStop;

	@ViewById(R.id.btn_forward)
	protected Button btnForward;

	@ViewById(R.id.btn_backward)
	protected Button btnBackward;

	@ViewById(R.id.txt_music_title)
	protected EditText txtSongTitle;

	@ViewById(R.id.txt_artist_name)
	protected EditText txtArtist;

	@ViewById(R.id.txt_music_album)
	protected EditText txtAlbum;
	
	private MusicPlayer musicPlayer;
	private TracksManager tracksManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		musicPlayer = new MusicPlayer(getApplicationContext());
		tracksManager = new TracksManager(this);
		decipherIntent();
	}
	
	@AfterViews
	protected void afterViews(){
		btnBackward.setEnabled(false);
		btnStop.setEnabled(false);
		btnForward.setEnabled(false);
	}

	private void decipherIntent() {
		Intent intent = getIntent();
		String action = intent.getAction();

		if (Intent.ACTION_MAIN.equals(action))
			return;
	}

	@Click(R.id.btn_play)
	public void play() {
		musicPlayer.play(tracksManager.getTracks(false).get((int)(Math.random() * 150)));

		Track currentPlaying = musicPlayer.getCurrentPlaying();
		txtSongTitle.setText(currentPlaying.getTitle());
		txtAlbum.setText(currentPlaying.getAlbumTitle());
		txtArtist.setText(currentPlaying.getArtistName());
	}

	@Click(R.id.btn_pause)
	public void pause() {
		musicPlayer.pause();
	}
}