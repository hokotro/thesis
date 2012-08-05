package com.kadar.image.aws.handler;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.BatchPutAttributesRequest;
import com.amazonaws.services.simpledb.model.CreateDomainRequest;
import com.amazonaws.services.simpledb.model.DeleteAttributesRequest;
import com.amazonaws.services.simpledb.model.DeleteDomainRequest;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.ReplaceableItem;
import com.amazonaws.services.simpledb.model.SelectRequest;
import com.kadar.image.message.handler.StatisticMessage;

public class DBStatHandler implements Serializable{

	private static final long serialVersionUID = 1L;

	private AmazonSimpleDB sdb;
	private String myDomain;

	public DBStatHandler(String domain) throws IOException{
        sdb = new AmazonSimpleDBClient(new PropertiesCredentials(
				//getClass().getResourceAsStream("../../../../../AwsCredentials.properties"));
				getClass().getClassLoader().getResourceAsStream("AwsCredentials.properties")));
        this.myDomain = domain;
	}
	
	public void putStatToDB(StatisticMessage sm) throws com.amazonaws.AmazonServiceException,com.amazonaws.AmazonClientException{
		//createDomainIfNotExists(myDomain);
		sdb.createDomain(new CreateDomainRequest(myDomain));
		
		String key, statType, value, time;
		key = (sm.getKeyOfImage() != null) ? sm.getKeyOfImage() : "";
		statType = (sm.getStatisticType() != null) ? sm.getStatisticType() : "";
		value = (sm.getValue() != null) ? sm.getValue() : "";
		time = (sm.getTime() != 0) ? String.valueOf(sm.getTime()) : "";
		
		java.util.List<ReplaceableItem> Data = new java.util.ArrayList<ReplaceableItem>();        
		Data.add(new ReplaceableItem(String.valueOf(System.currentTimeMillis())).withAttributes(
                new ReplaceableAttribute("key", key, true),
        		new ReplaceableAttribute("statisticType", statType, true),                
        		new ReplaceableAttribute("value", value, true),
        		new ReplaceableAttribute("Time", time, true)
                ));
		sdb.batchPutAttributes(new BatchPutAttributesRequest(myDomain, Data));		
	}
	
	public java.util.List<String> getItems(String myDomain) throws com.amazonaws.AmazonServiceException,com.amazonaws.AmazonClientException{
		java.util.List<String> items = new java.util.ArrayList<String>();
        
		//String selectExpression = "select * from `" + myDomain + "` where Category = 'Clothes'";
        String selectExpression = "select * from `" + myDomain + "`";
        SelectRequest selectRequest = new SelectRequest(selectExpression);
        for (Item item : sdb.select(selectRequest).getItems()) {
        	items.add(item.getName());
            System.out.println("  Item");
            System.out.println("    Name: " + item.getName());
            for (Attribute attribute : item.getAttributes()) {
                System.out.println("      Attribute");
                System.out.println("        Name:  " + attribute.getName());
                System.out.println("        Value: " + attribute.getValue());
            }
        }
        return items;
	}	
	
	public String getAttribute(String myDomain, String key, String name) throws com.amazonaws.AmazonServiceException,com.amazonaws.AmazonClientException{
		String selectExpression = "select * from `" + myDomain + "` where itemName() = '"+key+"'";
        SelectRequest selectRequest = new SelectRequest(selectExpression);
        for (Item item : sdb.select(selectRequest).getItems()) {
            for (Attribute attribute : item.getAttributes()) {
            	if(name.equals(attribute.getName()))
            		return  attribute.getValue();
            }
        }
		return "";
	}

	public void setThumbnail(String myDomain, String key, String value) throws com.amazonaws.AmazonServiceException,com.amazonaws.AmazonClientException {
		setAttribute(myDomain, key, "thumbnail", value);
	}		
	public void setSmall(String myDomain, String key, String value) throws com.amazonaws.AmazonServiceException,com.amazonaws.AmazonClientException {
		setAttribute(myDomain, key, "small", value);
	}		
	public void setMedium(String myDomain, String key, String value) throws com.amazonaws.AmazonServiceException,com.amazonaws.AmazonClientException {
		setAttribute(myDomain, key, "medium", value);
	}		
	public void setLarge(String myDomain, String key, String value) throws com.amazonaws.AmazonServiceException,com.amazonaws.AmazonClientException {
		setAttribute(myDomain, key, "large", value);
	}		
	private void setAttribute(String myDomain, String key, String name, String value) throws com.amazonaws.AmazonServiceException,com.amazonaws.AmazonClientException {
        List<ReplaceableAttribute> replaceableAttributes = new ArrayList<ReplaceableAttribute>();
        replaceableAttributes.add(new ReplaceableAttribute(name, value, true));
        sdb.putAttributes(new PutAttributesRequest(myDomain, key, replaceableAttributes));

		/*
		String selectExpression = "select * from `" + myDomain + "` where itemName() = '" + key + "'";
        SelectRequest selectRequest = new SelectRequest(selectExpression);
        for (Item item : sdb.select(selectRequest).getItems()) {
            for (Attribute attribute : item.getAttributes()) {
            	if(name.equals(attribute.getName())){
            		
            		java.util.List<ReplaceableItem> Data = new java.util.ArrayList<ReplaceableItem>();        
            		Data.add(new ReplaceableItem(item.getName()).withAttributes(        
                    		new ReplaceableAttribute(name, value, true)
                            ));
            		sdb.batchPutAttributes(new BatchPutAttributesRequest(myDomain, Data));
            		
            	}
            }
        }
        */
	}	

	public void deleteItem(String myDomain, String key) throws com.amazonaws.AmazonServiceException,com.amazonaws.AmazonClientException {
        sdb.deleteAttributes(new DeleteAttributesRequest(myDomain, key));
	}
	
	public void deleteDomain(String myDomain) throws com.amazonaws.AmazonServiceException,com.amazonaws.AmazonClientException {
    	sdb.deleteDomain(new DeleteDomainRequest(myDomain));
	}
}
