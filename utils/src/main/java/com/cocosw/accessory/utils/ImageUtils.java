package com.cocosw.accessory.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.view.View;

/**
 * ImageUtils
 * <ul>
 * convert between Bitmap, byte array, Drawable
 * <li>{@link #bitmapToByte(Bitmap)}</li>
 * <li>{@link #bitmapToDrawable(Bitmap)}</li>
 * <li>{@link #byteToBitmap(byte[])}</li>
 * <li>{@link #byteToDrawable(byte[])}</li>
 * <li>{@link #drawableToBitmap(Drawable)}</li>
 * <li>{@link #drawableToByte(Drawable)}</li>
 * </ul>
 * <ul>
 * get image
 * <li>{@link #getInputStreamFromUrl(String, int)}</li>
 * <li>{@link #getBitmapFromUrl(String, int)}</li>
 * <li>{@link #getDrawableFromUrl(String, int)}</li>
 * </ul>
 * <ul>
 * scale image
 * <li>{@link #scaleImageTo(Bitmap, int, int)}</li>
 * <li>{@link #scaleImage(Bitmap, float, float)}</li>
 * </ul>
 * 
 * @author Trinea 2012-6-27
 */
public class ImageUtils {

    /**
     * convert Bitmap to byte array
     * 
     * @param b
     * @return
     */
    public static byte[] bitmapToByte(Bitmap b) {
        if (b == null) {
            return null;
        }

        ByteArrayOutputStream o = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, o);
        return o.toByteArray();
    }

    /**
     * convert byte array to Bitmap
     * 
     * @param b
     * @return
     */
    public static Bitmap byteToBitmap(byte[] b) {
        return (b == null || b.length == 0) ? null : BitmapFactory.decodeByteArray(b, 0, b.length);
    }

    /**
     * convert Drawable to Bitmap
     * 
     * @param d
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable d) {
        return d == null ? null : ((BitmapDrawable)d).getBitmap();
    }

    /**
     * convert Bitmap to Drawable
     * 
     * @param b
     * @return
     */
    public static Drawable bitmapToDrawable(Bitmap b) {
        return b == null ? null : new BitmapDrawable(b);
    }

    /**
     * convert Drawable to byte array
     * 
     * @param d
     * @return
     */
    public static byte[] drawableToByte(Drawable d) {
        return bitmapToByte(drawableToBitmap(d));
    }

    /**
     * convert byte array to Drawable
     * 
     * @param b
     * @return
     */
    public static Drawable byteToDrawable(byte[] b) {
        return bitmapToDrawable(byteToBitmap(b));
    }

    /**
     * get input stream from network by imageurl, you need to close inputStream yourself
     * 
     * @param imageUrl
     * @param readTimeOut read time out, if less than 0, not set
     * @return
     * @throws MalformedURLException
     * @throws IOException
     */
    public static InputStream getInputStreamFromUrl(String imageUrl, int readTimeOut) {
        InputStream stream = null;
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            if (readTimeOut > 0) {
                con.setReadTimeout(readTimeOut);
            }
            stream = con.getInputStream();
        } catch (MalformedURLException e) {
            closeInputStream(stream);
            throw new RuntimeException("MalformedURLException occurred. ", e);
        } catch (IOException e) {
            closeInputStream(stream);
            throw new RuntimeException("IOException occurred. ", e);
        }
        return stream;
    }

    /**
     * get drawable by imageUrl
     * 
     * @param imageUrl
     * @param readTimeOut read time out, if less than 0, not set
     * @return
     */
    public static Drawable getDrawableFromUrl(String imageUrl, int readTimeOut) {
        InputStream stream = getInputStreamFromUrl(imageUrl, readTimeOut);
        Drawable d = Drawable.createFromStream(stream, "src");
        closeInputStream(stream);
        return d;
    }

    /**
     * get Bitmap by imageUrl
     * 
     * @param imageUrl
     * @return
     */
    public static Bitmap getBitmapFromUrl(String imageUrl, int readTimeOut) {
        InputStream stream = getInputStreamFromUrl(imageUrl, readTimeOut);
        Bitmap b = BitmapFactory.decodeStream(stream);
        closeInputStream(stream);
        return b;
    }

    /**
     * scale image
     * 
     * @param org
     * @param newWidth
     * @param newHeight
     * @return
     */
    public static Bitmap scaleImageTo(Bitmap org, int newWidth, int newHeight) {
        return scaleImage(org, (float)newWidth / org.getWidth(), (float)newHeight / org.getHeight());
    }

    /**
     * scale image
     * 
     * @param org
     * @param scaleWidth sacle of width
     * @param scaleHeight scale of height
     * @return
     */
    public static Bitmap scaleImage(Bitmap org, float scaleWidth, float scaleHeight) {
        if (org == null) {
            return null;
        }

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(org, 0, 0, org.getWidth(), org.getHeight(), matrix, true);
    }

    /**
     * close inputStream
     * 
     * @param s
     */
    private static void closeInputStream(InputStream s) {
        if (s != null) {
            try {
                s.close();
            } catch (IOException e) {
                throw new RuntimeException("IOException occurred. ", e);
            }
        }
    }

    public static Bitmap cropToSquare(final Bitmap bitmap) {
        if (bitmap != null)// make a square!
        {
            if (bitmap.getWidth() > bitmap.getHeight()) {
                return Bitmap.createBitmap(bitmap,
                        (bitmap.getWidth() - bitmap.getHeight()) / 2, 0,
                        bitmap.getHeight(), bitmap.getHeight());
            } else if (bitmap.getWidth() < bitmap.getHeight()) {
                return Bitmap.createBitmap(bitmap, 0,
                        (bitmap.getHeight() - bitmap.getWidth()) / 2,
                        bitmap.getWidth(), bitmap.getWidth());
            }
        }
        return bitmap;
    }

    public static Bitmap getThumbnail(final ContentResolver contentResolver,
                                      final long id) {
        final Cursor cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.MediaColumns.DATA }, // Which columns
                // to return
                BaseColumns._ID + "=?", // Which rows to return
                new String[] { String.valueOf(id) }, // Selection arguments
                null);// order

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            final String filepath = cursor.getString(0);
            cursor.close();
            int rotation = 0;

            try {
                final ExifInterface exifInterface = new ExifInterface(filepath);
                final int exifRotation = exifInterface.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);

                if (exifRotation != ExifInterface.ORIENTATION_UNDEFINED) {
                    switch (exifRotation) {
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotation = 180;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotation = 270;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotation = 90;
                            break;
                    }
                }
            } catch (final IOException e) {
            }

            Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
                    contentResolver, id,
                    MediaStore.Images.Thumbnails.MINI_KIND, null);

            if (rotation != 0) {
                final Matrix matrix = new Matrix();
                matrix.setRotate(rotation);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), matrix, true);
            }

            return bitmap;
        } else {
            return null;
        }
    }


    /**
     * @param view
     * @param res
     */
    public static void setTileBg(final View view, final int res) {
        final Bitmap bitmap = BitmapFactory.decodeResource(view.getContext()
                .getResources(), res);
        final BitmapDrawable drawable = new BitmapDrawable(view.getContext()
                .getResources(), bitmap);
        drawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        drawable.setDither(true);
        drawable.mutate();
        view.setBackgroundDrawable(drawable);
    }

    public static void fixBackgroundRepeat(final View view) {
        final Drawable bg = view.getBackground();
        if (bg != null) {
            if (bg instanceof BitmapDrawable) {
                final BitmapDrawable bmp = (BitmapDrawable) bg;
                bmp.mutate(); // make sure that we aren't sharing state anymore
                bmp.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            }
        }
    }
}
