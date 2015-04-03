package com.sbpmap.Utils;

import android.widget.TextView;

/**
 * Created by JDima on 03/04/15.
 */
public class TextViewUtil {
    public static void setTextViewText(TextView tv, CharSequence cs) {
        tv.setText(cs);
        int height_in_pixels = tv.getLineCount() * tv.getLineHeight();
        tv.setHeight(height_in_pixels + 10);
    }
}
