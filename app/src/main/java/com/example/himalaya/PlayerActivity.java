package com.example.himalaya;



import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.himalaya.base.BaseActivity;
import com.example.himalaya.interfaces.IPlayerCallBack;
import com.example.himalaya.presenters.PlayerPresenter;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.text.SimpleDateFormat;
import java.util.List;

public class PlayerActivity extends BaseActivity implements IPlayerCallBack {

    private static final String TAG = "PlayerActivity";
    private ImageView mControlBtn;
    private PlayerPresenter mPlayerPresenter;

    private SimpleDateFormat mMinForMat = new SimpleDateFormat("mm:ss");
    private SimpleDateFormat mHourForMat = new SimpleDateFormat("hh:mm:ss");
    private TextView mTotalDuration;
    //这是UI界面中当前进度的控件
    private TextView mCurrentPosition;
    private SeekBar mDurationBar;

    //用于手动调节进度条时记录进度
    private int mCurrentProgress = 0;
    private boolean mIsUserTouchProgressBar = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        //测试一下播放
        mPlayerPresenter = PlayerPresenter.getPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);
        initView();
        intEvent();
        startPlay();
    }

    /**
     * 开始播放
     */
    private void startPlay() {
        if (mPlayerPresenter != null) {
            mPlayerPresenter.play();
        }
    }

    /**
     * 给控件设置相关的点击事件
     */
    private void intEvent() {
        mControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //若当前为播放，则暂停
                if (mPlayerPresenter.isPlay()){
                    mPlayerPresenter.pause();
                } else {
                    //若当前为暂停，则播放
                    mPlayerPresenter.play();
                }
            }
        });

        mDurationBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean isFromUser) {
                if (isFromUser){
                    mCurrentProgress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsUserTouchProgressBar = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //手离开进度条时更新
                mIsUserTouchProgressBar = false;
                mPlayerPresenter.seekTo(mCurrentProgress);
            }
        });
    }

    /**
     * 找到各个控件
     */
    private void initView() {
        mControlBtn = this.findViewById(R.id.play_or_pause_btn);
        mTotalDuration = this.findViewById(R.id.track_duration);
        mCurrentPosition = this.findViewById(R.id.current_position);
        mDurationBar = this.findViewById(R.id.track_seek_bar);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放资源
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unRegisterViewCallback(this);
            mPlayerPresenter = null;
        }
    }

    @Override
    public void onPlayStart() {
        //开始播放，修改UI层
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.mipmap.stop_normal);
        }
    }

    @Override
    public void onPlayPause() {
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.mipmap.play_normal);
        }
    }

    @Override
    public void onPlayStop() {
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.mipmap.play_normal);
        }
    }

    @Override
    public void onPlayError() {

    }

    @Override
    public void onNextPlay(Track track) {

    }

    @Override
    public void onPrePlay(Track track) {

    }

    @Override
    public void onListLoaded(List<Track> list) {

    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {

    }

    @Override
    public void onProgressChange(int currentDuration, int total) {
        mDurationBar.setMax(total);
        //更新播放进度 更新进度条
        String totalDuration;
        String currentPosition;
        if (total > 1000 * 60 * 60){
            totalDuration = mHourForMat.format(total);
            currentPosition = mHourForMat.format(currentDuration);
        } else {
            totalDuration = mMinForMat.format(total);
            currentPosition = mMinForMat.format(currentDuration);
        }
        if (mTotalDuration != null) {
            mTotalDuration.setText(totalDuration);
        }
        if (mCurrentPosition != null) {
            mCurrentPosition.setText(currentPosition);
        }
        //计算进度
        if (!mIsUserTouchProgressBar) {
            mDurationBar.setProgress(currentDuration);
        }

    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinished() {

    }
}
