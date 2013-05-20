package com.ventura.musicexplorer.ui.artist;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import oauth.signpost.OAuthConsumer;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.ClipboardManager;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.googlecode.androidannotations.annotations.res.AnimationRes;
import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.ventura.androidutils.exception.LazyInternetConnectionException;
import com.ventura.androidutils.exception.NoInternetConnectionException;
import com.ventura.musicexplorer.R;
import com.ventura.musicexplorer.business.ArtistService;
import com.ventura.musicexplorer.constants.GlobalConstants;
import com.ventura.musicexplorer.entity.Image;
import com.ventura.musicexplorer.entity.artist.Artist;
import com.ventura.musicexplorer.ui.BaseActivity;
import com.ventura.musicexplorer.ui.release.ReleasesListActivity_;
import com.ventura.musicexplorer.ui.widget.ButtonGroup;
import com.ventura.musicexplorer.ui.widget.KeyValuePanel;
import com.ventura.musicexplorer.util.ImageDownloaderTask;
import com.ventura.musicexplorer.util.ImageLoader;

@EActivity(R.layout.artist_info)
public class ArtistViewerActivity extends BaseActivity implements
		OnItemSelectedListener {
	final String TAG = getClass().getName();

	@ViewById(R.id.loadingArtistInfoProgressBar)
	ProgressBar mActivityLoadingBar;

	@ViewById(R.id.artist_info)
	LinearLayout mBaseLayout;

	@ViewById(R.id.artist_image)
	ImageView mArtistImageView;

	@ViewById(android.R.id.progress)
	ProgressBar mArtistImageDownloadProgressBar;

	@SuppressWarnings("deprecation")
	@ViewById(R.id.artist_images_gallery)
	Gallery mArtistImageGallery;
	
	@ViewById(R.id.btn_show_artist_gallery)
	Button btnShowGallery;
	
	@AnimationRes
	Animation fadeIn;
	
	@AnimationRes
	Animation fadeOut;

	@ViewById(R.id.artist_bio)
	TextView mArtistBio;

	ImageAdapter mArtistImagesAdapter;

	ImageDownloaderTask imageDownloaderTask = new ImageDownloaderTask();

	private Artist mCurrentArtist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final ActionBar actionBar = getSupportActionBar();
		// Enable navigation to parent activity
		actionBar.setDisplayHomeAsUpEnabled(true);

		Intent intent = this.getIntent();
		Artist artist = (Artist) intent.getSerializableExtra(Artist.KEY);
		// Handling discogs diff
		if (artist == null) {
			artist = new Artist(
					intent.getIntExtra(
							com.ventura.musicexplorer.discogs.entity.Artist.KEY_ID,
							0),
					intent.getStringExtra(com.ventura.musicexplorer.discogs.entity.Artist.KEY_NAME));
		}
		this.mCurrentArtist = artist;
		OAuthConsumer consumer = this.getConsumer(this.sharedPreferences);

		getSupportActionBar().setTitle(artist.getName());
		getSupportActionBar()
				.setSubtitle(R.string.title_activity_artist_viewer);
	}

	@AfterViews
	void afterViews() {
		this.getArtist(this.mCurrentArtist.getId());
	}

	@Background
	void getArtist(int artistId) {
		ArtistService artistService = new ArtistService(this);
		try {
			updateView(artistService.getArtist(artistId));
		} catch (NoInternetConnectionException e) {
			e.printStackTrace();
		} catch (LazyInternetConnectionException e) {
			e.printStackTrace();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This is called when the Home (Up) button is pressed
			// in the Action Bar.
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@UiThread
	void updateView(Artist artist) {
		mActivityLoadingBar.setVisibility(View.GONE);
		mBaseLayout.setVisibility(View.VISIBLE);

		// artist
		this.mCurrentArtist = artist;

		if (this.mCurrentArtist.getImages() != null
				&& this.mCurrentArtist.getImages().size() > 0) {
			Image firstImage = this.mCurrentArtist.getImages().get(0);

			new ImageLoader(this).displayImage(firstImage.getUrl().toString(),
					mArtistImageView);
			// TODO
			mArtistImagesAdapter = new ImageAdapter(this,
					this.mCurrentArtist.getImages());
			mArtistImageGallery.setAdapter(mArtistImagesAdapter);

			mArtistImageGallery.setOnItemSelectedListener(this);

			this.setArtistMainImage(firstImage);

			if (firstImage.getHeight() > 0 && firstImage.getWidth() > 0) {
				mArtistImageView.setMinimumHeight(firstImage.getHeight());
				mArtistImageView.setMinimumWidth(firstImage.getWidth());
			}
		} else {
			mArtistImageView.setVisibility(View.GONE);
			mArtistImageDownloadProgressBar.setVisibility(View.GONE);
		}

		getSupportActionBar().setTitle(this.mCurrentArtist.getName());
		String profile = this.mCurrentArtist.getProfile();

		if (profile != null && !profile.equals("")) {
			mArtistBio.setText(profile);
		} else {
			mArtistBio.setText(getString(R.string.no_biography));
		}

		buildAditionalInformationView();
	}

	private void openArtistDiscogsProfile() {
		try {
			if (this.mCurrentArtist.getDiscogsUrl() != null) {
				Intent profileIntent = new Intent(Intent.ACTION_VIEW,
						Uri.parse(this.mCurrentArtist.getDiscogsUrl()
								.toString()));
				this.startActivity(profileIntent);
			}
		} catch (Exception e) {
			Log.e(TAG, "Error opening artist's profile on Discogs", e);
		}
	}

	private void openArtistReleases() {
		if (this.isConnected()) {
			// Intent releasesIntent = new Intent(this,
			// ReleasesViewerActivity.class);
			Intent releasesIntent = new Intent(this,
					ReleasesListActivity_.class);

			for (int i = 0; this.mCurrentArtist.getImages() != null
					&& i < this.mCurrentArtist.getImages().size(); i++) {
				// this.mCurrentArtist.getImages().get(0).setBitmap(null);
			}

			releasesIntent.putExtra(Artist.KEY, this.mCurrentArtist);

			releasesIntent.putExtra(GlobalConstants.EXTRA_ARTIST_ID,
					this.mCurrentArtist.getId());
			releasesIntent.putExtra(GlobalConstants.EXTRA_ARTIST_NAME,
					this.mCurrentArtist.getName());

			this.startActivity(releasesIntent);
		} else {
			Toast.makeText(this, R.string.message_no_internet_connection,
					Toast.LENGTH_LONG).show();
		}
	}

	private void buildAditionalInformationView() {
		Artist artist = this.mCurrentArtist;
		if (artist.getExternalUrls().size() > 0) {
			this.buildExternalUrlsView();
		}

		if (artist.getNameVariations().size() > 0
				|| (artist.getRealName() != null && artist.getRealName() != "")) {
			this.buildNameVariationsView();
		}

		if (artist.getMembers().size() > 0) {
			this.buildBandMembersView();
		}

		if (artist.getGroups().size() > 0) {
			this.buildGroupsView();
		}
	}

	private void buildExternalUrlsView() {
		String urlMetadataAppendFormatting = " - %1s";

		ButtonGroup externalUrlsContainer = (ButtonGroup) findViewById(R.id.external_urls_container);
		List<Button> externalUrlsArray = new ArrayList<Button>();
		Button externalUrlRow = null;
		String linkText = null;

		if (this.mCurrentArtist.getExternalUrls().size() > 0)
			externalUrlsContainer.setVisibility(View.VISIBLE);

		int externalUrlsLength = this.mCurrentArtist.getExternalUrls().size();

		Collections.sort(this.mCurrentArtist.getExternalUrls());

		for (int i = 0; i < externalUrlsLength; i++) {
			final String currentExternalUrl = this.mCurrentArtist
					.getExternalUrls().get(i).getUrl().toString()
					.toLowerCase(Locale.US);
			String username = this.mCurrentArtist.getExternalUrls().get(i)
					.getUrl().toString()
					.substring(currentExternalUrl.lastIndexOf("/") + 1);

			externalUrlRow = (Button) this.getLayoutInflater().inflate(
					R.layout.button_group_item, null);
			externalUrlRow.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

			try {
				switch (this.mCurrentArtist.getExternalUrls().get(i).getType()) {
				case ArtistWebsite:
					externalUrlRow.setCompoundDrawablesWithIntrinsicBounds(
							R.drawable.own_website, 0, 0, 0);
					linkText = this.getString(R.string.own_website)
							+ String.format(urlMetadataAppendFormatting,
									currentExternalUrl);
					break;
				case Facebook:
					externalUrlRow.setCompoundDrawablesWithIntrinsicBounds(
							R.drawable.facebook_32, 0, 0, 0);
					linkText = this.getString(R.string.facebook)
							+ String.format(urlMetadataAppendFormatting,
									username);
					break;
				case Twitter:
					externalUrlRow.setCompoundDrawablesWithIntrinsicBounds(
							R.drawable.twitter_32, 0, 0, 0);
					linkText = this.getString(R.string.twitter)
							+ String.format(urlMetadataAppendFormatting, "@"
									+ username);
					break;
				case Tumblr:
					externalUrlRow.setCompoundDrawablesWithIntrinsicBounds(
							R.drawable.tumblr_32, 0, 0, 0);
					int indexOfFirstDot = currentExternalUrl.indexOf(".");
					int indexOfDoubleSlash = currentExternalUrl.indexOf("//");
					username = currentExternalUrl.substring(
							indexOfDoubleSlash + 2, indexOfFirstDot);
					linkText = this.getString(R.string.tumblr)
							+ String.format(urlMetadataAppendFormatting,
									username);
					break;
				case MySpace:
					externalUrlRow.setCompoundDrawablesWithIntrinsicBounds(
							R.drawable.myspace_32, 0, 0, 0);
					linkText = this.getString(R.string.myspace)
							+ String.format(urlMetadataAppendFormatting,
									username);
					break;
				case Wikipedia:
					externalUrlRow.setCompoundDrawablesWithIntrinsicBounds(
							R.drawable.wordpress_32, 0, 0, 0);
					linkText = this.getString(R.string.wikipedia)
							+ String.format(urlMetadataAppendFormatting,
									username.replace("_", " "));
					break;
				case YouTube:
					externalUrlRow.setCompoundDrawablesWithIntrinsicBounds(
							R.drawable.youtube_32, 0, 0, 0);
					linkText = this.getString(R.string.youtube)
							+ String.format(urlMetadataAppendFormatting,
									username);
					break;
				case Vimeo:
					externalUrlRow.setCompoundDrawablesWithIntrinsicBounds(
							R.drawable.vimeo_32, 0, 0, 0);
					linkText = this.getString(R.string.vimeo);
					break;
				case GooglePlus:
					externalUrlRow.setCompoundDrawablesWithIntrinsicBounds(
							R.drawable.google_plus_32, 0, 0, 0);
					linkText = this.getString(R.string.googleplus);
					break;
				case OtherWebsite:
				default:
					externalUrlRow.setCompoundDrawablesWithIntrinsicBounds(
							R.drawable.other_website, 0, 0, 0);
					linkText = this.getString(R.string.other_website)
							+ String.format(urlMetadataAppendFormatting,
									currentExternalUrl);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			externalUrlRow.setText(linkText);

			externalUrlRow.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent openExternalUrlIntent = new Intent(
							Intent.ACTION_VIEW, Uri.parse(currentExternalUrl));
					startActivity(openExternalUrlIntent);
				}
			});

			externalUrlRow.setOnLongClickListener(new OnLongClickListener() {
				public boolean onLongClick(View v) {
					new AlertDialog.Builder(ArtistViewerActivity.this)
							.setTitle(currentExternalUrl)
							.setItems(R.array.external_urls_context_menu_items,
									new DialogInterface.OnClickListener() {

										public void onClick(
												DialogInterface dialog,
												int which) {
											ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
											switch (which) {
											case 0:
												cm.setText(currentExternalUrl);
												Toast.makeText(
														getBaseContext(),
														"Link copied to clipboard...",
														Toast.LENGTH_SHORT)
														.show();
												break;

											default:
												break;
											}
										}
									}).show();
					return true;
				}
			});

			externalUrlRow.setMovementMethod(LinkMovementMethod.getInstance());

			externalUrlsArray.add(externalUrlRow);
		}
		externalUrlsContainer.addViews(externalUrlsArray);
	}

	private void buildNameVariationsView() {
		if (mCurrentArtist.getRealName() != null
				&& mCurrentArtist.getRealName() != "") {
			KeyValuePanel keyValuePanel = (KeyValuePanel) findViewById(R.id.real_name);
			keyValuePanel.setKeyValue(getString(R.string.real_name),
					this.mCurrentArtist.getRealName());

			keyValuePanel.setVisibility(View.VISIBLE);
		}

		if (this.mCurrentArtist.getNameVariations().size() > 0) {
			KeyValuePanel nameVariationsKeyValuePanel = (KeyValuePanel) findViewById(R.id.aliases);
			nameVariationsKeyValuePanel.setKeyValue(
					getString(R.string.aliases),
					this.createList(this.mCurrentArtist.getNameVariations()));

			nameVariationsKeyValuePanel.setVisibility(View.VISIBLE);
		}
	}

	private void buildBandMembersView() {
		if (this.mCurrentArtist.getMembers().size() > 0) {
			Collections.sort(this.mCurrentArtist.getMembers());

			ButtonGroup buttonGroup = (ButtonGroup) findViewById(R.id.members_container);

			for (int i = 0; i < this.mCurrentArtist.getMembers().size(); i++) {
				buttonGroup.setVisibility(View.VISIBLE);
				final Artist member = this.mCurrentArtist.getMembers().get(i);

				Button button = (Button) this.getLayoutInflater().inflate(
						R.layout.button_group_item, null);

				button.setText(member.getName());

				if (member.isActive()) {
					button.setCompoundDrawablesWithIntrinsicBounds(
							R.drawable.green_mark, 0, 0, 0);
				} else {
					button.setCompoundDrawablesWithIntrinsicBounds(
							R.drawable.red_mark, 0, 0, 0);
				}

				button.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						openNewArtistInfo(member);
					}
				});
				buttonGroup.addButton(button);
			}
		}
	}

	private void buildGroupsView() {
		if (this.mCurrentArtist.getGroups().size() > 0) {
			Collections.sort(this.mCurrentArtist.getGroups());

			ButtonGroup buttonGroup = (ButtonGroup) findViewById(R.id.members_container);

			buttonGroup.setGroupTitle(getString(R.string.groups));
			buttonGroup.setVisibility(View.VISIBLE);
			List<Button> buttonsToAdd = new ArrayList<Button>();
			for (int i = 0; i < this.mCurrentArtist.getGroups().size(); i++) {
				final Artist group = this.mCurrentArtist.getGroups().get(i);

				Button button = (Button) this.getLayoutInflater().inflate(
						R.layout.button_group_item, null);

				button.setText(group.getName());

				if (group.isActive()) {
					button.setCompoundDrawablesWithIntrinsicBounds(
							R.drawable.green_mark, 0, 0, 0);
				} else {
					button.setCompoundDrawablesWithIntrinsicBounds(
							R.drawable.red_mark, 0, 0, 0);
				}

				button.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						openNewArtistInfo(group);
					}
				});
				buttonsToAdd.add(button);
			}
			buttonGroup.addViews(buttonsToAdd);
		}
	}

	private void openNewArtistInfo(Artist artist) {
		if (this.isConnected()) {
			Intent openArtistInfoIntent = new Intent(this,
					ArtistViewerActivity_.class);
			openArtistInfoIntent.setAction(Intent.ACTION_SEND);
			openArtistInfoIntent.putExtra(Artist.KEY, artist);
			startActivity(openArtistInfoIntent);
		} else {
			Toast.makeText(this, R.string.message_no_internet_connection,
					Toast.LENGTH_LONG).show();
		}
	}

	@Click(R.id.btn_show_artist_gallery)
	public void onShowGalleryButtonClicked(View button){
		btnShowGallery.startAnimation(fadeOut);
		
		fadeOut.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
			}
			public void onAnimationRepeat(Animation animation) {
			}
			
			public void onAnimationEnd(Animation animation) {
				btnShowGallery.setVisibility(View.GONE);
				
				mArtistImageGallery.setVisibility(View.INVISIBLE);
				mArtistImageGallery.startAnimation(fadeIn);
				mArtistImageGallery.setVisibility(View.VISIBLE);
				
				fadeOut.setAnimationListener(null);
			}
		});
	}
	
	public void saveArtistImage() {
		if (this.mCurrentArtist.getImages().size() == 0)
			return;

		if (this.imageDownloaderTask.isDownloading()) {
			Toast.makeText(ArtistViewerActivity.this,
					getString(R.string.image_already_being_downloaded),
					Toast.LENGTH_SHORT).show();
			return;
		}

		final String directory = Environment.getExternalStorageDirectory()
				+ "/Artists Images/";

		Image selectedImage = (Image) mArtistImageGallery.getSelectedItem();
		int selImagePos = mArtistImageGallery.getSelectedItemPosition();

		final String fileName = mCurrentArtist.getName()
				+ (selImagePos == 0 ? "" : "-" + (selImagePos + 1)) + ".jpg";

		UrlImageViewHelper.setUrlDrawable(mArtistImageView, selectedImage
				.getUrl().toString(), R.drawable.no_image,
				new UrlImageViewCallback() {
					@Override
					public void onLoaded(ImageView imageView,
							Bitmap loadedBitmap, String url,
							boolean loadedFromCache) {
						saveImage(loadedBitmap, directory, fileName);
						return;
					}
				});
	}

	private void saveImage(Bitmap image, String directory, String fileName) {
		File imgPath = new File(directory);
		if (!imgPath.exists()) {
			imgPath.mkdirs();
		}

		if (directory.charAt(directory.length() - 1) != "/".toCharArray()[0]) {
			directory += "/";
		}

		fileName = directory + fileName;
		File imgFile = new File(fileName);

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(imgFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		image.compress(Bitmap.CompressFormat.PNG, 90, fos);

		Toast.makeText(this,
				String.format(getString(R.string.image_saved), fileName),
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);
	}

	// Menu

	@Override
	public final boolean onCreateOptionsMenu(Menu menu) {
		SubMenu subMenu = menu.addSubMenu("Actions");
		subMenu.add(0, R.id.menu_download_artist_image, 0,
				R.string.menu_download_artist_image);
		subMenu.getItem().setShowAsAction(
				MenuItem.SHOW_AS_ACTION_ALWAYS
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getItemId() == R.id.menu_download_artist_image) {
			this.saveArtistImage();
		} else {
			return super.onMenuItemSelected(featureId, item);
		}
		return true;
	}

	// UI buttons
	public void onOpenProfileButtonClicked(View button) {
		this.openArtistDiscogsProfile();
	}

	public void onOpenReleasesButtonClicked(View button) {
		this.openArtistReleases();
	}

	/**
	 * Event fired when an image is selected in the artist images gallery
	 */
	@Override
	public void onItemSelected(AdapterView<?> adapter, View view, int position,
			long arg3) {
		Image artistImage = (Image) mArtistImagesAdapter.getItem(position);
		this.setArtistMainImage(artistImage);
	}

	@Override
	public void onNothingSelected(AdapterView<?> adapter) {

	}

	public void setArtistMainImage(Image image) {
		mArtistImageDownloadProgressBar.setVisibility(View.VISIBLE);

		this.downloadMainImage(image);
	}

	void downloadMainImage(final Image image) {
		try {
			UrlImageViewHelper.setUrlDrawable(mArtistImageView, image.getUrl()
					.toString(), R.drawable.no_image,
					new UrlImageViewCallback() {
						@Override
						public void onLoaded(ImageView imageView,
								Bitmap loadedBitmap, String url,
								boolean loadedFromCache) {
							afterDownloadArtistMainImage(imageView, image,
									loadedBitmap);
						}
					});
		} catch (Exception e) {
			this.onArtistMainImageDownloadError();
			e.printStackTrace();
		}
	}

	void onArtistMainImageDownloadError() {
		mArtistImageView.setVisibility(View.GONE);
		mArtistImageDownloadProgressBar.setVisibility(View.GONE);
	}

	void afterDownloadArtistMainImage(ImageView artistImageView, Image image,
			Bitmap imageBmp) {
		// Avoid the show of all images when user rolls the gallery,
		// from first to last picture, for instance
		if (mArtistImageGallery.getSelectedItem() == image) {
			mArtistImageView.setMinimumHeight(imageBmp.getHeight());
			mArtistImageView.setMinimumWidth(imageBmp.getWidth());
			mArtistImageDownloadProgressBar.setVisibility(View.GONE);
		}
	}
}
