package com.cocosw.accessory.views.adapter;

/*
 * Copyright 2013 Simon Vig Therkildsen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.util.LongSparseArray;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.view.ViewTreeObserver;
import android.widget.Adapter;
import android.widget.AdapterView;

@TargetApi(18)
public final class AdapterViewAnimator {


    /**
     * Callback used for providing custom animations
     */
    public interface Callback {


        /**
         * Use to handle animating adding a view.
         *
         * @param parent   The AdapterView where the view is being added.
         * @param view     The view that is being added.
         * @param position The position of the item the view represents.
         * @param id       The id of the item the view represents.
         * @return True if a custom animation has been started, false to use default.
         */
        boolean onAddView(AdapterView parent, View view, int position, long id);


        /**
         * Use to handle animating adding a view.
         *
         * @param parent      The AdapterView where the view is being moved.
         * @param view        The view that is being moved.
         * @param position    The position of the item the view represents.
         * @param id          The id of the item the view represents.
         * @param startBounds The bounds of the view before the dataset changed.
         * @param endAction   An action that must be executed when the custom animation finishes.
         * @return True if a custom animation has been started, false to use default.
         */
        boolean onMoveView(AdapterView parent, View view, int position, long id, Rect startBounds,
                           Runnable endAction);


        /**
         * Use to handle animating adding a view.
         *
         * @param parent      The AdapterView where the view is being removed.
         * @param view        The view that is being removed.
         * @param id          The id of the item the view represents.
         * @param startBounds The bounds of the view before the dataset changed.
         * @return True if a custom animation has been started, false to use default.
         */
        boolean onRemoveView(AdapterView parent, View view, long id, Rect startBounds);
    }


    private static final int DURATION_ADD = 350;
    private static final int DURATION_REMOVE = 250;
    private static final int DURATION_MOVE = 300;


    private AdapterView adapterView;


    private LongSparseArray<Rect> viewBounds = new LongSparseArray<Rect>();


    private LongSparseArray<View> idToViewMap = new LongSparseArray<View>();


    private boolean animateCalled;


    private ViewGroupOverlay overlay;


    private Callback callback;


    public AdapterViewAnimator(AdapterView adapterView) {
        this(adapterView, null);
    }


    public AdapterViewAnimator(AdapterView adapterView, Callback callback) {
        this.adapterView = adapterView;
        this.callback = callback;
        beforeDataSetChanged();
    }


    private void beforeDataSetChanged() {
        Adapter adapter = adapterView.getAdapter();
        final int firstVisiblePosition = adapterView.getFirstVisiblePosition();
        for (int i = 0, childCount = adapterView.getChildCount(); i < childCount; i++) {
            final int position = firstVisiblePosition + i;
            final long id = adapter.getItemId(position);
            final View child = adapterView.getChildAt(i);
            Rect r = new Rect(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
            ViewCompat.setHasTransientState(child, false);
            viewBounds.put(id, r);
            idToViewMap.put(id, child);
        }
    }


    public void animate() {
        if (animateCalled) {
            throw new RuntimeException("animate must only be called once");
        }
        animateCalled = true;


        final ViewTreeObserver observer = adapterView.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                observer.removeOnPreDrawListener(this);


                Adapter adapter = adapterView.getAdapter();
                final int firstVisiblePosition = adapterView.getFirstVisiblePosition();
                for (int i = 0, childCount = adapterView.getChildCount(); i < childCount; i++) {
                    final int position = firstVisiblePosition + i;
                    final long id = adapter.getItemId(position);
                    idToViewMap.remove(id);
                    final View child = adapterView.getChildAt(i);


                    final Rect bounds = viewBounds.get(id);
                    Runnable endAction = new Runnable() {
                        @Override
                        public void run() {
                            ViewCompat.setHasTransientState(child, false);
                        }
                    };
                    if (bounds != null) {
                        if (callback == null || !callback.onMoveView(adapterView, child, position, id, bounds,
                                endAction)) {
                            final int dx = bounds.left - child.getLeft();
                            final int dy = bounds.top - child.getTop();
                            ViewCompat.setTranslationX(child, dx);
                            ViewCompat.setTranslationY(child, dy);
                            ViewCompat.animate(child)
                                    .setDuration(DURATION_MOVE)
                                    .translationX(0.0f)
                                    .translationY(0.0f)
                                    .withEndAction(endAction);
                        }
                    } else {
                        if (callback == null || !callback.onAddView(adapterView, child, position, id)) {
                            ViewCompat.setAlpha(child, 0.0f);
                            ViewCompat.animate(child).setDuration(DURATION_ADD).alpha(1.0f);
                        }
                    }
                }


                int[] adapterViewLocation = new int[2];
                int[] hostViewLocation = new int[2];
                final int size = idToViewMap.size();
                for (int i = 0; i < size; i++) {
                    final long id = idToViewMap.keyAt(i);
                    final View child = idToViewMap.get(id);
                    ViewCompat.setHasTransientState(child, false);
                    final View viewCopy = new ViewCopy(child);
                    Rect bounds = viewBounds.get(id);


                    if (overlay == null) {
                        ViewGroup parent = (ViewGroup) adapterView.getParent();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
                            overlay = parent.getOverlay();
                        adapterView.getLocationOnScreen(adapterViewLocation);
                        parent.getLocationOnScreen(hostViewLocation);
                    }


                    overlay.add(viewCopy);
                    viewCopy.offsetLeftAndRight(adapterViewLocation[0] - hostViewLocation[0]);
                    viewCopy.offsetTopAndBottom(adapterViewLocation[1] - hostViewLocation[1]);


                    if (callback == null || !callback.onRemoveView(adapterView, viewCopy, id, bounds)) {
                        ViewCompat.animate(viewCopy)
                                .setDuration(DURATION_REMOVE)
                                .alpha(0.0f)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        overlay.remove(viewCopy);
                                    }
                                });
                    }
                }


                return true;
            }
        });
    }


    public class ViewCopy extends View {


        private Bitmap viewCopy;


        public ViewCopy(View view) {
            super(view.getContext());
            copyView(view);
            measure(MeasureSpec.makeMeasureSpec(view.getWidth(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(view.getHeight(), MeasureSpec.EXACTLY));
            layout(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        }


        private void copyView(View view) {
            Bitmap b = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            view.draw(c);
            viewCopy = b;
        }


        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawBitmap(viewCopy, 0.0f, 0.0f, null);
        }


        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
                    MeasureSpec.getSize(heightMeasureSpec));
        }
    }
}
