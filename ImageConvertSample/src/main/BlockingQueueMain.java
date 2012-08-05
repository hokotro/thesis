package main;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.kadar.image.blockingqueue.implementation.Consumer;
import com.kadar.image.blockingqueue.implementation.Producer;
import com.kadar.image.convert.service.ImageConvertService;
import com.kadar.image.convert.service.ImageConvertServiceRemote;
import com.kadar.image.message.handler.MessageHandler;
import com.kadar.image.message.handler.TaskMessage;
import com.kadar.image.message.handler.TaskMessageType;
import com.kadar.scalable.application.Scalable;
import com.kadar.scalable.application.ScalableApplication;


public class BlockingQueueMain {
	private static int queueSize;

	public static void main(String[] args) {		
		String tmpQueueSize = System.getProperty("queuesize","4");
		queueSize = Integer.parseInt(tmpQueueSize);
		System.out.println("Size of BlockingQueue: " + queueSize);
		
		BlockingQueue<TaskMessage> bqueue = new ArrayBlockingQueue<TaskMessage>(queueSize);

        try {
        	MessageHandler ims = new MessageHandler("default-convert-queue");
        	//ims.createQueue("alma");
        	//ims.deleteQueue("alma");
        	//for(String q: ims.listQueue()){
        	//	System.out.println(q);
        	//}
        	
        	//ims.sendMessage("bambusz-005.jpg");
        	ImageConvertServiceRemote convertservice = new ImageConvertService();
        	Scalable scaleservice = new ScalableApplication(ims, convertservice); 
        	
        	Thread producer = new Thread(new Producer(bqueue,ims));
        	Thread consumer = new Thread(new Consumer(bqueue,scaleservice));
        	producer.start();
        	consumer.start();
        	
        	
        	
        } catch (IOException ioe) {
	        System.out.println("IOException was expected.");
	        ioe.printStackTrace();
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it " +
                    "to Amazon SQS, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered " +
                    "a serious internal problem while trying to communicate with SQS, such as not " +
                    "being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
	}
}
