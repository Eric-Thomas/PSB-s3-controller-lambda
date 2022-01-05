package com.psb.controller;

import com.psb.client.AWSS3Client;
import com.psb.exception.AWSS3ClientException;
import com.psb.exception.AWSS3ClientNotFoundException;
import com.psb.exception.LimitTooHighException;
import com.psb.model.s3.S3Playlist;
import com.psb.model.s3.S3Playlists;
import com.psb.model.s3.S3User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/s3")
public class S3Controller {

	private AWSS3Client s3Client;

	@Autowired
	public S3Controller(AWSS3Client s3Client) {
		this.s3Client = s3Client;
	}
	
	@GetMapping(path = "/load/users/{userID}/playlists")
	public S3Playlists loadPlaylists(@PathVariable String userID,
									 @RequestParam(value="limit", defaultValue = "10") int limit, @RequestParam(defaultValue = "0") int offset)
			throws AWSS3ClientException, AWSS3ClientNotFoundException, LimitTooHighException {
		return s3Client.getPlaylists(userID, limit, offset);
	}

	@GetMapping(path = "/load/users/{userID}/playlists/{playlistID}")
	public S3Playlist loadPlaylists(@PathVariable String userID, @PathVariable String playlistID)
			throws AWSS3ClientException, AWSS3ClientNotFoundException {
		return s3Client.getPlaylist(userID, playlistID);
	}
	
	@GetMapping(path = "/load/users")
	public List<S3User> loadUsers() throws AWSS3ClientException {
		return s3Client.getAllUsers();
	}

}
