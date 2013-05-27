package com.cocosw.accessory.views;

import android.view.View;

public interface ItemViewClickLisener {

    /**
     * List中某个View被点击时触发的事件,需要先注册Lisener,利用setOnViewClickInList
     * 
     * @param position
     * @param view
     */
    public void onItemViewClick(final int position, final View view);
}