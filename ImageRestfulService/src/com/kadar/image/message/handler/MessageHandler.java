package com.kadar.image.message.handler;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

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
import com.kadar.image.aws.handler.EC2Handler;


public class MessageHandler {
	
	private AmazonSQS sqs;
	private String myQueueUrl;

	/*
	public MessageHandler() throws AmazonServiceException, AmazonClientException, IOException { 
        sqs = new AmazonSQSClient(new PropertiesCredentials(
        		MessageHandler.class.getResourceAsStream("../../../../../AwsCredentials.properties")));                     
	}
	*/
	
	public MessageHandler(String queueName) throws AmazonServiceException, AmazonClientException, IOException { 
        sqs = new AmazonSQSClient(new PropertiesCredentials(
        		//getClass().getResourceAsStream("../../../../../AwsCredentials.properties")));
        		MessageHandler.class.getClassLoader().getResourceAsStream("AwsCredentials.properties")));
		
        if( isQueue(queueName) ){
        	this.myQueueUrl = this.getQueueUrlByName(queueName);
        }
        else {        	
        	this.myQueueUrl = createQueue(queueName);
        }
	}

	private String ConvertToJSON(TaskMessage taskmessage) throws JsonGenerationException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		Writer strWriter = new StringWriter();
		mapper.writeValue(strWriter, taskmessage);		
		String userDataJSON = strWriter.toString();		
		//System.out.println(userDataJSON);
		return userDataJSON;		
	}
	private String ConvertToJSON(StatisticMessage statisticmessage) throws JsonGenerationException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		Writer strWriter = new StringWriter();
		mapper.writeValue(strWriter, statisticmessage);		
		String userDataJSON = strWriter.toString();		
		//System.out.println(userDataJSON);
		return userDataJSON;		
	}

	private TaskMessage ConvertTaskMessageFromJSON(String userDataJSON) throws JsonParseException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		TaskMessage tm = mapper.readValue(userDataJSON, TaskMessage.class);
		tm.setEndTime(System.currentTimeMillis());
		return tm;
		//return message.toString();
	}
	private StatisticMessage ConvertStatisticMessageFromJSON(String userDataJSON) throws JsonParseException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		StatisticMessage tm = mapper.readValue(userDataJSON, StatisticMessage.class);
		//tm.setEndTime(System.currentTimeMillis());
		return tm;
		//return message.toString();
	}
	
	public String getMyQueueUrl() {
		return myQueueUrl;
	}

	public void setMyQueueUrl(String myQueueUrl) {
		this.myQueueUrl = myQueueUrl;		
	}
	
	/*
	public void setMyQueue(String queueName) {
		if( isQueue(queueName) ){
        	this.myQueueUrl = this.getQueueUrlByName(queueName);
        }
        else {        	
        	this.myQueueUrl = createQueue(queueName);
        }
	}
	*/

	private Boolean isQueue(String str){
		//System.out.println( "Is Queue with Name :" + str );
		List<String> list = sqs.listQueues().getQueueUrls();
		return list.contains(str);
	}
	
	private String getQueueUrlByName(String name) throws QueueNameExistsException {
		//System.out.println( "Getting QueueUrl by Name: " + name );
		for (String queueUrl : sqs.listQueues().getQueueUrls()) {
			if( queueUrl.contains(name) ) return queueUrl;
		}
		throw new QueueNameExistsException("QueueName doesnt exists.");
	}
	
	public String createQueue(String myQueue) throws AmazonServiceException, AmazonClientException, JsonGenerationException, JsonMappingException, IOException { 
        // Create a queue
        //System.out.println("Creating a new SQS queue called " + myQueue);
        CreateQueueRequest createQueueRequest = new CreateQueueRequest(myQueue);
        
        this.myQueueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();
        /*
        TaskMessage tmsg = new TaskMessage.Builder().setMessageType(TaskMessageType.QueueCreate).build();
        this.sendMessage(tmsg);
        String ready = this.receiveMessage();
        while(ready == null ){
        	;
        }
        */
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
	public void sendMessage(TaskMessage message) throws AmazonServiceException, AmazonClientException, JsonGenerationException, JsonMappingException, IOException {
        // Send a message
        //System.out.println("Sending a message to MyQueue.\n");
        sqs.sendMessage(new SendMessageRequest(myQueueUrl, this.ConvertToJSON(message) ));        
	}
	
	public void sendMessage(StatisticMessage message) throws AmazonServiceException, AmazonClientException, JsonGenerationException, JsonMappingException, IOException {
        // Send a message
        //System.out.println("Sending a message to MyQueue.\n");
        sqs.sendMessage(new SendMessageRequest(myQueueUrl, this.ConvertToJSON(message) ));       
	}
	
	public String receiveMessage() throws AmazonServiceException, AmazonClientException {		
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl);        
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        if(!messages.isEmpty()) {
        	Message message = messages.get(0);
        	return message.getBody();
		}
        return null;
	}
	public String receiveMessageWithDelete() throws AmazonServiceException, AmazonClientException {		
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl);        
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        if(!messages.isEmpty()) {
        	Message message = messages.get(0);
        	deleteMessage(message.getReceiptHandle());
        	return message.getBody();
		}
        return null;
	}

	public List<String> receiveMessages(int maxNumberOfMessages) throws AmazonServiceException, AmazonClientException {
		List<String> list = new ArrayList<String>();
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl).withMaxNumberOfMessages(maxNumberOfMessages);        
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        if(!messages.isEmpty()) {
        	for(Message message : messages){
        		list.add(message.getBody());
        	}
		}
        return list;
	}

	public List<String> receiveMessagesWithDelete(int maxNumberOfMessages) throws AmazonServiceException, AmazonClientException {
		List<String> list = new ArrayList<String>();
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl).withMaxNumberOfMessages(maxNumberOfMessages);        
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        if(!messages.isEmpty()) {
        	for(Message message : messages){
        		list.add(message.getBody());
        		deleteMessage(message.getReceiptHandle());
        	}
		}
        return list;
	}

	public TaskMessage receiveTaskMessage() throws AmazonServiceException, AmazonClientException, JsonParseException, JsonMappingException, IOException {		
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl);        
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        if(!messages.isEmpty()) {
        	Message message = messages.get(0);
        	TaskMessage tm = this.ConvertTaskMessageFromJSON(message.getBody());
			tm.setMessageId(message.getMessageId());
			tm.setReceiptHandle(message.getReceiptHandle());
	        return tm;	        
		}
        return null;
	}

	public TaskMessage receiveTaskMessageWithDelete() throws AmazonServiceException, AmazonClientException, JsonParseException, JsonMappingException, IOException {		
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl);        
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        if(!messages.isEmpty()) {
        	Message message = messages.get(0);
        	deleteMessage(message.getReceiptHandle());
 
        	TaskMessage tm = this.ConvertTaskMessageFromJSON(message.getBody());
			tm.setMessageId(message.getMessageId());
			tm.setReceiptHandle(message.getReceiptHandle());
	        return tm;	    
		}
        return null;
	}

	public List<TaskMessage> receiveTaskMessages(int maxNumberOfMessages) throws AmazonServiceException, AmazonClientException, JsonParseException, JsonMappingException, IOException {
		List<TaskMessage> list = new ArrayList<TaskMessage>();
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl).withMaxNumberOfMessages(maxNumberOfMessages);        
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        if(!messages.isEmpty()) {
        	for(Message message: messages){
            	TaskMessage tm = this.ConvertTaskMessageFromJSON(message.getBody());
    			tm.setMessageId(message.getMessageId());
    			tm.setReceiptHandle(message.getReceiptHandle());
    	        list.add(tm);	        
        	}
		}
        return list;
	}
	public List<TaskMessage> receiveTaskMessagesWithDelete(int maxNumberOfMessages) throws AmazonServiceException, AmazonClientException, JsonParseException, JsonMappingException, IOException {
		List<TaskMessage> list = new ArrayList<TaskMessage>();
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl).withMaxNumberOfMessages(maxNumberOfMessages);        
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        if(!messages.isEmpty()) {
        	for(Message message: messages){
            	TaskMessage tm = this.ConvertTaskMessageFromJSON(message.getBody());
    			tm.setMessageId(message.getMessageId());
    			tm.setReceiptHandle(message.getReceiptHandle());
    	        list.add(tm);	   
    	        deleteMessage(tm);
        	}
		}
        return list;
	}
	
	public StatisticMessage receiveStatisticMessage() throws AmazonServiceException, AmazonClientException, JsonParseException, JsonMappingException, IOException {		
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl);        
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        if(!messages.isEmpty()) {
        	Message message = messages.get(0);

        	StatisticMessage sm = this.ConvertStatisticMessageFromJSON(message.getBody());
        	return sm;
		}
        return null;
	}
	
	public StatisticMessage receiveStatisticMessageWithDelete() throws AmazonServiceException, AmazonClientException, JsonParseException, JsonMappingException, IOException {		
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl);        
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        if(!messages.isEmpty()) {
        	Message message = messages.get(0);
        	deleteMessage(message.getReceiptHandle());

        	StatisticMessage sm = this.ConvertStatisticMessageFromJSON(message.getBody());        	
        	return sm;
		}
        return null;
	}

	public List<StatisticMessage> receiveStatisticMessages(int maxNumberOfMessages) throws AmazonServiceException, AmazonClientException, JsonParseException, JsonMappingException, IOException {
		List<StatisticMessage> list = new ArrayList<StatisticMessage>();
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl).withMaxNumberOfMessages(maxNumberOfMessages);        
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        if(!messages.isEmpty()) {
        	for(Message message : messages){
        		StatisticMessage sm = this.ConvertStatisticMessageFromJSON(message.getBody());
        		list.add(sm);
        	}
		}
        return list;
	}
	public List<StatisticMessage> receiveStatisticMessagesWithDelete(int maxNumberOfMessages) throws AmazonServiceException, AmazonClientException, JsonParseException, JsonMappingException, IOException {
		List<StatisticMessage> list = new ArrayList<StatisticMessage>();
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl).withMaxNumberOfMessages(maxNumberOfMessages);        
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        if(!messages.isEmpty()) {
        	for(Message message : messages){
        		if(message.getBody() != null){
        			StatisticMessage sm = this.ConvertStatisticMessageFromJSON(message.getBody());
        			list.add(sm);
        			deleteMessage(message.getReceiptHandle());
        		}
        	}
		}
        return list;
	}
	

	public List<TaskMessage> receiveMessages() throws AmazonServiceException, AmazonClientException, JsonParseException, JsonMappingException, IOException {
		List<TaskMessage> list = new ArrayList<TaskMessage>();
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl);        
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        for (Message message : messages) { 
        	TaskMessage tm = this.ConvertTaskMessageFromJSON(message.getBody());
			tm.setMessageId(message.getMessageId());
			tm.setReceiptHandle(message.getReceiptHandle());
	        
	        list.add(tm);

        }
        return list;
	}
	
	public void receiveMessagesToPrintln() throws AmazonServiceException, AmazonClientException {
        // Receive messages
        //System.out.println("Receiving messages from MyQueue.\n");
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
        //System.out.println("Deleting a message: " + ReceiptHandle);
        sqs.deleteMessage(new DeleteMessageRequest(myQueueUrl, ReceiptHandle));
	}
	public void deleteMessage(TaskMessage imagemessage) throws AmazonServiceException, AmazonClientException { 
		//ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl);
		//List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();    
        // Delete a message
        //System.out.println("Deleting a message: " + imagemessage.getMessageBodyInJSON());
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
