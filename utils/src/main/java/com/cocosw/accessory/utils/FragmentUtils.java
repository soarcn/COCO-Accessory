package com.cocosw.accessory.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

public class FragmentUtils {

    public static void addFragmentToStack(final FragmentActivity act,
                                          final int id, final Fragment newFragment) {
        final FragmentTransaction ft = act.getSupportFragmentManager()
                .beginTransaction();
        ft.replace(id, newFragment);
        try {
            ft.addToBackStack(null).commit();
        } catch (final Exception ignored) {
        }

    }

    public static void replaceFragment(final FragmentActivity act,
                                       final int id, final Fragment newFragment) {
        final FragmentTransaction ft = act.getSupportFragmentManager()
                .beginTransaction();
        try {
            ft.replace(id, newFragment).commit();
        } catch (final Exception ignored) {
        }

    }

    public static void addFragmentToStack(final FragmentActivity act,
                                          final int id, final Class<? extends Fragment> clz) {
        FragmentUtils.addFragmentToStack(act, id,
                Fragment.instantiate(act, clz.getName()));
    }

    public static void replaceFragment(final FragmentActivity act,
                                       final int id, final Class<? extends Fragment> clz) {
        FragmentUtils.replaceFragment(act, id,
                Fragment.instantiate(act, clz.getName()));
    }

    public static <T extends Fragment> T findFragment(FragmentActivity activity, int resId) {
        return (T) activity.getSupportFragmentManager().findFragmentById(resId);
    }

    public static <T extends Fragment> T findFragment(FragmentActivity activity, String tag) {
        return (T) activity.getSupportFragmentManager().findFragmentByTag(tag);
    }
}
