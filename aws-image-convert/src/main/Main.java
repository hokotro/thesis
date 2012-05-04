package main;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;

import service.ImageConvertService;
import service.ImageMessageService;
import service.ImageService;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		ImageConvertService imageconvertservice;
		ImageMessageService imagemessageservice;
		
		// TODO Auto-generated method stub
		try{			
			imageconvertservice = new ImageConvertService(); 
			imagemessageservice = new ImageMessageService(); 
			
			while(true){
				for(String key : imagemessageservice.receiveImageKeys("kg-imageconvert-queue"))
					try{
						imageconvertservice.generateThumbnail("kg-images", key);
						
					} catch (AmazonClientException ace) {
						"NoSuchKey".equals(ace.getMessage());
						continue;
					}
			}
			
		} catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it " +
                    "to Amazon SQS, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered " +
                    "a serious internal problem while trying to communicate with SQS, such as not " +
                    "being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        } catch ( IOException ioex ){
        	ioex.printStackTrace();        	
        }
		
	}

}
