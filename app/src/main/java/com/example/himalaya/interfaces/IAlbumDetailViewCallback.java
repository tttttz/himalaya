package com.example.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IAlbumDetailViewCallback {

    /**
     * 专辑详情内容加载出来
     *
     * @param tracks
     */
    void onDetailListLoaded(List<Track> tracks);

    /**
     * 把album传给UI
     *
     * @param album
     */
    void onAlbumLoaded(Album album);

    /**
     * 网络错误
     */
    void onNetworkError(int errorCode, String errorMsg);

    /**
     *加载更多的结果
     * @param size  size size > 0表示刷新成功，size <= 0表示刷新失败
     */
    void onLoadMoreFinished(int size);

    /**
     * 下拉刷新
     * @param size size > 0表示刷新成功，size <= 0表示刷新失败
     */
    void onRefreshMoreFinished(int size);

}
