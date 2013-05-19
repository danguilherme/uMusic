package com.ventura.musicexplorer.ui.release;

import com.googlecode.androidannotations.annotations.EActivity;
import com.ventura.musicexplorer.R;
import com.ventura.musicexplorer.discogs.entity.ArtistRelease;
import com.ventura.musicexplorer.discogs.entity.Master;
import com.ventura.musicexplorer.ui.BaseActivity;

@EActivity(R.layout.master_info)
public class MasterViewerActivity extends BaseActivity {
	
	ArtistRelease artistRelease;
	Master master;
}
