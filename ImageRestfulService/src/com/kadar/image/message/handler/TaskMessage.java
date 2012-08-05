package com.kadar.image.message.handler;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.regex.Pattern;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class TaskMessage {
	//private static final Pattern pattern = Pattern.compile("<==>");
	//private StringBuilder message = new StringBuilder();

	private String MessageType;
	private String Value;
	private float diffTime;
	private String KeyOfImage;
	private long startTime;
	private long endTime;
	private String MessageBodyInJSON;
	private String MessageId;
	private String ReceiptHandle;
	
	public static class Builder{
		private String MessageType;
		private String Value;
		private String KeyOfImage;
		private long startTime;
		private long endTime;
		private float diffTime;
		private String MessageBodyInJSON;
		private String MessageId;
		private String ReceiptHandle;

		public Builder setMessageType(String val){
			MessageType = val; return this;
		}
		public Builder setValue(String val){
			Value = val; return this;
		}		
		public Builder setKeyOfImage(String val){
			KeyOfImage = val; return this;
		}		
		public Builder setMessageBodyInJSON(String val) {
			MessageBodyInJSON = val; 
			return this;
		}
		public Builder setStartTime(long val){
			startTime = val; return this;
		}
		public Builder setEndTime(long val){
			endTime = val; return this;
		}
		public Builder setMessageId(String val){
			MessageId = val; return this;
		}
		public Builder setReceiptHandle(String val){
			ReceiptHandle = val; return this;
		}
		public TaskMessage build() {
			return new TaskMessage(this);
		}		
	}
	
	public TaskMessage(){
		
	}
	
	private TaskMessage(Builder builder) {
		MessageType = builder.MessageType;
		Value = builder.Value;		
		KeyOfImage = builder.KeyOfImage;
		startTime = builder.startTime;
		endTime = builder.endTime;
		//diffTime = builder.diffTime;
		if( startTime != 0l && endTime != 0l )
			diffTime = (float) (System.currentTimeMillis() - startTime) / 1000;		
		MessageBodyInJSON = builder.MessageBodyInJSON;
		MessageId = builder.MessageId;
		ReceiptHandle = builder.ReceiptHandle;
	}

	public String getMessageType() {
		return MessageType;
	}

	public void setMessageType(String messageType) {
		MessageType = messageType;
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

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public float getDiffTime() {
		return diffTime;
	}

	public void setDiffTime(float diffTime) {
		this.diffTime = diffTime;
	}

	public String getMessageBodyInJSON() {
		return MessageBodyInJSON;
	}

	public void setMessageBodyInJSON(String messageBodyInJSON) {
		MessageBodyInJSON = messageBodyInJSON;
	}

	public String getMessageId() {
		return MessageId;
	}

	public void setMessageId(String messageId) {
		MessageId = messageId;
	}

	public String getReceiptHandle() {
		return ReceiptHandle;
	}

	public void setReceiptHandle(String receiptHandle) {
		ReceiptHandle = receiptHandle;
	}

	@Override
	public String toString() {
		return "TaskMessage [MessageType=" + MessageType + ", Value=" + Value
				+ ", diffTime=" + diffTime + ", KeyOfImage=" + KeyOfImage
				+ ", startTime=" + startTime + ", endTime=" + endTime
				+ ", MessageBodyInJSON=" + MessageBodyInJSON + ", MessageId="
				+ MessageId + ", ReceiptHandle=" + ReceiptHandle + "]";
	}


}
