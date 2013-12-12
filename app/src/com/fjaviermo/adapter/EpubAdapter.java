package com.fjaviermo.adapter;

import java.util.List;

import android.app.Activity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dropbox.sync.android.DbxFileInfo;
import com.fjaviermo.Utils.Util;
import com.fjaviermo.dropdroid.R;

public class EpubAdapter extends ArrayAdapter<DbxFileInfo> {

	private final List<DbxFileInfo> mEntries;
	private final Activity mActivity;

	static class ViewHolder {
		public TextView epubName;
		public TextView epubDate;
		public TextView epubSize;

	}
	
	public EpubAdapter(Activity activity, List<DbxFileInfo> entries) {
		super(activity, R.layout.epub_row, entries);
		mEntries = entries;
		mActivity = activity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = mActivity.getLayoutInflater();
			rowView = inflater.inflate(R.layout.epub_row, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.epubName = (TextView) rowView.findViewById(R.id.epub_name);
			viewHolder.epubDate = (TextView) rowView.findViewById(R.id.epub_date);
			viewHolder.epubSize = (TextView) rowView.findViewById(R.id.epub_size);
			rowView.setTag(viewHolder);
		}

		ViewHolder holder = (ViewHolder) rowView.getTag();
		
		DbxFileInfo info = mEntries.get(position);
		holder.epubName.setText(info.path.getName());
		String date = DateFormat.getMediumDateFormat(mActivity.getApplicationContext()).format(info.modifiedTime) 
				+ " " + DateFormat.getTimeFormat(mActivity.getApplicationContext()).format(info.modifiedTime);
		holder.epubDate.setText(date);
		holder.epubSize.setText(Util.readableFileSize(info.size));

		return rowView;
	}
}