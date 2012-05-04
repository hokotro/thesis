package main;


import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		//getsayPlainTextHello();
		postsayPlainTextHello();
		
	}

	
	public static void getsayPlainTextHello(){
		try {			 
			Client client = Client.create();	 
			WebResource webResource = client
				.resource("http://localhost:8080/aws-image/rest/image/hello/");
	 
			ClientResponse response = webResource.accept("text/plain")
	                   .get(ClientResponse.class);
	 
			if (response.getStatus() != 200) {
			   throw new RuntimeException("Failed : HTTP error code : "
				+ response.getStatus());
			}	 
			String output = response.getEntity(String.class);
			System.out.println(output);	 
		  } catch (Exception e) {	 
			e.printStackTrace();	 
		  }
	}
	
	public static void postsayPlainTextHello(){
		
		try {	 
			Client client = Client.create();
	 
			WebResource webResource = client
			   .resource("http://localhost:8080/aws-image/rest/image/hello/");
			
			ClientResponse response = webResource
					.type("text/plain")
					.post(ClientResponse.class, "alma");
			 
			if (response.getStatus() != 201) {
			   throw new RuntimeException("Failed : HTTP error code : "
				+ response.getStatus());
			}	 
			String output = response.getEntity(String.class);
			System.out.println(output);	 
		  } catch (Exception e) {	 
			e.printStackTrace();	 
	    }
	}
	
}
