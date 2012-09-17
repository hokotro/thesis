package main;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.kadar.message.handler.TaskMessage;
import com.kadar.statistic.GraphProvider;
import com.kadar.statistic.StatHolder;
import com.kadar.util.ConcurrentListHolderMap;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;

public class ImageManagementSample {
	
	/**
	 * A holder for groups 
	 */
	private static Map<Integer, Group> groups = new ConcurrentHashMap<Integer, Group>();
	/**
	 * we delegate a job: each group have tasks from this job, and we are waiting for the end of this job, also the end of all tasks
	 * jobs and tasks are represented in this class, in a Map<String, List<String>>
	 * O(1) lists contain the task element or not
	 */
	private static ConcurrentListHolderMap<String, Integer> assignedtasks = 
			new ConcurrentListHolderMap<String, Integer>();

	/**
	 * data structure for holding statistic of each group
	 * 
	 */
	private static final Map<Integer, StatHolder<TaskMessage>> groupstatistic = 
			new ConcurrentHashMap<Integer, StatHolder<TaskMessage>>();

	/**
	 * data structure for holding statistic of each group
	 * 
	 */
	private static final Map<String, StatHolder<TaskMessage>> instancestatistic = 
			new ConcurrentHashMap<String, StatHolder<TaskMessage>>();
	/**
	 * pedig olyan szép lenne, ha így kellene, de elég simán egy instance-hoz tárolni
	 * eléggé macerás, összetett és pazarló benne turkálni
	 */
	//private static final Map<Integer, Map<String,StatHolder<TaskMessage>> > instancestatistic = 
			//new ConcurrentHashMap<Integer, Map<String,StatHolder<TaskMessage>> >();
	
	
	/**
	 *
	 * 
	 */
	private static StatHolder<TaskMessage> statholder = new StatHolder<TaskMessage>();
	


	public static void main(String[] args) throws AmazonServiceException, AmazonClientException, IOException {
				
		
		// realtime
		//initFromProperties();
		
		// testing
		Group group1 = new Group("group1", getInstanceType("micro"), "1024x768", 1);		
		groups.put(group1.getGroupId(), group1);

		//Group group2 = new Group("group2", getInstanceType("micro"), "50%", 1);
		//groups.put(group2.getGroupId(), group2);
		//groupstatistic.put(group2.getGroupId(), new StatHolder<TaskMessage>() );
		
		
		/*
		 * initialize for statistic : global stat, group stat, instance stat
		 */
		statholder.setDelay(120000);
		
		//groupstatistic, instancestatisic feltöltése
		for(Map.Entry<Integer, Group> entry: groups.entrySet()){
			Group group = entry.getValue();
			StatHolder<TaskMessage> sh = new StatHolder<TaskMessage>();
			sh.setDelay(120000);
			group.setInstancestatistic(instancestatistic);
			groupstatistic.put(group.getGroupId(), sh );
			for(String instance: group.getInstanceIds()){
				instancestatistic.put(instance, new StatHolder<TaskMessage>());
			}
		}
		
		
		//receiving tasks from Amazon s3-queue, delegate tasks, 
		//keeps track of delegated tasks and jobs with assignedtasks data structure
		Thread s3consumer = new Thread(new S3Consumer(groups, assignedtasks));
		s3consumer.start();

		
		
		Thread statproducer = new Thread(new StatProducer(statholder, groupstatistic, instancestatistic, assignedtasks));
		statproducer.start();
		
		
		Thread graphprovider = new Thread(new GraphProvider(statholder));
		graphprovider.start();
		
		while(true){
			System.out.println("Raw tasktime avarage " + statholder.getRawAvarageFromTaskTime());
			for(TaskMessage msg: statholder.getValues()){
				//System.out.println(msg);
			}
			
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/*
		groupstatistic.put(group1.getGroupid(), new StatHolder() );
		groupstatistic.put(group2.getGroupid(), new StatHolder() );
		
		
		
		
		Thread statproducer = new Thread(new StatProducer(statAvgHolder, statholder, groupstatistic, groups, taskissues ));
		statproducer.start();
		
		
		StatCollection statcollection = new StatCollection(statholder);
		Thread statcollectionthread = new Thread(statcollection);
		statcollectionthread.start();
		*/
		
//		Thread graphconsumer = new Thread(new GraphConsumer(statcollection));
//		graphconsumer.start();
		
		/*
		while(true){
		
			
			for(float f: groupstatistic.get(group1.getGroupid()).getCollection()){
				System.out.print(f + ", ");
			}
			System.out.println();
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		*/
		
	}

	/**
	 * @throws AmazonServiceException
	 * @throws NumberFormatException
	 * @throws AmazonClientException
	 * @throws IOException
	 * 
	 * example:  java -jar -Dgroup:50%=micro:1 workspace/ImageManagementSample.jar
	 */
	private static void initFromProperties() throws AmazonServiceException, NumberFormatException, AmazonClientException, IOException{
		Properties Properties = System.getProperties();		
		for(Object o : Properties.stringPropertyNames()){
			String key = (String) o;
			if( key.contains("group") ){
				String value = Properties.get(key).toString();

				int mid = key.lastIndexOf(":");
				String groupname = key.substring(0,mid);
				String convertsize = key.substring(mid+1,key.length());
				
				mid = value.lastIndexOf(":");
				String instancetype = value.substring(0,mid);
				String instancepiece = value.substring(mid+1,value.length());
				
				System.out.println("Groupname: " + groupname);
				System.out.println("ConvertSize: " + convertsize);
				System.out.println("Instancetype: " + instancetype);
				System.out.println("Piece of instances: " + instancepiece);

				/*
				Group group = new Group(getInstanceType(instancetype), convertsize, Integer.parseInt(instancepiece));
				groups.put(group.getGroupid(),group);				
				groupstatistic.put(group.getGroupId(), new StatHolder<TaskMessage>() );
				*/
			}
		}
	}
	
	private static String getInstanceType(String str){
		if(str.equals("micro")) return "t1.micro";
		if(str.equals("small")) return "m1.small";
		if(str.equals("medium")) return "m1.medium";
		if(str.equals("large")) return "m1.large";
		if(str.equals("xlarge")) return "m1.xlarge";
		return null;
	}
}
