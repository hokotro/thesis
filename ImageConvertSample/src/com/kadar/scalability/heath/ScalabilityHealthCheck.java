package com.kadar.scalability.heath;

import java.io.IOException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.kadar.image.awsImageHandler.QueueMessageHandler;
import com.kadar.image.awsImageHandler.StatisticMessage;

public class ScalabilityHealthCheck implements ScalabilityHealth, Runnable{

	private QueueMessageHandler statistic;
	
	public ScalabilityHealthCheck() throws AmazonServiceException, AmazonClientException, IOException { 
		statistic = new QueueMessageHandler("default-statistic-queue"); 
	}

	@Override
	public void run() {
		while(true){
			doCheck();
		}		
	}
	
	@Override
	public void doCheck() {
		StatisticMessage sm = statistic.receiveStatisticMessage();
		
	}

}
