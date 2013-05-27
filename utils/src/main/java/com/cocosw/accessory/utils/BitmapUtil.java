package com.cocosw.accessory.utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.*;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.view.View;

import java.io.*;


public final class BitmapUtil {
	public static Bitmap decodeFile(final String filepath, final int size,
			final boolean square) {
		return BitmapUtil.decodeFile(new File(filepath), size, square);
	}

	public static Bitmap decodeFile(final File file, final int size,
			final boolean square) {
		try {
			// decode image size
			BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
			bitmapOptions.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(file), null,
					bitmapOptions);

			// Find the correct scale value. It should be the power of 2.
			if (size > 0) {
				int width_tmp = bitmapOptions.outWidth, height_tmp = bitmapOptions.outHeight;
				int scale = 1;
				while (true) {
					if (width_tmp / 2 < size || height_tmp / 2 < size) {
						break;
					}
					width_tmp /= 2;
					height_tmp /= 2;
					scale++;
				}
				// decode with inSampleSize
				bitmapOptions = new BitmapFactory.Options();
				bitmapOptions.inSampleSize = scale;
				bitmapOptions.inScaled = true;
				if (square) {
					return BitmapUtil.cropToSquare(BitmapFactory.decodeFile(
							file.getAbsolutePath(), bitmapOptions));
				}
				return BitmapFactory.decodeFile(file.getAbsolutePath(),
						bitmapOptions);
			}
			return null;
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
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
				new String[] { MediaColumns.DATA }, // Which columns
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

	public static void createScaledImage(final String sourceFile,
			final String destinationFile, int desiredWidth,
			final int desiredHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(sourceFile, options);

		int srcWidth = options.outWidth;
		if (desiredWidth > srcWidth) {
			desiredWidth = srcWidth;
		}

		int inSampleSize = 1;
		while (srcWidth / 2 > desiredWidth) {
			srcWidth /= 2;
			inSampleSize *= 2;
		}

		final float desiredScale = (float) desiredWidth / srcWidth;

		options.inJustDecodeBounds = false;
		options.inDither = false;
		options.inSampleSize = inSampleSize;
		options.inScaled = false;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Bitmap sampledSrcBitmap = BitmapFactory.decodeFile(sourceFile, options);

		final Matrix matrix = new Matrix();
		matrix.postScale(desiredScale, desiredScale);
		Bitmap scaledBitmap = Bitmap.createBitmap(sampledSrcBitmap, 0, 0,
				sampledSrcBitmap.getWidth(), sampledSrcBitmap.getHeight(),
				matrix, true);
		sampledSrcBitmap = null;

		try {
			final FileOutputStream out = new FileOutputStream(destinationFile);
			scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, out);
			scaledBitmap = null;
		} catch (final IOException e) {
			e.printStackTrace();
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
		drawable.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
		drawable.setDither(true);
		drawable.mutate();
		view.setBackgroundDrawable(drawable);
	}

	// public static Bitmap createRepeater(View view,Bitmap src){
	// int wcount = (view.getWidth() + src.getWidth() - 1) /src.getWidth();
	// Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
	// src.getHeight(),Config.ARGB_8888);
	// Canvas canvas = new Canvas(bitmap);
	// for (int idx = 0; idx < wcount; ++ idx) {
	// canvas.drawBitmap(src, idx * src.getWidth(), 0, null);
	// }
	// return bitmap;
	// 　　 }

	public static void fixBackgroundRepeat(final View view) {
		final Drawable bg = view.getBackground();
		if (bg != null) {
			if (bg instanceof BitmapDrawable) {
				final BitmapDrawable bmp = (BitmapDrawable) bg;
				bmp.mutate(); // make sure that we aren't sharing state anymore
				bmp.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
			}
		}
	}

	public static Bitmap fromByte(final byte[] data) {
		if (data == null || data.length == 0) {
			return null;
		}
		return BitmapFactory.decodeByteArray(data, 0, data.length);
	}

	public static Bitmap drawableToBitmap(final Drawable drawable) {
		if (drawable == null || drawable.getIntrinsicWidth() <= 0
				|| drawable.getIntrinsicHeight() < 0) {
			return null;
		}
		final Bitmap bitmap = Bitmap
				.createBitmap(
						drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight(),
						drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
								: Bitmap.Config.RGB_565);
		final Canvas canvas = new Canvas(bitmap);

		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;

	}

	public static byte[] toByte(final Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
		return out.toByteArray();
	}

	public static Drawable toDrawable(final Bitmap bitmap) {
		return new BitmapDrawable(bitmap);
	}
}
