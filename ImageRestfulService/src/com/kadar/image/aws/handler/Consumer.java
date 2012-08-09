package com.kadar.image.aws.handler;

import java.io.IOException;

import com.kadar.image.convert.service.ImageConvertServiceRemote;
import com.kadar.image.message.handler.MessageHandler;
import com.kadar.image.message.handler.StatisticMessage;
import com.kadar.image.message.handler.StatisticMessageType;
import com.kadar.image.message.handler.TaskMessage;
import com.kadar.scalable.application.Scalable;

public class Consumer implements Runnable {

	private MessageHandler mh;
	private String InstanceType;
	private TaskMessage tm;
	private String bucketName = "kg-images";
	private ImageConvertServiceRemote convertservice;
	
	public Consumer(String InstanceType, TaskMessage tm) {
		this.InstanceType = InstanceType;
		this.tm = tm;
	}
	
	@Override
	public void run() {
		try{
			long startTime = System.currentTimeMillis();	
	
	        if(InstanceType.equals("t1.micro")){
	    		convertservice.generateSmall(bucketName, tm.getKeyOfImage());
	        }
	        if(InstanceType.equals("m1.small")){
	    		convertservice.generateSmall(bucketName, tm.getKeyOfImage());
	        }
	        if(InstanceType.equals("m1.medium")){
	    		convertservice.generateMedium(bucketName, tm.getKeyOfImage());
	        }
	        if(InstanceType.equals("m1.large")){
	    		convertservice.generateLarge(bucketName, tm.getKeyOfImage());
	        }
	        	
	        /*
	        convertservice.generateLarge(bucketName, key);
			convertservice.generateMedium(bucketName, key);
			convertservice.generateThumbnail(bucketName, key);
			*/
			float endTime = (float) (System.currentTimeMillis() - startTime) / 1000;
			
			/*
			StatisticMessage statisticmessage = new StatisticMessage.Builder()
				.WhatAbout(StatisticMessageType.AllImageConvertion)
				.Value(String.valueOf(endTime))				
				.build();
			MessageHandler statisticservice = new MessageHandler("default-statistic-queue");
			statisticservice.sendMessage(statisticmessage);
			*/
			
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}

}
