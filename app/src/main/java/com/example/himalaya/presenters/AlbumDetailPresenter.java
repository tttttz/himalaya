package com.example.himalaya.presenters;

import com.example.himalaya.interfaces.IAlbumDetailPresenter;
import com.example.himalaya.interfaces.IAlbumDetailViewCallback;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.List;

public class AlbumDetailPresenter implements IAlbumDetailPresenter {

    private Album mTargetAlbum = null;

    private List<IAlbumDetailViewCallback> mCallbacks = new ArrayList<>();

    private AlbumDetailPresenter(){

    }

    private static AlbumDetailPresenter sInstance = null;

    /**
     * 懒汉式单例
     * @return
     */
    public static AlbumDetailPresenter getInstance(){
        if (sInstance == null){
            synchronized (AlbumDetailPresenter.class){
                if (sInstance == null){
                    sInstance = new AlbumDetailPresenter();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void pull2RefreshMore() {

    }

    @Override
    public void loadMore() {

    }

    @Override
    public void getAlbumDetail(int album, int page) {

    }

    @Override
    public void registerViewCallback(IAlbumDetailViewCallback detailViewCallback) {
        if (!mCallbacks.contains(detailViewCallback)){
            mCallbacks.add(detailViewCallback);
            if (mTargetAlbum != null) {
                detailViewCallback.onAlbumLoaded(mTargetAlbum);
            }
        }
    }

    @Override
    public void unregisterViewCallback(IAlbumDetailViewCallback detailViewCallback) {
            mCallbacks.remove(detailViewCallback);
    }

    public void setTargetAlbum(Album targetAlbum){
        this.mTargetAlbum = targetAlbum;
    }
}
