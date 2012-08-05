package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import metric.Config;
import metric.Metric;
import metric.StatisticConsumer;
import metric.StatisticProducer;

import s3.S3Consumer;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.kadar.image.aws.handler.EC2Handler;
import com.kadar.image.message.handler.DelayedStatMessage;
import com.kadar.image.message.handler.MessageHandler;
import com.kadar.image.message.handler.TaskMessage;
import com.kadar.image.message.handler.TaskMessageType;

public class Main {

    private static int NUMBEROFTHREAD = 4;

	private static EC2Handler ec2;
	private static MessageHandler s3mh;

	private static Map<String, MessageHandler> smallInstances = new HashMap<String, MessageHandler>();
	static DelayQueue<DelayedStatMessage> smallInstancesStat;
	private static int numberOfSmallInstance;
	
	public static void main(String[] args) throws AmazonServiceException, AmazonClientException, IOException {
		numberOfSmallInstance = Integer.parseInt(System.getProperty("small", "1"));

		ec2 = new EC2Handler();

		List<String> ids = new ArrayList<String>();
		for( String instanceId : ec2.listOfRunningInstances(Config.ConverterInstanceImageId, Config.smallInstanceType) ){
			ids.add(instanceId);
		}
		
		if(numberOfSmallInstance > ids.size() ){	
			for( String instanceId : ec2.runInstance(Config.ConverterInstanceImageId, Config.smallInstanceType, numberOfSmallInstance - ids.size()) ){
				ids.add(instanceId);
			}
		}

		for( String instanceId : ids ){		
			registerInstance(instanceId);
		}

		while(smallInstances.size() <= 0){
			;;
		}

		ExecutorService executor = Executors.newFixedThreadPool(NUMBEROFTHREAD);

		Runnable worker = new S3Consumer(smallInstances);
		

		smallInstancesStat = new DelayQueue<DelayedStatMessage>();
		Runnable statworker = new StatisticProducer(smallInstances, smallInstancesStat);
		Runnable stat2worker = new StatisticConsumer(smallInstances, smallInstancesStat);
		executor.execute(worker);
		executor.execute(statworker);
		executor.execute(stat2worker);
		
		while(true){
			if( smallInstancesStat.size() > Metric.stagnation){ 
				Object[] smallInstancesStatistic = smallInstancesStat.toArray();
				float[] statistic = new float[smallInstancesStatistic.length];
				int i=0;
				for(Object obj: smallInstancesStatistic){
					System.out.println((DelayedStatMessage)obj);
					statistic[i] = ((DelayedStatMessage)obj).getStatisticMessage().getTime();
					i++;
				}
				System.out.println(calculate(statistic, Metric.SmallImageConvertion));
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	
	private static void registerInstance(String instanceId) throws AmazonServiceException, AmazonClientException, IOException{		
		System.out.println("Register new instance: " + instanceId);
		MessageHandler mh = new MessageHandler(instanceId + "-queue");
		mh.sendMessage(
			 new TaskMessage.Builder()
			.setMessageType(TaskMessageType.InstanceStart)
			.setStartTime(System.currentTimeMillis())
			.build() 
			);
		smallInstances.put(instanceId, mh);
	}
	
	/*
	 * megszámolom mennyi lépte túl a köszöböt, ha ez több mint a 10% akkor true
	 */
	private static boolean calculate(float[] values, float threshold){
		int piecesOfThreshold = 0;
		for(float val: values){
			if(val > threshold ) piecesOfThreshold++; 
		}
				
		float a = values.length * 0.1f;
		return (piecesOfThreshold > a) ? true : false; 
//		float sum = 0f;
//		for(float dsm: values){			
//			sum += dsm;
//		}
//		float avg = sum / values.length;
//		return values.length / avg;
	}
	
}
