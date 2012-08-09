package main;
import java.io.File;
import java.io.IOException;

import umontreal.iro.lecuyer.randvar.ParetoGen;
import umontreal.iro.lecuyer.rng.LFSR113;
import umontreal.iro.lecuyer.rng.RandomStream;

import edu.uprm.cga.ininsim.simpack.utils.ParetoGenerator;


public class Curl implements Runnable {
	String path = ".";
	
	public Curl(){
		path = Config.ImageGaleryPath;
	}
	
	@Override
	public void run() { 
		
		/*
		 * simple Pareto generator
		ParetoGenerator pareto = new ParetoGenerator(3.0);
		for (int i = 0; i < 10; i++) {
			double p = pareto.generate();
			// ??
			//p = p * 1000;
			long pl = (new Double(p)).longValue();
			System.out.println( pl );
		}
		*/
		
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		String files[] = new String[listOfFiles.length];

		//for (int i = 0; i < listOfFiles.length; i++) 
		for (int i = 0; i < listOfFiles.length; i++) 
		{			 
			if (listOfFiles[i].isFile()) 
				files[i] = listOfFiles[i].getName();
		}

		RandomStream random = new LFSR113();
		ParetoGen pareto = new ParetoGen(random, Config.ParetoAlpha);
		for(int i = 0; i < listOfFiles.length; i++){
			double p = pareto.nextDouble();
		
			System.out.print("Pareto value: " + p );
			p = p * 1000;
			//p = p * 100;
			long pl = (new Double(p)).longValue();
			System.out.println(", " + p );

			String command = "curl -T " + path + files[i] + "  " + Config.InstanceAddress + ":8080/ImageRestfulService/rest/imageservice/putFile/" + files[i];
			System.out.println(command);
			
			try {
				Thread.sleep( pl );
			
				Process process = Runtime.getRuntime().exec(command);	
			} catch (IOException e) {
					e.printStackTrace();
			
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
				
		}
	}
}
