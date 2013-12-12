package com.fjaviermo.dropdroid;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class EpubListFragment extends ListFragment{

	private View mEmptyText;
	private View mLinkButton;
	private View mLoadingSpinner;

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

		return view;
	}
}