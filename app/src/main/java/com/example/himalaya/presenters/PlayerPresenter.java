package com.example.himalaya.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.himalaya.api.XimalayaApi;
import com.example.himalaya.base.BaseApplication;
import com.example.himalaya.interfaces.IPlayerCallBack;
import com.example.himalaya.interfaces.IPlayerPresenter;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.constants.PlayerConstants;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;

public class PlayerPresenter implements IPlayerPresenter, IXmAdsStatusListener, IXmPlayerStatusListener {

    private List<IPlayerCallBack> mIPlayerCallBacks = new ArrayList<>();

    private final XmPlayerManager mPlayerManager;

    public static final int DEFAULT_PLAY_INDEX = 0;

    private static final String TAG = "PlayerPresenter";
    private Track mCurrentTrack;
    private int mCurrentIndex = 0;
    private final SharedPreferences mPlayModeSp;

    private XmPlayListControl.PlayMode mCurrentPlayMode = PLAY_MODEL_LIST;

    private boolean mReverse = false;

    //PLAY_MODE_LIST
    //PLAY_MODE_LIST_LOOP
    //PLAY_MODE_RANDOM
    //PLAY_MODE_SINGLE_LOOP
    private static final int PLAY_MODEL_LIST_INT = 0;
    private static final int PLAY_MODEL_LIST_LOOP_INT = 1;
    private static final int PLAY_MODEL_RANDOM_INT = 2;
    private static final int PLAY_MODEL_SINGLE_LOOP_INT = 3;

    //sp's key and name
    public static final String PLAY_MODEL_SP_NAME = "PlayMod";
    public static final String PLAY_MODEL_SP_KEY = "currentPlayMod";
    private int mCurrentProgressPosition = 0;
    private int mProgressDuration = 0;

    private PlayerPresenter(){
        mPlayerManager = XmPlayerManager.getInstance(BaseApplication.getAppContext());
        //广告相关接口
        mPlayerManager.addAdsStatusListener(this);
        //注册播放器相关的接口
        mPlayerManager.addPlayerStatusListener(this);
        //需要记录当前的播放模式
        mPlayModeSp = BaseApplication.getAppContext().getSharedPreferences(PLAY_MODEL_SP_NAME, Context.MODE_PRIVATE);
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
        LogUtil.d(TAG, "list.size -- >" + list.size());
        if (mPlayerManager != null) {
            mPlayerManager.setPlayList(list, playIndex);
            isPlayListSet = true;
            mCurrentTrack = list.get(playIndex);
            mCurrentIndex = playIndex;
        } else {
            LogUtil.d(TAG, "mPlayerManager is null");
        }

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
        mCurrentPlayMode = mode;
        if (mPlayerManager != null) {
            mPlayerManager.setPlayMode(mode);
            for (IPlayerCallBack iPlayerCallBack : mIPlayerCallBacks) {
                iPlayerCallBack.onPlayModeChange(mode);
            }
            SharedPreferences.Editor editor = mPlayModeSp.edit();
            editor.putInt(PLAY_MODEL_SP_KEY, getIntByPlayMode(mode));
            editor.commit();
        }
    }

    private int getIntByPlayMode(XmPlayListControl.PlayMode mode){
        switch(mode){
            case PLAY_MODEL_SINGLE_LOOP:
                return PLAY_MODEL_SINGLE_LOOP_INT;
            case PLAY_MODEL_LIST:
                return PLAY_MODEL_LIST_INT;
            case PLAY_MODEL_RANDOM:
                return PLAY_MODEL_RANDOM_INT;
            case PLAY_MODEL_LIST_LOOP:
                return PLAY_MODEL_LIST_LOOP_INT;
        }
        return PLAY_MODEL_LIST_INT;
    }

    private XmPlayListControl.PlayMode getModeByInt (int index){
        switch(index){
            case PLAY_MODEL_SINGLE_LOOP_INT:
                return PLAY_MODEL_SINGLE_LOOP;
            case PLAY_MODEL_LIST_INT:
                return PLAY_MODEL_LIST;
            case PLAY_MODEL_RANDOM_INT:
                return PLAY_MODEL_RANDOM;
            case PLAY_MODEL_LIST_LOOP_INT:
                return PLAY_MODEL_LIST_LOOP;
        }
        return PLAY_MODEL_LIST;
    }

    @Override
    public void getPlayList() {
        if (mPlayerManager != null) {
            List<Track> playList = mPlayerManager.getPlayList();
            for (IPlayerCallBack iPlayerCallBack : mIPlayerCallBacks) {
                iPlayerCallBack.onListLoaded(playList);
            }
        }
    }

    @Override
    public void playByIndex(int index) {
        //切换播放器到index位置进行播放
        if (mPlayerManager != null) {
            mPlayerManager.play(index);
        }
    }

    @Override
    public void seekTo(int progress) {
        //更新播放器的进度
        mPlayerManager.seekTo(progress);
    }

    @Override
    public boolean isPlaying() {
        //返回当前播放状态
        return mPlayerManager.isPlaying();
    }

    @Override
    public void reversePlayList() {
        //将播放列表反转
        List<Track> playList = mPlayerManager.getPlayList();
        Collections.reverse(playList);
        //播放列表下标变换
        mReverse = !mReverse;
        mCurrentIndex = playList.size() - 1 - mCurrentIndex;
        mPlayerManager.setPlayList(playList, mCurrentIndex);
        mCurrentTrack = (Track) mPlayerManager.getCurrSound();
        for (IPlayerCallBack iPlayerCallBack : mIPlayerCallBacks) {
            iPlayerCallBack.onListLoaded(playList);
            iPlayerCallBack.onTrackUpdate(mCurrentTrack, mCurrentIndex);
            iPlayerCallBack.updateListOrder(mReverse);
        }
    }

    @Override
    public void playByAlbumId(long id) {
        // 1.获取专辑内容
        XimalayaApi ximalayaApi = XimalayaApi.getInstance();
        ximalayaApi.getAlbumDetail(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                // 2.设置给播放器
                List<Track> tracks = trackList.getTracks();
                if (tracks != null && tracks.size() > 0) {
                    mPlayerManager.setPlayList(tracks, 0);
                    isPlayListSet = true;
                    mCurrentTrack = tracks.get(DEFAULT_PLAY_INDEX);
                    mCurrentIndex = DEFAULT_PLAY_INDEX;
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(BaseApplication.getAppContext(), "请求数据错误", Toast.LENGTH_SHORT).show();
            }
        }, (int) id, 1);
        // 3.播放
    }


    @Override
    public void registerViewCallback(IPlayerCallBack iPlayerCallBack) {
        //通知当前节目
        iPlayerCallBack.onTrackUpdate(mCurrentTrack, mCurrentIndex);
        iPlayerCallBack.onProgressChange(mCurrentProgressPosition, mProgressDuration);
        //更寻状态
        handlePlayState(iPlayerCallBack);
        //从sp中拿当前的播放模式
        int modeIndex = mPlayModeSp.getInt(PLAY_MODEL_SP_KEY, PLAY_MODEL_LIST_INT);
        mCurrentPlayMode = getModeByInt(modeIndex);
        iPlayerCallBack.onPlayModeChange(mCurrentPlayMode);
        if (!mIPlayerCallBacks.contains(iPlayerCallBack)) {
            mIPlayerCallBacks.add(iPlayerCallBack);
        }
    }

    private void handlePlayState(IPlayerCallBack iPlayerCallBack) {
        int playerStatus = mPlayerManager.getPlayerStatus();

        if (PlayerConstants.STATE_STARTED == playerStatus) {
            iPlayerCallBack.onPlayStart();
        } else {
            iPlayerCallBack.onPlayPause();
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
        mPlayerManager.setPlayMode(mCurrentPlayMode);
        if (mPlayerManager.getPlayerStatus() == PlayerConstants.STATE_PREPARED) {
            //播放器准备完成，可以播放节目
            mPlayerManager.play();
        }
    }

    @Override
    public void onSoundSwitch(PlayableModel lastModel, PlayableModel curModel) {
        LogUtil.d(TAG, "onSoundSwitch");
        if (lastModel != null) {
            LogUtil.d(TAG, "lastModel" + lastModel.getKind());
        }
        LogUtil.d(TAG, "curModel" + curModel.getKind());
        mCurrentIndex = mPlayerManager.getCurrentIndex();
        if (curModel instanceof Track) {
            Track currentTrack = (Track) curModel;
            mCurrentTrack = currentTrack;
            LogUtil.d(TAG, "title == >" + currentTrack.getTrackTitle());
            LogUtil.d(TAG, " mCurrentIndex== >" + mCurrentIndex);
            for (IPlayerCallBack iPlayerCallBack : mIPlayerCallBacks) {
                iPlayerCallBack.onTrackUpdate(mCurrentTrack, mCurrentIndex);
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
        this.mCurrentProgressPosition = currPos;
        this.mProgressDuration = duration;
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

    //判断是否有播放列表的节目列表
    public boolean hasPlayList() {
        return isPlayListSet;
    }
    //===================播放器相关回调方法 end===========================
}
