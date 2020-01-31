package com.example.himalaya.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.himalaya.R;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DetailListAdapter extends RecyclerView.Adapter<DetailListAdapter.InnerHolder> {

    private List<Track> mDetailData = new ArrayList<>();

    //格式化时间
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat mDurationFormat = new SimpleDateFormat("mm:ss");

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_detail, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, final int position) {
        //找到控件，设置数据
        View itemView = holder.itemView;
        //顺序id
        TextView orderTv = itemView.findViewById(R.id.order_text);
        //标题
        TextView titleTv = itemView.findViewById(R.id.detail_item_title);
        //播放次数
        TextView playCountTv = itemView.findViewById(R.id.detail_item_play_count);
        //时长
        TextView durationTv = itemView.findViewById(R.id.detail_item_duration);
        //更新日期
        TextView updateDateTv = itemView.findViewById(R.id.detail_item_update_time);
        //设置数据
        Track track = mDetailData.get(position);
        orderTv.setText(position + 1 + "");
        titleTv.setText(track.getTrackTitle());
        playCountTv.setText(track.getPlayCount() + "");
        int durationMill = track.getDuration() * 1000;
        String duration = mDurationFormat.format(durationMill);
        durationTv.setText(duration);
        //格式化日期
        String updateTimeText = mSimpleDateFormat.format(track.getUpdatedAt());
        updateDateTv.setText(updateTimeText);

        //设置Item的点击事件
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "you click " + position + " item", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDetailData.size();
    }

    public void setData(List<Track> tracks) {
        //清除原来的数据
        mDetailData.clear();
        //添加新的数据
        mDetailData.addAll(tracks);
        //更新UI
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
