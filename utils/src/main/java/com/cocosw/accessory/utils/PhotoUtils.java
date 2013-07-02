package com.cocosw.accessory.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import java.io.File;

/**
 * 和图像资源相关的一些工具类
 * 
 * @author Administrator
 * 
 */
public class PhotoUtils {

    public static final int REQUEST_PICK_IMAGE = 0x120;
    public static final int REQUEST_TAKE_PHOTO = 0x123;
    public static final int REQUEST_CROP_IMAGE = 0x121;

    private final Activity context;
    private Uri mImageUri;
    private String tag;
    private final Fragment fg;

    public PhotoUtils(Activity context) {
	this.context = context;
	fg = null;
    }

    public PhotoUtils(Fragment fg) {
	context = fg.getActivity();
	this.fg = fg;
    }

    /**
     * 截取图片
     * 
     * @param photo
     * @param ax
     * @param ay
     * @param x
     * @param y
     */
    public void cropPhoto(final File photo, final int ax, final int ay,
	    final int x, final int y, boolean faceDetec, String... tag) {
	setTag(tag);
	final Intent intent = new Intent("com.android.camera.action.CROP");
	intent.setDataAndType(Uri.fromFile(photo), "image/*");
	intent.putExtra("crop", "true");
	// 比例
	intent.putExtra("aspectX", ax);
	intent.putExtra("aspectY", ay);
	// 大小
	intent.putExtra("outputX", x);
	intent.putExtra("outputY", y);

	intent.putExtra("noFaceDetection", faceDetec);

	intent.putExtra("output", Uri.fromFile(photo));
	intent.putExtra("outputFormat", "JPEG");

	startActivity(intent, REQUEST_CROP_IMAGE);
    }

    /**
     * 获取标记
     * 
     * @return
     */
    public String getTag() {
	return tag;
    }

    private File getTempFile() {
	final File file = new File(FileUtils.getCacheDir(context), "tmp_photo_"
		+ System.currentTimeMillis() + ".jpg");
	mImageUri = Uri.fromFile(file);
	return file;
    }

    /**
     * 辅助实现
     * 
     * @param requestCode
     * @param resultCode
     * @param intent
     * @return
     */
    public File onActivityResult(final int requestCode, final int resultCode,
	    final Intent intent) {
	if (resultCode != Activity.RESULT_OK) {
	    mImageUri = null;
	    return null;
	}
	Utils.dout(mImageUri);
	switch (requestCode) {
	case REQUEST_TAKE_PHOTO:
	case REQUEST_CROP_IMAGE: {
	    final File file = new File(mImageUri.getPath());
	    return file;
	}
	case REQUEST_PICK_IMAGE: {
	    final Uri uri = intent.getData();
	    final File file = uri == null ? null : new File(
		    Utils.getImagePathFromUri(context, uri));
	    if (file != null && file.exists())
		return file;
	    else {
		mImageUri = null;
	    }
	    break;
	}
	default: {
	    return null;
	}
	}
	return null;
    }

    /**
     * 打开图库
     */
    public void pickImage(String... tag) {
	setTag(tag);
	final Intent i = new Intent(Intent.ACTION_PICK,
		MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
	startActivity(i, REQUEST_PICK_IMAGE);
    }

    /**
     * 选择图库照片
     * 
     * @param ax
     * @param ay
     * @param x
     * @param y
     */
    public void pickImage(final int ax, final int ay, final int x, final int y,
	    String... tag) {
	setTag(tag);
	Intent intent;
	intent = new Intent(Intent.ACTION_GET_CONTENT, null);
	intent.setType("image/*");
	intent.putExtra("crop", "true");
	// 比例
	intent.putExtra("aspectX", ax);
	intent.putExtra("aspectY", ay);
	// 大小
	intent.putExtra("outputX", x);
	intent.putExtra("outputY", y);

	getTempFile();

	intent.putExtra("output", mImageUri);
	intent.putExtra("outputFormat", "JPEG");

	startActivity(intent, REQUEST_CROP_IMAGE);
    }

    /**
     * 选择图库照片
     * 
     */
    public void pickImage(final int x, final int y, String... tag) {
	pickImage(x, y, x, y, tag);
    }

    private void startActivity(Intent intent, int requestCode) {
	try {
	    if (fg != null) {
		fg.startActivityForResult(intent, requestCode);
	    } else {
		context.startActivityForResult(intent, requestCode);
	    }
	} catch (final ActivityNotFoundException e) {
	    e.printStackTrace();
	}
    }

    private void setTag(String[] tag2) {
	if (tag2.length != 0) {
	    tag = tag2[0];
	} else {
	    tag = null;
	}
    }

    /**
     * 打开相机拍照片
     *
     * @return
     */
    public File takePhoto(String... tag) {
	setTag(tag);
	final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	final File file = getTempFile();
	intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageUri);
	startActivity(intent, REQUEST_TAKE_PHOTO);
	return file;
    }

    public void cropPhoto(File mPhotoFile, int width, int height, boolean b,
	    String bg) {
	cropPhoto(mPhotoFile, width, height, width, height, b, tag);
    }
}
