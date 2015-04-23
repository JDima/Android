package edu.amd.spbstu.sbpmap.Utils;

import android.app.AlertDialog;

import android.content.Context;
import android.graphics.drawable.Drawable;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import edu.amd.spbstu.sbpmap.R;

/**
 * Created by JDima on 22/04/15.
 */
public class QustomProgressDialog extends AlertDialog.Builder {

    View mDialogView;

    private TextView mTitle;
    private TextView mMessage;
    private ImageView mIcon;
    ProgressBar mProgress;

    public QustomProgressDialog(Context context, int style) {
        super(context);

        mDialogView = View.inflate(context, R.layout.qustom_progress_bar, null);
        setView(mDialogView);

        mProgress = (ProgressBar) mDialogView.findViewById(R.id.progress_bar);
        mTitle = (TextView) mDialogView.findViewById(R.id.progress_dialog_title);
        mMessage = (TextView) mDialogView.findViewById(R.id.progress_bar_msg);
        mIcon = (ImageView) mDialogView.findViewById(R.id.pb_icon);
        mProgress.setScrollBarStyle(style);
        setCancelable(false);
        //mProgressBar = View.inflate(context, R.id.progress_bar, null)
    }

    @Override
    public AlertDialog show() {
        if (mTitle.getText().equals("")) mDialogView.findViewById(R.id.topPanel).setVisibility(View.GONE);
        return super.show();
    }

    @Override
    public QustomProgressDialog setTitle(CharSequence text) {
        mTitle.setText(text);
        return this;
    }


    @Override
    public QustomProgressDialog setMessage(int textResId) {
        mMessage.setText(textResId);
        return this;
    }

    @Override
    public QustomProgressDialog setMessage(CharSequence text) {
        mMessage.setText(text);
        return this;
    }

    @Override
    public QustomProgressDialog setIcon(int drawableResId) {
        mIcon.setImageResource(drawableResId);
        return this;
    }

    @Override
    public QustomProgressDialog setIcon(Drawable icon) {
        mIcon.setImageDrawable(icon);
        return this;
    }

    public QustomProgressDialog setProgressStyle(int style) {
        mProgress.setScrollBarStyle(style);
        return this;
    }

    public QustomProgressDialog setMax(int max) {
        mProgress.setMax(max);
        return this;
    }

    public QustomProgressDialog setProgress(int progress) {
        mProgress.setProgress(progress);
        return this;
    }

    public QustomProgressDialog setIndeterminate(boolean indeterminate) {
        mProgress.setIndeterminate(indeterminate);
        return this;
    }

    public QustomProgressDialog incrementProgressBy(int inc) {
        //mProgress.incrementProgressBy(inc);
        return this;
    }
}
