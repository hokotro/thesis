package service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class ImageMessageService {
	private AmazonSQS sqs;
	
	public ImageMessageService() throws com.amazonaws.AmazonServiceException,
										com.amazonaws.AmazonClientException, 
										IOException{
		sqs = new AmazonSQSClient(new PropertiesCredentials(
			ImageMessageService.class.getResourceAsStream("AwsCredentials.properties")));
	}
	
	public String createQueue(String name) throws com.amazonaws.AmazonServiceException,
												  com.amazonaws.AmazonClientException{
		CreateQueueRequest createQueueRequest = new CreateQueueRequest().withQueueName(name);
		String myQueueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();
		return myQueueUrl;
	}
	
	public List<String> listQueue() throws com.amazonaws.AmazonServiceException,
	  										com.amazonaws.AmazonClientException{
		List<String> list = new ArrayList<String>();
		// List queues
		//System.out.println("Listing all queues in your account.\n");
		for (String queueUrl : sqs.listQueues().getQueueUrls()) {
		    //System.out.println("  QueueUrl: " + queueUrl);
			list.add(queueUrl);
		}
		//System.out.println();
		return list;
	}
	
	public void sendMessage(String myQueueUrl, String MessageBody) 
			throws com.amazonaws.AmazonServiceException,
					com.amazonaws.AmazonClientException{
		sqs.sendMessage(new SendMessageRequest()
		.withQueueUrl(myQueueUrl)
		.withMessageBody(MessageBody));
		
		//sqs.deleteMessage(new DeleteMessageRequest().);
	}
	

	public List<String> receiveImageKeys(String myQueue)
			 throws com.amazonaws.AmazonServiceException,
			  com.amazonaws.AmazonClientException{
		List<String> list = new ArrayList<String>();
		String myQueueUrl = sqs.getQueueUrl(new GetQueueUrlRequest(myQueue)).getQueueUrl();
		
		System.out.println("Receiving messages from MyQueue.\n");
		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl);
		List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
		for (Message message : messages) {
			list.add(message.getBody());
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
		return list;
	}
	public void receiveMessage(String myQueue)
			 throws com.amazonaws.AmazonServiceException,
			  com.amazonaws.AmazonClientException{		
		String myQueueUrl = sqs.getQueueUrl(new GetQueueUrlRequest(myQueue)).getQueueUrl();
		
		System.out.println("Receiving messages from MyQueue.\n");
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
		System.out.println();
	}
}
