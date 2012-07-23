package com.kadar.image.awsImageHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.QueueNameExistsException;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;


public class QueueMessageHandler {
	
	private AmazonSQS sqs;
	private String myQueueUrl = "default-convert-Queue";

	public QueueMessageHandler() throws AmazonServiceException, AmazonClientException, IOException { 
        sqs = new AmazonSQSClient(new PropertiesCredentials(
        		QueueMessageHandler.class.getResourceAsStream("AwsCredentials.properties")));                     
	}
	
	public QueueMessageHandler(String queueName) throws AmazonServiceException, AmazonClientException, IOException { 
        sqs = new AmazonSQSClient(new PropertiesCredentials(
        		QueueMessageHandler.class.getResourceAsStream("AwsCredentials.properties")));
        
        if( isQueue(queueName) ){
        	this.myQueueUrl = this.getQueueUrlByName(queueName);
        }
        else {        	
        	this.myQueueUrl = createQueue(queueName);
        }
	}
	
	public String getMyQueueUrl() {
		return myQueueUrl;
	}

	public void setMyQueueUrl(String myQueueUrl) {
		this.myQueueUrl = myQueueUrl;		
	}
	
	public void setMyQueue(String queueName) {
		if( isQueue(queueName) ){
        	this.myQueueUrl = this.getQueueUrlByName(queueName);
        }
        else {        	
        	this.myQueueUrl = createQueue(queueName);
        }
	}	

	private Boolean isQueue(String str){
		System.out.println( "Is Queue with Name :" + str );
		for (String queueUrl : sqs.listQueues().getQueueUrls()) {
			if( queueUrl.contains(str) ) return true; 
		}
		return false;
	}
	
	private String getQueueUrlByName(String name) throws QueueNameExistsException {
		System.out.println( "Getting QueueUrl by Name: " + name );
		for (String queueUrl : sqs.listQueues().getQueueUrls()) {
			if( queueUrl.contains(name) ) return queueUrl;
		}
		throw new QueueNameExistsException("QueueName doesnt exists.");
	}
	
	public String createQueue(String myQueue) throws AmazonServiceException, AmazonClientException { 
        // Create a queue
        System.out.println("Creating a new SQS queue called " + myQueue);
        CreateQueueRequest createQueueRequest = new CreateQueueRequest(myQueue);
        
        this.myQueueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();
        return myQueueUrl;
	}
	
	public List<String> listQueue() throws AmazonServiceException, AmazonClientException { 
        // List queues		
        //System.out.println("Listing all queues in your account.\n");
		List<String> list = new ArrayList<String>();
        for (String queueUrl : sqs.listQueues().getQueueUrls()) {
            //System.out.println("  QueueUrl: " + queueUrl);
        	list.add(queueUrl);
        }
        System.out.println();
        return list;
	}

	public void sendMessage(String message) throws AmazonServiceException, AmazonClientException {
        // Send a message
        //System.out.println("Sending a message to MyQueue.\n");
        sqs.sendMessage(new SendMessageRequest(myQueueUrl, message));        
	}
	public void sendMessage(TaskMessage message) throws AmazonServiceException, AmazonClientException {
        // Send a message
        //System.out.println("Sending a message to MyQueue.\n");
        sqs.sendMessage(new SendMessageRequest(myQueueUrl, message.getBody()));        
	}
	public void sendMessage(StatisticMessage message) throws AmazonServiceException, AmazonClientException {
        // Send a message
        //System.out.println("Sending a message to MyQueue.\n");
        sqs.sendMessage(new SendMessageRequest(myQueueUrl, message.getBody()));        
	}

	public TaskMessage receiveTaskMessage() throws AmazonServiceException, AmazonClientException {		
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl);        
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        if(!messages.isEmpty()) {
        	Message message = messages.get(0);
        //if(null != message){
        	TaskMessage im = new TaskMessage.Builder()
        				//.WhatToDo(ImageMessageType.ImageToConvert)
        				.Body(message.getBody())
        				.MessageId(message.getMessageId())
        				.ReceiptHandle(message.getReceiptHandle())
        				.build();
        	return im;
		}
        return null;
	}
	public StatisticMessage receiveStatisticMessage() throws AmazonServiceException, AmazonClientException {		
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl);        
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        if(!messages.isEmpty()) {
        	Message message = messages.get(0);
        //if(null != message){
        	StatisticMessage im = new StatisticMessage.Builder()
        				//.WhatToDo(ImageMessageType.ImageToConvert)
        				.Body(message.getBody())
        				.MessageId(message.getMessageId())
        				.ReceiptHandle(message.getReceiptHandle())
        				.build();
        	return im;
		}
        return null;
	}

	public List<TaskMessage> receiveMessages() throws AmazonServiceException, AmazonClientException {
		List<TaskMessage> list = new ArrayList<TaskMessage>();
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl);        
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        for (Message message : messages) {
        	TaskMessage im = new TaskMessage.Builder()
        				//.WhatToDo(ImageMessageType.ImageToConvert)
        				.MessageId(message.getMessageId())
        				.ReceiptHandle(message.getReceiptHandle())
        				.Body(message.getBody())
        				.build();
        	list.add(im);
        }
        return list;
	}
	
	public void receiveMessagesToPrintln() throws AmazonServiceException, AmazonClientException {
        // Receive messages
        System.out.println("Receiving messages from MyQueue.\n");
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl);
        receiveMessageRequest.setMaxNumberOfMessages(10);
        receiveMessageRequest.setVisibilityTimeout(20);
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        for (Message message : messages) {
            System.out.println("  Message");
            System.out.println("    MessageId:     " + message.getMessageId());
            System.out.println("    ReceiptHandle: " + message.getReceiptHandle());
            System.out.println("    MD5OfBody:     " + message.getMD5OfBody());
            System.out.println("    Body:          " + message.getBody());
            for (Entry<String, String> entry : message.getAttributes().entrySet()) {
                System.out.println("  Attribute");
                System.out.println("    Name:  " + entry.getKey());
                System.out.println("    Value: " + entry.getValue());
            }
        }
        System.out.println();
	}

	public void deleteMessage(String ReceiptHandle) throws AmazonServiceException, AmazonClientException { 
		//ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl);
		//List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();    
        // Delete a message
        System.out.println("Deleting a message: " + ReceiptHandle);
        sqs.deleteMessage(new DeleteMessageRequest(myQueueUrl, ReceiptHandle));
	}
	public void deleteMessage(TaskMessage imagemessage) throws AmazonServiceException, AmazonClientException { 
		//ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl);
		//List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();    
        // Delete a message
        System.out.println("Deleting a message: " + imagemessage.getBody());
        sqs.deleteMessage(new DeleteMessageRequest(myQueueUrl, imagemessage.getReceiptHandle()));        
	}
	
	public void deleteQueue(String str) throws AmazonServiceException, AmazonClientException {
		String qToDel = str;
		if( ! str.contains("http:") ) {
			qToDel = getQueueUrlByName(str);			
		}
        // Delete a queue
        //System.out.println("Deleting the test queue.\n");
        sqs.deleteQueue(new DeleteQueueRequest(qToDel));
	}
}
