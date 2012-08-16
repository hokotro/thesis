package main;

import java.io.File;
import java.io.IOException;
import com.kadar.aws.handler.S3Handler;
import com.kadar.image.config.Config;
import com.kadar.message.handler.MessageHandler;
import com.kadar.message.handler.TaskMessage;

public class ConvertConsumer implements Runnable {

	private final String InstanceId;
	private final S3Handler s3;
	private TaskMessage tm;
	private final String bucketName;
	
	public ConvertConsumer(String InstanceId, TaskMessage tm) throws IOException {

		this.InstanceId = InstanceId;
		this.tm = tm;
		s3 = new S3Handler();
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
			tm.setStartConvertTime(System.currentTimeMillis());
			
			Process p = Runtime.getRuntime().exec(new String[] {
					"convert", "temp." + fname + "." + ext, 
					"-resize", tm.getConvertValue(), 
					newfile
					 });
			p.waitFor();						
			
			File convertedfile = new File(newfile);
			s3.putObjectWithPublicRead(bucketName, newfile, convertedfile);
			
			tm.setEndConvertTime(System.currentTimeMillis());			
			MessageHandler smh = new MessageHandler(Config.statisticMessageQueue);
			smh.sendMessage(tm);
			
			//delete tmp
			Process pd = Runtime.getRuntime().exec(new String[] {
				"rm", "temp." + fname + "." + ext
			});
			pd.waitFor();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
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
