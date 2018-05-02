package utils;

import chord.ChordManager;

public class Utils {

	public static boolean inBetween(short inf, short sup, short value) {
		if(sup <= inf) { //If Sup = inf procura no meio do circulo todo
			sup  = (short) (sup + Math.pow(2, ChordManager.getM()));
		}
		if(value < inf) {
			value = (short) (value + Math.pow(2, ChordManager.getM()));
		}
		return ((inf < value) && (value <= sup));
		
	}
}
