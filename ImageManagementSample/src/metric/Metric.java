package metric;

import com.kadar.image.message.handler.StatisticMessage;
import com.kadar.image.message.handler.StatisticMessageType;

public class Metric {
	/*
	 * pangás: darabszám, mely megadja h mennyi elemmel nem foglalkozok még,
	 * ezen felül kezdek csak számolni statisztikát, mely megmondja
	 * szükség van e új instance indítására
	 */
	public static int stagnation = 2;
	/*
	 * threshold for conversion of small images
	 * when it is upper then 10%, we have to do scale
	 */
	public static final float SmallImageConvertion = 0.5f;
	public static final float MediumImageConvertion = 0.5f;
	public static final float LargeImageConvertion = 0.5f;
	/*
	 * we must have a boolean that detect, we just have scaling up
	 * in interval of threshold of starting new instance we have'nt scale up
	 */
	private static boolean scaleUp = false;
	/*
	 * threshold of starting new instance
	 */
	public static final long InstanceStartUpThresholdTime = 110000l;
	//104, 94 92 106 97 103
	
	/*
	public static boolean checkThreshold(StatisticMessage msg) throws InterruptedException{
		
		if(scaleUp){
			//Thread.sleep((int)InstanceStartUpThresholdTime);
			scaleUp = false;
		}else{
			if(msg.getStatisticType().equals(StatisticMessageType.SmallImageConvertion)){
				if(SmallImageConvertion <= msg.getTime()){
					scaleUp = true;
					return false;
				}
			}
		}
		return true;
	}
	*/



}
