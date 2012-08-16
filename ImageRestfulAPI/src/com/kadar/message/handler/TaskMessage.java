package com.kadar.message.handler;


public class TaskMessage {

	private String MessageType;
	private String KeyOfImage;
	private String OriginalValue;
	private String ConvertValue;
	private long startJobTime;
	private long endJobTime;
	private long startConvertTime;
	private long endConvertTime;
	private long Timestamp;
	private String GroupId;
	private String InstanceId;
			
	private String MessageBodyInJSON;
	private String MessageId;
	private String ReceiptHandle;
	
	public static class Builder{
		private String MessageType;
		private String KeyOfImage;
		private String OriginalValue;
		private String ConvertValue;
		private long startJobTime;
		private long endJobTime;
		private long startConvertTime;
		private long endConvertTime;
		private long Timestamp;
		private String GroupId;
		private String InstanceId;
		
		private String MessageBodyInJSON;
		private String MessageId;
		private String ReceiptHandle;

		public Builder setMessageType(String val){
			MessageType = val; return this;
		}
		public Builder setKeyOfImage(String val){
			KeyOfImage = val; return this;
		}
		public Builder setOriginalValue(String val){
			OriginalValue = val; return this;
		}		
		public Builder setConvertValue(String val){
			ConvertValue = val; return this;
		}		

		public Builder setstartJobTime(long val){
			startJobTime = val; return this;
		}
		public Builder setendJobTime(long val){
			endJobTime = val; return this;
		}
		public Builder setstartConvertTime(long val){
			startConvertTime = val; return this;
		}
		public Builder setendConvertTime(long val){
			endConvertTime = val; return this;
		}
		public Builder setTimestamp(long val){
			Timestamp = val; return this;
		}
		public Builder setGroupId(String val) {
			GroupId = val; 
			return this;
		}
		public Builder setInstanceId(String val) {
			InstanceId = val; 
			return this;
		}

		public Builder setMessageBodyInJSON(String val) {
			MessageBodyInJSON = val; 
			return this;
		}
		public Builder setMessageId(String val) {
			MessageId = val; 
			return this;
		}
		public Builder setReceiptHandle(String val) {
			ReceiptHandle = val; 
			return this;
		}
		
		public TaskMessage build() {
			return new TaskMessage(this);
		}		
	}
	
	public TaskMessage(){
		
	}
	
	private TaskMessage(Builder builder) {
		MessageType = builder.MessageType;
		KeyOfImage = builder.KeyOfImage;
		OriginalValue = builder.OriginalValue;		
		ConvertValue = builder.ConvertValue;

		startJobTime = builder.startJobTime;
		endJobTime = builder.endJobTime;
		startConvertTime = builder.startConvertTime;
		endConvertTime = builder.endConvertTime;
		Timestamp = builder.Timestamp;
		
		GroupId = builder.GroupId;
		InstanceId = builder.InstanceId;
		//if( startTime != 0l && endTime != 0l )
			//diffTime = (float) (System.currentTimeMillis() - startTime) / 1000;		
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

	public String getKeyOfImage() {
		return KeyOfImage;
	}

	public void setKeyOfImage(String keyOfImage) {
		KeyOfImage = keyOfImage;
	}

	public String getOriginalValue() {
		return OriginalValue;
	}

	public void setOriginalValue(String originalValue) {
		OriginalValue = originalValue;
	}

	public String getConvertValue() {
		return ConvertValue;
	}

	public void setConvertValue(String convertValue) {
		ConvertValue = convertValue;
	}

	public long getStartJobTime() {
		return startJobTime;
	}

	public void setStartJobTime(long startJobTime) {
		this.startJobTime = startJobTime;
	}

	public long getEndJobTime() {
		return endJobTime;
	}

	public void setEndJobTime(long endJobTime) {
		this.endJobTime = endJobTime;
	}

	public long getStartConvertTime() {
		return startConvertTime;
	}

	public void setStartConvertTime(long startConvertTime) {
		this.startConvertTime = startConvertTime;
	}

	public long getEndConvertTime() {
		return endConvertTime;
	}

	public void setEndConvertTime(long endConvertTime) {
		this.endConvertTime = endConvertTime;
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


	public String getInstanceId() {
		return InstanceId;
	}

	public void setInstanceId(String instanceId) {
		InstanceId = instanceId;
	}

	public String getGroupId() {
		return GroupId;
	}

	public void setGroupId(String groupId) {
		GroupId = groupId;
	}

	public long getTimestamp() {
		return Timestamp;
	}

	public void setTimestamp(long timestamp) {
		Timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "TaskMessage [MessageType=" + MessageType + ", KeyOfImage="
				+ KeyOfImage + ", OriginalValue=" + OriginalValue
				+ ", ConvertValue=" + ConvertValue + ", startJobTime="
				+ startJobTime + ", endJobTime=" + endJobTime
				+ ", startConvertTime=" + startConvertTime
				+ ", endConvertTime=" + endConvertTime + ", Timestamp="
				+ Timestamp + ", GroupId=" + GroupId + ", InstanceId="
				+ InstanceId + ", MessageBodyInJSON=" + MessageBodyInJSON
				+ ", MessageId=" + MessageId + ", ReceiptHandle="
				+ ReceiptHandle + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((ConvertValue == null) ? 0 : ConvertValue.hashCode());
		result = prime * result + ((GroupId == null) ? 0 : GroupId.hashCode());
		result = prime * result
				+ ((InstanceId == null) ? 0 : InstanceId.hashCode());
		result = prime * result
				+ ((KeyOfImage == null) ? 0 : KeyOfImage.hashCode());
		result = prime
				* result
				+ ((MessageBodyInJSON == null) ? 0 : MessageBodyInJSON
						.hashCode());
		result = prime * result
				+ ((MessageId == null) ? 0 : MessageId.hashCode());
		result = prime * result
				+ ((MessageType == null) ? 0 : MessageType.hashCode());
		result = prime * result
				+ ((OriginalValue == null) ? 0 : OriginalValue.hashCode());
		result = prime * result
				+ ((ReceiptHandle == null) ? 0 : ReceiptHandle.hashCode());
		result = prime * result + (int) (Timestamp ^ (Timestamp >>> 32));
		result = prime * result
				+ (int) (endConvertTime ^ (endConvertTime >>> 32));
		result = prime * result + (int) (endJobTime ^ (endJobTime >>> 32));
		result = prime * result
				+ (int) (startConvertTime ^ (startConvertTime >>> 32));
		result = prime * result + (int) (startJobTime ^ (startJobTime >>> 32));
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
		if (ConvertValue == null) {
			if (other.ConvertValue != null)
				return false;
		} else if (!ConvertValue.equals(other.ConvertValue))
			return false;
		if (GroupId == null) {
			if (other.GroupId != null)
				return false;
		} else if (!GroupId.equals(other.GroupId))
			return false;
		if (InstanceId == null) {
			if (other.InstanceId != null)
				return false;
		} else if (!InstanceId.equals(other.InstanceId))
			return false;
		if (KeyOfImage == null) {
			if (other.KeyOfImage != null)
				return false;
		} else if (!KeyOfImage.equals(other.KeyOfImage))
			return false;
		if (MessageBodyInJSON == null) {
			if (other.MessageBodyInJSON != null)
				return false;
		} else if (!MessageBodyInJSON.equals(other.MessageBodyInJSON))
			return false;
		if (MessageId == null) {
			if (other.MessageId != null)
				return false;
		} else if (!MessageId.equals(other.MessageId))
			return false;
		if (MessageType == null) {
			if (other.MessageType != null)
				return false;
		} else if (!MessageType.equals(other.MessageType))
			return false;
		if (OriginalValue == null) {
			if (other.OriginalValue != null)
				return false;
		} else if (!OriginalValue.equals(other.OriginalValue))
			return false;
		if (ReceiptHandle == null) {
			if (other.ReceiptHandle != null)
				return false;
		} else if (!ReceiptHandle.equals(other.ReceiptHandle))
			return false;
		if (Timestamp != other.Timestamp)
			return false;
		if (endConvertTime != other.endConvertTime)
			return false;
		if (endJobTime != other.endJobTime)
			return false;
		if (startConvertTime != other.startConvertTime)
			return false;
		if (startJobTime != other.startJobTime)
			return false;
		return true;
	}


}
