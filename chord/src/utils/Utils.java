package utils;

import java.io.IOException;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import chord.ChordManager;

public class Utils {
	
	public static final int TIME_MAX_TO_SLEEP = 400;
	public static final String ENCODING_TYPE = "ISO-8859-1";
	public static final int MAX_LENGTH_CHUNK = 64000;
	public static final int BYTE_TO_KBYTE = 1000;
	
	static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	static {
		logger.setUseParentHandlers(false);
		try {

			logger.addHandler(new FileHandler("AnabelaSilvaLOG.txt", true));
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void randonSleep(int time) throws InterruptedException {
		Random r = new Random();
		Thread.sleep(r.nextInt(time));
	}

	/**
	 * Between two limits, excluding the lower one and including the upper one.
	 * @param inf
	 * @param sup
	 * @param value
	 * @return True if value is inbetween limits.
	 */
	public static boolean inBetween(short inf, short sup, short value) {
		if(sup <= inf) { //procura no meio do circulo todo
			sup  = (short) (sup + Math.pow(2, ChordManager.getM()));
		}
		if(value < inf) {
			value = (short) (value + Math.pow(2, ChordManager.getM()));
		}
		return ((inf < value) && (value <= sup));
		
	}
	
	public static void log(String message) {
		logger.info(message);
	}
	
	
}

  
//FileHandler fh;  
//
//try {  
//
//    // This block configure the logger with handler and formatter  
//    fh = new FileHandler("C:/temp/test/MyLogFile.log");  
//    logger.addHandler(fh);
//    SimpleFormatter formatter = new SimpleFormatter();  
//    fh.setFormatter(formatter);  
//
//    // the following statement is used to log any messages  
//    logger.info("My first log");  
//
//} catch (SecurityException e) {  
//    e.printStackTrace();  
//} catch (IOException e) {  
//    e.printStackTrace();  
//}  

//logger.info("Hi How r u?"); 