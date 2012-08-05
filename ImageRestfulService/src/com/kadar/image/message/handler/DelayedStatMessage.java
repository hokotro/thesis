package com.kadar.image.message.handler;


import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import com.kadar.image.message.handler.TaskMessage;

public class DelayedStatMessage implements Delayed {
	private long queueInsertTime;
	private long endOfDelay;
	private StatisticMessage StatisticMessage;
	
	@Override
	public int compareTo(Delayed o) {
		int ret = 0;
		DelayedStatMessage tm = (DelayedStatMessage) o;
		if ( this.endOfDelay < tm.endOfDelay ) ret = -1;
		else if ( this.endOfDelay > tm.endOfDelay ) ret = 1;
		else if ( this.getQueueInsertTime() == tm.getQueueInsertTime() ) ret = 0;
		return ret;
	}

	@Override
	public long getDelay(TimeUnit unit) {
		long tmp = unit.convert((getQueueInsertTime()-System.currentTimeMillis())+endOfDelay, TimeUnit.MILLISECONDS);
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

	public StatisticMessage getStatisticMessage() {
		return StatisticMessage;
	}

	public void setStatisticMessage(StatisticMessage statisticMessage) {
		StatisticMessage = statisticMessage;
	}

	@Override
	public String toString() {
		return "DelayedStatMessage [StatisticMessage=" + StatisticMessage + "]";
	}

	
}
