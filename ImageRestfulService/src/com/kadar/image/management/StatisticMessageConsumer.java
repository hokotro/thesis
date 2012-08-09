package com.kadar.image.management;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.BatchPutAttributesRequest;
import com.amazonaws.services.simpledb.model.CreateDomainRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.ReplaceableItem;
import com.kadar.image.message.handler.MessageHandler;
import com.kadar.image.message.handler.StatisticMessage;
import com.kadar.image.message.handler.StatisticMessageType;

public class StatisticMessageConsumer implements Runnable{

	private static final String SimpleDBDomain = "default-statistic";
	
	private MessageHandler statistic;
	private AmazonSimpleDB sdb;
	
	public StatisticMessageConsumer() throws IOException{
		statistic = new MessageHandler("default-statistic-queue"); 

        sdb = new AmazonSimpleDBClient(new PropertiesCredentials(
        		MessageHandler.class.getResourceAsStream("AwsCredentials.properties")));
        sdb.createDomain(new CreateDomainRequest(SimpleDBDomain));
	}
	
	@Override
	public void run() {
		while(true){
			try {
				
				StatisticMessage sm;
				sm = statistic.receiveStatisticMessage();			
				if(null != sm){
					long timeInMillis = System.currentTimeMillis();		
					Calendar cal = Calendar.getInstance();		
					cal.setTimeInMillis(timeInMillis);		
					java.util.Date date = cal.getTime();
					List<ReplaceableItem> list = new ArrayList<ReplaceableItem>();
					list.add(new ReplaceableItem(String.valueOf(timeInMillis)).withAttributes(
			                new ReplaceableAttribute("key", sm.getKeyOfImage(), true),
			                new ReplaceableAttribute("value", sm.getValue(), true),
			                new ReplaceableAttribute("date", date.toString(), true)
			                ));			
					sdb.batchPutAttributes(new BatchPutAttributesRequest(SimpleDBDomain, list));
					
					
					if( sm.getStatisticType() == StatisticMessageType.AllImageConvertion ){
						if( Float.valueOf(sm.getValue()) <= 10){
							//start new instance
							
						}
					}
				}
			} catch (AmazonServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AmazonClientException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//statistic.deleteMessage(sm.getReceiptHandle());
			

		}
	}

}
