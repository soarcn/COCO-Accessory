package com.cocosw.accessory.views;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Wrapper for bundle, enable chain style programming
 */
public class CocoBundle {

    private static final String INDEX = "_index";
    private static CocoBundle builder;
    private Bundle bundle;

    public CocoBundle(final Bundle bundle) {
        this.bundle = bundle;
    }

    public CocoBundle() {
        bundle = new Bundle();
    }

    public CocoBundle setBundle(final Bundle bundle) {
        this.bundle = bundle;
        return this;
    }

    public static CocoBundle builder(final Bundle bundle) {
        if (CocoBundle.builder == null) {
            CocoBundle.builder = new CocoBundle(bundle);
        }
        CocoBundle.builder.setBundle(bundle);
        return CocoBundle.builder;
    }

    public CocoBundle setIndex(final long index) {
        return put(CocoBundle.INDEX, index);
    }

    public long getIndex() {
        return bundle == null ? -1 : bundle.getLong(CocoBundle.INDEX, -1);
    }

    public CocoBundle put(final String key, final boolean value) {
        bundle.putBoolean(key, value);
        return this;
    }

    public CocoBundle put(final String key, final byte value) {
        bundle.putByte(key, value);
        return this;
    }

    public CocoBundle put(final String key, final char value) {
        bundle.putChar(key, value);
        return this;
    }

    public CocoBundle put(final String key, final short value) {
        bundle.putShort(key, value);
        return this;
    }

    public CocoBundle put(final String key, final int value) {
        bundle.putInt(key, value);
        return this;
    }

    public CocoBundle put(final String key, final long value) {
        bundle.putLong(key, value);
        return this;
    }

    public CocoBundle put(final String key, final float value) {
        bundle.putFloat(key, value);
        return this;
    }

    public CocoBundle put(final String key, final double value) {
        bundle.putDouble(key, value);
        return this;
    }

    public CocoBundle put(final String key, final String value) {
        bundle.putString(key, value);
        return this;
    }

    public CocoBundle put(final String key, final CharSequence value) {
        bundle.putCharSequence(key, value);
        return this;
    }

    public CocoBundle put(final String key, final Parcelable value) {
        bundle.putParcelable(key, value);
        return this;
    }

    public CocoBundle put(final String key,
                          final Parcelable[] value) {
        bundle.putParcelableArray(key, value);
        return this;
    }

    public CocoBundle put(final String key,
                          final ArrayList<? extends Parcelable> value) {
        bundle.putParcelableArrayList(key, value);
        return this;
    }

    public CocoBundle put(final String key,
                          final SparseArray<? extends Parcelable> value) {
        bundle.putSparseParcelableArray(key, value);
        return this;
    }

    public CocoBundle putIntegerArrayList(final String key,
                                          final ArrayList<Integer> value) {
        bundle.putIntegerArrayList(key, value);
        return this;
    }

    public CocoBundle putStringArrayList(final String key,
                                         final ArrayList<String> value) {
        bundle.putStringArrayList(key, value);
        return this;
    }

    public CocoBundle put(final String key, final Serializable value) {
        bundle.putSerializable(key, value);
        return this;
    }

    public CocoBundle put(final String key, final boolean[] value) {
        bundle.putBooleanArray(key, value);
        return this;
    }

    public CocoBundle put(final String key, final byte[] value) {
        bundle.putByteArray(key, value);
        return this;
    }

    public CocoBundle put(final String key, final short[] value) {
        bundle.putShortArray(key, value);
        return this;
    }

    public CocoBundle put(final String key, final char[] value) {
        bundle.putCharArray(key, value);
        return this;
    }

    public CocoBundle put(final String key, final int[] value) {
        bundle.putIntArray(key, value);
        return this;
    }

    public CocoBundle put(final String key, final long[] value) {
        bundle.putLongArray(key, value);
        return this;
    }

    public CocoBundle put(final String key, final float[] value) {
        bundle.putFloatArray(key, value);
        return this;
    }

    public CocoBundle put(final String key, final double[] value) {
        bundle.putDoubleArray(key, value);
        return this;
    }

    public CocoBundle put(final String key, final String[] value) {
        bundle.putStringArray(key, value);
        return this;
    }

    public CocoBundle put(final String key, final Bundle value) {
        bundle.putBundle(key, value);
        return this;
    }

    public Bundle getBundle() {
        return bundle;
    }

}
