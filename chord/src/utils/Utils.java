package utils;

import java.util.Random;

import chord.ChordManager;

public class Utils {
	
	public static final int TIME_MAX_TO_SLEEP = 400;
	public static final String ENCODING_TYPE = "ISO-8859-1";
	public static final int MAX_LENGTH_CHUNK = 64000;
	public static final int BYTE_TO_KBYTE = 1000;
	
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
		
	}
}
