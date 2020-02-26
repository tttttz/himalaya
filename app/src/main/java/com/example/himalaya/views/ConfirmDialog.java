package com.example.himalaya.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.himalaya.R;

public class ConfirmDialog extends Dialog {

    private View mCancelSub;
    private View mGiveUp;
    private OnDialogActionClickListener mClickListener = null;

    public ConfirmDialog(Context context) {
        this(context, 0);
    }

    public ConfirmDialog(Context context, int themeResId) {
        this(context, true, null);
    }

    protected ConfirmDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_comfirm);
        initView();
        initListener();
    }

    private void initListener() {
        mGiveUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onGiveUpClick();
                    dismiss();
                }
            }
        });

        mCancelSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onCancelSubClick();
                    dismiss();
                }
            }
        });
    }

    private void initView() {
        mCancelSub = this.findViewById(R.id.dialog_cancel_sub_tv);
        mGiveUp = this.findViewById(R.id.dialog_give_up_tv);
    }

    public void setOnDialogActionClickListener(OnDialogActionClickListener listener){
        mClickListener = listener;
    }

    public interface OnDialogActionClickListener{
        void onCancelSubClick();
        void onGiveUpClick();
    }
}
