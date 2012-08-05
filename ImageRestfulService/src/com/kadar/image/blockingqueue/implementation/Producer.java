package com.kadar.image.blockingqueue.implementation;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.kadar.image.message.handler.MessageHandler;
import com.kadar.image.message.handler.TaskMessage;

public class Producer implements Runnable {

    private final BlockingQueue queue;
    private MessageHandler messageservice;

    public Producer(BlockingQueue queue, MessageHandler mservice) {
        this.queue = queue;
        this.messageservice = mservice;
    }

    @Override
    public void run() {
        while(true){
       		try{
       	       	TaskMessage im = messageservice.receiveTaskMessage();
       			if(null != im ){
       				queue.put(im);
       				messageservice.deleteMessage(im.getReceiptHandle());
       			}
			} catch(InterruptedException ex){
	            Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);				
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
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
}