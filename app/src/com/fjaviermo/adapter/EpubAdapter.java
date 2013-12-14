package com.fjaviermo.adapter;

import java.util.List;

import android.app.Activity;
import android.text.format.DateFormat;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dropbox.sync.android.DbxFileInfo;
import com.fjaviermo.Utils.Util;
import com.fjaviermo.dropdroid.R;

public class EpubAdapter extends ArrayAdapter<DbxFileInfo>{

	private final List<DbxFileInfo> mEntries;
	private final Activity mActivity;
	GestureDetector mGestureDetector;
	ObtainCoverImageListener mListener;

	static class ViewHolder {
		public TextView epubName;
		public TextView epubDate;
		public TextView epubSize;

	}

	public EpubAdapter(Activity activity, List<DbxFileInfo> entries, ObtainCoverImageListener callback) {
		super(activity, R.layout.epub_row, entries);
		mEntries = entries;
		mActivity = activity;
		mListener = callback;
		mGestureDetector = new GestureDetector(activity.getApplicationContext(), new GestureListener());
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = mActivity.getLayoutInflater();
			rowView = inflater.inflate(R.layout.epub_row, null);
			ViewHolder viewHolder = new ViewHolder();

			rowView.setOnTouchListener( new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					boolean isDoubleTap = mGestureDetector.onTouchEvent(event);
					if(isDoubleTap) {
						mListener.ObtainCoverImage(position);
					}

					return true;
				}
			});
			
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

	public interface ObtainCoverImageListener {
		void ObtainCoverImage(int position);
	}

	private class GestureListener extends GestureDetector.SimpleOnGestureListener {

		// event when double tap occurs
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			return true;
		}
	}
}