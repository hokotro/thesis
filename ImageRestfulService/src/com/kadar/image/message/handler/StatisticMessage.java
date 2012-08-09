package com.kadar.image.message.handler;

import java.util.regex.Pattern;

import com.kadar.image.message.handler.TaskMessage.Builder;

public class StatisticMessage {
	//private static final Pattern pattern = Pattern.compile("<==>");
	//private StringBuilder message = new StringBuilder();

	private String Value;
	private float Time;
	private String InstanceId;
	private String StatisticType;
	private String KeyOfImage;
	//private long startTime;
	//private long endTime;
	private String MessageBodyInJSON;
	
	public static class Builder{
		private String StatisticType;
		private String Value;
		private String KeyOfImage;
		private String InstanceId;
		private long startTime;
		private long endTime;
		private float Time;
		private String MessageBodyInJSON;
		
		public Builder setStatisticType(String val){
			StatisticType = val; return this;
		}
		public Builder setValue(String val){
			Value = val; return this;
		}		
		public Builder setKeyOfImage(String val){
			KeyOfImage = val;
			return this;
		}
		public Builder setInstanceId(String val){
			InstanceId = val;
			return this;
		}
		/*
		public Builder setstartTime(long val){
			startTime = val; return this;
		}
		public Builder setendTime(long val){
			endTime = val; return this;
		}
		*/
		public Builder setTime(float val){
			Time = val; return this;
		}		
		public Builder setMessageBodyInJSON(String val) {
			MessageBodyInJSON = val; 
			return this;
		}
		public StatisticMessage build(){
			return new StatisticMessage(this);
		}		
	}
	
	public StatisticMessage(){
		
	}
	private StatisticMessage(Builder builder){
		StatisticType = builder.StatisticType;
		Value = builder.Value;
		KeyOfImage = builder.KeyOfImage;
		InstanceId = builder.InstanceId;
		//startTime = builder.startTime;
		//endTime = builder.endTime;
		Time = builder.Time;
		//if( startTime != 0 && endTime != 0 )
		//	diffTime = (float) (System.currentTimeMillis() - startTime) / 1000;	
		MessageBodyInJSON = builder.MessageBodyInJSON;
	}

	public String getStatisticType() {
		return StatisticType;
	}

	public void setStatisticType(String statisticType) {
		StatisticType = statisticType;
	}

	public String getValue() {
		return Value;
	}

	public void setValue(String value) {
		Value = value;
	}

	public String getKeyOfImage() {
		return KeyOfImage;
	}

	public void setKeyOfImage(String keyOfImage) {
		KeyOfImage = keyOfImage;
	}



	public float getTime() {
		return Time;
	}
	public void setTime(float time) {
		Time = time;
	}
	public String getMessageBodyInJSON() {
		return MessageBodyInJSON;
	}

	public void setMessageBodyInJSON(String messageBodyInJSON) {
		MessageBodyInJSON = messageBodyInJSON;
	}
	public String getInstanceId() {
		return InstanceId;
	}
	public void setInstanceId(String instanceId) {
		InstanceId = instanceId;
	}
	@Override
	public String toString() {
		return "StatisticMessage [Value=" + Value + ", Time=" + Time
				+ ", InstanceId=" + InstanceId + ", StatisticType="
				+ StatisticType + ", KeyOfImage=" + KeyOfImage
				+ ", MessageBodyInJSON=" + MessageBodyInJSON + "]";
	}
	

	
}
