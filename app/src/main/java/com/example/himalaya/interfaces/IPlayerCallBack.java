package com.example.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

public interface IPlayerCallBack {

    /**
     * 开始播放
     */
    void onPlayStart();

    /**
     * 播放暂停
     */
    void onPlayPause();

    /**
     * 播放停止
     */
    void onPlayStop();

    /**
     * 播放错误
     */
    void onPlayError();

    /**
     * 播放下一首
     */
    void onNextPlay(Track track);

    /**
     * 播放上一首
     */
    void onPrePlay(Track track);

    /**
     * 播放数据加载完成
     * @param list
     */
    void onListLoaded(List<Track> list);

    /**
     * 播放模式改变
     * @param playMode
     */
    void onPlayModeChange(XmPlayListControl.PlayMode playMode);

    /**
     * 进度条的改变
     *
     * @param currentProgress
     * @param total
     */
    void onProgressChanged(long currentProgress, long total);

    /**
     * 广告正在加载
     */
    void onAdLoading();

    /**
     * 广告结束
     */
    void onAdFinished();
}
