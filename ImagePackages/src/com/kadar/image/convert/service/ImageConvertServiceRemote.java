package com.kadar.image.convert.service;

import java.io.IOException;

public interface ImageConvertServiceRemote {

	public String generateThumbnail(String bucketName, String key) throws IOException;
	
	public String generateSmall(String bucketName, String key) throws IOException;
	
	public String generateMedium(String bucketName, String key) throws IOException;
	
	public String generateLarge(String bucketName, String key) throws IOException;
}
