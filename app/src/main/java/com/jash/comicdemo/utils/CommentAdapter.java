package com.jash.comicdemo.utils;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentAdapter<T> extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private Context context;
    private List<T> data;
    private Map<Class<?>, Pair<Integer, Integer>> map;

    public CommentAdapter(Context context, List<T> data, Map<Class<?>, Pair<Integer, Integer>> map) {
        this.context = context;
        this.data = data;
        this.map = map;
    }

    public CommentAdapter(Context context, List<T> data, int layoutId, int variableId) {
        this.context = context;
        this.data = data;
        map = new HashMap<>();
        map.put(null, Pair.create(layoutId, variableId));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(context), viewType, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        T t = data.get(position);
        holder.binding.setVariable(getType(t).second, t);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return getType(data.get(position)).first;
    }

    private Pair<Integer, Integer> getType(T t) {
        if (map.size() == 1) {
            return map.get(null);
        } else {
            return map.get(t.getClass());
        }
    }
    public boolean contains(T t) {
        return data.contains(t);
    }
    public void add(T t, Comparator<T> comparator) {
        data.add(t);
        Collections.sort(data, comparator);
        notifyItemInserted(data.indexOf(t));
    }
    public void add(T t) {
        add(data.size(), t);
    }

    public void add(int position, T t) {
        data.add(position, t);
        notifyItemInserted(position);
    }

    public void addAll(Collection<? extends T> collection) {
        addAll(data.size(), collection);
    }

    public void addAll(int position, Collection<? extends T> collection) {
        data.addAll(position, collection);
        notifyItemRangeInserted(position, collection.size());
    }

    public void clear() {
        int size = data.size();
        data.clear();
        notifyItemRangeRemoved(0, size);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ViewDataBinding binding;

        public ViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
