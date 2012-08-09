package main;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ManagementLogger {

	Logger logger = Logger.getLogger("management");	   
	FileHandler fh;
	
	public ManagementLogger(){
		try {

	    // This block configure the logger with handler and formatter
	    fh = new FileHandler("/home/hokotro/workspace/management.log", true);
	    logger.addHandler(fh);
	    logger.setLevel(Level.ALL);
	    SimpleFormatter formatter = new SimpleFormatter();
	    fh.setFormatter(formatter);

	    // the following statement is used to log any messages   
	    //logger.log(Level.WARNING,"My first log");

		} catch (SecurityException e) {
			e.printStackTrace();
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	}

	public void info(String msg){
		logger.info(msg);
	}
	
	public void warning(String msg){
		logger.warning(msg);
	}
	
	
	
}
