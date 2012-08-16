package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.DelayQueue;

import statcollection.StatCollection;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.kadar.aws.handler.EC2Handler;
import com.kadar.image.config.Config;
import com.kadar.message.handler.TaskMessage;
import com.kadar.message.handler.TaskMessageType;
import com.kadar.message.handler.MessageHandler;

public class ImageManagementSample {
	

	private static List<Group> groups;
	private static StatHolder statAvgHolder, statholder;
	private static TaskIssues taskissues;

	public static void main(String[] args) throws AmazonServiceException, AmazonClientException, IOException {
		//ec2 = new EC2Handler();
		//ec2.TerminateInstancesByImageId(Config.ConverterInstanceId);
		

		groups = new ArrayList<Group>();
		statAvgHolder = new StatHolder();
		statAvgHolder.setDelay(Config.StatisticConsumerDelay);
		statholder = new StatHolder();
		statholder.setDelay(Config.StatisticConsumerDelayForGraph);
		taskissues = new TaskIssues();
		
		// realtime
		//initFromProperties();
		
		// testing
		Group group1 = new Group(getInstanceType("micro"), "1024x768", 1);
		Group group2 = new Group(getInstanceType("micro"), "50%", 1);
		groups.add(group1);
		groups.add(group2);
		
		
		
		Thread s3consumer = new Thread(new S3Consumer(groups,taskissues));
		s3consumer.start();
		
		Thread statproducer = new Thread(new StatProducer(statAvgHolder, statholder, taskissues));
		statproducer.start();
		
		
		StatCollection statcollection = new StatCollection(statholder);
		Thread statcollectionthread = new Thread(statcollection);
		statcollectionthread.start();
		
		
//		Thread graphconsumer = new Thread(new GraphConsumer(statcollection));
//		graphconsumer.start();
		
		/*		
		while(true){
			
			for(float f: statcollection.getCollection()){
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
				
				System.out.println(groupname);
				System.out.println(convertsize);
				System.out.println(instancetype);
				System.out.println(instancepiece);

				
				Group group = new Group(getInstanceType(instancetype), convertsize, Integer.parseInt(instancepiece));
				groups.add(group);
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
