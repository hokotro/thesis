package metric;

import java.io.IOException;
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

public class StatisticProducer implements Runnable {

	private MessageHandler smh;
	private EC2Handler ec2;
	private DelayQueue<DelayedStatMessage> smallInstancesStat;
	private DelayQueue<DelayedStatMessage> mediumInstancesStat;
	private DelayQueue<DelayedStatMessage> largeInstancesStat;
	
	
	public StatisticProducer(DelayQueue<DelayedStatMessage> smallInstancesStat,
			DelayQueue<DelayedStatMessage> mediumInstancesStat,
			DelayQueue<DelayedStatMessage> largeInstancesStat
		) throws AmazonServiceException, AmazonClientException, IOException{
		
		this.smallInstancesStat = smallInstancesStat;
		this.mediumInstancesStat = mediumInstancesStat;
		this.largeInstancesStat = largeInstancesStat;
		smh = new MessageHandler(Config.STATISTICQUEUE);
		ec2 = new EC2Handler();
	}
	
	@Override
	public void run() {

		while(true){
			try {
				for(StatisticMessage msg: smh.receiveStatisticMessagesWithDelete(Config.numberOfMaxReceivedMessage)){
					if(msg != null){
						
						if(msg.getStatisticType().equals(StatisticMessageType.StartInstance)){
							System.out.println("Instance startup time: " + msg.getTime());

						}else if(msg.getStatisticType().equals(StatisticMessageType.SmallImageConvertion)){
							
							DelayedStatMessage dtm = new DelayedStatMessage();
							dtm.setStatisticMessage(msg);
							dtm.setEndOfDelay(Config.StatisticConsumerDelay);
							dtm.setQueueInsertTime(System.currentTimeMillis());
							this.smallInstancesStat.add(dtm);
							System.out.println("in: " + msg);
						
						}else if(msg.getStatisticType().equals(StatisticMessageType.MediumImageConvertion)){
						
							DelayedStatMessage dtm = new DelayedStatMessage();
							dtm.setStatisticMessage(msg);
							dtm.setEndOfDelay(Config.StatisticConsumerDelay);
							dtm.setQueueInsertTime(System.currentTimeMillis());
							this.mediumInstancesStat.add(dtm);
							System.out.println("in: " + msg);
					
						}else if(msg.getStatisticType().equals(StatisticMessageType.LargeImageConvertion)){
					
							DelayedStatMessage dtm = new DelayedStatMessage();
							dtm.setStatisticMessage(msg);
							dtm.setEndOfDelay(Config.StatisticConsumerDelay);
							dtm.setQueueInsertTime(System.currentTimeMillis());
							this.largeInstancesStat.add(dtm);
							System.out.println("in: " + msg);
				
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
		}

	}

}
