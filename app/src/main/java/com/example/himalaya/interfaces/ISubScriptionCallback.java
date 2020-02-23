package com.example.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

public interface ISubScriptionCallback {
    /**
     * 调用添加时通知UI结果
     *
     * @param isSuccess
     */
    void onAddResult(boolean isSuccess);

    /**
     * 删除订阅的回调方法
     *
     * @param isSuccess
     */
    void onDeleteResult(boolean isSuccess);

    /**
     * 订阅专辑加载结果的回调方法
     *
     * @param albumList
     */
    void onSubscriptionLoaded(List<Album> albumList);
}
