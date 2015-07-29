package com.cocosw.accessory.utils;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.os.Build;
import android.text.ClipboardManager;

/**
 * Project: Accessory
 * Created by LiaoKai(soarcn) on 2015/7/29.
 */
public class ClipboardUtils {

    /**
     * copy text to clipboard
     */
    @SuppressLint("NewApi")
    public static void copyToClipboard(Context ctx, String text) {
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < Build.VERSION_CODES.HONEYCOMB) {
            ClipboardManager clipboardManager = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboardManager.setText(text);
        } else {
            android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
            // clipboardManager.setText(view.getText()); // Deprecated
            ClipData clip = ClipData.newPlainText(null, text);
            clipboardManager.setPrimaryClip(clip);
        }
    }

    /**
     * paste text from clipboard
     */
    @SuppressLint("NewApi")
    public static String pasteFromClipboard(Context ctx) {
        String text = null;
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < Build.VERSION_CODES.HONEYCOMB) {
            ClipboardManager clipboardManager = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboardManager.hasText())
                text = clipboardManager.getText().toString();
        } else {
            android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboardManager.hasPrimaryClip()) {
                CharSequence pasteData = clipboardManager.getPrimaryClip().getItemAt(0).getText();
                if (pasteData != null)
                    text = pasteData.toString();
            }
        }
        return text;
    }
}
