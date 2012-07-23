package com.kadar.image.awsImageHandler;

import java.io.*;


import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class Image {

    private AmazonS3 s3;

    public Image()throws com.amazonaws.AmazonServiceException,com.amazonaws.AmazonClientException, IOException{
    	s3 = new AmazonS3Client(new PropertiesCredentials(
				 Image.class.getResourceAsStream("AwsCredentials.properties")));
    }

	public Image(String properties) throws com.amazonaws.AmazonServiceException,com.amazonaws.AmazonClientException, IOException{
		 s3 = new AmazonS3Client(new PropertiesCredentials(
				 Image.class.getResourceAsStream(properties)));
	}
	
	public Image(AWSCredentials credentials){
		 s3 = new AmazonS3Client(credentials);
	}

	public void putObject(String bucketName, String key, File file) throws com.amazonaws.AmazonServiceException,com.amazonaws.AmazonClientException{
		s3.putObject(new PutObjectRequest(bucketName, key, file)
		.withCannedAcl(CannedAccessControlList.PublicRead));	
	}
	public ObjectMetadata getObjectToFile(String bucketName, String key, File file) throws com.amazonaws.AmazonServiceException,com.amazonaws.AmazonClientException{
		return s3.getObject(new GetObjectRequest(bucketName, key), file);
	}
 
}
