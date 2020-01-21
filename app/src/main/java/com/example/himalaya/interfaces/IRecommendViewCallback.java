package com.example.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

public interface IRecommendViewCallback {

    /**
     * 获取推荐内容
     *
     * @param result
     */
    void onRecommendListLoad(List<Album> result);

    /**
     * 加载更多
     *
     * @param result
     */
    void onLoadMore(List<Album> result);

    /**
     * 下拉刷新更多
     *
     * @param result
     */
    void onRefreshMore(List<Album> result);


}
