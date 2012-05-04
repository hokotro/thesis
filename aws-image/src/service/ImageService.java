package service;

import java.io.*;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;

import awsImageHandler.ImageDBHandler;
import awsImageHandler.ImageHandler;

public class ImageService {
	//private static final String bucketName = "kg-images";
	//private static final String domainName = "kg-images";
	private ImageHandler imageuploadhandler;
	private ImageDBHandler imagedbhandler;

	public ImageService() throws IOException {
		AWSCredentials credentials = new PropertiesCredentials(
				ImageService.class.getResourceAsStream("AwsCredentials.properties"));
		imageuploadhandler = new ImageHandler(credentials);
		imagedbhandler = new ImageDBHandler(credentials);
	}
	public ImageService(String name) throws IOException {
		imageuploadhandler = new ImageHandler(name);
		imagedbhandler = new ImageDBHandler(name);
	}
	
	public ImageService(AWSCredentials credentials) {
		imageuploadhandler = new ImageHandler(credentials);
		imagedbhandler = new ImageDBHandler(credentials);
	}
	
	public void putImage(String bucketName, String key, File file) throws com.amazonaws.AmazonServiceException,com.amazonaws.AmazonClientException, IOException{
		//each bucket has domain
		String domainName = bucketName;
		//upload image to S3
		imageuploadhandler.putFileToBucket(bucketName, key, file);
		//save image details to Simpledb
		imagedbhandler.putImageToDB(domainName, key, "https://s3.amazonaws.com/" + bucketName + "/" + key);
		//send SQS message to convert

	}

	public void getObjectToFile(String bucketName, String key, File file){
		imageuploadhandler.getObjectToFile(bucketName, key, file);
	}
	
	public String getImageUrl(String myDomain, String key) throws com.amazonaws.AmazonServiceException,com.amazonaws.AmazonClientException{
		return imagedbhandler.getAttribute(myDomain,key,"url");
	}
	public String getThumbnailImageUrl(String myDomain, String key) throws com.amazonaws.AmazonServiceException,com.amazonaws.AmazonClientException{
		return imagedbhandler.getAttribute(myDomain,key,"thumbnail");
	}
	public String getSmallImageUrl(String myDomain, String key) throws com.amazonaws.AmazonServiceException,com.amazonaws.AmazonClientException{
		return imagedbhandler.getAttribute(myDomain,key,"small");
	}
	public String getMediumImageUrl(String myDomain, String key) throws com.amazonaws.AmazonServiceException,com.amazonaws.AmazonClientException{
		return imagedbhandler.getAttribute(myDomain,key,"medium");
	}
	public String getLargeImageUrl(String myDomain, String key) throws com.amazonaws.AmazonServiceException,com.amazonaws.AmazonClientException{
		return imagedbhandler.getAttribute(myDomain,key,"large");
	}	
	
	public List<String> getBuckets(){
		return imageuploadhandler.listBuckets();
	}
	
	
}
