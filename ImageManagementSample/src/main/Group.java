package main;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.kadar.aws.handler.EC2Handler;
import com.kadar.image.config.Config;
import com.kadar.message.handler.MessageHandler;
import com.kadar.message.handler.TaskMessage;
import com.kadar.message.handler.TaskMessageType;

public class Group {

	Map<String, MessageHandler> instances = new HashMap<String, MessageHandler>();
	final String groupid;
	final String instancetype;
	final String convertsize;
	//int pieceofinstance = 1;
	final EC2Handler ec2;
	
	public Group(String instancetype, String convertsize, int pieceofinstance) 
			throws AmazonServiceException, AmazonClientException, IOException{
			
		Random random = new Random();			
		this.groupid = new BigInteger(130, random).toString(32);;
		
		this.instancetype = instancetype;
		this.convertsize = convertsize;
		
		ec2 = new EC2Handler();				
		
		// just for testing
		List<String> instanceIDs = ec2.listOfRunningInstances(Config.ConverterInstanceImageId, instancetype);
		if( instanceIDs.size() == 0 )
			instanceIDs = ec2.runInstance(Config.ConverterInstanceImageId, instancetype, pieceofinstance);
		
		for( String id : instanceIDs ){
			MessageHandler mh = new MessageHandler(id + "-queue");
			mh.sendMessage(
					new TaskMessage.Builder()
					.setMessageType(TaskMessageType.InstanceStart)
					.setstartJobTime(System.currentTimeMillis())
					.build() 
					);
			instances.put( id, mh);
		}
		

		while(instances.size() <= 0){
			;;
		}
		
		//StringBuilder idToLog = new StringBuilder();
		System.out.print("Group " + groupid + " instances: ");
		for(String id: instances.keySet()){
			System.out.print(id + ", ");
			//idToLog.append(id + ", ");
		}
		System.out.println();
	}
	
	public void addJob(TaskMessage msg) throws AmazonServiceException, JsonGenerationException, JsonMappingException, AmazonClientException, IOException{
		String worker = getWorker();

		if( worker != null && msg != null){
			msg.setGroupId(groupid);
			msg.setConvertValue(convertsize);

			MessageHandler mh = instances.get(worker);
			mh.sendMessage(msg);
		}	
	}
	
	
	public Set<String> getInstanceIds(){
		return Collections.unmodifiableSet(instances.keySet());
	}
	
	/*
	 * jelenleg java random, 
	 * TODO we choose an instance by group and each instance statistic and propability 
	 */
	private String getWorker(){
		List<String> list = new ArrayList<String>();
		for(String str : instances.keySet() ){
			list.add(str);
		}
		Random random = new Random();
		int id = random.nextInt(list.size());
		return list.get(id);
	}
}
