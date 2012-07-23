package com.kadar.image.awsImageHandler;

import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Producer implements Runnable {

    private final BlockingQueue queue;
    private QueueMessageHandler messageservice;

    public Producer(BlockingQueue queue, QueueMessageHandler mservice) {
        this.queue = queue;
        this.messageservice = mservice;
    }

    @Override
    public void run() {
        while(true){
        	TaskMessage im = messageservice.receiveTaskMessage();
			if(null != im ){
       			try{
					queue.put(im);
    				messageservice.deleteMessage(im.getReceiptHandle());
				} catch(InterruptedException ex){
	                Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
        }
    }
}