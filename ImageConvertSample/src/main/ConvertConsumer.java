package main;

import java.io.IOException;

import com.kadar.image.aws.handler.DBImageHandler;
import com.kadar.image.aws.handler.DBStatHandler;
import com.kadar.image.convert.service.ImageConvertService;
import com.kadar.image.convert.service.ImageConvertServiceRemote;
import com.kadar.image.message.handler.MessageHandler;
import com.kadar.image.message.handler.StatisticMessage;
import com.kadar.image.message.handler.StatisticMessageType;
import com.kadar.image.message.handler.TaskMessage;
import com.kadar.image.message.handler.TaskMessageType;
import com.kadar.scalable.application.Scalable;

public class ConvertConsumer implements Runnable {

	private MessageHandler mh;
	private String InstanceId;
	//private String InstanceType;
	private TaskMessage tm;
	private String bucketName = "kg-images";
	private String DBDomain = "kg-images";
	private ImageConvertServiceRemote convertservice;
	DBImageHandler db;
	
	public ConvertConsumer(String InstanceId, /*String InstanceType,*/ TaskMessage tm) throws IOException {
		//this.InstanceType = InstanceType;
		this.InstanceId = InstanceId;
		this.tm = tm;
		convertservice = new ImageConvertService(); 
		db = new DBImageHandler();
	}
	
	@Override
	public void run() {
		try{
			long startTime = System.currentTimeMillis();	

			String value = "";
			String stat = "";

	        if(TaskMessageType.ConvertSmallImage.equals(tm.getMessageType())){
	        	value = convertservice.generateSmall(bucketName, tm.getKeyOfImage());
				db.setSmall(DBDomain, tm.getKeyOfImage(), value );
				stat = StatisticMessageType.SmallImageConvertion;
	        }
	        if(TaskMessageType.ConvertMediumImage.equals(tm.getMessageType())){
	        	value = convertservice.generateMedium(bucketName, tm.getKeyOfImage());
				db.setMedium(DBDomain, tm.getKeyOfImage(), value );
				stat = StatisticMessageType.MediumImageConvertion;				
	        }
	        if(TaskMessageType.ConvertLargeImage.equals(tm.getMessageType())){
	        	value = convertservice.generateLarge(bucketName, tm.getKeyOfImage());
				db.setLarge(DBDomain, tm.getKeyOfImage(), value );
				stat = StatisticMessageType.LargeImageConvertion;
	        }

	        
			float endTime = (float) (System.currentTimeMillis() - startTime) / 1000;
			
			
			StatisticMessage sm = new StatisticMessage.Builder()
				.setKeyOfImage(tm.getKeyOfImage())
				.setStatisticType(stat)
				.setTime(endTime)
				.setValue(tm.getValue())
				.setInstanceId(this.InstanceId)
				.build();
			MessageHandler statisticservice = new MessageHandler("default-statistic-queue");
			statisticservice.sendMessage(sm);
			
			DBStatHandler statdb = new DBStatHandler("default-statistic");
			statdb.putStatToDB(sm);
			
			System.out.println("Converting successful: " 
					+ "Key of Image: " + tm.getKeyOfImage()
					+ ", convert: " + stat				
					+ ", time: " + endTime);
			
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}

}
