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
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		try {
			ft.addToBackStack(null).commit();
		} catch (final Exception e) {
		}

	}

	public static void replaceFragment(final FragmentActivity act,
			final int id, final Fragment newFragment) {
		final FragmentTransaction ft = act.getSupportFragmentManager()
				.beginTransaction();
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		try {
			ft.replace(id, newFragment).commit();
		} catch (final Exception e) {
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
}
