package com.example.himalaya.views;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.example.himalaya.R;
import com.example.himalaya.base.BaseApplication;

public class SobPopWindow extends PopupWindow {

    private final View mPopView;
    private View mCloseBtn;

    public SobPopWindow(){
        //设置宽高
        super(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOutsideTouchable(true);
        //载入View
        mPopView = LayoutInflater.from(BaseApplication.getAppContext()).inflate(R.layout.pop_play_list, null);
        //设置内容
        setContentView(mPopView);
        //设置窗口弹出和关闭的动画
        setAnimationStyle(R.style.pop_animation);

        initView();
        initEvent();
    }

    private void initView(){
        mCloseBtn = mPopView.findViewById(R.id.play_list_close_btn);
    }

    private void initEvent() {
        //点击后消失
        mCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SobPopWindow.this.dismiss();
            }
        });
    }
}
