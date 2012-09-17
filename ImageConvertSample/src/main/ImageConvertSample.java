package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.kadar.aws.handler.EC2Handler;
import com.kadar.message.handler.MessageHandler;
import com.kadar.message.handler.TaskMessage;
import com.kadar.message.handler.TaskMessageType;

public class ImageConvertSample {

	private static String InstanceId;
	private static final int NUMBEROFTHREAD = 4;
	private static final int MaxNumberOfReceivedMessages = 10;
	private static boolean running = true;
	private static EC2Handler ec2;
	private static MessageHandler mh;
	private static MessageHandler smh;

	//logger
	static Logger logger = Logger.getLogger("convertsamplelogger");
	static FileHandler fh;
	
	public static void main(String[] args) 
			//throws AmazonServiceException, AmazonClientException, IOException, InterruptedException 
			{

	    try {
	      // This block configure the logger with handler and formatter
	      fh = new FileHandler("/home/ubuntu/convertsample.log", true);
	      logger.addHandler(fh);
	      logger.setLevel(Level.ALL);
	      SimpleFormatter formatter = new SimpleFormatter();
	      fh.setFormatter(formatter);

	      // the following statement is used to log any messages   
	      //logger.log(Level.WARNING,"My first log");

	    } catch (SecurityException e) {
	      e.printStackTrace();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
		
		
		 
		try {
			InstanceId = getInstanceId();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		}	
		if(InstanceId == null || InstanceId.equals("") || InstanceId.equals(" ") ){
			InstanceId = "localhost";			
		}
	    System.out.println( InstanceId );

		try {
			ec2 = new EC2Handler();
			mh = new MessageHandler(InstanceId + "-queue");
			smh = new MessageHandler("default-statistic-queue");
		} catch (AmazonServiceException e) {
			logger.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		} catch (AmazonClientException e) {
			logger.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		}


	    System.out.println("Config: " 
	    		+ "InstanceId: " + InstanceId 
	    		+ ", InstanceQueue: " + InstanceId + "-queue");   
		System.out.println("Waiting for converting ..." ); 	    
		
	    
		ExecutorService executor = Executors.newFixedThreadPool(NUMBEROFTHREAD);
		while(running){
			try {
				for(TaskMessage tm : mh.receiveTaskMessagesWithDelete(MaxNumberOfReceivedMessages) ){
					if(tm != null){
						
						if(tm.getMessageType().equals(TaskMessageType.InstanceShutdown)){
							running = false;
							
						}else if(tm.getMessageType().equals(TaskMessageType.InstanceStart)){
							
							tm.setEndJobTime(System.currentTimeMillis());
							smh.sendMessage(tm);
							//System.out.println("Instance started: " + endTime);
							
						}else if(tm.getMessageType().equals(TaskMessageType.ImageToConvert)
								//|| tm.getMessageType().equals(TaskMessageType.ConvertMediumImage)
								//|| tm.getMessageType().equals(TaskMessageType.ConvertLargeImage)
								){
							
							Runnable worker = new ConvertConsumer(InstanceId, tm);
							executor.execute(worker);
							
						}
					}
				}
			} catch (AmazonServiceException e) {
				logger.log(Level.SEVERE, e.getMessage());
				e.printStackTrace();
			} catch (JsonParseException e) {
				logger.log(Level.SEVERE, e.getMessage());
				e.printStackTrace();
			} catch (JsonMappingException e) {
				logger.log(Level.SEVERE, e.getMessage());
				e.printStackTrace();
			} catch (JsonGenerationException e) {
				logger.log(Level.SEVERE, e.getMessage());
				e.printStackTrace();
			} catch (AmazonClientException e) {
				logger.log(Level.SEVERE, e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				logger.log(Level.SEVERE, e.getMessage());
				e.printStackTrace();
			}
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				logger.log(Level.SEVERE, e.getMessage());
				e.printStackTrace();
			}
		}
		
		// This will make the executor accept no new threads
		// and finish all existing threads in the queue		
		executor.shutdown();
		// Wait until all threads are finish
		while (!executor.isTerminated()) {
			System.out.println("Waiting for all threads ...");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				logger.log(Level.SEVERE, e.getMessage());
				e.printStackTrace();
			}
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