package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.kadar.image.aws.handler.EC2Handler;
import com.kadar.image.message.handler.MessageHandler;
import com.kadar.image.message.handler.StatisticMessage;
import com.kadar.image.message.handler.StatisticMessageType;
import com.kadar.image.message.handler.TaskMessage;
import com.kadar.image.message.handler.TaskMessageType;

public class Main {

	private static String InstanceId;
	private static String InstanceType;
	private static final int NUMBEROFTHREAD = 4;
	private static boolean running = true;
	private static EC2Handler ec2;
	private static MessageHandler mh;
	private static MessageHandler smh;

	public static void main(String[] args) 
			throws AmazonServiceException, AmazonClientException, IOException, InterruptedException {
		ec2 = new EC2Handler();
		 
		InstanceId = getInstanceId();	
		if(InstanceId != null){
			InstanceType = ec2.getInstanceTypeById(InstanceId);
			if(InstanceType == null){
				InstanceType = "t1.micro";
			}
		}else{
			InstanceId = "i-cd5cb0b6";
			InstanceType = "t1.micro";
		}

			mh = new MessageHandler(InstanceId + "-queue");
			smh = new MessageHandler("default-statistic-queue");

	    System.out.println("Config: " 
	    		+ "InstanceId: " + InstanceId
	    		+ ", InstanceType: " + InstanceType 
	    		+ ", InstanceQueue: " + InstanceId + "-queue");   

    	/*
    	int i = 0;
    	while( i < 5 && InstanceType == null){
    		//System.out.println("Waiting for config ...");
    		InstanceType = ims.receiveMessageWithDelete();
    		i++;
    		Thread.sleep(1000);    		
    	}   	
    	
        if( InstanceType == null ){
        	InstanceType = "t1.micro";
        }
		System.out.println("Config: " + InstanceType );    
    	*/

		System.out.println("Waiting for converting ..." ); 	    
	    
		ExecutorService executor = Executors.newFixedThreadPool(NUMBEROFTHREAD);
		while(running){			
			TaskMessage tm = mh.receiveTaskMessageWithDelete();
			if(tm != null){
				
				if(tm.getMessageType().equals(TaskMessageType.InstanceShutdown)){
					running = false;
					
				}else if(tm.getMessageType().equals(TaskMessageType.InstanceStart)){
					
					long startTime = Long.valueOf(tm.getStartTime());
					float endTime = (float) (System.currentTimeMillis() - startTime) / 1000;					
					StatisticMessage sm = new StatisticMessage.Builder()
						.setStatisticType(StatisticMessageType.StartInstance)						
						.setTime(endTime)
						.build();
					smh.sendMessage(sm);
					System.out.println("Instance started: " + endTime);
					
				}else if(tm.getMessageType().equals(TaskMessageType.ImageToConvert)){
								
					Runnable worker = new ConvertConsumer(InstanceId, InstanceType, tm);
					executor.execute(worker);
				}
			}
		}
		
		// This will make the executor accept no new threads
		// and finish all existing threads in the queue		
		executor.shutdown();
		// Wait until all threads are finish
		while (!executor.isTerminated()) {
			System.out.println("Waiting for all threads ...");
			Thread.sleep(1000);
		}
		System.out.println("Shutting down ...");
		instanceShutdown();	
		System.out.println("Finished all threads");    			
    	
	}
	
	private static String getInstanceId() throws IOException{
		Process process = Runtime.getRuntime().exec("wget -q -O - http://169.254.169.254/latest/meta-data/instance-id");
    	BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));  
    	String response = "";  
    	String temp = "";  
    	while ( (temp = stdInput.readLine()) != null )  
    	{  
    		response += temp;
    	}
    	System.out.println(response);
    	return response;
	}
	
	
	
	private static void instanceShutdown(){
		
		try {			
			ec2.TerminateInstance(InstanceId);
			
			mh.deleteQueue(InstanceId + "-queue");
						
		} catch (AmazonServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AmazonClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
