package com.cocosw.accessory.views;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * User: Administrator
 * Date: 13-8-12
 * Time: 下午10:32
 */
public class AdapterUtils {

    //<uses-permission android:name="android.permission.GET_ACCOUNTS" />
    // change the EditText to an AutoCompleteTextView and call setAdapter(getEmailAddressAdapter(context)) on that View.
    // http://jdamcd.com/email-auto-complete/
    private ArrayAdapter<String> getEmailAddressAdapter(Context context) {
        Account[] accounts = AccountManager.get(context).getAccountsByType("com.google");
        String[] addresses = new String[accounts.length];
        for (int i = 0; i < accounts.length; i++) {
            addresses[i] = accounts[i].name;
        }
        return new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, addresses);
    }
}
