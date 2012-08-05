package metric;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.DelayQueue;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.kadar.image.aws.handler.EC2Handler;
import com.kadar.image.message.handler.DelayedStatMessage;
import com.kadar.image.message.handler.MessageHandler;
import com.kadar.image.message.handler.StatisticMessage;
import com.kadar.image.message.handler.StatisticMessageType;
import com.kadar.image.message.handler.TaskMessage;
import com.kadar.image.message.handler.TaskMessageType;

public class StatisticConsumer implements Runnable {

	private static Map<String, MessageHandler> smallInstances;
	private DelayQueue<DelayedStatMessage> smallInstancesStat;
	
	public StatisticConsumer(Map<String,MessageHandler> smallInstances, DelayQueue<DelayedStatMessage> smallInstancesStat) throws AmazonServiceException, AmazonClientException, IOException{
		this.smallInstances = smallInstances;
		this.smallInstancesStat = smallInstancesStat;
	}
	
	
	@Override
	public void run() {
		while(true){
			/*DelayedStatMessage msg = smallInstancesStat.poll();			
			while(msg == null){
				msg = smallInstancesStat.poll();	
				smallInstancesStat.take();
			}	*/
			DelayedStatMessage msg;
			try {
				msg = smallInstancesStat.take();
				System.out.println(msg);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
	
}
