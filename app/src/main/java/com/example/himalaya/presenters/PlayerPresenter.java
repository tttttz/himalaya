package com.example.himalaya.presenters;

import com.example.himalaya.base.BaseApplication;
import com.example.himalaya.interfaces.IPlayerCallBack;
import com.example.himalaya.interfaces.IPlayerPresenter;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.ArrayList;
import java.util.List;

public class PlayerPresenter implements IPlayerPresenter, IXmAdsStatusListener, IXmPlayerStatusListener {

    private List<IPlayerCallBack> mIPlayerCallBacks = new ArrayList<>();

    private final XmPlayerManager mPlayerManager;

    private static final String TAG = "PlayerPresenter";
    private String mTrackTitle;

    private PlayerPresenter(){
        mPlayerManager = XmPlayerManager.getInstance(BaseApplication.getAppContext());
        //广告相关接口
        mPlayerManager.addAdsStatusListener(this);
        //注册播放器相关的接口
        mPlayerManager.addPlayerStatusListener(this);
    }

    private static PlayerPresenter sPlayerPresenter;
    //单例
    public static PlayerPresenter getPlayerPresenter(){
        if (sPlayerPresenter == null) {
            synchronized (PlayerPresenter.class) {
                if (sPlayerPresenter == null){
                    sPlayerPresenter = new PlayerPresenter();
                }
            }
        }
        return sPlayerPresenter;
    }

    private boolean isPlayListSet = false;
    public void setPlayList(List<Track> list, int playIndex){
        if (mPlayerManager != null) {
            mPlayerManager.setPlayList(list, playIndex);
            isPlayListSet = true;
        } else {
            LogUtil.d(TAG, "mPlayerManager is null");
        }
        Track track = list.get(playIndex);
        mTrackTitle = track.getTrackTitle();
    }

    @Override
    public void play() {
        if (isPlayListSet){
            mPlayerManager.play();
        }
    }

    @Override
    public void pause() {
        if (mPlayerManager != null) {
            mPlayerManager.pause();
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void playPre() {
        //播放上一个节目
        if (mPlayerManager != null) {
            mPlayerManager.playPre();
        }
    }

    @Override
    public void playNext() {
        //播放下一个节目
        if (mPlayerManager != null) {
            mPlayerManager.playNext();
        }
    }

    @Override
    public void switchPlayMode(XmPlayListControl.PlayMode mode) {

    }

    @Override
    public void getPlayList() {

    }

    @Override
    public void playByIndex(int index) {

    }

    @Override
    public void seekTo(int progress) {
        //更新播放器的进度
        mPlayerManager.seekTo(progress);
    }

    @Override
    public boolean isPlay() {
        //返回当前播放状态
        return mPlayerManager.isPlaying();
    }

    @Override
    public void registerViewCallback(IPlayerCallBack iPlayerCallBack) {
        iPlayerCallBack.onTrackTitleUpdate(mTrackTitle);
        if (!mIPlayerCallBacks.contains(iPlayerCallBack)) {
            mIPlayerCallBacks.add(iPlayerCallBack);
        }
    }

    @Override
    public void unRegisterViewCallback(IPlayerCallBack iPlayerCallBack) {
        mIPlayerCallBacks.remove(iPlayerCallBack);
    }

    //===================广告相关的回调方法 start===========================
    @Override
    public void onStartGetAdsInfo() {
        LogUtil.d(TAG, "onStartGetAdsInfo");
    }

    @Override
    public void onGetAdsInfo(AdvertisList advertisList) {
        LogUtil.d(TAG, "onGetAdsInfo");
    }

    @Override
    public void onAdsStartBuffering() {
        LogUtil.d(TAG, "onAdsStartBuffering");
    }

    @Override
    public void onAdsStopBuffering() {
        LogUtil.d(TAG, "onAdsStopBuffering");
    }

    @Override
    public void onStartPlayAds(Advertis advertis, int i) {
        LogUtil.d(TAG, "onStartPlayAds");
    }

    @Override
    public void onCompletePlayAds() {
        LogUtil.d(TAG, "onCompletePlayAds");
    }

    @Override
    public void onError(int what, int extra) {
        LogUtil.d(TAG, "onError what = > " + what + "extra = > " + extra);
    }
    //===================广告相关的回调方法 end===========================

    //===================播放器相关回调方法 start===========================
    @Override
    public void onPlayStart() {
        LogUtil.d(TAG, "onPlayStart");
        for (IPlayerCallBack iPlayerCallBack : mIPlayerCallBacks) {
            iPlayerCallBack.onPlayStart();
        }
    }

    @Override
    public void onPlayPause() {
        LogUtil.d(TAG, "onPlayPause");
        for (IPlayerCallBack iPlayerCallBack : mIPlayerCallBacks) {
            iPlayerCallBack.onPlayPause();
        }
    }

    @Override
    public void onPlayStop() {
        LogUtil.d(TAG, "onPlayStop");
    }

    @Override
    public void onSoundPlayComplete() {
        LogUtil.d(TAG, "onSoundPlayComplete");
    }

    @Override
    public void onSoundPrepared() {
        LogUtil.d(TAG, "onSoundPrepared");
    }

    @Override
    public void onSoundSwitch(PlayableModel lastModel, PlayableModel curModel) {
        LogUtil.d(TAG, "onSoundSwitch");
        if (lastModel != null) {
            LogUtil.d(TAG, "lastModel" + lastModel.getKind());
        }
        LogUtil.d(TAG, "curModel" + curModel.getKind());
        if (curModel instanceof Track) {
            Track currentTrack = (Track) curModel;
            mTrackTitle = currentTrack.getTrackTitle();
            LogUtil.d(TAG, "title == >" + currentTrack.getTrackTitle());
            for (IPlayerCallBack iPlayerCallBack : mIPlayerCallBacks) {
                iPlayerCallBack.onTrackTitleUpdate(mTrackTitle);
            }
        }
    }

    @Override
    public void onBufferingStart() {
        LogUtil.d(TAG, "onBufferingStart");
    }

    @Override
    public void onBufferingStop() {
        LogUtil.d(TAG, "onBufferingStop");
    }

    @Override
    public void onBufferProgress(int progress) {
        LogUtil.d(TAG, "onBufferProgress.." + progress);
    }

    @Override
    public void onPlayProgress(int currPos, int duration) {
        //单位是毫秒
        for (IPlayerCallBack iPlayerCallBack : mIPlayerCallBacks) {
            iPlayerCallBack.onProgressChange(currPos, duration);
        }
        LogUtil.d(TAG, "onPlayProgress..." + currPos + "duration -- >" + duration);
    }

    @Override
    public boolean onError(XmPlayerException e) {
        LogUtil.d(TAG, "onError e-->" + e);
        return false;
    }
    //===================播放器相关回调方法 end===========================
}
