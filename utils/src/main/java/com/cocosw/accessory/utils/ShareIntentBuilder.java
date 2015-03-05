package com.cocosw.accessory.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;

/**
 * ShareIntentBuilder
 * Created by gabrieleporcelli on 27/01/15.
 * <p/>
 * Example:
 * <p/>
 * new ShareIntentBuilder(context)
 * .setType("image/*")
 * .setSubject("My Title or res id")
 * .setText("My Text or res id")
 * .setAttachment(myUri)
 * .setChooserTitle("Chooser Title")
 * .start();
 */
@SuppressWarnings("UnusedDeclaration")
public class ShareIntentBuilder {

    private Intent mIntent;
    private Context mContext;

    public ShareIntentBuilder(Context context) {
        mContext = context;
        mIntent = new Intent();
        mIntent.setAction(Intent.ACTION_SEND); //set default action
        mIntent.setType("*/*"); //set default type
    }

    public Intent getIntent() {
        return mIntent;
    }

    public ShareIntentBuilder setSubject(CharSequence subject) {
        mIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        return this;
    }

    public ShareIntentBuilder setSubject(Integer subjectId) {
        mIntent.putExtra(Intent.EXTRA_SUBJECT, getTextFromId(subjectId));
        return this;
    }

    public ShareIntentBuilder setText(CharSequence text) {
        mIntent.putExtra(Intent.EXTRA_TEXT, text);
        return this;
    }

    public ShareIntentBuilder setText(Integer textId) {
        mIntent.putExtra(Intent.EXTRA_TEXT, getTextFromId(textId));
        return this;
    }

    public ShareIntentBuilder setAttachment(Uri resourceUri) {
        mIntent.putExtra(Intent.EXTRA_STREAM, resourceUri);
        return this;
    }

    public ShareIntentBuilder setAttachment(BitmapDrawable mDrawable) {
        mIntent.putExtra(Intent.EXTRA_STREAM, getUriFromBitmapDrawable(mDrawable));
        return this;
    }

    public ShareIntentBuilder setAttachment(ImageView imageView) {
        mIntent.putExtra(Intent.EXTRA_STREAM, getUriFromImageview(imageView));
        return this;
    }

    /**
     * Specifies an explicit type (a MIME type) of the intent data. Normally the type is inferred from the data itself. By setting this attribute, you disable that evaluation and force an explicit type.
     */
    public ShareIntentBuilder setType(CharSequence charsequenceType) {
        mIntent.putExtra(Intent.EXTRA_STREAM, charsequenceType);
        return this;
    }

    public ShareIntentBuilder setChooserTitle(CharSequence chooserTitle) {
        Intent.createChooser(mIntent, chooserTitle);
        return this;
    }

    public ShareIntentBuilder setChooserTitle(Integer chooserTitleId) {
        mIntent = Intent.createChooser(mIntent, getTextFromId(chooserTitleId));
        return this;
    }

    public void start() {
        mContext.startActivity(mIntent);
    }


    //private methods
    private String getTextFromId(Integer textRes) {
        return mContext.getString(textRes);
    }

    private Uri getUriFromImageview(ImageView imageView) {
        Drawable mDrawable = imageView.getDrawable();
        return getUriFromBitmapDrawable((BitmapDrawable) mDrawable);
    }

    private Uri getUriFromBitmapDrawable(BitmapDrawable mDrawable) {
        Bitmap mBitmap = mDrawable.getBitmap();
        String path = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), mBitmap, "", null);
        return Uri.parse(path);
    }

}
