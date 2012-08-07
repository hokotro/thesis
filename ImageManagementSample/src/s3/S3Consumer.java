package s3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import metric.Config;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;


import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.kadar.image.aws.handler.EC2Handler;
import com.kadar.image.message.handler.MessageHandler;
import com.kadar.image.message.handler.TaskMessage;
import com.kadar.image.message.handler.TaskMessageType;

public class S3Consumer implements Runnable {
	private static EC2Handler ec2;
	private MessageHandler s3mh;

	private Map<String, MessageHandler> smallInstances;
	private Map<String, MessageHandler> mediumInstances;
	private Map<String, MessageHandler> largeInstances;
	
	public S3Consumer(Map<String, MessageHandler> smallInstances, 
			Map<String, MessageHandler> mediumInstances, 
			Map<String, MessageHandler> largeInstances) 
					throws AmazonServiceException, AmazonClientException, IOException{
		s3mh = new MessageHandler(Config.S3QUEUE);
		ec2 = new EC2Handler();
		this.smallInstances = smallInstances;
		this.mediumInstances = mediumInstances;
		this.largeInstances = largeInstances;
	}

	@Override
	public void run() {

		System.out.println("Messages from " + Config.S3QUEUE + ": ");
		while(true){

			try {
				String smallworker, mediumworker, largeworker;
				smallworker = (smallInstances.size() > 0) ? smallworker = getSmallWorker() : null;
				mediumworker = (mediumInstances.size() > 0) ? mediumworker = getMediumWorker() : null;
				largeworker = (largeInstances.size() > 0) ? largeworker = getLargeWorker() : null;
						

				for(TaskMessage tm:  
					s3mh.receiveTaskMessagesWithDelete(Config.numberOfMaxReceivedMessage)){			
						if( smallworker != null && tm != null){
							System.out.println("Message from S3 to small worker : " + tm);	
							tm.setMessageType(TaskMessageType.ConvertSmallImage);						
							sendMessage(smallworker, smallInstances, tm);							
						}		
						if( mediumworker != null && tm != null){
							System.out.println("Message from S3 to medium worker : " + tm);		
							tm.setMessageType(TaskMessageType.ConvertMediumImage);					
							sendMessage(mediumworker, mediumInstances, tm);							
						}		
						if( largeworker != null && tm != null){
							System.out.println("Message from S3 to large worker : " + tm);		
							tm.setMessageType(TaskMessageType.ConvertLargeImage);					
							sendMessage(largeworker, largeInstances, tm);							
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
		}

	}

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
