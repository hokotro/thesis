package com.kadar.scalable.application;

import com.kadar.image.message.handler.TaskMessage;

public interface Scalable {

	public void doBusinessLogic(String str);
	
	public void doBusinessLogic(TaskMessage im);
		
	
}
