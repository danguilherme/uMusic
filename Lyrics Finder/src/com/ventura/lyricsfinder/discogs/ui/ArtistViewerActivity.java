package com.ventura.lyricsfinder.discogs.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import oauth.signpost.OAuthConsumer;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.ClipboardManager;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import com.ventura.lyricsfinder.R;
import com.ventura.lyricsfinder.constants.GlobalConstants;
import com.ventura.lyricsfinder.discogs.DiscogsService;
import com.ventura.lyricsfinder.discogs.ImageDownloaderTask;
import com.ventura.lyricsfinder.discogs.OnImageDownloadListener;
import com.ventura.lyricsfinder.discogs.entity.Artist;
import com.ventura.lyricsfinder.discogs.entity.ExternalUrl;
import com.ventura.lyricsfinder.discogs.entity.Image;
import com.ventura.lyricsfinder.exception.LazyInternetConnectionException;
import com.ventura.lyricsfinder.exception.NoInternetConnectionException;
import com.ventura.lyricsfinder.ui.BaseActivity;
import com.ventura.lyricsfinder.ui.widget.ButtonGroup;
import com.ventura.lyricsfinder.util.ImageLoader;

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

		mBaseLayout = (FrameLayout) this.getLayoutInflater().inflate(
				R.layout.artist_info, null);
		this.setContentView(mBaseLayout);

		Intent intent = this.getIntent();
		Artist artist = new Artist(intent.getIntExtra(Artist.KEY_ID, 0),
				intent.getStringExtra(Artist.KEY_NAME), null);
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

	private void fillView(Artist artist) {
		mBaseLayout.setVisibility(View.VISIBLE);

		final ProgressBar artistImageDownloadProgressBar = (ProgressBar) findViewById(android.R.id.progress);

		this.mCurrentArtist = artist;
		if (this.mCurrentArtist.getImages()!= null && this.mCurrentArtist.getImages().size() > 0) {
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
			mArtistBio.setText("This artist has no Bio. Add one!");
		}

		buildAditionalInformationView();
	}

	private void openArtistDiscogsProfile() {
		Intent profileIntent = new Intent(Intent.ACTION_VIEW,
				Uri.parse(this.mCurrentArtist.getDiscogsUrl().toString()));
		this.startActivity(profileIntent);
	}

	private void showArtistReleasesActivity() {
		Intent releasesIntent = new Intent(this, ReleasesViewerActivity.class);
		releasesIntent.putExtra(GlobalConstants.EXTRA_ARTIST_ID,
				this.mCurrentArtist.getId());
		releasesIntent.putExtra(GlobalConstants.EXTRA_ARTIST_NAME,
				this.mCurrentArtist.getName());

		this.startActivity(releasesIntent);
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
					.getExternalUrls().get(i).getExternalUrl().toString()
					.toLowerCase(Locale.US);
			String username = this.mCurrentArtist.getExternalUrls().get(i)
					.getExternalUrl().toString()
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
			Log.i(TAG, this.mCurrentArtist.getName() + "'s external url: "
					+ this.mCurrentArtist.getExternalUrls().get(i));
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

			key.setText("Real Name:");
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

			key.setText("Known name variations:");
			value.setText(this.createList(this.mCurrentArtist.getNameVariations()));

			mArtistExtraInfoContent.addView(nameVariationsKeyValuePanel);
		}
	}

	private void buildBandMembersView() {
		if (this.mCurrentArtist.getMembers().size() > 0) {
			Collections.sort(this.mCurrentArtist.getMembers());
			// Add the name variations one by one, separated by comma and
			// finalized with a dot.
			ButtonGroup buttonGroup = (ButtonGroup) findViewById(R.id.members_container);

			List<Button> buttonsToAdd = new ArrayList<Button>();
			for (int i = 0; i < this.mCurrentArtist.getMembers().size(); i++) {
				buttonGroup.setVisibility(View.VISIBLE);
				final Artist actualMember = this.mCurrentArtist.getMembers()
						.get(i);

				Button button = (Button) this.getLayoutInflater().inflate(
						R.layout.button_group_item, null);
				button.setText(actualMember.getName() + " - "
						+ (actualMember.isActive() ? "Active" : "Left group"));

				button.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						Intent openArtistInfoIntent = new Intent(
								v.getContext(), ArtistViewerActivity.class);

						openArtistInfoIntent.setAction(Intent.ACTION_SEND);
						openArtistInfoIntent.putExtra(Artist.KEY_ID,
								actualMember.getId());
						openArtistInfoIntent.putExtra(Artist.KEY_NAME,
								actualMember.getName());
						startActivity(openArtistInfoIntent);
					}
				});
				buttonsToAdd.add(button);
			}
			buttonGroup.addViews(buttonsToAdd);
		}
	}

	public void saveArtistImage() {
		if (this.mCurrentArtist.getImages().size() == 0)
			return;

		if (this.imageDownloaderTask.isDownloading()) {
			Toast.makeText(
					ArtistViewerActivity.this,
					"The image is already being downloaded. When it is finished, try again.",
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
				Toast.makeText(
						ArtistViewerActivity.this,
						"It wasn't possible to bring your photo. Please try again later."
								+ "Message: " + error, Toast.LENGTH_SHORT)
						.show();
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

		// Catch the status message here because we set fileName right after
		// this line.
		String statusMessage = "Image saved at " + directory + fileName;

		fileName = directory + fileName;

		File imgFile = new File(fileName);

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(imgFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		image.compress(Bitmap.CompressFormat.PNG, 90, fos);

		Toast.makeText(this, statusMessage, Toast.LENGTH_SHORT).show();
	}

	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);
	}

	private class GetArtistTask extends AsyncTask<Void, Void, Artist> {
		private ProgressDialog mProgressDialog;
		private Context mContext;
		private OAuthConsumer mConsumer;
		private Artist mArtist;

		public GetArtistTask(Context context, OAuthConsumer consumer,
				Artist artist) {

			this.mProgressDialog = new ProgressDialog(context);
			this.mProgressDialog
					.setTitle(getString(R.string.message_fetching_artists_list_title));
			this.mProgressDialog
					.setMessage(getString(R.string.message_fetching_artists_list_body));
			this.mProgressDialog.setCancelable(true);
			this.mContext = context;
			this.mConsumer = consumer;
			this.mArtist = artist;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			this.mProgressDialog.show();
		}

		@Override
		protected Artist doInBackground(Void... params) {
			boolean showMocked = false;

			if (showMocked) {
				Artist ladyGaga = null;
				try {
					ladyGaga = new Artist(1103159, "Lady Gaga", new URL(
							"http://www.discogs.com/artist/Lady+Gaga"));
					ladyGaga.setRealName("Stefani Joanne Angelina Germanotta");
					ladyGaga.setProfileUrl(new URL(
							"http://api.discogs.com/artists/1103159"));
					ladyGaga.setProfile("[b]Lady Gaga[/b], born March 28, 1986 in New York City, New York, USA, of italian origins"
							+ " (sometimes spelt in CamelCase as \"Lady GaGa\") is a theatrical dance-pop singer/songwriter/pianist"
							+ " who created a buzz for herself in the New York underground before making her major-label debut on"
							+ " Interscope Records in 2008.");
					ladyGaga.getExternalUrls().add(
							new ExternalUrl(
									new URL("http://www.ladygaga.com/"),
									ladyGaga));
					ladyGaga.getExternalUrls().add(
							new ExternalUrl(new URL(
									"http://www.ladygaga.co.uk/"), ladyGaga));
					ladyGaga.getExternalUrls().add(
							new ExternalUrl(new URL(
									"http://ladygagaonline.net/"), ladyGaga));
					ladyGaga.getExternalUrls().add(
							new ExternalUrl(new URL(
									"http://www.myspace.com/ladygaga"),
									ladyGaga));
					ladyGaga.getExternalUrls().add(
							new ExternalUrl(new URL(
									"http://www.facebook.com/ladygaga"),
									ladyGaga));
					ladyGaga.getExternalUrls().add(
							new ExternalUrl(new URL(
									"http://twitter.com/ladygaga"), ladyGaga));
					ladyGaga.getExternalUrls().add(
							new ExternalUrl(new URL(
									"http://en.wikipedia.org/wiki/Lady_Gaga"),
									ladyGaga));
					ladyGaga.getExternalUrls().add(
							new ExternalUrl(new URL(
									"http://www.youtube.com/ladygagaofficial"),
									ladyGaga));
					ladyGaga.getExternalUrls().add(
							new ExternalUrl(new URL(
									"http://www.youtube.com/LadyGagaVEVO"),
									ladyGaga));
					ladyGaga.getExternalUrls().add(
							new ExternalUrl(new URL("http://gagadaily.com/"),
									ladyGaga));
					ladyGaga.getExternalUrls().add(
							new ExternalUrl(new URL(
									"http://www.facebook.com/gagadaily"),
									ladyGaga));
					ladyGaga.getExternalUrls().add(
							new ExternalUrl(new URL(
									"http://twitter.com/gagadaily"), ladyGaga));
					ladyGaga.getExternalUrls().add(
							new ExternalUrl(new URL(
									"http://ladygaga.tumblr.com/"), ladyGaga));
					ladyGaga.getExternalUrls().add(
							new ExternalUrl(new URL(
									"http://www.muzu.tv/ladygaga/"), ladyGaga));

					ladyGaga.getNameVariations().add("Gaga");
					ladyGaga.getNameVariations().add("Lady Ga Ga");
					ladyGaga.getNameVariations().add("Lady Gagging");
					ladyGaga.getNameVariations().add("LGG");
					ladyGaga.getNameVariations().add("S. Germanotta");

					ladyGaga.getImages()
							.add(new Image(
									new URL(
											"http://api.discogs.com/image/A-1103159-1272566620.jpeg"),
									460, 296, "primary"));
					// Secondary Images:
					/*
					 * secondary image
					 * (http://api.discogs.com/image/A-1103159-1329717112.jpeg,
					 * 540x554), secondary image
					 * (http://api.discogs.com/image/A-1103159-1329716922.jpeg,
					 * 395x594), secondary image
					 * (http://api.discogs.com/image/A-1103159-1306589056.jpeg,
					 * 266x400), secondary image
					 * (http://api.discogs.com/image/A-1103159-1307320883.png,
					 * 500x511), secondary image
					 * (http://api.discogs.com/image/A-1103159-1307320923.jpeg,
					 * 391x500), secondary image
					 * (http://api.discogs.com/image/A-1103159-1214806539.jpeg,
					 * 600x450), secondary image
					 * (http://api.discogs.com/image/A-1103159-1260998707.jpeg,
					 * 442x653), secondary image
					 * (http://api.discogs.com/image/A-1103159-1252600502.jpeg,
					 * 526x474), secondary image
					 * (http://api.discogs.com/image/A-1103159-1252600491.jpeg,
					 * 455x600), secondary image
					 * (http://api.discogs.com/image/A-1103159-1263291693.jpeg,
					 * 200x320), secondary image
					 * (http://api.discogs.com/image/A-1103159-1329716950.jpeg,
					 * 396x594), secondary image
					 * (http://api.discogs.com/image/A-1103159-1305062263.jpeg,
					 * 600x758), secondary image
					 * (http://api.discogs.com/image/A-1103159-1273194478.jpeg,
					 * 600x404), secondary image
					 * (http://api.discogs.com/image/A-1103159-1212081753.jpeg,
					 * 590x458), secondary image
					 * (http://api.discogs.com/image/A-1103159-1270308741.jpeg,
					 * 376x490), secondary image
					 * (http://api.discogs.com/image/A-1103159-1265113163.jpeg,
					 * 390x559), secondary image
					 * (http://api.discogs.com/image/A-1103159-1259173913.jpeg,
					 * 500x379), secondary image
					 * (http://api.discogs.com/image/A-1103159-1233855014.jpeg,
					 * 333x500), secondary image
					 * (http://api.discogs.com/image/A-1103159-1226919528.jpeg,
					 * 470x313), secondary image
					 * (http://api.discogs.com/image/A-1103159-1225988647.jpeg,
					 * 267x399), secondary image
					 * (http://api.discogs.com/image/A-1103159-1270308713.jpeg,
					 * 376x490), secondary image
					 * (http://api.discogs.com/image/A-1103159-1270308724.jpeg,
					 * 376x490), secondary image
					 * (http://api.discogs.com/image/A-1103159-1270308733.jpeg,
					 * 376x490), secondary image
					 * (http://api.discogs.com/image/A-1103159-1270308750.jpeg,
					 * 376x490), secondary image
					 * (http://api.discogs.com/image/A-1103159-1270308759.jpeg,
					 * 376x490), secondary image
					 * (http://api.discogs.com/image/A-1103159-1270308770.jpeg,
					 * 600x400), secondary image
					 * (http://api.discogs.com/image/A-1103159-1270308779.jpeg,
					 * 376x490), secondary image
					 * (http://api.discogs.com/image/A-1103159-1270308791.jpeg,
					 * 376x490), secondary image
					 * (http://api.discogs.com/image/A-1103159-1270308799.jpeg,
					 * 376x490), secondary image
					 * (http://api.discogs.com/image/A-1103159-1270308808.jpeg,
					 * 376x490), secondary image
					 * (http://api.discogs.com/image/A-1103159-1270308816.jpeg,
					 * 376x490), secondary image
					 * (http://api.discogs.com/image/A-1103159-1270308826.jpeg,
					 * 376x490), secondary image
					 * (http://api.discogs.com/image/A-1103159-1270308834.jpeg,
					 * 600x450), secondary image
					 * (http://api.discogs.com/image/A-1103159-1270308840.jpeg,
					 * 376x490), secondary image
					 * (http://api.discogs.com/image/A-
					 * 1103159-1355396246-2824.jpeg, 600x410), secondary image
					 * (http
					 * ://api.discogs.com/image/A-1103159-1355396262-5998.jpeg,
					 * 600x380), secondary image
					 * (http://api.discogs.com/image/A-
					 * 1103159-1355396268-2247.jpeg, 600x430), secondary image
					 * (http
					 * ://api.discogs.com/image/A-1103159-1355396273-5411.jpeg,
					 * 419x580), secondary image
					 * (http://api.discogs.com/image/A-
					 * 1103159-1355396279-8752.jpeg, 600x371), secondary image
					 * (http
					 * ://api.discogs.com/image/A-1103159-1355396285-8086.jpeg,
					 * 600x439), secondary image
					 * (http://api.discogs.com/image/A-
					 * 1103159-1355396290-6339.jpeg, 600x449), secondary image
					 * (http
					 * ://api.discogs.com/image/A-1103159-1355396294-9312.jpeg,
					 * 443x579), secondary image
					 * (http://api.discogs.com/image/A-
					 * 1103159-1355396300-1813.jpeg, 600x449)
					 */
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return ladyGaga;
			} else {
				DiscogsService discogsService = new DiscogsService(
						this.mContext, this.mConsumer);

				try {
					return discogsService.getArtistInfo(this.mArtist.getId());
				} catch (NoInternetConnectionException e) {
					Toast.makeText(mContext, "No internet connection...",
							Toast.LENGTH_LONG).show();
					e.printStackTrace();
				} catch (LazyInternetConnectionException e) {
					Toast.makeText(mContext,
							"Your connection is lazy! Try again?",
							Toast.LENGTH_LONG).show();
					e.printStackTrace();
				} catch (Exception e) {
					Log.e(TAG, e.getMessage());
				}

				return null;
			}
		}

		@Override
		protected void onPostExecute(Artist result) {
			super.onPostExecute(result);
			this.mProgressDialog.dismiss();
			fillView(result);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = new MenuInflater(this);
		menuInflater.inflate(R.menu.artist_info_menu, menu);
		return true;
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
		this.showArtistReleasesActivity();
	}
}
