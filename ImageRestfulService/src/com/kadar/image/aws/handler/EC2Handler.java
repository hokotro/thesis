package com.kadar.image.aws.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesResult;
import com.kadar.image.message.handler.MessageHandler;

public class EC2Handler {
	public AmazonEC2 ec2;
	MessageHandler mh;
	
	public EC2Handler() throws AmazonServiceException, AmazonClientException, IOException { 
		AWSCredentials credentials = new PropertiesCredentials(
				//getClass().getResourceAsStream("../../../../../AwsCredentials.properties"));
				getClass().getClassLoader().getResourceAsStream("AwsCredentials.properties"));
			ec2 = new AmazonEC2Client(credentials);
			mh = new MessageHandler("default-message-queue");
	}
	
	public List<String> runInstance(String ImageId, String InstanceType, int p ){
        RunInstancesRequest runInstancesRequest = new RunInstancesRequest()
        .withInstanceType(InstanceType)
        .withImageId(ImageId)
        .withMinCount(p)
        .withMaxCount(p)
        .withSecurityGroupIds("default-security-group")
        .withKeyName("default-key-pair")        
        //.withUserData(Base64.encodeBase64String(myUserData.getBytes()))
        ;
        
        List<String> ids = new ArrayList<String>();
        RunInstancesResult runInstances = ec2.runInstances(runInstancesRequest);
        for( Instance instance : runInstances.getReservation().getInstances()){
        	ids.add(instance.getInstanceId());
        }
        return ids;
	}

	public int numberOfRunningInstances(String ImageId, String InstanceType){
		//System.out.println(InstanceType);
		int piece = 0;
		for (Reservation reservation : ec2.describeInstances().getReservations()) {
        	for (Instance instance : reservation.getInstances()) {
        		//System.out.println(instance.getInstanceType());
        		if(InstanceType.equals(instance.getInstanceType()) 
        				&& instance.getImageId().equals(ImageId) 
        				&& (
       						InstanceStateName.Running.toString().equals(instance.getState().getName())
        				   || InstanceStateName.Pending.toString().equals(instance.getState().getName())
        					)	
        				){
        			piece++;
        		}
        	}
        }
		return piece;
	}
	
	public List<String> listOfRunningInstances(String ImageId, String InstanceType){
		List<String> list = new ArrayList<String>();
		for (Reservation reservation : ec2.describeInstances().getReservations()) {
        	for (Instance instance : reservation.getInstances()) {
        		if(InstanceType.equals(instance.getInstanceType()) 
        				&& instance.getImageId().equals(ImageId) 
        				&& (
       						InstanceStateName.Running.toString().equals(instance.getState().getName())
        				   || InstanceStateName.Pending.toString().equals(instance.getState().getName())
        					)	
        				){
        			list.add(instance.getInstanceId());
        		}
        	}
        }
		return list;
	}
	
	public void TerminateInstance(String InstanceId){
		List<String > instanceIds = new ArrayList<String>();
		instanceIds.add(InstanceId);
		TerminateInstancesRequest terminateRequest = new TerminateInstancesRequest(instanceIds);
		TerminateInstancesResult tir = ec2.terminateInstances(terminateRequest);
	} 
	
	public void TerminateInstancesByImageId(String ImageId ){
		List<String > instanceIds = new ArrayList<String>();
		
		for (Reservation reservation : ec2.describeInstances().getReservations()) {
        	for (Instance instance : reservation.getInstances()) {
        		if(ImageId.equals(instance.getImageId()) 
        				){
        			String id = instance.getInstanceId();
        			instanceIds.add(id);
        			mh.deleteQueue(id + "-queue");
        		}
        	}
		}
		TerminateInstancesRequest terminateRequest = new TerminateInstancesRequest(instanceIds);
		TerminateInstancesResult tir = ec2.terminateInstances(terminateRequest);
	}
	
	public  String getInstanceTypeById(String InstanceId){
		for (Reservation reservation : ec2.describeInstances().getReservations()) {
        	for (Instance instance : reservation.getInstances()) {
        		if(InstanceId.equals( instance.getInstanceId()) ){
        			return instance.getInstanceType();
        		} 
        	}
		}
		return null;
	}
}
