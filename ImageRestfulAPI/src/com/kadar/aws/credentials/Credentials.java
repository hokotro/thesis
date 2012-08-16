package com.kadar.aws.credentials;

import java.io.InputStream;

public class Credentials {

	public static InputStream getCredentials(){
		return Credentials.class.getClassLoader().getResourceAsStream("AwsCredentials.properties");
	}
	
}
