package com.pabirul.notifyguard;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class LoadingDialogFragment extends DialogFragment {

    private static final String ARG_MESSAGE = "message";
    private String message = "Scanning for Adware...";

    public static LoadingDialogFragment newInstance(String message) {
        LoadingDialogFragment fragment = new LoadingDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    public static LoadingDialogFragment newInstance() {
        return new LoadingDialogFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false); // Prevent user from dismissing by tapping outside
        
        if (getArguments() != null) {
            message = getArguments().getString(ARG_MESSAGE, "Scanning for Adware...");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) {
            // Make dialog background transparent
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            // Remove dialog title
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, 
                             @Nullable ViewGroup container, 
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_loading, container, false);
        TextView loadingText = view.findViewById(R.id.loadingText);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        
        loadingText.setText(message);
        
        return view;
    }
    
    @Override
    public void onStart() {
        super.onStart();
        // Make dialog non-dismissible when touching outside
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            
            // Set dialog dimensions
            if (dialog.getWindow() != null) {
                int width = ViewGroup.LayoutParams.WRAP_CONTENT;
                int height = ViewGroup.LayoutParams.WRAP_CONTENT;
                dialog.getWindow().setLayout(width, height);
            }
        }
    }
    
    // Optional: Method to update message if needed
    public void updateMessage(String newMessage) {
        this.message = newMessage;
        if (getView() != null) {
            TextView loadingText = getView().findViewById(R.id.loadingText);
            if (loadingText != null) {
                loadingText.setText(newMessage);
            }
        }
    }
}