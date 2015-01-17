package com.cocosw.adapter;

import android.app.Activity;
import android.database.Cursor;

/**
 * Project: Accessory
 * Created by LiaoKai(soarcn) on 2015/1/17.
 */
public class TestAdapter extends SingleTypeCursorAdapter {
    public TestAdapter(Activity activity, Cursor cursor, int flags, int layoutResourceId) {
        super(activity, cursor, flags, layoutResourceId);
    }

    @Override
    protected int[] getChildViewIds() {
        return new int[0];
    }
}
