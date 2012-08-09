package com.kadar.scalable.application;

import java.io.IOException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.kadar.image.convert.service.ImageConvertServiceRemote;
import com.kadar.image.message.handler.MessageHandler;
import com.kadar.image.message.handler.StatisticMessage;
import com.kadar.image.message.handler.StatisticMessageType;
import com.kadar.image.message.handler.TaskMessage;

public class ScalableApplication implements Scalable {

	private String bucketName = "kg-images";
	//
	private MessageHandler messageservice;
	private MessageHandler statisticservice;
	private ImageConvertServiceRemote convertservice;
	
	public ScalableApplication(MessageHandler mservice, ImageConvertServiceRemote cservice){
		messageservice = mservice;
		this.convertservice = cservice;
		
		try{
			this.statisticservice = new MessageHandler("default-statistic-queue");
			
		} catch (IOException ioe) {
	        System.out.println("IOException was expected.");
	        ioe.printStackTrace();
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
        }
	}
	
	@Override
	public void doBusinessLogic(String str) {

	}

	@Override
	public void doBusinessLogic(TaskMessage im) {

		String key = im.getValue();
		System.out.println(key);
		
		try{
            long startTime = System.currentTimeMillis();	
            
            
            convertservice.generateLarge(bucketName, key);
			convertservice.generateMedium(bucketName, key);
			convertservice.generateSmall(bucketName, key);
			convertservice.generateThumbnail(bucketName, key);			
			float endTime = (float) (System.currentTimeMillis() - startTime) / 1000;
			
			StatisticMessage statisticmessage = new StatisticMessage.Builder()
				.setStatisticType(StatisticMessageType.AllImageConvertion)
				.setKeyOfImage(key)
				.setTime(endTime)
				//.setValue(val) ide jön a kép mérete
				.build();
			statisticservice.sendMessage(statisticmessage);
			
            //System.out.println(String.format("It took %.2f seconds to receive %d messages",
            //		(float) (System.currentTimeMillis() - startTime) / 1000, TOTAL_MESSAGES_NUM));
            //System.out.println();
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}

}
