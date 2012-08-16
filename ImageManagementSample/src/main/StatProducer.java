package main;



import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.kadar.image.config.Config;
import com.kadar.message.handler.MessageHandler;
import com.kadar.message.handler.TaskMessage;
import com.kadar.message.handler.TaskMessageType;

public class StatProducer implements Runnable {

	private final MessageHandler smh;
	private final StatHolder statholder1;
	private final StatHolder statholder2;
	private final TaskIssues taskissues;
	
	public StatProducer(StatHolder holder, StatHolder holder2, TaskIssues taskissues) throws AmazonServiceException, AmazonClientException, IOException{
		this.statholder1 = holder;
		this.statholder2 = holder2;
		this.taskissues = taskissues;
		smh = new MessageHandler(Config.statisticMessageQueue);
	}
	
	
	@Override
	public void run() {

		while(true){
			
			try {
				for(TaskMessage msg: smh.receiveTaskMessagesWithDelete(Config.numberOfMaxReceivedMessage)){
					
					if( msg != null && msg.getMessageType().equals(TaskMessageType.ImageToConvert) ){
						
						taskissues.removeValue(msg.getKeyOfImage(), msg.getGroupId());
						if( taskissues.isKeyEmpty(msg.getKeyOfImage()) ){
							msg.setEndJobTime(System.currentTimeMillis());

							statholder1.add(msg);
							statholder2.add(msg);
							
							
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
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}

}
