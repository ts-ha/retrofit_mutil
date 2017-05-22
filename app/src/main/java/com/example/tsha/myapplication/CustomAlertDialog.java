package com.example.tsha.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;



public class CustomAlertDialog {

    public static class Builder extends AlertDialog.Builder {

        private CharSequence mTitle;
        private boolean mCloseBtnEnabled;

        private AlertDialog mDialog;

        public Builder(Context context) {
            super(context, R.style.AppTheme_CustomDialog);
        }

        public Builder(Context context, int theme) {
            super(context, theme);
        }

        @Override
        public Builder setTitle(int titleId) {
            return setTitle(titleId, false);
        }

        public Builder setTitle(int titleId, boolean closeBtnEnabled) {
            mTitle = getContext().getText(titleId);
            mCloseBtnEnabled = closeBtnEnabled;
            return this;
        }

        @Override
        public Builder setTitle(CharSequence title) {
            return setTitle(title, false);
        }

        public Builder setTitle(CharSequence title, boolean closeBtnEnabled) {
            mTitle = title;
            mCloseBtnEnabled = closeBtnEnabled;
            return this;
        }

        @Override
        public AlertDialog create() {
            if (!TextUtils.isEmpty(mTitle)) {
                View customTitleView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_custom_title, null);
                TextView titleView = (TextView) customTitleView.findViewById(R.id.title);
                if (mCloseBtnEnabled) {
                    customTitleView.findViewById(R.id.close).setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            mDialog.cancel();
                        }
                    });
                } else {
                    customTitleView.findViewById(R.id.close).setVisibility(View.GONE);
                }
                titleView.setText(mTitle);
                setCustomTitle(customTitleView);
            }

            mDialog = super.create();

            mDialog.setOnShowListener(new OnShowListener() {
                public void onShow(DialogInterface dialog) {
                    makeCustom(mDialog);
                }
            });

            return mDialog;
        }

    }

    public static void setCustomTitle(final AlertDialog dialog, String title, boolean closeBtnEnabled) {
        if (!TextUtils.isEmpty(title)) {
            View customTitleView = LayoutInflater.from(dialog.getContext()).inflate(R.layout.dialog_custom_title, null);
            TextView titleView = (TextView) customTitleView.findViewById(R.id.title);
            if (closeBtnEnabled) {
                customTitleView.findViewById(R.id.close).setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
            } else {
                customTitleView.findViewById(R.id.close).setVisibility(View.GONE);
            }
            titleView.setText(title);
            dialog.setCustomTitle(customTitleView);
        }
    }

    public static void setCustomTitle(final AlertDialog dialog, String title) {
        setCustomTitle(dialog, title, false);
    }

    public static void makeCustom(AlertDialog dialog) {
        if (dialog != null) {
            int dividerId = dialog.getContext().getResources()
                    .getIdentifier("titleDivider", "id", "android");
            View divider = dialog.findViewById(dividerId);
            if (divider != null) {
                divider.setVisibility(View.INVISIBLE);
            }

            dividerId = dialog.getContext().getResources()
                    .getIdentifier("buttonPanel", "id", "android");
            divider = dialog.findViewById(dividerId);
            if (divider != null && divider instanceof LinearLayout) {
                ((LinearLayout) divider).setDividerDrawable(null);
            }

        }
    }

}
