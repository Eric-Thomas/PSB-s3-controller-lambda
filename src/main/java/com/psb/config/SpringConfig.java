package com.psb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.http.apache.ApacheHttpClient;

import java.nio.file.Paths;

@Configuration
public class SpringConfig {

	@Value("${runtime.env}")
	private String environment;

	@Bean
	public S3Client getS3Client() {
		if (environment.equals("local")) {
			ProfileFile profileFile = ProfileFile.builder().content(Paths.get("credentials"))
					.type(ProfileFile.Type.CREDENTIALS).build();
			ProfileCredentialsProvider provider = ProfileCredentialsProvider.builder().profileFile(profileFile).build();
			Region region = Region.US_EAST_1;
			return S3Client.builder().credentialsProvider(provider).region(region).build();
		} else {
			return S3Client.builder()
					.region(Region.US_EAST_1)
					.httpClient(ApacheHttpClient.builder().build())
					.build();
		}
	}
}
