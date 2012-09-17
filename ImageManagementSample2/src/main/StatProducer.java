package main;

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.kadar.image.config.Config;
import com.kadar.message.handler.MessageHandler;
import com.kadar.message.handler.TaskMessage;
import com.kadar.message.handler.TaskMessageType;
import com.kadar.statistic.StatHolder;
import com.kadar.util.ConcurrentListHolderMap;

public class StatProducer implements Runnable {

	/**
	 * check the assigned tasks are completed: 
	 * end of task: all job in each groups are completed 
	 * string key: id of an task
	 * integer value: id of an group
	 */
	private final ConcurrentListHolderMap<String, Integer> assignedtasks;

	/** 
	 * integer key: groupid
	 * statholder value: statholder with taskmessages
	 */
	private final Map<Integer, StatHolder<TaskMessage>> groupstatistic;
	/** 
	 * string key: instanceid
	 * statholder value: statholder with taskmessages
	 */
	private final Map<String, StatHolder<TaskMessage>> instancestatistic;

	private final StatHolder<TaskMessage> statholder;

	private final MessageHandler smh;
	
	public StatProducer( StatHolder<TaskMessage> statholder,
			Map<Integer, StatHolder<TaskMessage>> groupstatistic,
			Map<String, StatHolder<TaskMessage>> instancestatistic,
			ConcurrentListHolderMap<String, Integer> assignedtasks) 
					throws AmazonServiceException, AmazonClientException, IOException{
		
		this.statholder = statholder;
		this.groupstatistic = groupstatistic;
		this.instancestatistic = instancestatistic;
		this.assignedtasks = assignedtasks;
		
		this.smh = new MessageHandler(Config.statisticMessageQueue);
	}
	
	
	@Override
	public void run() {

		while(true){
			
			try {
				for(TaskMessage msg: smh.receiveTaskMessagesWithDelete(Config.numberOfMaxReceivedMessage)){
					
					if( msg != null && msg.getMessageType().equals(TaskMessageType.ImageToConvert) ){
						
						
						assignedtasks.removeValue(msg.getKeyOfImage(), Integer.parseInt(msg.getGroupId()));
						if( assignedtasks.isKeyEmpty(msg.getKeyOfImage()) ){							
							msg.setEndTaskTime(System.currentTimeMillis());

							statholder.add(msg);
							groupstatistic.get(Integer.parseInt(msg.getGroupId())).add(msg);
							instancestatistic.get(msg.getInstanceId()).add(msg);
							
							System.out.println(msg);							
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
