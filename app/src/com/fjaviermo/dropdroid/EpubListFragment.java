package com.fjaviermo.dropdroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dropbox.sync.android.DbxAccountManager;

public class EpubListFragment extends ListFragment{


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
	}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                // We are now linked.
                showLinkedView(true);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

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
	}
}