package metric;

import java.util.concurrent.Callable;
import java.util.concurrent.DelayQueue;

import com.kadar.image.message.handler.DelayedStatMessage;

public class MetricCallable implements Callable<Boolean> {

	DelayQueue<DelayedStatMessage> InstancesStat = new DelayQueue<DelayedStatMessage>();
	float metric;
	
	public MetricCallable(DelayQueue<DelayedStatMessage> InstanceStat, float metric){
		this.InstancesStat = InstanceStat;
		this.metric = metric;
	}
	
	@Override
	public Boolean call() throws Exception {
		//System.out.println("Stat: " + metric);			
		if( InstancesStat.size() > Metric.stagnation){ 				
			float[] statistic = getRawStatistic(InstancesStat);		
		
			return calculate(statistic, metric);
		}
		return false;
	}	
	
	
	/*
	 * megszámolom mennyi lépte túl a köszöböt, ha ez több mint a 10% akkor true
	 */
	private static boolean calculate(float[] values, float threshold){
		int piecesOfThreshold = 0;
		for(float val: values){
			if(val > threshold ) piecesOfThreshold++; 
		}
				
		float a = values.length * 0.1f;
		return (piecesOfThreshold > a) ? true : false; 
	}
	
	/*
	 * get raw statistic from DelayMessageQueue contained StatMessages to an array of float
	 */
	private static float[] getRawStatistic( DelayQueue<DelayedStatMessage> instancesStat ){
		Object[] instancesStatistic = instancesStat.toArray();
		float[] statistic = new float[ instancesStatistic.length ];
		int i=0;
		for(Object obj: instancesStatistic){
			//System.out.println((DelayedStatMessage)obj);
			statistic[i] = ((DelayedStatMessage)obj).getStatisticMessage().getTime();
			i++;
		}
		return statistic;
	}


}
