package com.example.himalaya.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.DetailActivity;
import com.example.himalaya.R;
import com.example.himalaya.adapters.RecommendListAdapter;
import com.example.himalaya.base.BaseFragment;
import com.example.himalaya.interfaces.IRecommendViewCallback;
import com.example.himalaya.presenters.AlbumDetailPresenter;
import com.example.himalaya.presenters.RecommendPresenter;
import com.example.himalaya.utils.LogUtil;
import com.example.himalaya.views.UILoader;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class RecommendFragment extends BaseFragment implements IRecommendViewCallback, UILoader.OnRetryClickListener, RecommendListAdapter.OnRecommendItemClickListener {

    private static final String TAG = "RecommendFragment";
    private View mRootView;
    private RecyclerView mRecommendRV;
    private RecommendListAdapter mRecommendListAdapter;
    private RecommendPresenter mRecommendPresenter;
    private UILoader mUILoader;

    @Override
    protected View onSubViewLoad(final LayoutInflater layoutInflater, ViewGroup container) {

        mUILoader = new UILoader(getContext()) {
            @Override
            protected View getSuccessView(ViewGroup container) {
                return createSuccessView(layoutInflater, container);
            }
        };

        //获取到逻辑层的对象
        mRecommendPresenter = RecommendPresenter.getInstance();
        //先要设置通知接口的注册
        mRecommendPresenter.registerViewCallback(this);
        //获取推荐列表
        mRecommendPresenter.getRecommendList();

        if (mUILoader.getParent() instanceof ViewGroup) {
            ((ViewGroup) mUILoader.getParent()).removeView(mUILoader);
        }

        mUILoader.setOnRetryClickListener(this);

        //返回view给界面显示
        return mUILoader;
    }

    private View createSuccessView(LayoutInflater layoutInflater, ViewGroup container) {
        //view加载完成
        mRootView = layoutInflater.inflate(R.layout.fragment_recommend, container, false);
        //RecyclerView的使用
        //1.找到对应的控件
        mRecommendRV = mRootView.findViewById(R.id.recommend_list);
        //2.设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecommendRV.setLayoutManager(linearLayoutManager);
        //设置每个item的间隔
        mRecommendRV.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 5);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 5);
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.right = UIUtil.dip2px(view.getContext(), 5);
            }
        });
        //3.设置适配器
        mRecommendListAdapter = new RecommendListAdapter();
        mRecommendRV.setAdapter(mRecommendListAdapter);
        mRecommendListAdapter.setOnRecommendItemClickListener(this);
        return mRootView;
    }


    //将数据展示更新到UI
    @Override
    public void onRecommendListLoad(List<Album> result) {
        LogUtil.d(TAG, "onSuccess");
        //当获取到推荐内容时，该方法就会被调用（成功时）
        //数据回来以后更新UI
        mRecommendListAdapter.setData(result);
        mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
    }

    @Override
    public void onNetworkError() {
        LogUtil.d(TAG, "onNetworkError");
        mUILoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
    }

    @Override
    public void onEmpty() {
        LogUtil.d(TAG, "onEmpty");
        mUILoader.updateStatus(UILoader.UIStatus.EMPTY);
    }

    @Override
    public void onLoading() {
        LogUtil.d(TAG, "onLoading");
        mUILoader.updateStatus(UILoader.UIStatus.LOADING);
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //取消接口注册，避免内存泄漏
        if (mRecommendPresenter != null) {
            mRecommendPresenter.unRegisterViewCallback(this);
        }
    }

    @Override
    public void onRetryClick() {
        //表示网络不佳时用户点击重试。
        //重新获取数据即可
        if (mRecommendPresenter != null) {
            mRecommendPresenter.getRecommendList();
        }
    }

    /**
     * 推荐页面的点击跳转
     * @param position
     * @param album
     */
    @Override
    public void onItemClick(int position, Album album) {
        AlbumDetailPresenter.getInstance().setTargetAlbum(album);
        //item（条目）被点击，跳转到详情页面
        Intent intent = new Intent(getContext(), DetailActivity.class);
        startActivity(intent);
    }
}
