package com.kadar.statistic;

public interface StatHolderItem extends Comparable<StatHolderItem>{

	public long getTimestamp();
	
	public void setTimestamp(long timestamp);
		
	public long getStartTaskTime();
	
	public long getEndTaskTime();
	
	public long getStartJobTime();
	
	public long getEndJobTime();
	
}
