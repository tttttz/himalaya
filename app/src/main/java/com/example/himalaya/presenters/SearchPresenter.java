package com.example.himalaya.presenters;

import androidx.annotation.Nullable;

import com.example.himalaya.api.XimalayaApi;
import com.example.himalaya.interfaces.ISearchCallback;
import com.example.himalaya.interfaces.ISearchPresenter;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.ArrayList;
import java.util.List;

public class SearchPresenter implements ISearchPresenter {

    private List<ISearchCallback> mCallbacks = new ArrayList<>();
    //当前关键字
    private String mCurrentKeyword = null;

    private XimalayaApi mXimalayaApi;

    private static final String TAG = "SearchPresenter";

    //默认页数
    private static final int DEFAULT_PAGE = 1;

    private int mCurrentPage = DEFAULT_PAGE;

    private SearchPresenter() {
        mXimalayaApi = XimalayaApi.getInstance();
    }

    private static SearchPresenter sSearchPresenter = null;

    public static SearchPresenter getSearchPresenter() {
        if (sSearchPresenter == null) {
            synchronized (SearchPresenter.class) {
                if (sSearchPresenter == null) {
                    sSearchPresenter = new SearchPresenter();
                }
            }
        }
        return sSearchPresenter;
    }

    @Override
    public void doSearch(String keyword) {
        //用于重新搜索，在网络不好时可以使用
        this.mCurrentKeyword = keyword;
        search(keyword);
    }

    @Override
    public void reSearch() {
        search(mCurrentKeyword);
    }

    private void search(String keyword) {
        mXimalayaApi.searchByKeyword(keyword, mCurrentPage, new IDataCallBack<SearchAlbumList>() {
            @Override
            public void onSuccess(SearchAlbumList searchAlbumList) {
                List<Album> albums = searchAlbumList.getAlbums();
                if (albums != null) {
                    LogUtil.d(TAG, "album size -- >" + albums.size());
                    for (ISearchCallback callback : mCallbacks) {
                        callback.onSearchResultLoad(albums);
                    }
                } else {
                    LogUtil.d(TAG, "album is null...");
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtil.d(TAG, "search errorCode -- >" + errorCode);
                LogUtil.d(TAG, "search errorMsg -- >" + errorMsg);
                for (ISearchCallback callback : mCallbacks) {
                    callback.onError(errorCode, errorMsg);
                }
            }
        });
    }



    @Override
    public void loadMore() {

    }

    /**
     * 获取热词
     */
    @Override
    public void getHotWord() {
        mXimalayaApi.getHotWords(new IDataCallBack<HotWordList>() {
            @Override
            public void onSuccess(HotWordList hotWordList) {
                if (hotWordList != null) {
                    List<HotWord> hotWords = hotWordList.getHotWordList();
                    LogUtil.d(TAG, "hotWords size -- >" + hotWords.size());
                    for (ISearchCallback callback : mCallbacks) {
                        callback.onHotWordLoad(hotWords);
                    }
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtil.d(TAG, "getHotWord errorCode -- >" + errorCode);
                LogUtil.d(TAG, "getHotWord errorMsg -- >" + errorMsg);
            }
        });
    }

    @Override
    public void getRecommendWord(String keyword) {
        mXimalayaApi.getSuggestWord(keyword, new IDataCallBack<SuggestWords>() {
            @Override
            public void onSuccess(@Nullable SuggestWords suggestWords) {
                if (suggestWords != null) {
                    List<QueryResult> keyWordList = suggestWords.getKeyWordList();
                    LogUtil.d(TAG, "keyWordList size -- > " + keyWordList.size());
                    for (ISearchCallback iSearchCallback : mCallbacks) {
                        iSearchCallback.onRecommendWordLoaded(keyWordList);
                    }
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtil.d(TAG, "getRecommendWord errorCode -- > " + errorCode);
                LogUtil.d(TAG, "getRecommendWord errorMsg -- > " + errorMsg);
            }
        });
    }

    @Override
    public void registerViewCallback(ISearchCallback iSearchCallback) {
        if (!mCallbacks.contains(iSearchCallback)) {
            mCallbacks.add(iSearchCallback);
        }
    }

    @Override
    public void unRegisterViewCallback(ISearchCallback iSearchCallback) {
        mCallbacks.remove(iSearchCallback);
    }
}
