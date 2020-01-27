package com.example.himalaya.interfaces;

public interface IAlbumDetailPresenter {

    /**
     * 下拉刷新更多内容
     */
    void pull2RefreshMore();

    /**
     * 上接加载更多
     */
    void loadMore();

    /**
     * 获取专辑详情
     * @param album
     * @param page
     */
    void getAlbumDetail(int album, int page);

    /**
     * 注册UI通知
     * @param detailViewCallback
     */
    void registerViewCallback(IAlbumDetailViewCallback detailViewCallback);

    /**
     * 取消注册UI
     * @param detailViewCallback
     */
    void unregisterViewCallback(IAlbumDetailViewCallback detailViewCallback);
}
