package com.pabirul.notifyguard;

import android.app.Dialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;

public class AppPermissionsDialog extends DialogFragment {

    private static final String ARG_PACKAGE_NAME = "package_name";
    private static final String ARG_APP_NAME = "app_name";
    
    private String packageName;
    private String appName;
    private PackageManager packageManager;

    public static AppPermissionsDialog newInstance(String packageName, String appName) {
        AppPermissionsDialog fragment = new AppPermissionsDialog();
        Bundle args = new Bundle();
        args.putString(ARG_PACKAGE_NAME, packageName);
        args.putString(ARG_APP_NAME, appName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            packageName = getArguments().getString(ARG_PACKAGE_NAME);
            appName = getArguments().getString(ARG_APP_NAME);
        }
        packageManager = requireContext().getPackageManager();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_app_permissions, null);
        
        TextView appNameTextView = view.findViewById(R.id.appNameTextView);
        TextView titleTextView = view.findViewById(R.id.titleTextView);
        LinearLayout permissionsContainer = view.findViewById(R.id.permissionsContainer);
        Button okButton = view.findViewById(R.id.okButton);
        
        appNameTextView.setText(appName);
        titleTextView.setText(appName + " Permissions");
        
        // Load and display permissions
        List<String> permissions = getAppPermissions(packageName);
        
        if (permissions.isEmpty()) {
            TextView noPermissionsText = new TextView(getContext());
            noPermissionsText.setText("No permissions requested");
            noPermissionsText.setPadding(8, 8, 8, 8);
            permissionsContainer.addView(noPermissionsText);
        } else {
            for (String permission : permissions) {
                TextView permissionView = createPermissionTextView(permission);
                permissionsContainer.addView(permissionView);
            }
        }
        
        okButton.setOnClickListener(v -> dismiss());
        
        builder.setView(view);
        return builder.create();
    }

    private List<String> getAppPermissions(String packageName) {
        List<String> permissions = new ArrayList<>();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            if (packageInfo.requestedPermissions != null) {
                for (String permission : packageInfo.requestedPermissions) {
                    // Format the permission name for better readability
                    String formattedPermission = formatPermissionName(permission);
                    permissions.add(formattedPermission);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to load permissions", Toast.LENGTH_SHORT).show();
        }
        return permissions;
    }

    private String formatPermissionName(String permission) {
        // Remove android.permission. prefix and replace underscores with spaces
        String formatted = permission.replace("android.permission.", "");
        formatted = formatted.replace("com.google.android.gms.permission.", "Google ");
        formatted = formatted.replace("com.android.", "");
        formatted = formatted.replace("_", " ");
        formatted = formatted.toLowerCase();
        
        // Capitalize first letter of each word
        String[] words = formatted.split(" ");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                      .append(word.substring(1))
                      .append(" ");
            }
        }
        return result.toString().trim();
    }

    private TextView createPermissionTextView(String permission) {
        TextView textView = new TextView(getContext());
        textView.setText("• " + permission);
        textView.setPadding(8, 4, 8, 4);
        textView.setTextSize(14);
        return textView;
    }
}