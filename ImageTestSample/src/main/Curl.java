package main;
import java.io.File;
import java.io.IOException;


public class Curl implements Runnable {
	
	String path = ".";
	
	public Curl(){
		path = Config.ImageGaleryPath;
		
	}
	
	@Override
	public void run() {

		 
		String file;
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();

		//for (int i = 0; i < listOfFiles.length; i++) 
		for (int i = 0; i < listOfFiles.length / 2; i++) 
		{			 
			if (listOfFiles[i].isFile()) 
			{
				file = listOfFiles[i].getName();
				String command = "curl -T " + path + file + "  " + Config.InstanceAddress + ":8080/ImageRestfulService/rest/imageservice/putFile/" + file;
				//System.out.println(command);
				
				try {
					Process process = Runtime.getRuntime().exec(command);
					System.out.println(process.getOutputStream());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
