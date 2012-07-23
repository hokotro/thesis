package com.kadar.image.awsImageHandler;

import java.util.regex.Pattern;

public class TaskMessage {
	private static final Pattern pattern = Pattern.compile("<==>");
	//private StringBuilder message = new StringBuilder();
	
	private String WhatToDo;
	private String Value;
	private String Body;
	private String InstanceId;
	private String InstanceType;
	private String MessageId;
	private String ReceiptHandle;
	
	public static class Builder{
		private String WhatToDo;
		private String Value;
		private String Body;		
		private String InstanceId;
		private String InstanceType;
		private String MessageId;
		private String ReceiptHandle;

		/*
		public Builder(String wtd, String val){
			this.WhatToDo = wtd;
			this.Value = val;
		}
		*/
		
		public Builder WhatToDo(String val){
			WhatToDo = val; return this;
		}
		public Builder Value(String val){
			Value = val; return this;
		}		
		public Builder Body(String val){
			Body = val; 
			MessageFromQueue(Body);
			return this;
		}
		private void MessageFromQueue(String message){
	        String[] split = pattern.split(message);
	        if( split.length == 2 ){
	        	WhatToDo = split[0];
		        Value = split[1];
	        }else{
	        	Value = message;
	        }
		}
		public Builder InstanceId(String val){
			InstanceId = val; return this;
		}
		public Builder InstanceType(String val){
			InstanceType = val; return this;
		}
		public Builder MessageId(String val){
			MessageId = val; return this;
		}
		public Builder ReceiptHandle(String val){
			ReceiptHandle = val; return this;
		}
		public TaskMessage build(){
			//if( Body == null || "".equals(Body))
				//throw new IllegalStateException(); 
			return new TaskMessage(this);
		}		
	}
	
	private TaskMessage(Builder builder){
		WhatToDo = builder.WhatToDo;
		Value = builder.Value;
		MessageToQueue();
		//Body = builder.Body;	
		InstanceId = builder.InstanceId;
		InstanceType = builder.InstanceType;
		MessageId = builder.MessageId;
		ReceiptHandle = builder.ReceiptHandle;		
	}
	
	private String MessageToQueue(){
		StringBuilder message = new StringBuilder();
		message.append(WhatToDo);
		message.append("<==>");
		message.append(Value);
		Body = message.toString();
		//message.append("<==>");
		//message.append(Body);
		//message.append("<==>");
		/*
		message.append(InstanceId);
		message.append("<==>");
		message.append(MessageId);
		message.append("<==>");
		message.append(ReceiptHandle);
		*/
		return message.toString();
	}

	public String getWhatToDo() {
		return WhatToDo;
	}

	public void setWhatToDo(String whatToDo) {
		WhatToDo = whatToDo;
	}

	public String getValue() {
		return Value;
	}

	public void setValue(String value) {
		Value = value;
	}

	public String getBody() {
		return Body;
	}

	public void setBody(String body) {
		Body = body;
	}

	public String getInstanceId() {
		return InstanceId;
	}

	public void setInstanceId(String instanceId) {
		InstanceId = instanceId;
	}

	public String getInstanceType() {
		return InstanceType;
	}

	public void setInstanceType(String instanceType) {
		InstanceType = instanceType;
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

	public static Pattern getPattern() {
		return pattern;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Body == null) ? 0 : Body.hashCode());
		result = prime * result
				+ ((InstanceId == null) ? 0 : InstanceId.hashCode());
		result = prime * result
				+ ((InstanceType == null) ? 0 : InstanceType.hashCode());
		result = prime * result
				+ ((MessageId == null) ? 0 : MessageId.hashCode());
		result = prime * result
				+ ((ReceiptHandle == null) ? 0 : ReceiptHandle.hashCode());
		result = prime * result + ((Value == null) ? 0 : Value.hashCode());
		result = prime * result
				+ ((WhatToDo == null) ? 0 : WhatToDo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TaskMessage other = (TaskMessage) obj;
		if (Body == null) {
			if (other.Body != null)
				return false;
		} else if (!Body.equals(other.Body))
			return false;
		if (InstanceId == null) {
			if (other.InstanceId != null)
				return false;
		} else if (!InstanceId.equals(other.InstanceId))
			return false;
		if (InstanceType == null) {
			if (other.InstanceType != null)
				return false;
		} else if (!InstanceType.equals(other.InstanceType))
			return false;
		if (MessageId == null) {
			if (other.MessageId != null)
				return false;
		} else if (!MessageId.equals(other.MessageId))
			return false;
		if (ReceiptHandle == null) {
			if (other.ReceiptHandle != null)
				return false;
		} else if (!ReceiptHandle.equals(other.ReceiptHandle))
			return false;
		if (Value == null) {
			if (other.Value != null)
				return false;
		} else if (!Value.equals(other.Value))
			return false;
		if (WhatToDo == null) {
			if (other.WhatToDo != null)
				return false;
		} else if (!WhatToDo.equals(other.WhatToDo))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TaskMessage [WhatToDo=" + WhatToDo + ", Value=" + Value
				+ ", Body=" + Body + ", InstanceId=" + InstanceId
				+ ", InstanceType=" + InstanceType + ", MessageId=" + MessageId
				+ ", ReceiptHandle=" + ReceiptHandle + "]";
	}
	
	
}
