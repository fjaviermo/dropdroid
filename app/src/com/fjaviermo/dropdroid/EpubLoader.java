package com.fjaviermo.dropdroid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxFileSystem.PathListener.Mode;
import com.dropbox.sync.android.DbxPath;
import com.fjaviermo.Utils.DropDroidConfig;
import com.fjaviermo.comparator.EpubNameComparator;

public class EpubLoader extends AsyncTaskLoader<List<DbxFileInfo>> {

	private final DbxPath mPath;
	private final DbxAccountManager mAccountManager;
	private final Comparator<DbxFileInfo> mSortComparator;
	private List<DbxFileInfo> mCachedContents;

	/**
	 * Creates a EpubLoader for the given path.  Defaults to a case-insensitive i18n-aware sort.
	 *
	 * @param context
	 *            Used to retrieve the application context
	 * @param path
	 *            Path of folder to start of load
	 */
	public EpubLoader(Context context, DbxAccountManager accountManager, DbxPath path) {
		this(context, accountManager, path,  new EpubNameComparator(true));
	}

	/**
	 * Creates a EpubLoader for the given path.
	 *
	 * @param context
	 *            Used to retrieve the application context
	 * @param path
	 *            Path of folder to start of load
	 * @param sortComparator
	 *            A comparator for sorting the files before they're
	 *            delivered. May be null for no sort.
	 */
	public EpubLoader(Context context, DbxAccountManager accountManager, DbxPath path, Comparator<DbxFileInfo> sortComparator) {
		super(context);
		mAccountManager = accountManager;
		mPath = path;
		mSortComparator = sortComparator;
	}

	/** a listener that forces a reload when folder contents change */
	private DbxFileSystem.PathListener mChangeListener = new DbxFileSystem.PathListener() {
		@Override
		public void onPathChange(DbxFileSystem fs, DbxPath registeredPath, Mode registeredMode) {
			onContentChanged();
		}
	};

	@Override
	protected void onStartLoading() {
		DbxFileSystem fs = DropDroidConfig.getDbxFileSystem(mAccountManager);
		if (fs != null) {
			fs.addPathListener(mChangeListener, mPath, Mode.PATH_OR_DESCENDANT);
		}
		if (mCachedContents != null) {
			deliverResult(mCachedContents);
		}
		if (takeContentChanged() || mCachedContents == null) {
			forceLoad();
		}
	}

	@Override
	protected void onForceLoad() {
		super.onForceLoad();
	}

	@Override
	protected void onStopLoading() {
		DbxFileSystem fs = DropDroidConfig.getDbxFileSystem(mAccountManager);
		if (fs != null) {
			fs.removePathListener(mChangeListener, mPath, Mode.PATH_OR_DESCENDANT);
		}
		cancelLoad();
	}

	@Override
	protected void onReset() {
		onStopLoading();

		mCachedContents = null;
	}

	@Override
	public void deliverResult(List<DbxFileInfo> data) {
		if (isReset()) {
			// An async result came back after the loader is stopped
			return;
		}

		mCachedContents = data;

		if (isStarted()) {
			super.deliverResult(data);
		}
	}

	@Override
	public List<DbxFileInfo> loadInBackground() {
		DbxFileSystem fs = DropDroidConfig.getDbxFileSystem(mAccountManager);
		if (fs != null) {
			try {
				List<DbxFileInfo> entries = getAllFiles(fs.listFolder(mPath));

				if (mSortComparator != null) {
					Collections.sort(entries, mSortComparator);
				}

				return entries;
			} catch (DbxException e) {
				e.printStackTrace();
			}
		}

		return new ArrayList<DbxFileInfo>(0);
	}

	/**
	 * Creamos una lista con todos los archivos que tiene la cuenta de dropbox
	 * a partir de la lista de archivos y carpetas que se pasa como parámetro
	 * @param listFolder lista de archivos y carpetas de dropbox
	 * @return Lista de todos los archivos
	 * @throws DbxException
	 */
	private List<DbxFileInfo> getAllFiles(List<DbxFileInfo> listFolder) throws DbxException {
		DbxFileSystem fs = DropDroidConfig.getDbxFileSystem(mAccountManager);

		List<DbxFileInfo> files = new ArrayList<DbxFileInfo>();
		for(DbxFileInfo element : listFolder) {
			if(element.isFolder) {
				// Hacemos esto para obtener todos los archivos y no solo
				// los de la carpeta original
				files.addAll(getAllFiles(fs.listFolder(element.path)));
			} else {
				files.add(element);
			}
		}
		return files;
	}
}