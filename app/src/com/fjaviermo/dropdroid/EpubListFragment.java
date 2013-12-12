package com.fjaviermo.dropdroid;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxPath;
import com.fjaviermo.Utils.DropDroidConfig;
import com.fjaviermo.adapter.EpubAdapter;

public class EpubListFragment extends ListFragment implements LoaderCallbacks<List<DbxFileInfo>>{


	private View mEmptyText;
	private View mLinkButton;
	private View mLoadingSpinner;
	private DbxAccountManager mAccountManager;

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
		return new EpubLoader(getActivity(), mAccountManager, DbxPath.ROOT);
	}

	@Override
	public void onLoadFinished(Loader<List<DbxFileInfo>> loader, List<DbxFileInfo> data) {
		mLoadingSpinner.setVisibility(View.GONE);
		mEmptyText.setVisibility(View.VISIBLE);

		setListAdapter(new EpubAdapter(getActivity(), data));
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
}