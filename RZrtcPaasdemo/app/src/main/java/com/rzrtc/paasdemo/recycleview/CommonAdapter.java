package com.rzrtc.paasdemo.recycleview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class CommonAdapter<DATA> extends RecyclerView.Adapter<CommonHolder> {
    private int resID;
    private ResType resType;
    private Context mContext;
    private LayoutInflater inflater;
    private List<DATA> data = new CopyOnWriteArrayList<>();
    public CommonAdapter(Context context, int resID) {
        Log.e(TAG, "CommonAdapter: " );
        mContext = context;
        inflater = LayoutInflater.from(context);
        this.resID = resID;
    }

    public CommonAdapter(Context context, ResType resType) {
        mContext = context;
        inflater = LayoutInflater.from(context);
        this.resType = resType;
    }

    public List<DATA> getData() {
        return data;
    }

    private static final String TAG = "CommonAdapter";

    public void setData(List<DATA> data) {
        this.data.clear();
        this.data.addAll(data);
        Log.d(TAG, "setData: " + data.size());
        notifyDataSetChanged();
    }

    public void setData(int position, DATA data) {
        Log.e(TAG, "setData: "+position );
        this.data.set(position, data);
        notifyItemChanged(position);
    }


    public void addData(DATA data) {
        Log.e(TAG, "addData: "+data );
        this.data.add(data);
        notifyItemInserted(this.data.indexOf(data));
        changePosition(this.data.indexOf(data));
    }
    public void addFirst(DATA data) {
        this.data.add(0,data);
        notifyItemInserted(this.data.indexOf(data));
        changePosition(this.data.indexOf(data));
    }

    public void removeData(DATA data) {
        int index = this.data.indexOf(data);
        Log.d(TAG, "crash_ removeData: " + index+" data.size"+this.data.size());
        this.data.remove(index);
        notifyItemRemoved(index);
        changePosition(index);
        Log.d(TAG, "crash_ notifyItemRemoved: " + index);
    }


    @Override
    public CommonHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (resType != null) {
            resID = resType.getResId();
        }
        Log.e(TAG, "onCreateViewHolder: " );
        View inflate = inflater.inflate(resID, parent, false);
        return new CommonHolder(inflate);
    }

    @Override
    public void onBindViewHolder(final CommonHolder holder, final int position) {
        Log.d(TAG, "crash_ onBindViewHolder: " + position+" data.size"+this.data.size());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onClick(data.get(position), position);
                }
            }
        });
        bindData(holder, data.get(position), position);
    }
    public void changePosition(int videoPosition) {
        if (videoPosition != this.data.size()) {
            notifyItemRangeChanged(videoPosition, this.data.size() - videoPosition);
        }
    }
    protected abstract void bindData(CommonHolder holder, DATA data, int position);

    @Override
    public int getItemCount() {
        Log.d(TAG, "crash_ getItemCount:  data.size"+this.data.size());
        return data.size();
    }

    private ItemClickListener<DATA> mListener;

    public void setItemClickListener(ItemClickListener<DATA> listener) {
        mListener = listener;
    }


}
