package com.cocosw.accessory.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

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

    private static final String TAG = "ImageUtils";

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
        return d == null ? null : ((BitmapDrawable) d).getBitmap();
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
     * Изменение размера пропорционально
     *
     * @param image Bitmap
     * @param max_w int
     * @param max_h int
     * @return Bitmap
     */
    public static Bitmap scale(Bitmap image, int max_w, int max_h) {
        float width = image.getWidth();
        float height = image.getHeight();
        float scale = 1f;
        if (width > height) {
            if (width > max_w) {
                scale = max_w / width;
            }
        } else {
            if (height > max_h) {
                scale = max_h / height;
            }
        }
        if (scale != 1f) {
            int w = (int) (width * scale);
            int h = (int) (height * scale);
            return Bitmap.createScaledBitmap(image, w, h, false);
        } else {
            return image;
        }
    }

    public static String getImagePathFromUri(final Context context,
                                             final Uri uri) {
        if (context == null || uri == null) {
            return null;
        }

        final String media_uri_start = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                .toString();

        if (uri.toString().startsWith(media_uri_start)) {

            final String[] proj = {MediaStore.MediaColumns.DATA};
            final Cursor cursor = context.getContentResolver().query(uri, proj,
                    null, null, null);

            if (cursor == null || cursor.getCount() <= 0) {
                return null;
            }

            final int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

            cursor.moveToFirst();

            final String path = cursor.getString(column_index);
            cursor.close();
            return path;
        } else {
            final String path = uri.getPath();
            if (path != null) {
                if (new File(path).exists()) {
                    return path;
                }
            }
        }
        return null;
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
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
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
        return scaleImage(org, (float) newWidth / org.getWidth(), (float) newHeight / org.getHeight());
    }

    /**
     * scale image
     *
     * @param org
     * @param scaleWidth  sacle of width
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
                new String[]{MediaStore.MediaColumns.DATA}, // Which columns
                // to return
                BaseColumns._ID + "=?", // Which rows to return
                new String[]{String.valueOf(id)}, // Selection arguments
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

    /**
     * Call {@link BitmapFactory#decodeFile(String, android.graphics.BitmapFactory.Options)}, retrying up to 4 times with an increased
     * {@link android.graphics.BitmapFactory.Options#inSampleSize} if an {@link OutOfMemoryError} occurs.<br/>
     * If after trying 4 times the file still could not be decoded, {@code null} is returned.
     *
     * @param imageFile The file to be decoded.
     * @param options The Options object passed to {@link BitmapFactory#decodeFile(String, android.graphics.BitmapFactory.Options)} (can be {@code null}).
     * @return The decoded bitmap, or {@code null} if it could not be decoded.
     */
    public static Bitmap tryDecodeFile(File imageFile, BitmapFactory.Options options) {
        Log.d(TAG, "tryDecodeFile imageFile=" + imageFile);
        int trials = 0;
        while (trials < 4) {
            try {
                Bitmap res = BitmapFactory.decodeFile(imageFile.getPath(), options);
                if (res == null) {
                    Log.d(TAG, "tryDecodeFile res=null");
                } else {
                    Log.d(TAG, "tryDecodeFile res width=" + res.getWidth() + " height=" + res.getHeight());
                }
                return res;
            } catch (OutOfMemoryError e) {
                if (options == null) {
                    options = new BitmapFactory.Options();
                    options.inSampleSize = 1;
                }
                Log.w(TAG, "tryDecodeFile Could not decode file with inSampleSize=" + options.inSampleSize + ", try with inSampleSize="
                        + (options.inSampleSize + 1), e);
                options.inSampleSize++;
                trials++;
            }
        }
        Log.w(TAG, "tryDecodeFile Could not decode the file after " + trials + " trials, returning null");
        return null;
    }

    /**
     * Returns an immutable version of the given bitmap.<br/>
     * The given bitmap is recycled. A temporary file is used (using {@link File#createTempFile(String, String)}) to avoid allocating twice the needed memory.
     */
    public static Bitmap asImmutable(Bitmap bitmap) throws IOException {
        // This is the file going to use temporally to dump the bitmap bytes
        File tmpFile = File.createTempFile(String.valueOf(System.currentTimeMillis()), null);
        Log.d(TAG, "getImmutable tmpFile=" + tmpFile);

        // Open it as an RandomAccessFile
        RandomAccessFile randomAccessFile = new RandomAccessFile(tmpFile, "rw");

        // Get the width and height of the source bitmap
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // Dump the bytes to the file.
        // This assumes the source bitmap is loaded using options.inPreferredConfig = Config.ARGB_8888 (hence the value of 4 bytes per pixel)
        FileChannel channel = randomAccessFile.getChannel();
        MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, width * height * 4);
        bitmap.copyPixelsToBuffer(buffer);

        // Recycle the source bitmap, this will be no longer used
        bitmap.recycle();

        // Create a new mutable bitmap to load the bitmap from the file
        bitmap = Bitmap.createBitmap(width, height, bitmap.getConfig());

        // Load it back from the temporary buffer
        buffer.position(0);
        bitmap.copyPixelsFromBuffer(buffer);

        // Cleanup
        channel.close();
        randomAccessFile.close();
        tmpFile.delete();

        return bitmap;
    }

    /**
     * List of EXIF tags used by {@link #copyExifTags(File, File)}.
     */
    //@formatter:off
    @SuppressLint("InlinedApi")
    private static final String[] EXIF_TAGS = new String[] {
            ExifInterface.TAG_APERTURE,
            ExifInterface.TAG_DATETIME,
            ExifInterface.TAG_EXPOSURE_TIME,
            ExifInterface.TAG_FLASH,
            ExifInterface.TAG_FOCAL_LENGTH,
            ExifInterface.TAG_GPS_ALTITUDE,
            ExifInterface.TAG_GPS_ALTITUDE_REF,
            ExifInterface.TAG_GPS_DATESTAMP,
            ExifInterface.TAG_GPS_LATITUDE,
            ExifInterface.TAG_GPS_LATITUDE_REF,
            ExifInterface.TAG_GPS_LONGITUDE,
            ExifInterface.TAG_GPS_LONGITUDE_REF,
            ExifInterface.TAG_GPS_PROCESSING_METHOD,
            ExifInterface.TAG_GPS_TIMESTAMP,
            ExifInterface.TAG_ISO,
            ExifInterface.TAG_MAKE,
            ExifInterface.TAG_MODEL,
            ExifInterface.TAG_WHITE_BALANCE,
    };
    //@formatter:on

    /**
     * Copy the EXIF tags from the source image file to the destination image file.
     *
     * @param sourceFile The existing source JPEG file.
     * @param destFile The existing destination JPEG file.
     * @throws IOException If EXIF information could not be read or written.
     */
    public static void copyExifTags(File sourceFile, File destFile) throws IOException {
        Log.d(TAG, "copyExifTags sourceFile=" + sourceFile + " destFile=" + destFile);
        ExifInterface sourceExifInterface = new ExifInterface(sourceFile.getPath());
        ExifInterface destExifInterface = new ExifInterface(destFile.getPath());
        boolean atLeastOne = false;
        for (String exifTag : EXIF_TAGS) {
            String value = sourceExifInterface.getAttribute(exifTag);
            if (value != null) {
                atLeastOne = true;
                destExifInterface.setAttribute(exifTag, value);
            }
        }
        if (atLeastOne) destExifInterface.saveAttributes();
    }

    /**
     * Retrieves the dimensions of the bitmap in the given file.
     *
     * @param bitmapFile The file containing the bitmap to measure.
     * @return A {@code Point} containing the width in {@code x} and the height in {@code y}.
     */
    public static Point getDimensions(File bitmapFile) {
        Log.d(TAG, "getDimensions bitmapFile=" + bitmapFile);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(bitmapFile.getPath(), options);
        int width = options.outWidth;
        int height = options.outHeight;
        Point res = new Point(width, height);
        Log.d(TAG, "getDimensions res=" + res);
        return res;
    }

    /**
     * Retrieves the rotation in the EXIF tags of the given file.
     *
     * @param bitmapFile The file from which to retrieve the info.
     * @return The rotation in degrees, or {@code 0} if there was no EXIF tags in the given file, or it could not be read.
     */
    public static int getExifRotation(File bitmapFile) {
        Log.d(TAG, "getExifRotation bitmapFile=" + bitmapFile);
        ExifInterface exifInterface;
        try {
            exifInterface = new ExifInterface(bitmapFile.getPath());
        } catch (IOException e) {
            Log.w(TAG, "getExifRotation Could not read exif info: returning 0", e);
            return 0;
        }
        int exifOrientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
        Log.d(TAG, "getExifRotation orientation=" + exifOrientation);
        int res = 0;
        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                res = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                res = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                res = 270;
                break;
        }
        Log.d(TAG, "getExifRotation res=" + res);
        return res;
    }

    /**
     * Creates a small version of the bitmap inside the given file, using the given max dimensions.<br/>
     * The resulting bitmap's dimensions will always be smaller than the given max dimensions.<br/>
     * The rotation EXIF tag of the given file, if present, is used to return a thumbnail that won't be rotated.
     *
     * @param bitmapFile The file containing the bitmap to create a thumbnail from.
     * @param maxWidth The wanted maximum width of the resulting thumbnail.
     * @param maxHeight The wanted maximum height of the resulting thumbnail.
     * @return A small version of the bitmap, or (@code null} if the given bitmap could not be decoded.
     */
    public static Bitmap createThumbnail(File bitmapFile, int maxWidth, int maxHeight) {
        Log.d(TAG, "createThumbnail imageFile=" + bitmapFile + " maxWidth=" + maxWidth + " maxHeight=" + maxHeight);
        // Get exif rotation
        int rotation = getExifRotation(bitmapFile);

        // Determine optimal inSampleSize
        Point originalDimensions = getDimensions(bitmapFile);
        int width = originalDimensions.x;
        int height = originalDimensions.y;
        int inSampleSize = 1;
        if (rotation == 90 || rotation == 270) {
            // In these 2 cases we invert the measured dimensions because the bitmap is rotated
            width = originalDimensions.y;
            height = originalDimensions.x;
        }
        int widthRatio = width / maxWidth;
        int heightRatio = height / maxHeight;

        // Take the max, because we don't care if one of the returned thumbnail's side is smaller
        // than the specified maxWidth/maxHeight.
        inSampleSize = Math.max(widthRatio, heightRatio);
        Log.d(TAG, "createThumbnail using inSampleSize=" + inSampleSize);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        Bitmap res = tryDecodeFile(bitmapFile, options);
        if (res == null) {
            Log.w(TAG, "createThumbnail Could not decode file, returning null");
            return null;
        }

        // Rotate if necessary
        if (rotation != 0) {
            Log.d(TAG, "createThumbnail rotating thumbnail");
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            Bitmap rotatedBitmap = null;
            try {
                rotatedBitmap = Bitmap.createBitmap(res, 0, 0, res.getWidth(), res.getHeight(), matrix, false);
                res.recycle();
                res = rotatedBitmap;
            } catch (OutOfMemoryError exception) {
                Log.w(TAG, "createThumbnail Could not rotate bitmap, keeping original orientation", exception);
            }
        }
        Log.d(TAG, "createThumbnail res width=" + res.getWidth() + " height=" + res.getHeight());

        return res;
    }

}
