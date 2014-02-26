package com.cocosw.accessory.views;

import android.widget.GridView;

/**
 * a gridView which is not scrollable and can be used in scrollview
 * <p/>
 * Project: app-parent
 * User: Liao Kai(soarcn@gmail.com)
 * Date: 13-12-6
 * Time: 下午6:21
 */
public class FixedGridView extends GridView {

    public FixedGridView(android.content.Context context,
                         android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置不滚动
     */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

    }
}
