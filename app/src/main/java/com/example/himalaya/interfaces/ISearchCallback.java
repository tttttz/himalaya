package com.example.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.List;

public interface ISearchCallback {

    /**
     * 搜索结果显示
     */
    void onSearchResultLoad(List<Album> result);

    /**
     * 获取热词
     *
     * @param result
     */
    void onHotWordLoad(List<HotWord> result);

    /**
     * 加载更多的结果返回
     *
     * @param result 结果
     * @param isOkay true表示加载成功，false表示没有更多
     */
    void onLoadMoreResult(List<Album> result, boolean isOkay);

    /**
     * 获取推荐的联想词
     *
     * @param keyWorkList
     */
    void onRecommendWordLoaded(List<QueryResult> keyWorkList);

    /**
     * 错误信息展示
     *
     * @param errorCode 错误代码
     * @param errorMsg 错误信息
     */
    void onError(int errorCode, String errorMsg);

}
