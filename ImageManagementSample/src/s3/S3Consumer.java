package s3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import metric.Config;

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
	
	public S3Consumer(Map<String, MessageHandler> smallInstances) throws AmazonServiceException, AmazonClientException, IOException{
		s3mh = new MessageHandler(Config.S3QUEUE);
		ec2 = new EC2Handler();
		this.smallInstances = smallInstances; 
	}

	@Override
	public void run() {

		System.out.println("Messages from " + Config.S3QUEUE + ": ");
		while(true){

			try {				
				if(smallInstances.size() > 0){ 
					String worker = getSmallWorker();
					
					if( worker != null){
						for(TaskMessage tm: s3mh.receiveTaskMessagesWithDelete(Config.numberOfMaxReceivedMessage)){
							if(tm != null){
								System.out.println("Message from S3: " + tm);
								
								MessageHandler mh = smallInstances.get(worker);
								TaskMessage ntm = new TaskMessage.Builder()
								.setKeyOfImage(tm.getKeyOfImage())
								.setValue(tm.getValue())								
								.setMessageType(TaskMessageType.ImageToConvert)
								.setStartTime(System.currentTimeMillis())				
								.build();
								
								mh.sendMessage(ntm);
							}
						}
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
	
	private String getSmallWorker(){
		List<String> list = new ArrayList<String>();
		for(String str : ec2.listOfRunningInstances(Config.ConverterInstanceImageId, Config.smallInstanceType) ){
			list.add(str);
		}
		Random random = new Random();
		int id = random.nextInt(list.size());
		return list.get(id);
	}

}
