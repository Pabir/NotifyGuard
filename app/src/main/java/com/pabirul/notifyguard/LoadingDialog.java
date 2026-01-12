package com.pabirul.notifyguard;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class LoadingDialog extends Dialog {

    private String loadingText;

    public LoadingDialog(@NonNull Context context) {
        super(context);
        this.loadingText = "Scanning for adware...";
    }

    public LoadingDialog(@NonNull Context context, String loadingText) {
        super(context);
        this.loadingText = loadingText;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_loading);
        
        // Set dialog properties
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, 
                            WindowManager.LayoutParams.WRAP_CONTENT);
            window.setDimAmount(0.7f); // Dim background
        }
        
        // Set loading text
        TextView loadingTextView = findViewById(R.id.loadingText);
        if (loadingText != null && !loadingText.isEmpty()) {
            loadingTextView.setText(loadingText);
        }
        
        // Prevent cancel by back button or touch outside
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }
    
    public void updateMessage(String message) {
        TextView loadingTextView = findViewById(R.id.loadingText);
        if (loadingTextView != null && message != null) {
            loadingTextView.setText(message);
        }
    }
}