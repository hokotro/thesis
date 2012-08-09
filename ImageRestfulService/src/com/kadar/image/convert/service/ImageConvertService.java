package com.kadar.image.convert.service;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.kadar.image.aws.handler.FileUtils;
import com.kadar.image.aws.handler.Image;
import com.kadar.image.aws.handler.ImageConverter;
import com.kadar.image.aws.handler.DBImageHandler;

public class ImageConvertService implements ImageConvertServiceRemote{

	private static final String bucketName = "kg-images";
	private static final String domainName = "kg-images";

    private static final int THUMBNAIL_LONG_EDGE=150;
    private static final int SMALL_LONG_EDGE=600;
    private static final int MEDIUM_LONG_EDGE=800;
    private static final int LARGE_LONG_EDGE=1280;

    
    //private Image image; 
    //private ImageDBHandler imagedbhandler;
    
    public ImageConvertService(){
        //Consumer consumer = new Consumer(queue,this);
        //new Thread(consumer).start();
    }
    /*
	public ImageConvertEJB() throws IOException {
		AWSCredentials credentials = new PropertiesCredentials(
				ImageConvertEJB.class.getResourceAsStream("AwsCredentials.properties"));
		image = new Image(credentials);
		imagedbhandler = new ImageDBHandler(credentials);
	}*/
	
	/*
	public ImageConvertEJB(String properties) throws com.amazonaws.AmazonServiceException,com.amazonaws.AmazonClientException, IOException{
		 image = new Image(properties);
		 imagedbhandler = new ImageDBHandler(properties);
	}
	
	public ImageConvertEJB(AWSCredentials credentials){
		 image = new Image(credentials);
		 imagedbhandler = new ImageDBHandler(credentials);
	}*/
	
    /*
    public void putIntoQueue(String str) throws InterruptedException{
    	queue.put(str);
    }
    */
    
	public String generateThumbnail(String bucketName, String key) throws IOException{
		DBImageHandler imagedbhandler = new DBImageHandler();
		generateImage(bucketName, key, THUMBNAIL_LONG_EDGE, "thumb_" );
		imagedbhandler.setThumbnail(domainName, key, "https://s3.amazonaws.com/" + bucketName + "/thumb_" + key);
		return "https://s3.amazonaws.com/" + bucketName + "/thumb_" + key;		
	}
	
	public String generateSmall(String bucketName, String key) throws IOException{
		DBImageHandler imagedbhandler = new DBImageHandler();
		generateImage(bucketName, key, SMALL_LONG_EDGE, "small_" );
		imagedbhandler.setSmall(domainName, key, "https://s3.amazonaws.com/" + bucketName + "/small_" + key);
		return "https://s3.amazonaws.com/" + bucketName + "/small_" + key;	
	}
	
	public String generateMedium(String bucketName, String key) throws IOException{
		DBImageHandler imagedbhandler = new DBImageHandler();
		generateImage(bucketName, key, MEDIUM_LONG_EDGE, "medium_" );
		imagedbhandler.setMedium(domainName, key, "https://s3.amazonaws.com/" + bucketName + "/medium_" + key);
		return "https://s3.amazonaws.com/" + bucketName + "/medium_" + key;	
	}
	
	public String generateLarge(String bucketName, String key) throws IOException{
		DBImageHandler imagedbhandler = new DBImageHandler();
		generateImage(bucketName, key, LARGE_LONG_EDGE, "large_" );
		imagedbhandler.setLarge(domainName, key, "https://s3.amazonaws.com/" + bucketName + "/large_" + key);
		return "https://s3.amazonaws.com/" + bucketName + "/large_" + key;	
	}	
	
	
	private void generateImage(String bucketName, String key, int longEdge, String prefix ) throws IOException{
		Image image = new Image();
		String fileExt = ".jpg";

		File file = File.createTempFile(key, fileExt);
        file.deleteOnExit();
        
        ObjectMetadata object = image.getObjectToFile(bucketName, key, file);
        String objectContentType = object.getContentType();
        if( "image/jpeg".equals( objectContentType ) )
        	fileExt = ".jpg";
        if( "image/png".equals( objectContentType ) )
        	fileExt = ".png";
        if( "image/gif".equals( objectContentType ) )
        	fileExt = ".gif";
        if( "image/bmp".equals( objectContentType ) )
        	fileExt = ".bmp";
        
		//byte [] photoData = FileUtils.readFileToByteArray(file);
		byte [] photoData = FileUtils.readBytes(file);

		//ImageConverter imageconverter = new ImageConverter();
		byte [] newphotoData = ImageConverter.scalePhoto(longEdge, photoData);
		
	
		File newfile = File.createTempFile(prefix + key, fileExt);
        newfile.deleteOnExit();
		//FileUtils.writeByteArrayToFile(newfile, newphotoData);
		FileUtils.writeBytes(newphotoData, newfile);
		
		image.putObject(bucketName, prefix + key, newfile);
	}
}