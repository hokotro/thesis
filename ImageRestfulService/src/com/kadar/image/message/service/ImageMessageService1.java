package com.kadar.image.message.service;

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
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;


public class ImageMessageService1 {

	private AmazonSQS sqs;
	private String myQueueUrl = "defaultQueue";
	

	public ImageMessageService1(PropertiesCredentials credentials, String myQueueUrl) throws AmazonServiceException, AmazonClientException, IOException{
		this.sqs = new AmazonSQSClient(credentials);
		this.myQueueUrl = myQueueUrl;
	}
	
	public ImageMessageService1(String myQueueUrl) throws AmazonServiceException, AmazonClientException, IOException{
      
		this.myQueueUrl = myQueueUrl;        
		this.sqs = new AmazonSQSClient(new PropertiesCredentials(
				getClass().getResourceAsStream("AwsCredentials.properties")));
		
		GetQueueAttributesResult result = sqs.getQueueAttributes(new GetQueueAttributesRequest("alma"));
		for(String str: result.getAttributes().keySet()){
			System.out.println(str);
		}
		Cred();
		//for(String lqr : sqs.listQueues().getQueueUrls()){
		//	System.out.println(lqr);
		//}
			
		/*
		GetQueueAttributesRequest gqar = new GetQueueAttributesRequest();
		gqar.setQueueUrl(myQueueUrl);
		gqar.setRequestCredentials(new PropertiesCredentials(
        		ImageMessageService.class.getResourceAsStream("AwsCredentials.properties")));
		GetQueueAttributesResult qar = sqs.getQueueAttributes(gqar);
		for( String str: qar.getAttributes().keySet()){
			System.out.println(str);
		}*/
					
		//createQueue(this.myQueueUrl);
	}
	
	public void Cred(){
		GetQueueAttributesResult result = sqs.getQueueAttributes(new GetQueueAttributesRequest());
		for(String str: result.getAttributes().keySet()){
			System.out.println(str);
		}
	}
		
	
	private String createQueue(String str) throws AmazonServiceException, AmazonClientException{
        // Create a queue
        //System.out.println("Creating a new SQS queue called MyQueue.\n");
        CreateQueueRequest createQueueRequest = new CreateQueueRequest(str);
        myQueueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();
		return myQueueUrl;
	}
	private void deleteQueue(String myQueueUrl) throws AmazonServiceException, AmazonClientException{
        // Delete a queue
        //System.out.println("Deleting the test queue.\n");
        sqs.deleteQueue(new DeleteQueueRequest(myQueueUrl));
	}
	
	public List<String> listQueueURLs() throws AmazonServiceException, AmazonClientException{
		List<String> list  =new ArrayList<String>();
        // List queues
        //System.out.println("Listing all queues in your account.\n");
        for (String queueUrl : sqs.listQueues().getQueueUrls()) {
            //System.out.println("  QueueUrl: " + queueUrl);
            list.add(queueUrl);
        }
        //System.out.println();
        return list;
	}
	
	public void sendMessage(String message) throws AmazonServiceException, AmazonClientException, IOException{
        // Send a message
        //System.out.println("Sending a message to MyQueue.\n");
		sqs = new AmazonSQSClient(new PropertiesCredentials(
				getClass().getResourceAsStream("AwsCredentials.properties")));
        sqs.sendMessage(new SendMessageRequest(myQueueUrl, message));
	}
	
	public void receiveMessage() throws AmazonServiceException, AmazonClientException{
        // Receive messages
        //System.out.println("Receiving messages from MyQueue.\n");
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl);
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        for (Message message : messages) {
            //System.out.println("  Message");
            //System.out.println("    MessageId:     " + message.getMessageId());
            //System.out.println("    ReceiptHandle: " + message.getReceiptHandle());
            //System.out.println("    MD5OfBody:     " + message.getMD5OfBody());
            //System.out.println("    Body:          " + message.getBody());
            for (Entry<String, String> entry : message.getAttributes().entrySet()) {
                //System.out.println("  Attribute");
                //System.out.println("    Name:  " + entry.getKey());
                //System.out.println("    Value: " + entry.getValue());
            }
        }
        //System.out.println();
	}
	
	public void receiveMessages() throws AmazonServiceException, AmazonClientException, IOException{
        // Receive messages
        //System.out.println("Receiving messages from MyQueue.\n");

		AmazonSQS sqs = new AmazonSQSClient(new PropertiesCredentials(
				getClass().getResourceAsStream("AwsCredentials.properties")));
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl);
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
        //System.out.println();
	}
	
	public void deleteMessage() throws AmazonServiceException, AmazonClientException{
		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl);    
		List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
    
        // Delete a message
        //System.out.println("Deleting a message.\n");
        String messageRecieptHandle = messages.get(0).getReceiptHandle();
        sqs.deleteMessage(new DeleteMessageRequest(myQueueUrl, messageRecieptHandle));
	}
	
}
