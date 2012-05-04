package service;

import java.io.*;

import Utils.FileUtils;
import awsImageHandler.Image;
import awsImageHandler.ImageConverter;
import awsImageHandler.ImageDBHandler;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class ImageConvertService {
	private static final String bucketName = "kg-images";
	private static final String domainName = "kg-images";

    private static final int THUMBNAIL_LONG_EDGE=150;
    private static final int SMALL_LONG_EDGE=600;
    private static final int MEDIUM_LONG_EDGE=800;
    private static final int LARGE_LONG_EDGE=1280;

    private Image image; 
    private ImageDBHandler imagedbhandler;

	public ImageConvertService() throws IOException {
		AWSCredentials credentials = new PropertiesCredentials(
				ImageService.class.getResourceAsStream("AwsCredentials.properties"));
		imagedbhandler = new ImageDBHandler(credentials);
	}
	
	public ImageConvertService(String properties) throws com.amazonaws.AmazonServiceException,com.amazonaws.AmazonClientException, IOException{
		 image = new Image(properties);
		 imagedbhandler = new ImageDBHandler(properties);
	}
	
	public ImageConvertService(AWSCredentials credentials){
		 image = new Image(credentials);
		 imagedbhandler = new ImageDBHandler(credentials);
	}
	
	public void generateThumbnail(String bucketName, String key) throws IOException{
		generateImage(bucketName, key, THUMBNAIL_LONG_EDGE, "thumb_" );
		imagedbhandler.setThumbnail(domainName, key, "https://s3.amazonaws.com/" + bucketName + "/thumb_" + key);
	}
	
	public void generateSmall(String bucketName, String key) throws IOException{
		generateImage(bucketName, key, SMALL_LONG_EDGE, "small_" );
		imagedbhandler.setSmall(domainName, key, "https://s3.amazonaws.com/" + bucketName + "/small_" + key);
	}
	
	public void generateMedium(String bucketName, String key) throws IOException{
		generateImage(bucketName, key, MEDIUM_LONG_EDGE, "medium_" );
		imagedbhandler.setMedium(domainName, key, "https://s3.amazonaws.com/" + bucketName + "/medium_" + key);
	}
	
	public void generateLarge(String bucketName, String key) throws IOException{
		generateImage(bucketName, key, LARGE_LONG_EDGE, "large_" );
		imagedbhandler.setLarge(domainName, key, "https://s3.amazonaws.com/" + bucketName + "/large_" + key);
	}	
	
	
	private void generateImage(String bucketName, String key, int longEdge, String prefix ) throws IOException{
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

		awsImageHandler.ImageConverter imageconverter = new ImageConverter();
		byte [] newphotoData = imageconverter.scalePhoto(longEdge, photoData);
		
	
		File newfile = File.createTempFile(prefix + key, fileExt);
        newfile.deleteOnExit();
		//FileUtils.writeByteArrayToFile(newfile, newphotoData);
		FileUtils.writeBytes(newphotoData, newfile);
		
		image.putObject(bucketName, prefix + key, newfile);
	}
}
