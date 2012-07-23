package com.kadar.image.awsImageHandler;

import java.util.concurrent.BlockingQueue;
import com.kadar.image.convert.service.ImageConvertServiceRemote;
import com.kadar.scalable.application.Scalable;

public class Consumer implements Runnable{
	
	
    protected BlockingQueue queue = null;
    private Scalable service;
    //private AwsSQSMessageHandler messageservice;

    //public Consumer() {}
    public Consumer(BlockingQueue queue, Scalable scservice) {
        this.queue = queue;
        this.service = scservice;
    }

    public void run() {
    	if(queue != null){
	    	while(true){
		        try {
		        	if( !queue.isEmpty() ){
		        		TaskMessage im = (TaskMessage) queue.take();
		        		service.doBusinessLogic(im);
		        	}
		        } catch (InterruptedException e) {
		            e.printStackTrace();
		        }
	    	}
    	}
    }
}