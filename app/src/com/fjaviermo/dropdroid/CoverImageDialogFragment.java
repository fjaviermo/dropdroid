package com.fjaviermo.dropdroid;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public final class CoverImageDialogFragment extends DialogFragment {
    private Bitmap mImage;
    private static final String IMAGE="image";
    
    /**
     * Create a new instance of CoverImageDialogFragment, providing "image"
     * as an argument.
     */
    static CoverImageDialogFragment newInstance(Bitmap image) {
        CoverImageDialogFragment coverImageDialog = new CoverImageDialogFragment();

        Bundle args = new Bundle();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        args.putByteArray(IMAGE, byteArray);
        coverImageDialog.setArguments(args);
        coverImageDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);        
        return coverImageDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        byte[] byteArray = getArguments().getByteArray(IMAGE);
        mImage = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_dialog, container, false);
		ImageView imgView=(ImageView)view.findViewById(R.id.thumbnail_epub);
		imgView.setImageBitmap(mImage);
        return view;
    }
}
