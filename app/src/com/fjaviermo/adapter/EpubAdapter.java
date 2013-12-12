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
	private final Activity mContext;

	static class ViewHolder {
		public TextView epubName;
		public TextView epubDate;
		public TextView epubSize;

	}
	
	public EpubAdapter(Activity context, List<DbxFileInfo> entries) {
		super(context, R.layout.epub_row, entries);
		mEntries = entries;
		mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = mContext.getLayoutInflater();
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
		String date = DateFormat.getMediumDateFormat(mContext).format(info.modifiedTime) + " " + 
				    DateFormat.getTimeFormat(mContext).format(info.modifiedTime);
		holder.epubDate.setText(date);
		holder.epubSize.setText(Util.readableFileSize(info.size));

		return rowView;
	}
}