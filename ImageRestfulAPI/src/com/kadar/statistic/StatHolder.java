package com.kadar.statistic;

import java.util.Collections;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;


public class StatHolder<V extends StatHolderItem  /* :) */  > {
	
	//public long delay = 120000l;
	public long delay = 10000l;

    private final ConcurrentSkipListSet<V> holder =
   		new ConcurrentSkipListSet<V>( new Comparator<V>() {
   			@Override
            public int compare(V a, V b) {
               	//return (int) (b.getTimestamp() - a.getTimestamp());
               	return a.compareTo(b);
   			}
    	} );

    public boolean add(V msg) {
    	msg.setTimestamp(System.currentTimeMillis());
    	
    	clear();
    	
    	synchronized(holder) { return holder.add(msg); }    	
    }
    
    public Set<V> getValues(){
    //public V[] getValues(){
    	//clear();    	
    	//V[] a = {};
    	//a = holder.toArray(a);
    	//return a;
    	return Collections.unmodifiableSet(holder);
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


    public float getRawAvarageFromJobTime(){
    	int n = holder.size();
    	float rawstat = 0;
    	
    	for(V msg: holder){
    		rawstat += (float) (msg.getEndJobTime() - msg.getStartJobTime()) / 1000;
    	}
    	
    	return (n > 0 ) ? rawstat / n : 0;
    }
    public float getRawAvarageFromTaskTime(){
    	int n = holder.size();
    	float rawstat = 0;
    	
    	for(V msg: holder){
    		rawstat += (float) (msg.getEndTaskTime() - msg.getStartTaskTime()) / 1000;    		
    	}
    	
    	return (n > 0 ) ? rawstat / n : 0;
    }

	public synchronized float[] getRawDataCollectionFromJobTime(){		
		float[] raw = new float[holder.size()];
		int i = 0;
		for(V msg: holder){
			raw[i] = (float) ( (msg.getEndJobTime() - msg.getStartJobTime()) ) / 1000 ;
			i++;
		}
		return raw;
	}

	public synchronized Float[] getRawFloatDataCollectionFromJobTime(){		
		Float[] raw = new Float[holder.size()];
		int i = 0;
		for(V msg: holder){
			raw[i] = new Float ( (msg.getEndJobTime() - msg.getStartJobTime()) ) / 1000;
			i++;
		}
		return raw;
	}
    
	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}
    
    
}