package com.example.himalaya.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.R;
import com.example.himalaya.utils.LogUtil;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.List;

public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.InnerHolder> {

    private static final String TAG = "AlbumListAdapter";
    private List<Album> mData = new ArrayList<>();
    private OnAlbumItemClickListener mItemClickListener = null;
    private OnAlbumItemLongClickListener mLongClickListener = null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //加载View
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend, parent, false);

        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        //设置数据
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    int clickPosition = (int) v.getTag();
                    mItemClickListener.onItemClick(clickPosition, mData.get(clickPosition));
                }
                LogUtil.d(TAG, "holder.itemView click -- >" + v.getTag());
            }
        });
        holder.setData(mData.get(position));
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //true表示消费了该事件
                if (mLongClickListener != null) {
                    int clickPosition =  (int) v.getTag();
                    mLongClickListener.onItemLongClick(mData.get(clickPosition));
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        //返回要显示的个数
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    public void setData(List<Album> albumList) {
        //每次都展示最新的数据
        if (mData != null) {
            mData.clear();
        }
        mData.addAll(albumList);
        //更新UI
        notifyDataSetChanged();
    }


    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setData(Album album) {
            //找到各个控件，设置数据
            //专辑封面
            ImageView albumCoverIv = itemView.findViewById(R.id.album_cover);
            //title
            TextView albumTitleTv = itemView.findViewById(R.id.album_title_tv);
            //描述
            TextView albumDescriptionTv = itemView.findViewById(R.id.album_description_tv);
            //播放数量
            TextView albumPlayCountTv = itemView.findViewById(R.id.album_play_count);
            //专辑内容数量
            TextView albumContentCountTv = itemView.findViewById(R.id.album_content_size);

            albumTitleTv.setText(album.getAlbumTitle());
            albumDescriptionTv.setText(album.getAlbumIntro());
            long playCount = album.getPlayCount() / 10000;
            albumPlayCountTv.setText(playCount + "万");
            albumContentCountTv.setText(album.getIncludeTrackCount() + "集");
            //使用Picasso设置专辑封面
            String coverUrlLarge = album.getCoverUrlLarge();
            if (!TextUtils.isEmpty(coverUrlLarge)) {
                Picasso.with(itemView.getContext()).load(coverUrlLarge).into(albumCoverIv);
            } else {
                albumCoverIv.setImageResource(R.mipmap.ximalay_logo);
            }
        }
    }

    public void setOnAlbumItemClickListener(OnAlbumItemClickListener listener){
        this.mItemClickListener = listener;
    }

    public interface OnAlbumItemClickListener {
        void onItemClick(int position, Album album);
    }

    public void setOnAlbumItemLongClickListener(OnAlbumItemLongClickListener longClickListener) {
        this.mLongClickListener = longClickListener;
    }
    /**
     * Item长按事件接口
     */
    public interface OnAlbumItemLongClickListener {
        void onItemLongClick( Album album);
    }
}
