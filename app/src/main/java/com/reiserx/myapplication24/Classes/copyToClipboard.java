package com.reiserx.myapplication24.Classes;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

public class copyToClipboard {
    public void copy(String data, Context context) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("", data);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, data+" copied to clipboard", Toast.LENGTH_SHORT).show();
    }
}
