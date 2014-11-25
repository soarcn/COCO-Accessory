package com.cocosw.accessory.views;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ScrollView;

import com.cocosw.accessory.views.complex.WebView;
import com.cocosw.accessory.views.layout.ObservableScrollView;

import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Project: Accessory
 * Created by LiaoKai(soarcn) on 2014/11/9.
 */
public class QuickReturn {

    private Object header;
    private Object footer;
    private Callback headecallback;
    private Callback footercallback;
    private boolean autohide;


    public QuickReturn header(View header) {
        return header(header, new ViewCallback());
    }

    public QuickReturn footer(View footer) {
        return footer(footer, new ViewCallback());
    }

    public <T> QuickReturn header(T obj, Callback<T> callback) {
        this.header = obj;
        this.headecallback = callback;
        return this;
    }

    public <T> QuickReturn footer(T obj, Callback<T> callback) {
        this.footer = obj;
        this.footercallback = callback;
        return this;
    }

    private QuickReturnType getReturnType() {
        if (header != null && footer != null) {
            return QuickReturnType.BOTH;
        }
        if (header != null)
            return QuickReturnType.HEADER;
        if (footer != null)
            return QuickReturnType.FOOTER;
        throw new IllegalArgumentException("You must provide header/footer view for QuickReturn");
    }

    public AbsListView.OnScrollListener listener(boolean autohide) {
        QuickReturnListViewOnScrollListener listener = new QuickReturnListViewOnScrollListener();
        listener.mQuickReturnType = getReturnType();
        listener.mCanSlideInIdleScrollState = autohide;
        if (header == null && footer == null)
            throw new IllegalArgumentException("You must provide header/footer view for QuickReturn");
        listener.q = this;
        return listener;
    }

    public ObservableScrollView.OnScrollChangedListener listener() {
        QuickReturnScrollViewOnScrollChangedListener listener = new QuickReturnScrollViewOnScrollChangedListener();
        listener.mQuickReturnType = getReturnType();
        if (header == null && footer == null)
            throw new IllegalArgumentException("You must provide header/footer view for QuickReturn");
        listener.q = this;
        return listener;
    }

    public WebView.OnScrollChangedListener weblistener() {
        QuickReturnWebViewOnScrollChangedListener listener = new QuickReturnWebViewOnScrollChangedListener();
        listener.mQuickReturnType = getReturnType();
        if (header == null && footer == null)
            throw new IllegalArgumentException("You must provide header/footer view for QuickReturn");
        listener.q = this;
        return listener;
    }

    public void apply(@NonNull AbsListView listView, boolean autohide) {

        listView.setOnScrollListener(listener(autohide));
    }

    public void apply(@NonNull ObservableScrollView scrollView) {
        scrollView.setOnScrollChangedListener(listener());
    }

    public void apply(@NonNull WebView webView) {
        webView.setOnScrollChangedListener(weblistener());
    }

    public interface Callback<T> {

        /**
         * Notify view(or other ui component) could show, the offset will be provided for further transform
         *
         * @param view
         * @param offset
         */
        public void show(T view, int offset);

        /**
         * Notify view(or other ui component) could hide, the offset will be provided for further transform
         *
         * @param view
         * @param offset
         */
        public void hide(T view, int offset);
    }

    private class ViewCallback implements Callback<View> {

        @Override
        public void show(View view, int offset) {
            ViewCompat.setTranslationY(view, offset);
        }

        @Override
        public void hide(View view, int offset) {
            ViewCompat.setTranslationY(view, offset);
        }
    }


    private enum QuickReturnType {
        HEADER(0x1),
        FOOTER(0x2),
        BOTH(0x4);
        private final int value;

        QuickReturnType(int i) {
            value = i;
        }

        int value() {
            return value;
        }
    }


    private class QuickReturnScrollViewOnScrollChangedListener implements ObservableScrollView.OnScrollChangedListener {

        // region Member Variables
        private int mMinFooterTranslation;
        private int mMinHeaderTranslation;
        private int mHeaderDiffTotal = 0;
        private int mFooterDiffTotal = 0;
        private QuickReturnType mQuickReturnType;
        public QuickReturn q;
        // endregion

        @Override
        public void onScrollChanged(ScrollView who, int l, int t, int oldl, int oldt) {
            int diff = oldt - t;
            if (diff == 0)
                return;
            if (diff < 0) { //scrolling down, showing
                if ((mQuickReturnType.value() & QuickReturnType.HEADER.value()) == QuickReturnType.HEADER.value()) {
                    mHeaderDiffTotal = Math.max(mHeaderDiffTotal + diff, mMinHeaderTranslation);
                    q.headecallback.show(q.header, mHeaderDiffTotal);
                }
                if ((mQuickReturnType.value() & QuickReturnType.FOOTER.value()) == QuickReturnType.FOOTER.value()) {
                    mFooterDiffTotal = Math.max(mFooterDiffTotal + diff, -mMinFooterTranslation);
                    q.footercallback.show(q.header, -mFooterDiffTotal);
                }
            } else {
                if ((mQuickReturnType.value() & QuickReturnType.HEADER.value()) == QuickReturnType.HEADER.value()) {
                    mHeaderDiffTotal = Math.max(mHeaderDiffTotal + diff, mMinHeaderTranslation);
                    q.headecallback.hide(q.header, mHeaderDiffTotal);
                }
                if ((mQuickReturnType.value() & QuickReturnType.FOOTER.value()) == QuickReturnType.FOOTER.value()) {
                    mFooterDiffTotal = Math.min(Math.max(mFooterDiffTotal + diff, -mMinFooterTranslation), 0);
                    q.footercallback.hide(q.footer, -mFooterDiffTotal);
                }
            }
        }
    }

    private class QuickReturnWebViewOnScrollChangedListener implements WebView.OnScrollChangedListener {

        // region Member Variables
        private int mMinFooterTranslation;
        private int mMinHeaderTranslation;
        private int mHeaderDiffTotal = 0;
        private int mFooterDiffTotal = 0;
        private QuickReturnType mQuickReturnType;
        public QuickReturn q;
        // endregion

        @Override
        public void onScrollChanged(WebView who, int l, int t, int oldl, int oldt) {
            int diff = oldt - t;
            if (diff == 0)
                return;
            if (diff < 0) { //scrolling down, showing
                if ((mQuickReturnType.value() & QuickReturnType.HEADER.value()) == QuickReturnType.HEADER.value()) {
                    mHeaderDiffTotal = Math.max(mHeaderDiffTotal + diff, mMinHeaderTranslation);
                    q.headecallback.show(q.header, mHeaderDiffTotal);
                }
                if ((mQuickReturnType.value() & QuickReturnType.FOOTER.value()) == QuickReturnType.FOOTER.value()) {
                    mFooterDiffTotal = Math.max(mFooterDiffTotal + diff, -mMinFooterTranslation);
                    q.footercallback.show(q.header, -mFooterDiffTotal);
                }
            } else {
                if ((mQuickReturnType.value() & QuickReturnType.HEADER.value()) == QuickReturnType.HEADER.value()) {
                    mHeaderDiffTotal = Math.max(mHeaderDiffTotal + diff, mMinHeaderTranslation);
                    q.headecallback.hide(q.header, mHeaderDiffTotal);
                }
                if ((mQuickReturnType.value() & QuickReturnType.FOOTER.value()) == QuickReturnType.FOOTER.value()) {
                    mFooterDiffTotal = Math.min(Math.max(mFooterDiffTotal + diff, -mMinFooterTranslation), 0);
                    q.footercallback.hide(q.footer, -mFooterDiffTotal);
                }
            }
        }
    }


    private class QuickReturnListViewOnScrollListener implements AbsListView.OnScrollListener {

        // region Member Variables
        private int mMinFooterTranslation;
        private int mMinHeaderTranslation;
        private int mPrevScrollY = 0;
        private int mHeaderDiffTotal = 0;
        private int mFooterDiffTotal = 0;
        private View mHeader;
        private View mFooter;
        private QuickReturnType mQuickReturnType;
        private boolean mCanSlideInIdleScrollState = false;
        public QuickReturn q;

        // endregion


        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

            if (scrollState == SCROLL_STATE_IDLE && mCanSlideInIdleScrollState) {
                int midHeader = -mMinHeaderTranslation / 2;
                int midFooter = mMinFooterTranslation / 2;
                if ((mQuickReturnType.value() & QuickReturnType.HEADER.value()) == QuickReturnType.HEADER.value()) {
                    if (-mHeaderDiffTotal > 0 && -mHeaderDiffTotal < midHeader) {
                        ViewCompat.animate(mHeader).translationY(0).setDuration(100).start();
                        mHeaderDiffTotal = 0;
                    } else if (-mHeaderDiffTotal < -mMinHeaderTranslation && -mHeaderDiffTotal >= midHeader) {
                        ViewCompat.animate(mHeader).translationY(mMinHeaderTranslation).setDuration(100).start();
                        mHeaderDiffTotal = mMinHeaderTranslation;
                    }
                }
                if ((mQuickReturnType.value() & QuickReturnType.FOOTER.value()) == QuickReturnType.FOOTER.value()) {
                    if (-mFooterDiffTotal > 0 && -mFooterDiffTotal < midFooter) { // slide up
                        ViewCompat.animate(mHeader).translationY(0).setDuration(100).start();
                        mFooterDiffTotal = 0;
                    } else if (-mFooterDiffTotal < mMinFooterTranslation && -mFooterDiffTotal >= midFooter) { // slide down
                        ViewCompat.animate(mHeader).translationY(mMinFooterTranslation).setDuration(100).start();
                        mFooterDiffTotal = -mMinFooterTranslation;
                    }
                }
            }
        }

        @Override
        public void onScroll(AbsListView listview, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            // apply extra on scroll listener
            int scrollY = getScrollY(listview);
            int diff = mPrevScrollY - scrollY;
            if (diff == 0)
                return;
            if (diff < 0) { // scrolling down
                if ((mQuickReturnType.value() & QuickReturnType.HEADER.value()) == QuickReturnType.HEADER.value()) {
                    mHeaderDiffTotal = Math.max(mHeaderDiffTotal + diff, mMinHeaderTranslation);
                    q.headecallback.show(q.header, mHeaderDiffTotal);
                }
                if ((mQuickReturnType.value() & QuickReturnType.FOOTER.value()) == QuickReturnType.FOOTER.value()) {
                    mFooterDiffTotal = Math.max(mFooterDiffTotal + diff, -mMinFooterTranslation);
                    q.footercallback.show(q.footer, -mFooterDiffTotal);
                }

            } else {
                if ((mQuickReturnType.value() & QuickReturnType.HEADER.value()) == QuickReturnType.HEADER.value()) {
                    mHeaderDiffTotal = Math.min(Math.max(mHeaderDiffTotal + diff, mMinHeaderTranslation), 0);
                    q.headecallback.hide(q.header, mHeaderDiffTotal);
                }
                if ((mQuickReturnType.value() & QuickReturnType.FOOTER.value()) == QuickReturnType.FOOTER.value()) {
                    mFooterDiffTotal = Math.min(Math.max(mFooterDiffTotal + diff, -mMinFooterTranslation), 0);
                    q.footercallback.hide(q.footer, -mFooterDiffTotal);
                }
            }
            mPrevScrollY = scrollY;
        }

    }

    private static int getScrollY(AbsListView lv) {
        Dictionary<Integer, Integer> sListViewItemHeights = new Hashtable<Integer, Integer>();
        View c = lv.getChildAt(0);
        if (c == null) {
            return 0;
        }
        int firstVisiblePosition = lv.getFirstVisiblePosition();
        int scrollY = -(c.getTop());
        sListViewItemHeights.put(lv.getFirstVisiblePosition(), c.getHeight());
        if (scrollY < 0)
            scrollY = 0;
        for (int i = 0; i < firstVisiblePosition; ++i) {
            if (sListViewItemHeights.get(i) != null) // (this is a sanity check)
                scrollY += sListViewItemHeights.get(i); //add all heights of the views that are gone
        }
        return scrollY;
    }

}
