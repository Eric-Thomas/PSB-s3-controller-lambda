package com.psb.model.spotify;

import lombok.Data;

import java.io.Serializable;

@Data
public class SpotifyImage implements Serializable{

	private static final long serialVersionUID = 757556048604592319L;
	
	private String height;
	private String width;
	private String url;

}
