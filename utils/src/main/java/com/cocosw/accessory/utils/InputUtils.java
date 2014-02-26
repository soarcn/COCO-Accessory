package com.cocosw.accessory.utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用于记录回复清空用户输入信息的辅助类
 *
 * @author sanyuan
 */
public class InputUtils {

    private static final int TOAST_DISPLAY_TIME = 3000;
    private final Activity act;
    private final ArrayList<TextView> list;

    public InputUtils(final Activity act) {
        this.act = act;
        list = new ArrayList<TextView>();
    }

    public void remember(final TextView v) {
        list.add(v);
    }

    public void remember(final int id) {
        final TextView v = getTextView(id);
        if (v != null) {
            list.add(v);
        }
    }

    private TextView getTextView(final int id) {
        final View v = act.findViewById(id);
        if (v != null && v instanceof TextView) {
            return (TextView) v;
        }
        return null;
    }

    public void onPause() {
        final SharedPreferences.Editor editor = act.getPreferences(0).edit();
        for (final TextView v : list) {
            editor.putString(String.valueOf(v.getId()), v.getText().toString());
        }
        editor.commit();
    }

    public void onResume() {
        final SharedPreferences prefs = act.getPreferences(0);
        if (list.isEmpty()) {
            return;
        }
        for (final TextView v : list) {
            final String restoredText = prefs.getString(
                    String.valueOf(v.getId()), null);
            if (restoredText != null) {
                v.setText(restoredText);
            }
        }
    }

    public void clear() {
        final SharedPreferences.Editor editor = act.getPreferences(0).edit();
        for (final TextView v : list) {
            v.setText(null);
        }
        editor.clear();
        editor.commit();
    }

    public void shakeField(final View view, final String str) {
        final Animation shake = AnimationHelper.flash(null);
        view.startAnimation(shake);
        Toast.makeText(act, str, InputUtils.TOAST_DISPLAY_TIME).show();
    }

    /**
     * 验证email
     *
     * @param view
     * @return
     */
    public boolean validateEmailField(final TextView view) {
        if (TextUtils.isEmpty(view.getText())) {
            shakeField(view, "请输入正确的email信息");
            view.setFocusable(true);
            return false;
        }
        final String regEx = "([\\w[_-][\\.]]+@+[\\w[_-]]+\\.+[A-Za-z]{2,3})|([\\"
                + "w[_-][\\.]]+@+[\\w[_-]]+\\.+[\\w[_-]]+\\.+[A-Za-z]{2,3})|"
                + "([\\w[_-][\\.]]+@+[\\w[_-]]+\\.+[\\w[_-]]+\\.+[\\w[_-]]+"
                + "\\.+[A-Za-z]{2,3})";
        final Pattern p = Pattern.compile(regEx);
        final Matcher matcher = p.matcher(view.getText());
        if (matcher.matches()) {
            return true;
        } else {
            shakeField(view, "请输入正确的email格式");
            view.setFocusable(true);
            return false;
        }

    }

    public boolean validateEmailField(final int id) {
        return validateEmailField(getTextView(id));
    }

    /**
     * 验证输入长度
     *
     * @param etNickname
     * @param i
     * @return
     */
    public boolean validateMinimalField(final int id, final int size,
                                        final String str) {
        final TextView v = getTextView(id);
        if (v == null) {
            Toast.makeText(act, "输入信息不存在", InputUtils.TOAST_DISPLAY_TIME)
                    .show();
            return false;
        }

        if (TextUtils.isEmpty(v.getText())) {
            shakeField(v, "输入信息不存在");
            v.setFocusable(true);
            return false;
        }

        if (v.getText().length() < size) {
            if (TextUtils.isEmpty(str)) {
                shakeField(v, "输入信息需要大于" + size + "个字符");
            } else {
                shakeField(v, str);
            }
            v.setFocusable(true);
            return false;
        }
        return true;
    }

    public boolean validateMinimalField(final int id, final int size) {
        return validateMinimalField(id, size, null);
    }

    public boolean validateNumField(final int id) {
        final TextView v = getTextView(id);
        if (v == null) {
            Toast.makeText(act, "输入信息不存在", InputUtils.TOAST_DISPLAY_TIME)
                    .show();
            return false;
        }

        if (!TextUtils.isDigitsOnly(v.getText())) {
            shakeField(v, "只接受数字信息");
            v.setFocusable(true);
            return false;
        }
        return true;
    }

    public boolean validateMobilePhoneField(final int id) {
        final TextView v = getTextView(id);

        if (v == null) {
            Toast.makeText(act, "输入信息不存在", InputUtils.TOAST_DISPLAY_TIME)
                    .show();
            return false;
        }

        if (TextUtils.isEmpty(v.getText())) {
            shakeField(v, "输入信息不存在");
            v.setFocusable(true);
            return false;
        }
        final String regEx = "[1]{1}[3,5,8,6]{1}[0-9]{9}"; // 表示a或f
        final boolean p = Pattern.compile(regEx).matcher(v.getText()).find();
        if (!p) {
            shakeField(v, "请输入正确的手机号");
            v.setFocusable(true);
            return false;
        }
        return true;

    }

    public void moveCursorToEnd(final int id) {
        final TextView v = getTextView(id);
        ((EditText) v).setSelection(v.length() - 1);
    }

}
