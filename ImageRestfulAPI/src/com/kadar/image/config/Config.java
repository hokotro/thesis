package com.kadar.image.config;

public class Config {

	public static final String bucketName = "kg-images";
	public static final String privateBucketName = "private-images";
	public static final String statisticMessageQueue = "default-statistic-queue";
	public static final String s3queue = "default-s3-upload-queue";
	public static final String ConverterInstanceImageId = "ami-538f3e3a";
	
	public static final int numberOfMaxReceivedMessage = 10;
	
	/*
	 * - One statistic live in this interval
	 * - "Length" of DelayedQueue, with contains the DelayedStatMessages, 
	 * which contains the real TaskMessages
	 * - One statistic interval: we have statistic from this interval
	 */
	//public static final long StatisticConsumerDelay = 600000l; //10 perc
	//public static final long StatisticConsumerDelay = 10000l; // 10 másodperc
	//public static final long StatisticConsumerDelay = 120000l; // 2 perc
	public static final long StatisticConsumerDelay = 60000l; // 1 perc
	/*
	 * one statistic live int this time for graph
	 */
	//public static final long StatisticConsumerDelayForGraph = 600000l; //10 perc
	//public static final long StatisticConsumerDelayForGraph = 120000l; // 2 perc
	public static final long StatisticConsumerDelayForGraph = 60000l; // 1 perc
	
	
	
	
	/*
	 * the address of the restful API, entrypoint of application
	 */
	public static String RestfulAPIInstanceAddress = "ec2-54-242-2-190.compute-1.amazonaws.com";
	/*
	 * the path of the directory, which contains a lot of images to test the application
	 */
	//public static final String ImageGaleryPath = "/home/hokotro/thesis_galery/";
	public static final String ImageGaleryPath = "/home/ubuntu/galery1/";	
	/*
	 * mérési eredmény: 
	 * - ha 1.0 vagy annál kisebb, akkor nagyobb a szórás, 
	 * van benne egy-két kirívóan magas érték ami nekünk jelen esetben nem megfelelő
	 * - ha 3, 5 vagy nagyobb, akkor meg túl közeli értékeket kapunk, 
	 * és ebben a nagyon kicsi intervallumhoz képes nem kapunk szignifikáns eltéréseket
	 */
	public static final double ParetoAlpha = 3.0;
}
