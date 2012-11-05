package com.ventura.lyricsfinder.lyrics.provider;

public class LyricProviderFactory {
	public static LyricProvider getLyricProvider(LyricProviders provider) throws Exception {
		switch (provider) {
		case TerraLetras:
			return new TerraLetras();
		case Lyrster:
			return new Lyrster();
		case AzLyrics:
			return new AzLyrics();
		default:
			throw new Exception(
					"The provider specified was not found");
		}
	}

	public static LyricProvider getLyricProvider(String provider) throws Exception {
		LyricProviders providerType = Enum.valueOf(LyricProviders.class, provider);
		return getLyricProvider(providerType);
	}
}
