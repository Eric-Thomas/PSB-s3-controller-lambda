package com.psb.client;

import com.psb.exception.AWSS3ClientException;
import com.psb.exception.AWSS3ClientNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AWSS3ClientTest {

	@Mock
	private S3Client s3;
	@InjectMocks
	private AWSS3Client s3Client;

	
	@Test
	void testGetPlaylist() {
		// TODO find way to mock s3 response
	}
	
	@Test
	void testGetPlaylistNoSuchKeyException() {
		when(s3.getObjectAsBytes(Mockito.any(GetObjectRequest.class))).thenThrow(NoSuchKeyException.class);
		assertThrows(AWSS3ClientNotFoundException.class, () -> {
			s3Client.getPlaylist("objectKey");
		});
	}
	
	@Test
	void testGetPlaylists() {
		// TODO find way to mock s3 response
	}
	
	@Test
	void testGetPlaylistsUserNotFound() {
		ListObjectsResponse listObjects = ListObjectsResponse.builder().build();
		// listObjects.contents() will return an empty list
		when(s3.listObjects(Mockito.any(ListObjectsRequest.class))).thenReturn(listObjects);
		assertThrows(AWSS3ClientNotFoundException.class, () -> {
			s3Client.getPlaylists("Non existant user", 10, 0);
		});
	}
	
	@Test
	void testGetAllUsers() {
		// TODO find way to mock s3 response
	}
	
	@Test
	void testGetAllUsersException() {
		when(s3.listObjects(Mockito.any(ListObjectsRequest.class))).thenThrow(AwsServiceException.class);
		assertThrows(AWSS3ClientException.class, () -> {
			s3Client.getAllUsers();
		});
	}

}
