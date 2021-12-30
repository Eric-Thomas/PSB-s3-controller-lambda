package com.psb.testUtils;

import com.psb.constants.Constants;
import com.psb.model.spotify.*;

import java.util.ArrayList;
import java.util.List;

public class SpotifyUtil {



	public SpotifyPlaylist createTestPlaylist() {
		SpotifyPlaylist testPlaylist = new SpotifyPlaylist();
		testPlaylist.setName(Constants.TEST_PLAYLIST_NAME);
		List<SpotifyImage> images = new ArrayList<>();
		images.add(createTestImage());
		testPlaylist.setImages(images);
		testPlaylist.setId(Constants.TEST_PLAYLIST_ID);
		testPlaylist.setTracksUrl(Constants.TRACKS_URL);
		return testPlaylist;
	}

	public SpotifyImage createTestImage() {
		SpotifyImage image = new SpotifyImage();
		image.setHeight("500");
		image.setWidth("500");
		image.setUrl(Constants.TEST_PLAYLIST_IMAGE_URL);
		return image;
	}

	public SpotifyTracks createTestTracks() {
		SpotifyTracks testTracks = new SpotifyTracks();
		List<SpotifyTrack> tracks = new ArrayList<>();
		tracks.add(createTestTrack());
		testTracks.setTracks(tracks);
		return testTracks;
	}

	public SpotifyTrack createTestTrack() {
		SpotifyTrack testTrack = new SpotifyTrack();
		testTrack.setAlbum(createTestAlbum());
		List<SpotifyArtist> artists = new ArrayList<>();
		artists.add(createTestArtist());
		testTrack.setArtists(artists);
		testTrack.setName(Constants.TEST_SONG_NAME);
		testTrack.setUri("Test uri");
		return testTrack;
	}

	public SpotifyAlbum createTestAlbum() {
		SpotifyAlbum testAlbum = new SpotifyAlbum();
		testAlbum.setName(Constants.TEST_ALBUM_NAME);
		return testAlbum;
	}

	public SpotifyArtist createTestArtist() {
		SpotifyArtist testArtist = new SpotifyArtist();
		testArtist.setName(Constants.TEST_ARTIST_NAME);
		return testArtist;
	}
}
