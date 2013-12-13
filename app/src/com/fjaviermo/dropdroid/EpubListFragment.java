package com.fjaviermo.dropdroid;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;
import com.fjaviermo.Utils.DropDroidConfig;
import com.fjaviermo.Utils.Util;
import com.fjaviermo.Utils.Util.SORT;
import com.fjaviermo.adapter.EpubAdapter;
import com.fjaviermo.adapter.EpubAdapter.ObtainCoverImageListener;
import com.fjaviermo.comparator.EpubDateComparator;
import com.fjaviermo.comparator.EpubNameComparator;


public class EpubListFragment extends ListFragment implements LoaderCallbacks<List<DbxFileInfo>>, 
ObtainCoverImageListener {

	private View mEmptyText;
	private View mLinkButton;
	private View mLoadingSpinner;
	private DbxAccountManager mAccountManager;
	private SORT mSort= Util.SORT.NAME;

	public EpubListFragment() {}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.epub_list, container, false);

		mEmptyText = view.findViewById(R.id.empty_text);
		mLinkButton = view.findViewById(R.id.link_button);
		mLoadingSpinner = view.findViewById(R.id.list_loading);

		mLinkButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mAccountManager.startLink(EpubListFragment.this, 0);
			}
		});

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		getListView().setEmptyView(view.findViewById(android.R.id.empty));

		if (!mAccountManager.hasLinkedAccount()) {
			showUnlinkedView();
		} else {
			showLinkedView(false);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		doLoad(false);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mAccountManager = DropDroidConfig.getAccountManager(activity);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.epub_list, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// handle item selection
		switch (item.getItemId()) {
		case R.id.order_by_name:
			mSort = Util.SORT.NAME;
			doLoad(true);
			return true;
		case R.id.order_by_date:
			mSort = Util.SORT.DATE;
			doLoad(true);
			return true;
		case R.id.unlink_dropbox:
			mAccountManager.unlink();
			setListAdapter(null);
			showUnlinkedView();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {
			if (resultCode == Activity.RESULT_OK) {
				showLinkedView(true);
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public Loader<List<DbxFileInfo>> onCreateLoader(int id, Bundle args) {
		Comparator<DbxFileInfo> sortComparator = null;
		switch (mSort) {
		case NAME:
			sortComparator = new EpubNameComparator(true);
			break;
		case DATE:
			sortComparator = new EpubDateComparator(true);
			break;
		}
		return new EpubLoader(getActivity(), mAccountManager, DbxPath.ROOT, sortComparator);
	}

	@Override
	public void onLoadFinished(Loader<List<DbxFileInfo>> loader, List<DbxFileInfo> data) {
		mLoadingSpinner.setVisibility(View.GONE);
		mEmptyText.setVisibility(View.VISIBLE);

		setListAdapter(new EpubAdapter(getActivity(), data,this));
	}

	@Override
	public void onLoaderReset(Loader<List<DbxFileInfo>> loader) {}

	private void showUnlinkedView() {
		getListView().setVisibility(View.GONE);
		mEmptyText.setVisibility(View.GONE);
		mLoadingSpinner.setVisibility(View.GONE);
		mLinkButton.setVisibility(View.VISIBLE);
		getView().postInvalidate();
	}

	private void showLinkedView(boolean reset) {
		getListView().setVisibility(View.VISIBLE);
		mEmptyText.setVisibility(View.GONE);
		mLoadingSpinner.setVisibility(View.VISIBLE);
		mLinkButton.setVisibility(View.GONE);
		getView().postInvalidate();
		doLoad(reset);
	}

	private void doLoad(boolean reset) {
		if (mAccountManager.hasLinkedAccount()) {
			mEmptyText.setVisibility(View.GONE);
			mLoadingSpinner.setVisibility(View.VISIBLE);

			if (reset) {
				getLoaderManager().restartLoader(0, null, this);
			} else {
				getLoaderManager().initLoader(0, null, this);
			}
		}
	}

	public void showDialog(Bitmap coverImage) {
		DialogFragment coverImageDialog = CoverImageDialogFragment.newInstance(coverImage);
		coverImageDialog.show(getActivity().getSupportFragmentManager(), null);		
	}

	@Override
	public void ObtainCoverImage(int position) {
		DbxFileInfo file = (DbxFileInfo) getListAdapter().getItem(position);
		ObtainCoverImage obtainImage = new ObtainCoverImage();
		obtainImage.execute(file.path);
	}
	
	private class ObtainCoverImage extends AsyncTask<DbxPath, Long, Bitmap> 
	{
	    private ProgressDialog dialog = new ProgressDialog(getActivity());

		@Override
		protected void onPreExecute() {
	        this.dialog.setMessage(getString(R.string.please_wait));
	        this.dialog.show();
		}

		@Override
		protected Bitmap doInBackground(DbxPath... params) {
			Bitmap coverImage = null;
			try {

				DbxFileSystem fs = DropDroidConfig.getDbxFileSystem(mAccountManager);
				DbxFile file = fs.open(params[0]);

				InputStream epubInputStream = file.getReadStream();
				// Load Book from inputStream
				Book book = (new EpubReader()).readEpub(epubInputStream);

				coverImage  = BitmapFactory.decodeStream(book.getCoverImage().getInputStream());
				file.close();
			} catch (IOException e) {
				Log.e("epublib", e.getMessage());
			}
			return coverImage;
		}
		@Override
		protected void onPostExecute(Bitmap result) {
	        if (dialog.isShowing()) {
	            dialog.dismiss();
	        }

			showDialog(result);
		}
	}
}