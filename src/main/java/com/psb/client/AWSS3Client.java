package com.psb.client;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.annotation.PreDestroy;

import com.psb.exception.LimitTooHighException;
import com.psb.model.s3.S3Playlists;
import com.psb.model.spotify.SpotifyPlaylist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.psb.exception.AWSS3ClientException;
import com.psb.exception.AWSS3ClientNotFoundException;
import com.psb.model.s3.S3Playlist;
import com.psb.model.s3.S3User;
import com.psb.util.Decompresser;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CommonPrefix;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.S3Object;

@Component
public class AWSS3Client {

	@Value("${aws.bucket.name}")
	private String bucketName;
	private S3Client s3;
	@Value("${s3.path.delimiter}")
	private String delimiter;
	
	private Logger logger = LoggerFactory.getLogger(AWSS3Client.class);
	@Autowired
	public AWSS3Client(S3Client s3) {
		this.s3 = s3;
	}

	@PreDestroy
	public void tearDown() {
		s3.close();
		logger.info("S3 client closed.");
	}

	public S3Playlist getPlaylist(String objectKey)
			throws AWSS3ClientException, AWSS3ClientNotFoundException {
		try {
			logger.info("Getting playlist with object key {}", objectKey);
			GetObjectRequest s3Request = GetObjectRequest.builder().bucket(bucketName).key(objectKey).build();
			ResponseBytes<GetObjectResponse> objectBytes = s3.getObjectAsBytes(s3Request);
			return (S3Playlist) SerializationUtils.deserialize(Decompresser.decompress(objectBytes.asByteArray()));
		} catch (NoSuchKeyException e) {
			throw new AWSS3ClientNotFoundException(
					"Error getting object from s3: Object key: " + objectKey + " does not exist");
		} catch (Exception e) {
			throw new AWSS3ClientException("Error getting object from s3\n" + e.getMessage());
		}
	}

	public S3Playlist getPlaylist(String userID, String playlistID) throws AWSS3ClientException, AWSS3ClientNotFoundException {
		String displayName = getDisplayName(userID + "/");
		String playlistObjectKey = userID + delimiter + displayName + delimiter + playlistID;
		return getPlaylist(playlistObjectKey);
	}

	public S3Playlists getPlaylists(String userID, int limit, int offset) throws AWSS3ClientException, AWSS3ClientNotFoundException, LimitTooHighException {

		if (limit > 10 || limit < 0){
			throw new LimitTooHighException("Limit must be between 0 and 10");
		}

		logger.info("Getting playlists for {}", userID);

		String prefix = userID + delimiter;
		ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder().bucket(bucketName).prefix(prefix).build();

		ListObjectsResponse res = s3.listObjects(listObjectsRequest);
		List<S3Object> objects = res.contents();
		
		if (objects.isEmpty()) {
			throw new AWSS3ClientNotFoundException(
					"Error getting object from s3: user: " + userID + " does not exist");
		}
		try {
			ListIterator<S3Object> s3ObjectIterator = objects.subList(offset, objects.size()).listIterator();
			S3Playlists playlists = new S3Playlists();
			playlists.setPlaylists(new ArrayList<>());
			while (s3ObjectIterator.hasNext() && playlists.getPlaylists().size() < limit) {
				String objectID = s3ObjectIterator.next().key();
				S3Playlist playlist = getPlaylist(objectID);
				logger.info("Found playlist {}", playlist.getPlaylist().getName());
				playlists.getPlaylists().add(playlist);
			}
			if (s3ObjectIterator.hasNext()) {
				ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentRequestUri();
				builder.scheme("https");
				builder.replaceQueryParam("limit", 10);
				builder.replaceQueryParam("offset", offset + limit);
				URI nextURI = builder.build().toUri();
				playlists.setNext(nextURI.toString());
			} else {
				playlists.setNext(null);
			}

			return playlists;
		} catch (IndexOutOfBoundsException e){
			return new S3Playlists();
		}
	}

	public List<S3User> getAllUsers() throws AWSS3ClientException {
		logger.info("Getting all users");
		ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder().bucket(bucketName).delimiter(delimiter)
				.build();
		try {
			ListObjectsResponse objects = s3.listObjects(listObjectsRequest);
			List<CommonPrefix> userIDPrefixes = objects.commonPrefixes();
			return getS3Users(userIDPrefixes);

		} catch (Exception e) {
			throw new AWSS3ClientException("Error getting object from s3\n" + e.getMessage());
		}
	}
	
	private List<S3User> getS3Users(List<CommonPrefix> userIDPrefixes) throws AWSS3ClientNotFoundException {
		List<S3User> users = new ArrayList<>();
		for (CommonPrefix userIDPrefix : userIDPrefixes) {
			String userID = userIDPrefix.prefix();
			String displayName = getDisplayName(userID);
			String id = userID.substring(0, userID.indexOf(delimiter));
			S3User user = new S3User();
			logger.info("Got user with display name {}", displayName);
			user.setDisplayName(displayName);
			user.setId(id);
			users.add(user);
		}
		return users;
	}
	
	private String getDisplayName(String userID) throws AWSS3ClientNotFoundException {
		// prefix must end with / we will add it if it doesn't already have one
		if (userID.charAt(userID.length() -1) != delimiter.charAt(0)){
			userID += delimiter;
		}
		ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder().bucket(bucketName).delimiter(delimiter).prefix(userID)
				.build();
		ListObjectsResponse objects = s3.listObjects(listObjectsRequest);
		if (objects.commonPrefixes().isEmpty()) {
			throw new AWSS3ClientNotFoundException(
					"Error getting object from s3: user: " + userID + " does not exist");
		}
		String fullPrefix = objects.commonPrefixes().get(0).prefix();
		return fullPrefix.substring(fullPrefix.indexOf(delimiter)+1, fullPrefix.length()-1);
	}

}
