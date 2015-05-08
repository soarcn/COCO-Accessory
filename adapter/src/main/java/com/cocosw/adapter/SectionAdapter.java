package com.cocosw.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * A very simple adapter that adds sections to adapters written for {@link ListView}s.
 * <br />
 * <b>NOTE: The adapter assumes that the data source of the decorated list adapter is sorted.</b>
 *
 * @author Ragunath Jawahar R <rj@mobsandgeeks.com>
 * @version 0.2
 */
public class SectionAdapter<T, K> extends BaseAdapter {

    // Debug
    static final boolean DEBUG = false;
    static final String TAG = SimpleSectionAdapter.class.getSimpleName();

    private final ViewUpdater updater = new ViewUpdater();

    // Constants
    private static final int VIEW_TYPE_SECTION_HEADER = 0;
    private final int[] mSectionViewIds;

    // Attributes
    private Context mContext;
    private BaseAdapter mListAdapter;
    private int mSectionHeaderLayoutId;
    private Sectionizer<T, K> mSectionizer;
    private LinkedHashMap<K, Integer> mSections;
    private View[] childViews;

    /**
     * Constructs a {@linkplain SimpleSectionAdapter}.
     *
     * @param context               The context for this adapter.
     * @param listAdapter           A {@link ListAdapter} that has to be sectioned.
     * @param sectionHeaderLayoutId Layout Id of the layout that is to be used for the header.
     * @param sectionViewIds        Ids of the section header layout.
     * @param sectionizer           Sectionizer for sectioning the {@link ListView}.
     */
    public SectionAdapter(Context context, BaseAdapter listAdapter,
                          int sectionHeaderLayoutId, int[] sectionViewIds,
                          Sectionizer<T, K> sectionizer) {
        if (context == null) {
            throw new IllegalArgumentException("context cannot be null.");
        } else if (listAdapter == null) {
            throw new IllegalArgumentException("listAdapter cannot be null.");
        } else if (sectionizer == null) {
            throw new IllegalArgumentException("sectionizer cannot be null.");
        }

        this.mContext = context;
        this.mListAdapter = listAdapter;
        this.mSectionHeaderLayoutId = sectionHeaderLayoutId;
        this.mSectionizer = sectionizer;
        this.mSections = new LinkedHashMap();
        this.mSectionViewIds = sectionViewIds;

        // Find sections
        findSections();
    }


    @Override
    public int getCount() {
        return mListAdapter.getCount() + getSectionCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        switch (getItemViewType(position)) {
            case VIEW_TYPE_SECTION_HEADER:
                if (view == null) {
                    view = View.inflate(mContext, mSectionHeaderLayoutId, null);
                    initialize(view, mSectionViewIds);
                }
                updater.setCurrentView(view);
                mSectionizer.update(position, updater, sectionTitleForPosition(position));
                return view;

            default:
                view = mListAdapter.getView(getIndexForPosition(position),
                        convertView, parent);
                break;
        }


        return view;
    }

    /**
     * Initialize view by binding indexed child views to tags on the root view
     * <p/>
     * Sub-classes may override this method but must call super
     *
     * @param view
     * @param children
     * @return view
     */
    public View initialize(final View view, final int[] children) {
        final View[] views = new View[children.length];
        for (int i = 0; i < children.length; i++)
            views[i] = view.findViewById(children[i]);
        view.setTag(views);
        childViews = views;
        return view;
    }


    @Override
    public boolean areAllItemsEnabled() {
        return mListAdapter.areAllItemsEnabled() && mSections.size() == 0;
    }

    @Override
    public int getItemViewType(int position) {
        int positionInCustomAdapter = getIndexForPosition(position);
        return mSections.values().contains(position) ?
                VIEW_TYPE_SECTION_HEADER :
                mListAdapter.getItemViewType(positionInCustomAdapter) + 1;
    }

    @Override
    public int getViewTypeCount() {
        return mListAdapter.getViewTypeCount() + 1;
    }

    @Override
    public boolean isEnabled(int position) {
        return !mSections.values().contains(position) && mListAdapter.isEnabled(getIndexForPosition(position));
    }

    @Override
    public Object getItem(int position) {
        return mListAdapter.getItem(getIndexForPosition(position));
    }

    @Override
    public long getItemId(int position) {
        return mListAdapter.getItemId(getIndexForPosition(position));
    }

    @Override
    public void notifyDataSetChanged() {
        mListAdapter.notifyDataSetChanged();
        findSections();
        super.notifyDataSetChanged();
    }

    /**
     * Returns the actual index of the object in the data source linked to the this list item.
     *
     * @param position List item position in the {@link ListView}.
     * @return Index of the item in the wrapped list adapter's data source.
     */
    public int getIndexForPosition(int position) {
        int nSections = 0;

        Set<Map.Entry<K, Integer>> entrySet = mSections.entrySet();
        for (Map.Entry<K, Integer> entry : entrySet) {
            if (entry.getValue() < position) {
                nSections++;
            }
        }

        return position - nSections;
    }

    private void findSections() {
        int n = mListAdapter.getCount();
        int nSections = 0;
        mSections.clear();

        for (int i = 0; i < n; i++) {
            K sectionName = mSectionizer.getSectionTitleForItem((T) mListAdapter.getItem(i));

            if (!mSections.containsKey(sectionName)) {
                mSections.put(sectionName, i + nSections);
                nSections++;
            }
        }

        if (DEBUG) {
            Log.d(TAG, String.format("Found %d sections.", mSections.size()));
        }
    }

    private int getSectionCount() {
        return mSections.size();
    }

    private K sectionTitleForPosition(int position) {
        K title = null;

        Set<Map.Entry<K, Integer>> entrySet = mSections.entrySet();
        for (Map.Entry<K, Integer> entry : entrySet) {
            if (entry.getValue() == position) {
                title = entry.getKey();
                break;
            }
        }
        return title;
    }

    @Override
    public boolean isEmpty() {
        return mListAdapter.isEmpty();
    }

    /**
     * Interface provides mechanism for supplying titles for instances based on the property they are
     * compared against. The parameterized type of the <b>Sectionizer</b> should be same as that of the
     * {@link SimpleSectionAdapter}.
     *
     * @author Ragunath Jawahar R <rj@mobsandgeeks.com>
     * @version 1.0
     */
    public interface Sectionizer<T, K> {

        /**
         * Returns the title for the given instance from the data source.
         *
         * @param instance The instance obtained from the data source of the decorated list adapter.
         * @return section title for the given instance.
         */
        K getSectionTitleForItem(T instance);

        /**
         * Update view for item
         *
         * @param position
         * @param updater
         * @param o
         */
        void update(int position, ViewUpdater updater, K o);
    }

}

