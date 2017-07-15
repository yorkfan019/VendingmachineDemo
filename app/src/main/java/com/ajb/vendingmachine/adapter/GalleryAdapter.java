package com.ajb.vendingmachine.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ajb.vendingmachine.R;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Administrator on 2017/7/2.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Integer> mDatas;
    private int mRecyclerViewWidth;

    public GalleryAdapter(Context context, List<Integer> datats) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mDatas = datats;
    }

    public GalleryAdapter(Context context, List<Integer> datats ,int recyclerViewWidth) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mDatas = datats;
        mRecyclerViewWidth = recyclerViewWidth;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View arg0) {
            super(arg0);
        }

        ImageView mImg;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.activity_index_gallery_item,
                viewGroup, false);
        view.getLayoutParams().width = mRecyclerViewWidth/5;
        view.getLayoutParams().height = mRecyclerViewWidth/5;
        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.mImg = (ImageView) view
                .findViewById(R.id.id_index_gallery_item_image);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
//        viewHolder.mImg.setImageResource(mDatas.get(i));
        Glide.with(mContext).load(mDatas.get(i)).fitCenter().into(viewHolder.mImg);
        //如果设置了回调，则设置点击事件
        if(mOnItemClickListener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onItemClick(viewHolder.itemView,i);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }
}
