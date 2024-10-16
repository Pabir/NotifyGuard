package com.pabirul.notifyguard;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class FullScreenImageDialog extends DialogFragment {

    private static final String ARG_IMAGE = "image";

    public static FullScreenImageDialog newInstance(Bitmap image) {
        FullScreenImageDialog fragment = new FullScreenImageDialog();
        Bundle args = new Bundle();
        args.putParcelable(ARG_IMAGE, image);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fullscreen_image, container, false);
        ImageView imageView = view.findViewById(R.id.imageViewFullScreen);
        if (getArguments() != null) {
            Bitmap image = getArguments().getParcelable(ARG_IMAGE);
            imageView.setImageBitmap(image);
        }
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        return dialog;
    }
}
