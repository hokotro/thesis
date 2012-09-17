package main;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.kadar.aws.handler.EC2Handler;
import com.kadar.image.config.Config;
import com.kadar.message.handler.MessageHandler;
import com.kadar.message.handler.TaskMessage;
import com.kadar.message.handler.TaskMessageType;
import com.kadar.statistic.StatHolder;

public class Group {

	private static final AtomicInteger counter = new AtomicInteger(0);
	
	private Map<String, MessageHandler> instances = new ConcurrentHashMap<String, MessageHandler>();

	private final Integer groupid;
	private final String name;
	private final String instancetype;
	private final String convertsize;

	/**
	 * handling instances in aws ec2
	 */
	private final EC2Handler ec2;


	private Map<String, StatHolder<TaskMessage>> instancestatistic = 
			new ConcurrentHashMap<String, StatHolder<TaskMessage>>();
	
	public Group(String name, String instancetype, String convertsize, int pieceofinstance) 
			throws AmazonServiceException, AmazonClientException, IOException{
			
		//Random random = new Random();			
		//this.groupid = new BigInteger(130, random).toString(12);
		this.groupid = counter.incrementAndGet();
		
		this.name = name;		
		this.instancetype = instancetype;
		this.convertsize = convertsize;
		
		ec2 = new EC2Handler();				
		
		// just for testing
		List<String> instanceIDs = ec2.listOfRunningInstances(Config.ConverterInstanceImageId, instancetype);
		if( instanceIDs.size() < pieceofinstance )
			instanceIDs = ec2.runInstance(Config.ConverterInstanceImageId, instancetype, pieceofinstance - instanceIDs.size() );
		
		for( String id : instanceIDs ){
			MessageHandler mh = new MessageHandler(id + "-queue");
			mh.sendMessage(
					new TaskMessage.Builder()
					.setMessageType(TaskMessageType.InstanceStart)
					.setstartJobTime(System.currentTimeMillis())
					.build() 
					);
			instances.put( id, mh);
			//instancestatistic.put(id,new StatHolder());
		}
		

		while(instances.size() <= 0){
			;;
		}
		

		System.out.print("Group " + groupid + " instances: ");
		for(String id: instances.keySet()){
			System.out.print(id + ", ");			
		}
		System.out.println();
	}
	
	public void addJob(TaskMessage msg) throws AmazonServiceException, JsonGenerationException, JsonMappingException, AmazonClientException, IOException{
		String worker = getWorker();

		if( worker != null && msg != null){
			msg.setGroupId(groupid.toString());
			msg.setConvertValue(convertsize);

			MessageHandler mh = instances.get(worker);
			mh.sendMessage(msg);
		}	
	}	
	
	/*
	 * jelenleg java random, 
	 * TODO we choose an instance by group and each instance statistic and propability 
	 */
	private String getWorker(){
		if( instances.size() == 1 ){
			for(String str : instances.keySet() ){
				return str;
			}
		}

		String instance = "";
		float max = Float.MAX_VALUE;
		float avg;
		for(Map.Entry<String, MessageHandler> entry: instances.entrySet()){
			avg = instancestatistic.get( entry.getKey() ).getRawAvarageFromJobTime();
			if(avg < max){  
				avg = max;
				instance = entry.getKey();
			}
		}
		return instance;
		/*
		int id = 0;
		List<String> list = new ArrayList<String>();
		if( instances.size() > 1 ){
		for(String str : instances.keySet() ){
			list.add(str);
		}
		Random random = new Random();
		id = random.nextInt(list.size());
		}
		return list.get(id);
		*/
	}

	public Set<String> getInstanceIds(){
		return Collections.unmodifiableSet(instances.keySet());
	}
	

	public String getInstancetype() {
		return instancetype;
	}

	public String getName() {
		return name;
	}

	public String getConvertsize() {
		return convertsize;
	}
	
	public Integer getGroupId(){
		return groupid;
	}
	
	/*
	public String getGroupId(){
		return groupid;
	}
	*/
	
	
	public List<String> scaleUpWithNumberOfInstances(int n) throws AmazonServiceException, AmazonClientException, IOException{
				
		List<String> instanceIDs = ec2.runInstance(Config.ConverterInstanceImageId, instancetype, n );
		
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
		return instanceIDs;
	}

	public Map<String, StatHolder<TaskMessage>> getInstancestatistic() {
		return instancestatistic;
	}

	public void setInstancestatistic(
			Map<String, StatHolder<TaskMessage>> instancestatistic) {
		this.instancestatistic = instancestatistic;
	} 
	
	
	
	
}
