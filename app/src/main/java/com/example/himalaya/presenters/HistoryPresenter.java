package com.example.himalaya.presenters;

import com.example.himalaya.base.BaseApplication;
import com.example.himalaya.data.HistoryDao;
import com.example.himalaya.data.IHistoryDao;
import com.example.himalaya.data.IHistoryDaoCallback;
import com.example.himalaya.interfaces.IHistoryCallback;
import com.example.himalaya.interfaces.IHistoryPresenter;
import com.example.himalaya.utils.Constants;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 历史记录数量最多100条，若超过100条则删掉最早的记录，并把当前记录添加进去。
 */
public class HistoryPresenter implements IHistoryPresenter, IHistoryDaoCallback {

    private static final String TAG = "HistoryPresenter";
    private final IHistoryDao mHistoryDao;

    List<IHistoryCallback> mCallbacks = new ArrayList<>();
    private List<Track> mCurrentHistories = null;
    private Track mCurrentAddTrack = null;

    private HistoryPresenter(){
        mHistoryDao = new HistoryDao();
        mHistoryDao.setCallback(this);
        listHistories();
    }

    private static HistoryPresenter sHistoryPresenter = null;

    public static HistoryPresenter getHistoryPresenter() {
        if (sHistoryPresenter == null) {
            synchronized (HistoryPresenter.class) {
                if (sHistoryPresenter == null) {
                    sHistoryPresenter = new HistoryPresenter();
                }
            }
        }
        return sHistoryPresenter;
    }

    //实现的presenter层接口的方法
    @Override
    public void listHistories() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mHistoryDao != null) {
                    mHistoryDao.listHistories();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    private boolean isDoDelAdOutOfSize = false;
    @Override
    public void addHistory(Track track) {
        //判断记录条数是否>=100
        if (mCurrentHistories != null && mCurrentHistories.size() >= Constants.MAX_HISTORY_COUNT) {
            //先木能添加，先删除最后一条数据
            isDoDelAdOutOfSize = true;
            delHistory(mCurrentHistories.get(mCurrentHistories.size() - 1));
            this.mCurrentAddTrack = track;
        } else {
            doAddHistory(track);
        }
    }

    private void doAddHistory(Track track) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mHistoryDao != null) {
                    mHistoryDao.addHistory(track);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void delHistory(Track track) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mHistoryDao != null) {
                    mHistoryDao.delHistory(track);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void clearHistories() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mHistoryDao != null) {
                    mHistoryDao.clearHistory();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void registerViewCallback(IHistoryCallback iHistoryCallback) {
        if (!mCallbacks.contains(iHistoryCallback)) {
            mCallbacks.add(iHistoryCallback);
        }
    }

    @Override
    public void unRegisterViewCallback(IHistoryCallback iHistoryCallback) {
        mCallbacks.remove(iHistoryCallback);
    }

    //实现的dao的callback
    @Override
    public void onHistoryAdd(boolean isSuccess) {
        listHistories();
    }

    @Override
    public void onHistoryDel(boolean isSuccess) {
        if (isDoDelAdOutOfSize && mCurrentAddTrack != null) {
            isDoDelAdOutOfSize = false;
            //添加当前的数据进到数据库里
            addHistory(mCurrentAddTrack);
        } else {
            listHistories();
        }
    }

    @Override
    public void onHistoryLoaded(final List<Track> tracks) {
        this.mCurrentHistories = tracks;
        LogUtil.d(TAG, "history size-->" + tracks.size());
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (IHistoryCallback callback : mCallbacks) {
                    callback.onHistoryLoaded(tracks);
                }
            }
        });
    }

    @Override
    public void onHistoriesClean(boolean isSuccess) {
        listHistories();
    }
}
