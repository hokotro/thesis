package com.kadar.image.management.s3upload;

import java.io.IOException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.kadar.image.message.handler.MessageHandler;
import com.kadar.image.message.handler.TaskMessage;

public class S3UploadConsumer implements Runnable{

	private MessageHandler mh;
	
	public S3UploadConsumer() throws AmazonServiceException, AmazonClientException, IOException{
		mh = new MessageHandler("default-s3-upload-queue");
	}
	
	private TaskMessage get(){
		return null;
	}
	
	@Override
	public void run() {
		
	}
}
