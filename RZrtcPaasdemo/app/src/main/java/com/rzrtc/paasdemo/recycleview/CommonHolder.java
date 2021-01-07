package com.rzrtc.paasdemo.recycleview;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CommonHolder extends RecyclerView.ViewHolder {
    private Resources resources;
    public SparseArray<View> viewMap = new SparseArray<>();

    public CommonHolder(View itemView) {
        super(itemView);
        resources = itemView.getResources();
    }

    public <V extends View> V findView(int resId) {
        View view = viewMap.get(resId);
        if (view == null) {
            View findView = itemView.findViewById(resId);
            view = findView;
            viewMap.put(resId, view);
        }
        return (V) view;
    }

    public CommonHolder setText(int resId, String text) {
        TextView view = findView(resId);
        view.setText(text);
        return this;
    }

    public CommonHolder setVisible(int resId, int visible) {
        View view = findView(resId);
        view.setVisibility(visible);
        return this;
    }

    public CommonHolder setBackGround(int resId, ChangeBackground background) {
        View view = findView(resId);
        background.change(view);
        return this;
    }

    public CommonHolder setBackGroundColor(int resId, int color) {
        View view = findView(resId);
        view.setBackgroundColor(color);
        return this;
    }

    public CommonHolder setImage(int viewId, int resId) {
        ImageView view = findView(viewId);
        view.setImageResource(resId);
        return this;
    }

    public CommonHolder setTextColor(int viewId, int color) {
        TextView view = findView(viewId);
        view.setTextColor(resources.getColor(color));
        return this;
    }

    public CommonHolder setTextDrawable(int viewId, int drawable) {
        TextView view = findView(viewId);
        Drawable leftDrawable = resources.getDrawable(drawable);
        leftDrawable.setBounds(0, 0, leftDrawable.getMinimumWidth(),
                leftDrawable.getMinimumHeight());
        view.setCompoundDrawablesRelative(leftDrawable, null, null, null);
        return this;
    }

    public CommonHolder setOnClickListener(int viewId, View.OnClickListener l) {
        View view = findView(viewId);
        view.setOnClickListener(l);
        return this;
    }

    public void addView(int iconContainer, @Nullable View view) {
        ViewGroup container = ((ViewGroup) findView(iconContainer));
        if (view != null && view.getParent() == null)
            container.addView(view);
    }

    public void removeView(int iconContainer, @NotNull View view) {
        ViewGroup container = ((ViewGroup) findView(iconContainer));
        if (view.getParent() != null) {
            container.removeView(view);
        }
    }
    public void removeAllView(int iconContainer) {
        ViewGroup container = ((ViewGroup) findView(iconContainer));
            container.removeAllViews();
    }
}
