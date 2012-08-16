package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;


import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;


import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.kadar.aws.handler.EC2Handler;
import com.kadar.image.config.Config;
import com.kadar.message.handler.MessageHandler;
import com.kadar.message.handler.TaskMessage;
import com.kadar.message.handler.TaskMessageType;

public class S3Consumer implements Runnable {
	private static EC2Handler ec2;
	private MessageHandler s3mh;

	private Map<String, MessageHandler> smallInstances;
	private Map<String, MessageHandler> mediumInstances;
	private Map<String, MessageHandler> largeInstances;
	
	final List<Group> groups;
	final TaskIssues taskissues;
	
	public S3Consumer(List<Group> groups, TaskIssues taskissues ) 
					throws AmazonServiceException, AmazonClientException, IOException{
		this.groups = groups;
		this.taskissues = taskissues;
		
		s3mh = new MessageHandler(Config.s3queue);
		ec2 = new EC2Handler();

	}

	@Override
	public void run() {

		System.out.println("Messages from " + Config.s3queue + ": ");
		while(true){

			try {

				for(TaskMessage msg:  
					s3mh.receiveTaskMessagesWithDelete(Config.numberOfMaxReceivedMessage)){			
	
					for(Group group: groups){
						group.addJob(msg);
						
						taskissues.put(msg.getKeyOfImage(), group.groupid);
					}

				}
				
				
			} catch (AmazonServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AmazonClientException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}

	}
	
/*
	private static void sendMessage(String workerid, Map<String, MessageHandler> instances, TaskMessage tm) throws AmazonServiceException, JsonGenerationException, JsonMappingException, AmazonClientException, IOException{
		TaskMessage ntm = new TaskMessage.Builder()
		.setKeyOfImage(tm.getKeyOfImage())
		.setValue(tm.getValue())								
		.setMessageType(tm.getMessageType())
		.setStartTime(System.currentTimeMillis())				
		.build();

		MessageHandler mh = instances.get(workerid);
		mh.sendMessage(ntm);
	}
	*/
	
	
	private String getSmallWorker(){
		List<String> list = new ArrayList<String>();
		for(String str : smallInstances.keySet() ){
			list.add(str);
		}
		Random random = new Random();
		int id = random.nextInt(list.size());
		return list.get(id);
	}
	private String getMediumWorker(){
		List<String> list = new ArrayList<String>();
		for(String str : mediumInstances.keySet() ){
			list.add(str);
		}
		Random random = new Random();
		int id = random.nextInt(list.size());
		return list.get(id);
	}
	private String getLargeWorker(){
		List<String> list = new ArrayList<String>();
		for(String str : largeInstances.keySet() ){
			list.add(str);
		}
		Random random = new Random();
		int id = random.nextInt(list.size());
		return list.get(id);
	}

}
