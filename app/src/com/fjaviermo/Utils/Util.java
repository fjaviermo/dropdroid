package com.fjaviermo.Utils;

import java.text.Collator;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Locale;

public class Util {

	public enum SORT { NAME, DATE};

	/**
	 * Devuelve el nombre de un archivo sin extension, si la extension
	 * es la correcta
	 */
	public static String getBaseName(String extension, String filename) {
		extension = "." + extension;
		if (filename.endsWith(extension)) {
			return filename.substring(0, filename.length() - extension.length());
		}
		return filename;
	}

	/**
	 * Devuelve el tamaño de una archivo en formato legible 
	 */
	public static String readableFileSize(long size) {
		if(size <= 0) return "0";
		final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

	/**
	 * Devuelve un valor negativo si lname es menor que rname,
	 * 0 si son iguales y un valor positivo si lname es mayor.
	 */
	public static int compareNames(String lname, String rname) {
		Collator c = Collator.getInstance(Locale.getDefault());
		c.setStrength(Collator.SECONDARY); // Case-insensitive

		return c.compare(lname, rname);
	}

	/**
	 * Un entero < 0 si ldate es mejor que rname, 0 son iguales, 
	 * y un entero > 0 si ldate es mayor.
	 */
	public static int compareDates(Date ldate, Date rdate) {
		return ldate.compareTo(rdate);
	}

	/**
	 * Devuelve la diferencia entre lsize y rsize.
	 */
	public static long compareSizes(long lsize, long rsize) {
		return lsize - rsize;
	}
}
