package com.example.himalaya;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.adapters.AlbumListAdapter;
import com.example.himalaya.adapters.SearchRecommendAdapter;
import com.example.himalaya.base.BaseActivity;
import com.example.himalaya.interfaces.ISearchCallback;
import com.example.himalaya.presenters.AlbumDetailPresenter;
import com.example.himalaya.presenters.SearchPresenter;
import com.example.himalaya.utils.LogUtil;
import com.example.himalaya.views.FlowTextLayout;
import com.example.himalaya.views.UILoader;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchActivity extends BaseActivity implements ISearchCallback, AlbumListAdapter.OnAlbumItemClickListener {


    private View mBackBtn;
    private EditText mInputBox;
    private View mSearchBtn;
    private FrameLayout mResultContainer;

    private static final String TAG = "SearchActivity";
    private SearchPresenter mSearchPresenter;
    private UILoader mUILoader;
    private RecyclerView mResultListView;
    private AlbumListAdapter mAlbumListAdapter;
    private FlowTextLayout mFlowTextLayout;
    private InputMethodManager mImm;
    private View mDeleteBtn;

    private static final int DEFAULT_TIME_SHOW_IMM = 600;
    private RecyclerView mSearchRecommendList;
    private SearchRecommendAdapter mRecommendAdapter;
    private TwinklingRefreshLayout mRefreshLayout;

    private boolean mNeedSuggestWords = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        initEvent();
        initPresenter();
    }

    private void initPresenter() {
        mImm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        mSearchPresenter = SearchPresenter.getSearchPresenter();
        mSearchPresenter.registerViewCallback(this);
        mSearchPresenter.getHotWord();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSearchPresenter != null) {
            mSearchPresenter.unRegisterViewCallback(this);
            mSearchPresenter = null;
        }
    }

    private void initEvent() {

        mAlbumListAdapter.setOnAlbumItemClickListener(this);

        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                LogUtil.d(TAG, "onLoadMore");
                if (mSearchPresenter != null) {
                    mSearchPresenter.loadMore();
                }
            }
        });

        if (mRecommendAdapter != null) {
            mRecommendAdapter.setItemClickListener(new SearchRecommendAdapter.ItemClickListener() {
                @Override
                public void onItemClick(String keyword) {
                    //LogUtil.d(TAG, "keyword -- >" + keyword);
                    mNeedSuggestWords = false;
                    SwitchToSearch(keyword);
                }
            });
        }

        mUILoader.setOnRetryClickListener(new UILoader.OnRetryClickListener() {
            @Override
            public void onRetryClick() {
                if (mSearchPresenter != null) {
                    mSearchPresenter.reSearch();
                    mUILoader.updateStatus(UILoader.UIStatus.LOADING);
                }
            }
        });

        mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInputBox != null) {
                    mInputBox.setText("");
                }
            }
        });

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //调用搜索逻辑
                String keyword = mInputBox.getText().toString().trim();
                if (TextUtils.isEmpty(keyword)) {
                    Toast.makeText(SearchActivity.this, "搜索关键字不能为空！",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mSearchPresenter != null) {
                    mSearchPresenter.doSearch(keyword);
                    mUILoader.updateStatus(UILoader.UIStatus.LOADING);
                }
            }
        });
        //搜索框内容改变时的监听事件
        mInputBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    mDeleteBtn.setVisibility(View.GONE);
                    mSearchPresenter.getHotWord();
                } else {
                    mDeleteBtn.setVisibility(View.VISIBLE);
                    if (mNeedSuggestWords){
                        getSuggestionWord(s.toString());
                    } else {
                        mNeedSuggestWords = true;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mFlowTextLayout.setClickListener(new FlowTextLayout.ItemClickListener() {
            @Override
            public void onItemClick(String text) {
                //搜索热词
                mNeedSuggestWords = false;
                SwitchToSearch(text);
            }
        });
    }

    private void SwitchToSearch(String text) {
        mInputBox.setText(text);
        mInputBox.setSelection(text.length());
        //搜索
        if (mSearchPresenter != null) {
            mSearchPresenter.doSearch(text);
        }
        if (mUILoader != null) {
            mUILoader.updateStatus(UILoader.UIStatus.LOADING);
        }
    }

    /**
     * 获取联想词
     *
     * @param keyword
     */
    private void getSuggestionWord(String keyword) {
        LogUtil.d(TAG, "getSuggestionWord -- >" + keyword);
        if (mSearchPresenter != null) {
            mSearchPresenter.getRecommendWord(keyword);
        }
    }

    public void initView() {
        mBackBtn = this.findViewById(R.id.search_back);
        mDeleteBtn = this.findViewById(R.id.search_input_delete);
        mDeleteBtn.setVisibility(View.GONE);
        mInputBox = this.findViewById(R.id.search_input);
        mInputBox.postDelayed(new Runnable() {
            @Override
            public void run() {
                mInputBox.requestFocus();
                mImm.showSoftInput(mInputBox, InputMethodManager.SHOW_IMPLICIT);
            }
        }, DEFAULT_TIME_SHOW_IMM);
        mSearchBtn = this.findViewById(R.id.search_btn);
        mResultContainer = this.findViewById(R.id.search_container);

        if (mUILoader == null) {
            mUILoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView();
                }
            };
            if (mUILoader.getParent() instanceof ViewGroup) {
                ((ViewGroup) mUILoader.getParent()).removeView(mUILoader);
            }
            mResultContainer.addView(mUILoader);
        }


    }

    /**
     * 创建数据请求成功的View
     *
     * @return 成功后的View
     */
    private View createSuccessView() {
        View resultView = LayoutInflater.from(this).inflate(R.layout.search_result_view, null, false);
        mRefreshLayout = resultView.findViewById(R.id.search_result_refresh_layout);
        mRefreshLayout.setOverScrollBottomShow(false);
        mRefreshLayout.setEnableRefresh(false);
        //显示热词
        mFlowTextLayout = resultView.findViewById(R.id.recommend_hot_word_view);
        //设置RecyclerView
        mResultListView = resultView.findViewById(R.id.result_list_view);
        LinearLayoutManager resultLayoutManager = new LinearLayoutManager(this);
        mResultListView.setLayoutManager(resultLayoutManager);
        mAlbumListAdapter = new AlbumListAdapter();
        mResultListView.setAdapter(mAlbumListAdapter);
        mResultListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 5);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 5);
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.right = UIUtil.dip2px(view.getContext(), 5);
            }
        });

        //搜索推荐联想词
        mSearchRecommendList = resultView.findViewById(R.id.search_recommend_view);
        LinearLayoutManager recommendLayoutManager = new LinearLayoutManager(this);
        mSearchRecommendList.setLayoutManager(recommendLayoutManager);
        mSearchRecommendList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 2);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 2);
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.right = UIUtil.dip2px(view.getContext(), 5);
            }
        });
        mRecommendAdapter = new SearchRecommendAdapter();
        mSearchRecommendList.setAdapter(mRecommendAdapter);
        return resultView;
    }

    @Override
    public void onSearchResultLoad(List<Album> result) {
        handleSearchResult(result);
        //点击搜索得到结果后，隐藏输入法键盘
        mImm.hideSoftInputFromWindow(mInputBox.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void handleSearchResult(List<Album> result) {
        hideSuccessView();
        mRefreshLayout.setVisibility(View.VISIBLE);
        //处理结果数据
        if (result != null) {
            if (result.size() == 0) {
                if (mUILoader != null) {
                    mUILoader.updateStatus(UILoader.UIStatus.EMPTY);
                }
            } else {
                //数据不为空，则设置数据
                mAlbumListAdapter.setData(result);
                mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
            }
        }
    }

    @Override
    public void onHotWordLoad(List<HotWord> hotWordLists) {
        hideSuccessView();
        //TODO: 做一个缓存
        mFlowTextLayout.setVisibility(View.VISIBLE);
        if (mUILoader != null) {
            mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
        LogUtil.d(TAG, "result size -->" + hotWordLists.size());
        List<String> hotWords = new ArrayList<>();
        hotWords.clear();
        for (HotWord hotWord : hotWordLists) {
            String searchWord = hotWord.getSearchword();
            hotWords.add(searchWord);
        }
        Collections.sort(hotWords);
        //更新UI
        mFlowTextLayout.setTextContents(hotWords);
    }

    @Override
    public void onLoadMoreResult(List<Album> result, boolean isOkay) {
        if (mRefreshLayout != null) {
            mRefreshLayout.finishLoadmore();
        }
        if (isOkay) {
            handleSearchResult(result);
        } else {
            Toast.makeText(SearchActivity.this, "没有更多内容", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 联想词展示
     *
     * @param keyWordList
     */
    @Override
    public void onRecommendWordLoaded(List<QueryResult> keyWordList) {
        //关键字的联想词
        LogUtil.d(TAG, "keyWordList size == > " + keyWordList.size());
        if (mRecommendAdapter != null) {
            mRecommendAdapter.setData(keyWordList);
        }
        //控制UI的状态和隐藏显示
        if (mUILoader != null) {
            mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
        //控制显示和隐藏
        hideSuccessView();
        mSearchRecommendList.setVisibility(View.VISIBLE);
    }

    @Override
    public void onError(int errorCode, String errorMsg) {
        if (mUILoader != null) {
            mUILoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
        }
    }

    private void hideSuccessView() {
        mSearchRecommendList.setVisibility(View.GONE);
        mRefreshLayout.setVisibility(View.GONE);
        mFlowTextLayout.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(int position, Album album) {
        AlbumDetailPresenter.getInstance().setTargetAlbum(album);
        //item（条目）被点击，跳转到详情页面
        Intent intent = new Intent(this, DetailActivity.class);
        startActivity(intent);
    }
}
