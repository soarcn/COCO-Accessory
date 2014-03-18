package com.cocosw.accessory.views;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

/**
 * Helper for email input field
 *
 * User: Liao Kai
 * Date: 13-8-12
 * Time: 下午10:32
 */
public class EmailTextViewHelper
{

    //<uses-permission android:name="android.permission.GET_ACCOUNTS" />
    // change the EditText to an AutoCompleteTextView and call setAdapter(getEmailAddressAdapter(context)) on that View.
    // http://jdamcd.com/email-auto-complete/
    public static void setEmailAddressAdapter(AutoCompleteTextView textView) {
        Account[] accounts = AccountManager.get(textView.getContext()).getAccountsByType("com.google");
        String[] addresses = new String[accounts.length];
        for (int i = 0; i < accounts.length; i++) {
            addresses[i] = accounts[i].name;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(textView.getContext(), android.R.layout.simple_dropdown_item_1line, addresses);
        textView.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        textView.setSelectAllOnFocus(true);
        textView.setAdapter(adapter);

    }
}
