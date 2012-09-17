package statcollection;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;


public class DelayedFloat implements Delayed {
	private long queueInsertTime;
	private long endOfDelay;
	private Float Value;


	public DelayedFloat(){}
	
	public DelayedFloat(float val){
		Value = val;
	}
	
	@Override
	public int compareTo(Delayed arg0) {
		int ret = 0;
		DelayedFloat tm = (DelayedFloat) arg0;
		if ( this.endOfDelay < tm.endOfDelay ) ret = -1;
		else if ( this.endOfDelay > tm.endOfDelay ) ret = 1;
		else if ( this.getQueueInsertTime() == tm.getQueueInsertTime() ) ret = 0;
		return ret;
	}

	@Override
	public long getDelay(TimeUnit arg0) {
		long tmp = arg0.convert((getQueueInsertTime()-System.currentTimeMillis())+endOfDelay, TimeUnit.MILLISECONDS);
		return tmp;
	}

	public long getQueueInsertTime() {
		return queueInsertTime;
	}

	public void setQueueInsertTime(long queueInsertTime) {
		this.queueInsertTime = queueInsertTime;
	}

	public long getEndOfDelay() {
		return endOfDelay;
	}

	public void setEndOfDelay(long endOfDelay) {
		this.endOfDelay = endOfDelay;
	}

	public Float getValue() {
		return Value;
	}

	public void setValue(Float value) {
		Value = value;
	}

	
}
