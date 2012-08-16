package com.kadar.aws.handler;

import java.io.*;
import java.util.ArrayList;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketWebsiteConfiguration;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.kadar.aws.credentials.Credentials;

public class S3Handler implements Serializable{

	private static final long serialVersionUID = 1L;
	private AmazonS3 s3;
	private static String indexDoc = "index.html";
	private static String errorDoc = "error.html";

	public S3Handler() throws com.amazonaws.AmazonServiceException,com.amazonaws.AmazonClientException, IOException{
        s3 = new AmazonS3Client(new PropertiesCredentials(
				//getClass().getResourceAsStream("../../../../../AwsCredentials.properties")
				getClass().getClassLoader().getResourceAsStream("AwsCredentials.properties")
				));
    }
	
	/*
	public S3Handler(AWSCredentials credentials) throws com.amazonaws.AmazonServiceException,com.amazonaws.AmazonClientException, IOException{
        s3 = new AmazonS3Client(credentials);
	}
	*/

	public void putObjectWithPublicRead(String bucketName, String key, File file) throws com.amazonaws.AmazonServiceException,com.amazonaws.AmazonClientException{
		createBucketIfNotExists(bucketName, key);

		PutObjectResult por = s3.putObject(new PutObjectRequest(bucketName, key, file)
		.withCannedAcl(CannedAccessControlList.PublicRead));
		
	}
		
	public void putObjectWithPrivateAccess(String bucketName,
			String key, File file) {
		createBucketIfNotExists(bucketName, key);

		PutObjectResult por = s3.putObject(new PutObjectRequest(bucketName, key, file)
		//.withCannedAcl(CannedAccessControlList.PublicRead)
		);	
	}
	
	public void putFileToBucket(String bucketName, String key, File file) throws com.amazonaws.AmazonServiceException,com.amazonaws.AmazonClientException{
		createBucketIfNotExists(bucketName, key);

		PutObjectResult por = s3.putObject(new PutObjectRequest(bucketName, key, file)
		.withCannedAcl(CannedAccessControlList.PublicRead));	
	}

	public void deleteFileFromBucket(String bucketName, String key) throws com.amazonaws.AmazonServiceException,com.amazonaws.AmazonClientException{
        s3.deleteObject(bucketName, key);
	}
	
	private void createBucketIfNotExists(String bucketName, String index) throws com.amazonaws.AmazonServiceException,com.amazonaws.AmazonClientException{
		//check then create (act)
		if(!s3.doesBucketExist(bucketName)){
			s3.createBucket(bucketName);
			//s3.setBucketWebsiteConfiguration(new setBucketWebsiteConfigurationRequest(bucketName,BucketWebsiteConfiguration));
			s3.setBucketWebsiteConfiguration(bucketName, 
	    		   new BucketWebsiteConfiguration(index, errorDoc));
		}
	}

	public java.util.List<String> listBuckets() throws com.amazonaws.AmazonServiceException,com.amazonaws.AmazonClientException{
		java.util.List<String> bucketNames = new ArrayList<String>();
        for (Bucket bucket : s3.listBuckets()) {
            bucketNames.add(bucket.getName());
        }
        return bucketNames;
		//return java.util.Collections.unmodifiableList(s3.listBuckets());
	}
	
	public java.util.List<String> listObjectKeys(String bucketName) throws com.amazonaws.AmazonServiceException,com.amazonaws.AmazonClientException{
		java.util.ArrayList<String> objectkeys = new ArrayList<String>();
        ObjectListing objectListing = s3.listObjects(new ListObjectsRequest()
        	.withBucketName(bucketName));
        
        for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
        	objectkeys.add(objectSummary.getKey());
            //objectSummary.getSize()                       
        }
		return objectkeys;
	}
	
	public String getObjectURL(String bucketName, String key) throws com.amazonaws.AmazonServiceException,com.amazonaws.AmazonClientException{
		//return "http://"+bucketName + ".s3.amazonaws.com/" + key;
		return "http://s3.amazonaws.com/" + bucketName + "/" + key;
	}
	public void getObjectToFile(String bucketName, String key, File file) throws com.amazonaws.AmazonServiceException,com.amazonaws.AmazonClientException{
		s3.getObject(new GetObjectRequest(bucketName, key), file);
	}

	
}