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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.ventura.androidutils.exception.LazyInternetConnectionException;
import com.ventura.androidutils.exception.NoInternetConnectionException;
import com.ventura.androidutils.utils.InnerActivityAsyncTask;
import com.ventura.musicexplorer.R;
import com.ventura.musicexplorer.business.ArtistService;
import com.ventura.musicexplorer.constants.GlobalConstants;
import com.ventura.musicexplorer.entity.Image;
import com.ventura.musicexplorer.entity.artist.Artist;
import com.ventura.musicexplorer.ui.BaseActivity;
import com.ventura.musicexplorer.ui.release.ReleasesViewerActivity;
import com.ventura.musicexplorer.ui.widget.ButtonGroup;
import com.ventura.musicexplorer.util.ImageDownloaderTask;
import com.ventura.musicexplorer.util.ImageLoader;
import com.ventura.musicexplorer.util.OnImageDownloadListener;

// TODO: Review SlidingDrawer's deprecation
@SuppressWarnings("deprecation")
public class ArtistViewerActivity extends BaseActivity {
	final String TAG = getClass().getName();

	private FrameLayout mBaseLayout;
	private ImageView mArtistImageView;
	private TextView mArtistBio;
	private TextView mArtistName;
	private SlidingDrawer mSlidingDrawer;
	private LinearLayout mArtistBioContent;
	private LinearLayout mArtistExtraInfoContent;

	private ImageDownloaderTask imageDownloaderTask = new ImageDownloaderTask();

	private Artist mCurrentArtist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Enable navigation to parent activity
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mBaseLayout = (FrameLayout) this.getLayoutInflater().inflate(
				R.layout.artist_info, null);
		this.setContentView(mBaseLayout);

		Intent intent = this.getIntent();
		Artist artist = (Artist) intent.getSerializableExtra(Artist.KEY);
		OAuthConsumer consumer = this.getConsumer(this.sharedPreferences);

		mArtistImageView = (ImageView) findViewById(R.id.artist_image);
		mArtistBio = (TextView) findViewById(R.id.artist_bio);
		mArtistName = (TextView) findViewById(R.id.artist_name);
		mSlidingDrawer = (SlidingDrawer) findViewById(R.id.artist_aditional_information_sliding);
		mArtistBioContent = (LinearLayout) mSlidingDrawer
				.findViewById(R.id.scroll_container);
		mArtistExtraInfoContent = (LinearLayout) this
				.findViewById(R.id.scroll_container);

		mSlidingDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
			public void onDrawerOpened() {
				onArtistInfoDrawerOpened();
			}
		});

		mSlidingDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {
			public void onDrawerClosed() {
				onArtistInfoDrawerClosed();
			}
		});

		mSlidingDrawer.open();

		mArtistName.setText(artist.getName());

		new GetArtistTask(this, consumer, artist).execute();
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

	private void onArtistInfoDrawerOpened() {
		LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(
				mArtistName.getLayoutParams());
		linearLayoutParams.height = LayoutParams.WRAP_CONTENT;

		mArtistName.setCompoundDrawablesWithIntrinsicBounds(0, 0,
				R.drawable.ic_slider_down, 0);

		mArtistName.setLayoutParams(linearLayoutParams);

		mArtistName.setVisibility(View.VISIBLE);
		mSlidingDrawer.findViewById(R.id.imageview_arrow_up).setVisibility(
				View.GONE);
	}

	private void onArtistInfoDrawerClosed() {
		LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(
				mArtistName.getLayoutParams());
		linearLayoutParams.height = 85;

		mArtistName.setCompoundDrawablesWithIntrinsicBounds(0, 0,
				R.drawable.ic_slider_up, 0);

		mArtistName.setLayoutParams(linearLayoutParams);

		mArtistName.setVisibility(View.GONE);
		mSlidingDrawer.findViewById(R.id.imageview_arrow_up).setVisibility(
				View.VISIBLE);
	}

	private void fillView(com.ventura.musicexplorer.entity.artist.Artist artist) {
		mBaseLayout.setVisibility(View.VISIBLE);

		final ProgressBar artistImageDownloadProgressBar = (ProgressBar) findViewById(android.R.id.progress);

		this.mCurrentArtist = artist;

		if (this.mCurrentArtist.getImages() != null
				&& this.mCurrentArtist.getImages().size() > 0) {
			final Image firstImage = this.mCurrentArtist.getImages().get(0);

			new ImageLoader(this).displayImage(firstImage.getUrl().toString(),
					mArtistImageView);

			OnImageDownloadListener imageDownloadListener = new OnImageDownloadListener() {
				public void onDownloadFinished(Bitmap result) {
					firstImage.setBitmap(result);
					if (firstImage.getHeight() > 0 && firstImage.getWidth() > 0) {
						mArtistImageView.setMinimumHeight(firstImage
								.getHeight());
						mArtistImageView.setMinimumWidth(firstImage.getWidth());
					}
					mArtistImageView.setImageBitmap(firstImage.getBitmap());

					artistImageDownloadProgressBar.setVisibility(View.GONE);
				}

				public void onDownloadError(String error) {
					mArtistImageView.setVisibility(View.GONE);
					artistImageDownloadProgressBar.setVisibility(View.GONE);
				}
			};

			imageDownloaderTask.setImage(firstImage);
			imageDownloaderTask.setImageDownloadListener(imageDownloadListener);
			imageDownloaderTask.execute();

			if (firstImage.getHeight() > 0 && firstImage.getWidth() > 0) {
				mArtistImageView.setMinimumHeight(firstImage.getHeight());
				mArtistImageView.setMinimumWidth(firstImage.getWidth());
			}
		} else {
			mArtistImageView.setVisibility(View.GONE);
			artistImageDownloadProgressBar.setVisibility(View.GONE);
		}

		mArtistName.setText(this.mCurrentArtist.getName());
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
			Intent releasesIntent = new Intent(this,
					ReleasesViewerActivity.class);
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
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

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

			LinearLayout keyValuePanel = (LinearLayout) this
					.getLayoutInflater()
					.inflate(R.layout.key_value_panel, null);
			TextView key = (TextView) keyValuePanel.findViewById(R.id.key);
			TextView value = (TextView) keyValuePanel.findViewById(R.id.value);

			key.setText(getString(R.string.real_name) + ":");
			value.setText(this.mCurrentArtist.getRealName());

			mArtistExtraInfoContent.addView(keyValuePanel);
		}

		if (this.mCurrentArtist.getNameVariations().size() > 0) {
			LinearLayout nameVariationsKeyValuePanel = (LinearLayout) this
					.getLayoutInflater()
					.inflate(R.layout.key_value_panel, null);

			TextView key = (TextView) nameVariationsKeyValuePanel
					.findViewById(R.id.key);
			TextView value = (TextView) nameVariationsKeyValuePanel
					.findViewById(R.id.value);

			key.setText(getString(R.string.aliases) + ":");
			value.setText(this.createList(this.mCurrentArtist
					.getNameVariations()));

			mArtistExtraInfoContent.addView(nameVariationsKeyValuePanel);
		}
	}

	private void buildBandMembersView() {
		if (this.mCurrentArtist.getMembers().size() > 0) {
			Collections.sort(this.mCurrentArtist.getMembers());

			ButtonGroup buttonGroup = (ButtonGroup) findViewById(R.id.members_container);

			List<Button> buttonsToAdd = new ArrayList<Button>();
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
				buttonsToAdd.add(button);
			}
			buttonGroup.addViews(buttonsToAdd);
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
					ArtistViewerActivity.class);
			openArtistInfoIntent.setAction(Intent.ACTION_SEND);
			openArtistInfoIntent.putExtra(Artist.KEY, artist);
			startActivity(openArtistInfoIntent);
		} else {
			Toast.makeText(this, R.string.message_no_internet_connection,
					Toast.LENGTH_LONG).show();
		}
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
		final String fileName = mCurrentArtist.getName() + ".jpg";

		final Image primaryImage = this.mCurrentArtist.getImages().get(0);

		// If the image is already downloaded, only save it
		if (primaryImage.getBitmap() != null) {
			saveImage(primaryImage.getBitmap(), directory, fileName);
			return;
		}

		OnImageDownloadListener imageDownloadListener = new OnImageDownloadListener() {
			public void onDownloadFinished(Bitmap result) {
				saveImage(result, directory, fileName);
			}

			public void onDownloadError(String error) {
				Toast.makeText(ArtistViewerActivity.this,
						getString(R.string.image_not_possible_to_download),
						Toast.LENGTH_SHORT).show();
			}
		};

		this.imageDownloaderTask.setImage(primaryImage);
		this.imageDownloaderTask
				.setImageDownloadListener(imageDownloadListener);
		this.imageDownloaderTask.execute();
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

	private class GetArtistTask extends
			InnerActivityAsyncTask<Void, Void, Artist> {
		private OAuthConsumer mConsumer;
		private Artist mArtist;

		public GetArtistTask(Context context, OAuthConsumer consumer,
				Artist artist) {
			super(context, getString(R.string.app_name),
					getString(R.string.message_fetching_artists_list));

			this.mConsumer = consumer;
			this.mArtist = artist;
		}

		@Override
		protected Artist doInBackground(Void... params) {
			ArtistService artistService = new ArtistService(this.getContext());
			try {
				return artistService.getArtist(this.mArtist.getId());// discogsService.getArtistInfo(this.mArtist.getId());
			} catch (NoInternetConnectionException e) {
				/*
				 * Toast.makeText( mContext, mContext.getResources().getString(
				 * R.string.message_no_internet_connection),
				 * Toast.LENGTH_LONG).show();
				 */
				e.printStackTrace();
			} catch (LazyInternetConnectionException e) {
				/*
				 * Toast.makeText( mContext, mContext.getResources().getString(
				 * R.string.message_lazy_internet_connection),
				 * Toast.LENGTH_LONG).show();
				 */
				e.printStackTrace();
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}

			return null;
		}

		@Override
		protected void onPostExecute(
				com.ventura.musicexplorer.entity.artist.Artist result) {
			super.onPostExecute(result);
			if (result != null) {
				fillView(result);
			}
		}

		@Override
		public void onProgressDialogCancelled(DialogInterface progressDialog) {

		}
	}

	// ****** EVENT HANDLERS ******
	@Override
	public void onBackPressed() {
		if (!this.mSlidingDrawer.isOpened()) {
			this.mSlidingDrawer.animateOpen();
		} else {
			super.onBackPressed();
		}
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
}
