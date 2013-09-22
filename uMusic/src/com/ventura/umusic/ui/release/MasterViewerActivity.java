package com.ventura.umusic.ui.release;

import com.googlecode.androidannotations.annotations.EActivity;
import com.ventura.umusic.R;
import com.ventura.umusic.discogs.entity.ArtistRelease;
import com.ventura.umusic.discogs.entity.Master;
import com.ventura.umusic.ui.BaseActivity;

@EActivity(R.layout.master_info)
public class MasterViewerActivity extends BaseActivity {
	
	ArtistRelease artistRelease;
	Master master;
}
