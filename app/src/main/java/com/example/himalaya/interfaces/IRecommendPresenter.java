package com.example.himalaya.interfaces;

import com.example.himalaya.base.IBasePresenter;

public interface IRecommendPresenter extends IBasePresenter<IRecommendViewCallback> {

    /**
     * 获取推荐内容
     */
    void getRecommendList();

    /**
     * 下拉刷新更多内容
     */
    void pull2RefreshMore();

    /**
     * 上接加载更多
     */
    void loadMore();



}
