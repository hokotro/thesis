package main;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageTestSample {

	private static final int NUMBEROFTHREAD = 1;
	//private static final int NUMBEROFTHREAD = 1;
	
	public static void main(String args[]) throws IOException, InterruptedException{

		ExecutorService executor = Executors.newFixedThreadPool(NUMBEROFTHREAD);
		int i = 0;
		while(i < 1) {
			/*
			 * a Curl run() metódusában van egy sleep(1000) !!
			 * vagy Pareto
			 */
			Runnable runnable = new Curl();
			executor.execute(runnable);
			i++;
		}
		
		// This will make the executor accept no new threads
		// and finish all existing threads in the queue		
		executor.shutdown();
		// Wait until all threads are finish
		while (!executor.isTerminated()) {
			//System.out.println("Waiting for all threads ...");
			//Thread.sleep(1000);
		}
		System.out.println("Finished all threads");    		
	}
}