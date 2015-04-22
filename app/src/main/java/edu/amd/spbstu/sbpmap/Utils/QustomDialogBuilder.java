package edu.amd.spbstu.sbpmap.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import edu.amd.spbstu.sbpmap.R;

/**
 * Created by JDima on 22/04/15.
 */
public class QustomDialogBuilder extends AlertDialog.Builder{

    private View mDialogView;

    private TextView mTitle;

    private ImageView mIcon;

    private TextView mMessage;

    public QustomDialogBuilder(Context context, boolean isTable) {
        super(context);

        if (isTable) {
            mDialogView = View.inflate(context, R.layout.qustom_table, null);
        } else {
            mDialogView = View.inflate(context, R.layout.qustom_dialog_layout, null);
        }
        setView(mDialogView);

        mTitle = (TextView) mDialogView.findViewById(R.id.alertTitle);
        mMessage = (TextView) mDialogView.findViewById(R.id.message);
        mIcon = (ImageView) mDialogView.findViewById(R.id.icon);
    }

    @Override
    public QustomDialogBuilder setTitle(CharSequence text) {
        mTitle.setText(text);
        return this;
    }


    @Override
    public QustomDialogBuilder setMessage(int textResId) {
        mMessage.setText(textResId);
        return this;
    }

    @Override
    public QustomDialogBuilder setMessage(CharSequence text) {
        mMessage.setText(text);
        return this;
    }

    @Override
    public QustomDialogBuilder setIcon(int drawableResId) {
        mIcon.setImageResource(drawableResId);
        return this;
    }

    @Override
    public QustomDialogBuilder setIcon(Drawable icon) {
        mIcon.setImageDrawable(icon);
        return this;
    }

    public QustomDialogBuilder setCustomView(int resId, Context context) {
        View customView = View.inflate(context, resId, null);
        ((FrameLayout)mDialogView.findViewById(R.id.customPanel)).addView(customView);
        return this;
    }

    @Override
    public AlertDialog show() {
        if (mTitle.getText().equals("")) mDialogView.findViewById(R.id.topPanel).setVisibility(View.GONE);
        return super.show();
    }

}
