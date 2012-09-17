package main;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.kadar.aws.handler.S3Handler;
import com.kadar.image.config.Config;
import com.kadar.message.handler.MessageHandler;
import com.kadar.message.handler.TaskMessage;

public class ConvertConsumer implements Runnable {

	private final String InstanceId;
	private S3Handler s3;
	private TaskMessage tm;
	private final String bucketName;

	//logger
	static Logger logger = Logger.getLogger("convertsamplelogger");
	static FileHandler fh;
	
	public ConvertConsumer(String InstanceId, TaskMessage tm) {
		
	    try {
		      // This block configure the logger with handler and formatter
		      fh = new FileHandler("/home/ubuntu/convertsample.log", true);
		      logger.addHandler(fh);
		      logger.setLevel(Level.ALL);
		      SimpleFormatter formatter = new SimpleFormatter();
		      fh.setFormatter(formatter);

		    } catch (SecurityException e) {
		      e.printStackTrace();
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
	    
		this.InstanceId = InstanceId;
		this.tm = tm;
		try {
			s3 = new S3Handler();
		} catch (AmazonServiceException e) {
			logger.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		} catch (AmazonClientException e) {
			logger.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		}
		bucketName = Config.bucketName;
		
	}
	
	@Override
	public void run() {
		tm.setInstanceId(InstanceId);

		String fileName = tm.getKeyOfImage();

		String[] filename = getFileExtension(fileName);
		String fname = filename[0];
		String ext = filename[1];
		
		File file = new File("temp." + fname + "." + ext);
		s3.getObjectToFile(bucketName, tm.getKeyOfImage(), file);
		
		String newfile = fname + "_" + tm.getConvertValue() + "." + ext;
		
		try {
			tm.setStartJobTime(System.currentTimeMillis());
			
			Process p = Runtime.getRuntime().exec(new String[] {
					"convert", "temp." + fname + "." + ext, 
					"-resize", tm.getConvertValue(), 
					newfile
					 });
			p.waitFor();						
			
			File convertedfile = new File(newfile);
			s3.putObjectWithPublicRead(bucketName, newfile, convertedfile);
			
			tm.setEndJobTime(System.currentTimeMillis());			
			MessageHandler smh = new MessageHandler(Config.statisticMessageQueue);
			smh.sendMessage(tm);
			
			//delete tmp
			Process pd = Runtime.getRuntime().exec(new String[] {
				"rm", "temp." + fname + "." + ext
			});
			pd.waitFor();
			
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		}		
	}

	private String[] getFileExtension(String fileName){
		String fname="";
		String ext="";
		int mid= fileName.lastIndexOf(".");
		fname=fileName.substring(0,mid);
		ext=fileName.substring(mid+1,fileName.length());  
		//System.out.println("File name ="+fname);
		//System.out.println("Extension ="+ext);  
		return new String[] { fname, ext };
	}
	
}
