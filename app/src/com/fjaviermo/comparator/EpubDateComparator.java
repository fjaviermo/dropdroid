package com.fjaviermo.comparator;

import java.util.Comparator;

import com.dropbox.sync.android.DbxFileInfo;
import com.fjaviermo.Utils.Util;

public class EpubDateComparator implements Comparator<DbxFileInfo> {
	private final boolean isAscending;

	public EpubDateComparator(boolean ascending) {
		isAscending = ascending;
	}

	@Override
	public int compare(DbxFileInfo lhs, DbxFileInfo rhs) {
		int rawCmp = rawCompare(lhs, rhs);
		return isAscending ? rawCmp : -rawCmp;
	}

	/**
	 * Comparamos los archivos según su fecha, nombre y tamaño.
	 */
	int rawCompare(DbxFileInfo lhs, DbxFileInfo rhs) {

		int cmp = Util.compareDates(lhs.modifiedTime, rhs.modifiedTime);
		if (0 != cmp) {
			return cmp;
		}
		cmp = Util.compareNames(lhs.path.getName(), rhs.path.getName());
		if (0 != cmp) {
			return cmp;
		}

		// Use size as final qualifier, though names should be unique in a real
		// folder listing.
		long longcmp = Util.compareSizes(lhs.size, rhs.size);
		if (0 != longcmp) {
			return longcmp < 0 ? -1 : 1;
		}

		return 0;
	}
}