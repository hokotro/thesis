package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import metric.Config;
import metric.Metric;
import metric.MetricCallable;
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

public class ImageManagementSample {

    private static int NUMBEROFTHREAD = 4;

	private static EC2Handler ec2;

	/*
	 * accessories for small instances
	 * smallInstances: Map: key: Id value: MessageHandler to a queue of the instance
	 * smallInstancesStat: statistic for instances of small type
	 * numberOfSmallInstance: number of small instances at the beginning
	 */
	private static int numberOfSmallInstance;
	private static Map<String, MessageHandler> smallInstances = new HashMap<String, MessageHandler>();
	static DelayQueue<DelayedStatMessage> smallInstancesStat = new DelayQueue<DelayedStatMessage>();
	
	private static int numberOfMediumInstance;
	private static Map<String, MessageHandler> mediumInstances = new HashMap<String, MessageHandler>();
	static DelayQueue<DelayedStatMessage> mediumInstancesStat = new DelayQueue<DelayedStatMessage>();
	
	private static int numberOfLargeInstance;
	private static Map<String, MessageHandler> largeInstances = new HashMap<String, MessageHandler>();
	static DelayQueue<DelayedStatMessage> largeInstancesStat = new DelayQueue<DelayedStatMessage>();
	
	
	public static void main(String[] args) throws AmazonServiceException, AmazonClientException, IOException, InterruptedException, ExecutionException {
		numberOfSmallInstance = Integer.parseInt(System.getProperty("small", "1"));
		numberOfMediumInstance = Integer.parseInt(System.getProperty("medium", "1"));
		numberOfLargeInstance = Integer.parseInt(System.getProperty("large", "1"));


		ec2 = new EC2Handler();


		//registerInstance("i-c9288db2", smallInstances);
		
		startUpSmallInstances();
		//startUpMediumInstances();
		//startUpLargeInstances();

		
		ExecutorService executor = Executors.newFixedThreadPool(NUMBEROFTHREAD);

		Runnable s3worker = new S3Consumer(smallInstances, mediumInstances, largeInstances);
		executor.execute(s3worker);
		
		// a run() metódusba kell a while(true)
		Runnable statproducer = new StatisticProducer(smallInstancesStat, mediumInstancesStat, largeInstancesStat);
		executor.execute(statproducer);
		
		// a run() metódusba kell a while(true)
		Runnable statconsumer = new StatisticConsumer(smallInstancesStat, mediumInstancesStat, largeInstancesStat);
		executor.execute(statconsumer);

		/*
		 * accessories of startup time of an instance 
		 */

		long startTime = 0;
		long endOfDelay = 0;
		boolean scaling = false;

		int sleep = 2000;		
		while(true){
			
			// a run() metódusba NEM kell a while(true)
			/*
			Runnable statproducer = new StatisticProducer(smallInstancesStat, mediumInstancesStat, largeInstancesStat);
			executor.execute(statproducer);
			
			Runnable statconsumer = new StatisticConsumer(smallInstancesStat, mediumInstancesStat, largeInstancesStat);
			executor.execute(statconsumer);
			*/
			
			Callable<Boolean> sworker = new MetricCallable(smallInstancesStat, Metric.SmallImageConvertion);
			Future<Boolean> ssubmit = executor.submit(sworker);
			float delay = getDelay(startTime,endOfDelay);
			//System.out.println(delay);
			if( ssubmit.get() && ! scaling ) {
				System.out.println("ScaleUp: ");				
				startTime = System.currentTimeMillis();
				endOfDelay = Metric.InstanceStartUpThresholdTime;
				scaling = true;
				//for(String id: ec2.runInstance(Config.ConverterInstanceImageId, Config.smallInstanceType, 1) ){
					//registerInstance(id, smallInstances);
				//}
			}
			if(scaling){
				System.out.println(getDelay(startTime,endOfDelay));
				if(getDelay(startTime,endOfDelay) < 0){
					startTime = 0;
					endOfDelay = 0;
					scaling = false;	
				}
			}
			//System.out.println();

			/*
			Callable<Boolean> mworker = new MetricCallable(mediumInstancesStat, Metric.MediumImageConvertion);
			Future<Boolean> msubmit = executor.submit(mworker);
			System.out.println(msubmit.get());

			Callable<Boolean> lworker = new MetricCallable(largeInstancesStat, Metric.LargeImageConvertion);
			Future<Boolean> lsubmit = executor.submit(lworker);
			System.out.println(lsubmit.get());
			*/
			
			
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
	public static float getDelay( long startTime, long endOfDelay ) {
		long tmp = startTime - System.currentTimeMillis() + endOfDelay; 
				//TimeUnit.MILLISECONDS);
		return (float) tmp / 1000;
	}

	
	/*
	 * register by instance id in the management machine
	 * add to the actual instance map, and send a startmessage to the queue of the instance
	 */
	private static void registerInstance(String instanceId, Map<String, MessageHandler> Instances) throws AmazonServiceException, AmazonClientException, IOException{		
		System.out.println("Register new instance: " + instanceId);
		MessageHandler mh = new MessageHandler(instanceId + "-queue");
		mh.sendMessage(
			 new TaskMessage.Builder()
			.setMessageType(TaskMessageType.InstanceStart)
			.setStartTime(System.currentTimeMillis())
			.build() 
			);
		Instances.put(instanceId, mh);
	}

	/*
	 * for small instances: 
	 * get all running instance by imageid and instance type
	 * if not enough, start more i
	 * register 
	 */
	private static void startUpSmallInstances() throws AmazonServiceException, AmazonClientException, IOException{
		List<String> instanceids = new ArrayList<String>();
		int cnt = 0;
		for( String instanceId : ec2.listOfRunningInstances(Config.ConverterInstanceImageId, 
				Config.smallInstanceType) ){
			if(cnt == numberOfSmallInstance) break;
			cnt++;
			instanceids.add(instanceId);
		}
		
		if(numberOfSmallInstance > instanceids.size() ){	
			for( String instanceId : ec2.runInstance(Config.ConverterInstanceImageId, 
					Config.smallInstanceType, numberOfSmallInstance - instanceids.size()) ){
				instanceids.add(instanceId);
			}
		}

		for( String instanceId : instanceids ){		
			registerInstance(instanceId, smallInstances);
		}

		while(smallInstances.size() <= 0){
			;;
		}
		
		System.out.print("Small instances: ");
		for(String id: smallInstances.keySet()){
			System.out.print(id);
		}
		System.out.println();
	}
	private static void startUpMediumInstances() throws AmazonServiceException, AmazonClientException, IOException{
		List<String> instanceids = new ArrayList<String>();
		int cnt = 0;
		for( String instanceId : ec2.listOfRunningInstances(Config.ConverterInstanceImageId, 
				Config.mediumInstanceType) ){
			if(cnt == numberOfMediumInstance) break;
			
			// ha netalántán egyeznének azonosítók, akkor ő már ne legyen más típusú
			// ez általában akkor fordulhat elő, ha azonos az instancetype
			if( ! smallInstances.keySet().contains(instanceId) ){
				instanceids.add(instanceId);
				cnt++;
			}
		}
			
		
		if(numberOfMediumInstance > instanceids.size() ){	
			for( String instanceId : ec2.runInstance(Config.ConverterInstanceImageId, 
					Config.mediumInstanceType, numberOfMediumInstance - instanceids.size()) ){
				instanceids.add(instanceId);
			}
		}

		for( String instanceId : instanceids ){		
			registerInstance(instanceId, mediumInstances);
		}

		while(mediumInstances.size() <= 0){
			;;
		}

		System.out.print("Medium instances: ");
		for(String id: mediumInstances.keySet()){
			System.out.print(id);
		}
		System.out.println();
	}
	private static void startUpLargeInstances() throws AmazonServiceException, AmazonClientException, IOException{
		List<String> instanceids = new ArrayList<String>();
		int cnt = 0;
		for( String instanceId : ec2.listOfRunningInstances(Config.ConverterInstanceImageId, 
				Config.largeInstanceType) ){
			if(cnt == numberOfMediumInstance) break;
			
			// ha netalántán egyeznének azonosítók, akkor ő már ne legyen más típusú
			// ez általában akkor fordulhat elő, ha azonos az instancetype
			if( ! smallInstances.keySet().contains(instanceId) 
					&& ! mediumInstances.keySet().contains(instanceId) ){
				instanceids.add(instanceId);
				cnt++;
			}
		}
		
		if(numberOfLargeInstance > instanceids.size() ){	
			for( String instanceId : ec2.runInstance(Config.ConverterInstanceImageId, 
					Config.largeInstanceType, numberOfLargeInstance - instanceids.size()) ){
				instanceids.add(instanceId);
			}
		}

		for( String instanceId : instanceids ){		
			registerInstance(instanceId, largeInstances);
		}

		while(largeInstances.size() <= 0){
			;;
		}
		
		System.out.print("Large instances: ");
		for(String id: largeInstances.keySet()){
			System.out.print(id);
		}
		System.out.println();
	}
	
}
