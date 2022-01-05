package com.psb.model.s3;

import com.psb.model.spotify.SpotifyPlaylist;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class S3Playlists implements Serializable {

    private static final long serialVersionUID = 2199502650686114692L;

    List<S3Playlist> playlists;
    String next;

}
