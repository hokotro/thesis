package metric;

import com.kadar.image.message.handler.StatisticMessage;
import com.kadar.image.message.handler.StatisticMessageType;

public class Metric {
	/*
	 * pangás: darabszám, mely megadja h mennyi elemmel nem foglalkozok még,
	 * ezen felül kezdek csak számolni statisztikát, mely megmondja
	 * szükség van e új instance indítására
	 */
	public static int stagnation = 50;
	/*
	 * threshold for conversion of small images
	 * when it is greater then 10%, we have to do scale up
	 */
	public static final float SmallImageConvertionScaleUp = 1f;
	public static final float MediumImageConvertionScaleUp = 0.8f;
	public static final float LargeImageConvertionScaleUp = 0.8f;
	/*
	 * threshold for conversion of small images
	 * when it is lower then 90%, we have to do scale up
	 */
	public static final float SmallImageConvertionConsolidate = 0.7f;
	public static final float MediumImageConvertionConsolidate = 0.7f;
	public static final float LargeImageConvertionConsolidate = 0.7f;
	/*
	 * the minimum number of convertation instances
	 * we start with this numbers
	 */
	public static final int minNumberOfSmallInstance = 4;
	public static final int minNumberOfMediumInstance = 1;
	public static final int minNumberOfLargeInstance = 1;
	/*
	 * the maximum number of convertation instances // to save money
	 */
	public static final int maxNumberOfSmallInstance = 5;
	public static final int maxNumberOfMediumInstance = 5;
	public static final int maxNumberOfLargeInstance = 5;
	/*
	 * we must have a boolean that detect, we just have scaling up
	 * in interval of threshold of starting new instance we have'nt scale up
	 */
	private static boolean scaleUp = false;
	/*
	 * threshold of starting new instance
	 */
	public static final long InstanceStartUpThresholdTime = 110000l; // 110 másodperc
	//104, 94 92 106 97 103	

	/*
	 * - One statistic live in this interval
	 * - "Length" of DelayedQueue, with contains the DelayedStatMessages, 
	 * which contains the real TaskMessages
	 * - One statistic interval: we have statistic from this interval
	 */
	//public static final long StatisticConsumerDelay = 600000l; //10 perc
	public static final long StatisticConsumerDelay = 120000l; // 2 perc
	/*
	 * the maximum number of received messages, 
	 * everywhere: taskmessages, statisticmessages etc. 
	 */
	public static final int numberOfMaxReceivedMessage = 10;
	
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
