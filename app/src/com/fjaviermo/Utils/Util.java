package com.fjaviermo.Utils;

import java.text.Collator;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Locale;

public class Util {

	public enum SORT { NAME, DATE};
	
	public static String readableFileSize(long size) {
		if(size <= 0) return "0";
		final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}
	
	public static int compareNames(String lname, String rname) {
		Collator c = Collator.getInstance(Locale.getDefault());
		c.setStrength(Collator.SECONDARY); // Case-insensitive

		return c.compare(lname, rname);
	}

	public static int compareDates(Date ldate, Date rdate) {
		return ldate.compareTo(rdate);
	}
}
