package com.ventura.lyricsfinder.oauth;

public class Constants {

	public static final String CONSUMER_KEY = "XkyKpfUvPGWqaaOtbrMP";
	public static final String CONSUMER_SECRET = "FJRDySYjfQFOxoEesKQGHEcBlVYlbQiv";

	public static final String SCOPE = "http://discogs.com";
	public static final String REQUEST_URL = "http://api.discogs.com/oauth/request_token";
	public static final String ACCESS_URL = "http://api.discogs.com/oauth/access_token";
	public static final String AUTHORIZE_URL = "http://www.discogs.com/oauth/authorize";

	public static final String API_REQUEST = "https://www.google.com/m8/feeds/contacts/default/full?alt=json";

	public static final String ENCODING = "UTF-8";

	public static final String OAUTH_CALLBACK_SCHEME = "lyricsfinder-app";
	public static final String OAUTH_CALLBACK_HOST = "callback";
	public static final String OAUTH_CALLBACK_URL = OAUTH_CALLBACK_SCHEME
			+ "://" + OAUTH_CALLBACK_HOST;
	
}
