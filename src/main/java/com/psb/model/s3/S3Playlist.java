package com.psb.model.s3;

import com.psb.model.spotify.SpotifyPlaylist;
import com.psb.model.spotify.SpotifyTracks;
import lombok.Data;

import java.io.Serializable;

@Data
public class S3Playlist implements Serializable {
	
	private static final long serialVersionUID = 4111762650686114692L;

	SpotifyPlaylist playlist;
	SpotifyTracks tracks;

}
