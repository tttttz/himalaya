package com.example.himalaya.presenters;

import com.example.himalaya.interfaces.IRecommendPresenter;
import com.example.himalaya.interfaces.IRecommendViewCallback;
import com.example.himalaya.utils.Constants;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendPresenter implements IRecommendPresenter {

    private static final String TAG = "RecommendPresenter";

    private List<IRecommendViewCallback> mCallbacks = new ArrayList<>();

    private RecommendPresenter(){

    }

    private static RecommendPresenter sInstance = null;

    /**
     * 懒汉式单例
     * 获取RecommendPresenter对象
     * @return
     */
    public static RecommendPresenter getInstance(){
        if (sInstance == null){
            synchronized (RecommendPresenter.class){
                if (sInstance == null){
                    sInstance = new RecommendPresenter();
                }
            }
        }
        return sInstance;
    }

    /**
     * 获取推荐内容（猜你喜欢）
     * 接口：3.10.6 获取猜你喜欢专辑
     */
    @Override
    public void getRecommendList() {
        //获取推荐内容
        //封装参数
        updateLoading();
        Map<String, String> map = new HashMap<>();
        //这个参数表示一页数据返回多少条
        map.put(DTransferConstants.LIKE_COUNT, Constants.COUNT_RECOMMEND + "");
        CommonRequest.getGuessLikeAlbum(map, new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(GussLikeAlbumList gussLikeAlbumList) {
                //数据获取成功
                if (gussLikeAlbumList != null) {
                    List<Album> albumList = gussLikeAlbumList.getAlbumList();
                    //数据回来后要更新UI
                    //updateRecommendUI(albumList);
                    handlerRecommendResult(albumList);
                }
            }

            @Override
            public void onError(int i, String s) {
                //数据获取失败
                LogUtil.d(TAG,"error -- >" + i);
                LogUtil.d(TAG,"errorMsg -- >" + s);
                handlerError();
            }
        });
    }

    @Override
    public void pull2RefreshMore() {

    }

    @Override
    public void loadMore() {

    }

    private void handlerError() {
        if (mCallbacks != null) {
            for (IRecommendViewCallback callback : mCallbacks) {
                callback.onNetworkError();
            }
        }
    }

    private void handlerRecommendResult(List<Album> albumList) {
        if (albumList != null) {
            //测试清空，为了显示一下空界面
            //albumList.clear();
            if (albumList.size() == 0) {
                //若数据条目为0
                for (IRecommendViewCallback callback : mCallbacks) {
                    callback.onEmpty();
                }
            } else {
                //通知UI更新
                for (IRecommendViewCallback callback : mCallbacks) {
                    callback.onRecommendListLoad(albumList);
                }
            }
        }
    }

    /**
     * 数据加载
     */
    private void updateLoading(){
        for (IRecommendViewCallback callback : mCallbacks) {
            callback.onLoading();
        }
    }


    @Override
    public void registerViewCallback(IRecommendViewCallback callback) {
        if (mCallbacks != null && !mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }

    @Override
    public void unRegisterViewCallback(IRecommendViewCallback callback) {
        if (mCallbacks != null) {
            mCallbacks.remove(callback);
        }
    }

}
