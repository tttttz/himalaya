package com.example.himalaya.interfaces;

import com.example.himalaya.base.IBasePresenter;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

public interface IPlayerPresenter extends IBasePresenter<IPlayerCallBack> {

    /**
     * 播放
     */
    void play();

    /**
     * 暂停
     */
    void pause();

    /**
     * 停止
     */
    void stop();

    /**
     * 播放上一首
     */
    void playPre();

    /**
     * 播放下一首
     */
    void playNext();

    /**
     * 切换播放模式
     *
     * @param mode
     */
    void switchPlayMode(XmPlayListControl.PlayMode mode);

    /**
     * 获取播放列表
     */
    void getPlayList();

    /**
     * 根据节目在列表中的位置播放
     *
     * @param index
     */
    void playByIndex(int index);

    /**
     * 切换播放进度
     *
     * @param progress
     */
    void seekTo(int progress);

    /**
     * 播放状态
     * @return
     */
    boolean isPlaying();

    /**
     * 播放列表反转
     */
    void reversePlayList();

    void playByAlbumId(long id);


}
