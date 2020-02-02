package com.example.himalaya;



import android.os.Bundle;

import com.example.himalaya.base.BaseActivity;
import com.example.himalaya.presenters.PlayerPresenter;

public class PlayerActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        //测试一下播放
        PlayerPresenter playerPresenter = PlayerPresenter.getPlayerPresenter();
        playerPresenter.play();
    }
}
