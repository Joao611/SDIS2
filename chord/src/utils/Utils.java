package utils;

import chord.ChordManager;

public class Utils {

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
}
