package main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentSkipListSet;

import statcollection.DelayedFloat;

import com.kadar.image.config.Config;
import com.kadar.message.handler.TaskMessage;

public class StatHolder {
	
	public long delay = 120000l;
		
    private final ConcurrentSkipListSet<TaskMessage> holder =
   		new ConcurrentSkipListSet<TaskMessage>( new Comparator<TaskMessage>() {
   			@Override
            public int compare(TaskMessage a, TaskMessage b) {
              	//return Integer.parseInt("" + (b.getTimestamp() - a.getTimestamp()));
               	return (int) (b.getTimestamp() - a.getTimestamp());
   			}
    	} );

    public boolean add(TaskMessage x) {
    	x.setTimestamp(System.currentTimeMillis());
    	
    	clear();
    	
    	return holder.add(x);    	
    }
    
    public TaskMessage[] getValues(){

    	//clear();
    	
    	TaskMessage[] a = {};
    	a = holder.toArray(a);
    	return a;
    }
    
    
    private void clear(){
    	try{
    		//float b =(float) (holder.first().getTimestamp() - System.currentTimeMillis() + Config.StatisticConsumerDelay) / 1000;
    		//System.out.println(b);
	    	while (holder.first().getTimestamp() - System.currentTimeMillis() + delay < 0 ) {    		
	    		//System.out.println("polling");
	    		holder.pollFirst();
	    	}
	    	
	    	//System.out.println("Next polling time: " + (float) (holder.first().getTimestamp() - System.currentTimeMillis() + Config.StatisticConsumerDelay) / 1000);
    	} catch (NoSuchElementException ex){
    		
    	}
    }

    
    public float getRawAvarage(){
    	    	
    	TaskMessage[] array = {};
    	array = holder.toArray(array);
    	
    	float rawstat = 0;
    	
    	for(TaskMessage msg: array){
    		//rawstat += (float) (msg.getEndConvertTime() - msg.getStartConvertTime()) / 1000;
    		rawstat += (float) (msg.getEndConvertTime() - msg.getStartConvertTime()) / 1000;
    		
    	}
    	
    	return (rawstat / array.length > 0 ) ? rawstat / array.length : 0;
    }

	public Collection<Float> getCollection(){		
		Collection<Float> raw = new ArrayList<Float>();
		for(TaskMessage msg: holder){
			raw.add( new Float( (msg.getEndConvertTime() - msg.getStartConvertTime()) / 1000 ) );
		}
		return Collections.unmodifiableCollection(raw);
	}
    
	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}
    
    
    
}