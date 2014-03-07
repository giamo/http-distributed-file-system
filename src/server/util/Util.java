package server.util;

import java.util.LinkedList;

/*
 * Classe con alcuni metodi di supporto per l'applicazione
 */
public class Util {
	public static String finePath(String path, String pathSeparator) {
		String ris = "";
		char separator;
		if (pathSeparator != null)
			separator = pathSeparator.charAt(0);
		else return null;
		int l = path.length();
		for (int i = l-1; i >=0; i--)
			if (path.charAt(i) == separator) {
				ris = path.substring(i+1);
				break;
			}
		return ris;		
	}
	
	public static String inizioPath(String path, String pathSeparator) {
		String ris = "";
		char separator;
		if (pathSeparator != null)
			separator = pathSeparator.charAt(0);
		else return null;
		int l = path.length();
		for (int i = l-1; i >= 0; i--)
			if (path.charAt(i) == separator) {
				ris = path.substring(0, i);
				break;
			}
		return ris;		
	}
	
	/* It returns a LinkedList of all subpaths in a path, starting from the longest one (the path itself) */
	public static LinkedList<String> deconstructPath(String path, String pathSeparator) {
		char separator;
		if (pathSeparator != null)
			separator = pathSeparator.charAt(0);
		else return null;
		LinkedList<String> res = new LinkedList<String>();
		
		for (int i = 1; i < path.length(); i++) {
			if (path.charAt(i) == separator)
				res.add(path.substring(0, i));
			
			if (i == path.length()-1 && path.charAt(i) != separator)
				res.add(path);
		}
		
		return res;
				
	}
	
	public static String formatDirectory(String path, String pathSeparator) {
		char separator;
		if (pathSeparator != null)
			separator = pathSeparator.charAt(0);
		else return null;
		
		if (path.charAt(path.length()-1) == separator)
			return path.substring(0, path.length()-1);
		else return path;
	}
	
	public static String formatSize(long size) {
		double d_size = size;
		int i;
		for (i = 0; i < 4; i++) {
			if (d_size/1024 < 1)
				break;
			else d_size/= 1024;
			
		}
		String ris = String.format("%.1f", d_size);
		switch(i) {
			case 0: ris+=" bytes"; break;
			case 1: ris+=" KB"; break;
			case 2: ris+=" MB"; break;
			case 3: ris+=" GB"; break;
			case 4: ris+=" TB"; break;
		}
		return ris;
	}
}
