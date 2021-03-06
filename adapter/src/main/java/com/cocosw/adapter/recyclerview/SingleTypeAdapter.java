/*
 * Copyright 2012 Kevin Sawicki <kevinsawicki@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cocosw.adapter.recyclerview;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Adapter for lists where only a single view type is used
 *
 * @param <V>
 */
public abstract class SingleTypeAdapter<V> extends TypeAdapter {

    private final LayoutInflater inflater;

    private final int layout;

    private final int[] children;

    private List<V> items;

    /**
     * Create adapter
     *
     * @param activity
     * @param layoutResourceId
     */
    public SingleTypeAdapter(final Activity activity, final int layoutResourceId) {
        this(activity.getLayoutInflater(), layoutResourceId);
    }

    /**
     * Create adapter
     *
     * @param context
     * @param layoutResourceId
     */
    public SingleTypeAdapter(final Context context, final int layoutResourceId) {
        this(LayoutInflater.from(context), layoutResourceId);
    }

    /**
     * Create adapter
     *
     * @param inflater
     * @param layoutResourceId
     */
    public SingleTypeAdapter(final LayoutInflater inflater,
                             final int layoutResourceId) {
        this.inflater = inflater;
        this.layout = layoutResourceId;

        items = new ArrayList<>();

        int[] childIds = getChildViewIds();
        if (childIds == null)
            childIds = new int[0];
        children = childIds;
    }

    /**
     * Get a list of all items
     *
     * @return list of all items
     */
    protected List<V> getItems() {
        return items;
    }

    /**
     * Set items to display
     *
     * @param items
     */
    public void setItems(final Collection<V> items) {
        if (items != null && !items.isEmpty())
            this.items = new ArrayList<>(items);
        else
            this.items = new ArrayList<>();
    }

    /**
     * Set items to display
     *
     * @param items
     */
    public void setItems(final List<V> items) {
        if (items != null && !items.isEmpty())
            this.items = items;
        else
            this.items = new ArrayList<>();
    }

    /**
     * Set items to display
     *
     * @param items
     */
    public void setItems(final V[] items) {
        if (items != null)
            this.items = new ArrayList<>(Arrays.asList(items));
        else
            this.items = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public V getItem(final int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return getItem(position).hashCode();
    }

    /**
     * Get child view ids to store
     * <p/>
     * The index of each id in the returned array should be used when using the
     * helpers to update a specific child view
     *
     * @return ids
     */
    protected abstract int[] getChildViewIds();

    /**
     * Initialize view
     *
     * @param view
     * @return view
     */
    protected View initialize(final View view) {
        return super.initialize(view, children);
    }

    /**
     * Update view for item
     *
     * @param position
     * @param view
     * @param item
     */
    protected void update(int position, View view, V item) {
        setCurrentView(view);
        update(position, item);
    }

    /**
     * Update item
     *
     * @param position
     * @param item
     */
    protected abstract void update(int position, V item);

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new GenericVH(initialize(inflater.inflate(layout, null)));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder genericVH, int position) {
        update(position, genericVH.itemView, getItem(position));
    }

}
