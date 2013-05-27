/*
 * Copyright 2012 Google Inc.
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

package com.cocosw.accessory.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

/**
 * A set of helper methods for showing contextual help information in the app.
 */
public class HelpUtils {

	public static boolean hasSeenTutorial(final Context context, final String id) {
		final SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		return sp.getBoolean("seen_tutorial_" + id, false);
	}

	private static void setSeenTutorial(final Context context, final String id) {
		// noinspection unchecked
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(final Void... voids) {
				final SharedPreferences sp = PreferenceManager
						.getDefaultSharedPreferences(context);
				sp.edit().putBoolean("seen_tutorial_" + id, true).commit();
				return null;
			}
		}.execute();
	}

	public static boolean needShowHelp(final Context context, final String id) {
		final boolean need = !HelpUtils.hasSeenTutorial(context, id);
		if (need) {
			HelpUtils.setSeenTutorial(context, id);
		}
		return need;
	}

}
