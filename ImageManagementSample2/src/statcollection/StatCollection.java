package statcollection;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.kadar.message.handler.MessageHandler;
import com.kadar.message.handler.TaskMessage;
import com.kadar.message.handler.TaskMessageType;
import com.kadar.statistic.StatHolder;



public class StatCollection implements Runnable{

	private final MessageHandler mh1, mh2;
	
	private final StatHolder statholder;
	ConcurrentLinkedQueue<Float> rawavarage = new ConcurrentLinkedQueue<Float>();
	
	public StatCollection(StatHolder statholder) throws AmazonServiceException, AmazonClientException, IOException{
		mh1 = new MessageHandler("default-sendgraph-queue");
		mh2 = new MessageHandler("default-receivegraph-queue");		
		this.statholder = statholder;
	}

	@Override
	public void run() {
		
		while(true){
		
			rawavarage.add(statholder.getRawAvarageFromTaskTime());
			
			try{
				for( TaskMessage msg : mh1.receiveTaskMessagesWithDelete(10) ){
					if(msg != null && msg.getMessageType().equals(TaskMessageType.sendGraphValues)){
				
						ObjectMapper mapper = new ObjectMapper();
						Writer strWriter = new StringWriter();
						mapper.writeValue(strWriter, getCollection());	
						
						String collection =  strWriter.toString();
						//System.out.println(collection);
						
	
						TaskMessage message = new TaskMessage.Builder()
						.setMessageType(TaskMessageType.receiveGraphValues)
						.setOriginalValue(collection)
						.build();
						mh2.sendMessage(message);
						
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
			
			
			
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
				
	}
	
	public Collection<Float> getCollection(){
		//for(Float f: statholder.getCollection())
		//	rawavarage.add( f );
		return Collections.unmodifiableCollection(rawavarage);
	}
	
}
