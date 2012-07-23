package com.kadar.scalable.application;

import com.kadar.image.awsImageHandler.TaskMessage;

public interface Scalable {

	public void doBusinessLogic(String str);
	
	public void doBusinessLogic(TaskMessage im);
		
	
}
