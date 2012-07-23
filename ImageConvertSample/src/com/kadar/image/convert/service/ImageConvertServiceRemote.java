package com.kadar.image.convert.service;

import java.io.IOException;

public interface ImageConvertServiceRemote {

	public void generateThumbnail(String bucketName, String key) throws IOException;
	
	public void generateSmall(String bucketName, String key) throws IOException;
	
	public void generateMedium(String bucketName, String key) throws IOException;
	
	public void generateLarge(String bucketName, String key) throws IOException;
}
