package metric;


public class Config{
	/*
	 * id of the converter machine image 
	 */
	public static final String ConverterInstanceImageId = "ami-856cc7ec";
	/*
	 * Amazon Instance type in this application to small instance type
	 */
	public static final String smallInstanceType = "t1.micro"; //"m1.small";
	/*
	 * Amazon Instance type in this application to medium instance type
	 */
	public static final String mediumInstanceType = "t1.micro"; //"m1.small";
	/*
	 * Amazon Instance type in this application to large instance type
	 */
	public static final String largeInstanceType = "t1.micro"; //"m1.small";
	/*
	 * S3 queue
	 */
	public static final String S3QUEUE = "default-s3-upload-queue";
	/*
	 * Queue to share Statistic, StatisticMessages
	 */
	public static final String STATISTICQUEUE = "default-statistic-queue";
	/*
	 * - One statistic live in this interval
	 * - "Length" of DelayedQueue, with contains the DelayedStatMessages, 
	 * which contains the real TaskMessages
	 * - One statistic interval: we have statistic from this interval
	 */
	public static final long StatisticConsumerDelay = 600000l; //10 perc
	/*
	 * the maximum number of received messages, 
	 * everywhere: taskmessages, statisticmessages etc. 
	 */
	public static final int numberOfMaxReceivedMessage = 10;
	
}