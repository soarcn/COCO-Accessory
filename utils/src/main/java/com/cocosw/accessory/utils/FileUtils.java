package com.cocosw.accessory.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.HashMap;

public class FileUtils {
    private static final String TAG = "FileUtils";
    private static String line;
    private static File filename;

    public static String SD = Environment.getExternalStorageDirectory()
            .getPath() + "/coco/";

    private static File phone_cache_dir;
    private static File sd_cache_dir;
    private static File currentDir;

    /**
     * 获得sd卡上的cache目录
     *
     * @param context
     * @return
     */
    public static File getSDCacheDir(final Context context) {
        if (FileUtils.sd_cache_dir != null) {
            return FileUtils.sd_cache_dir;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            FileUtils.sd_cache_dir = FileUtils.getCacheDir(context
                    .getExternalCacheDir());
        } else {
            FileUtils.sd_cache_dir = FileUtils.getCacheDir(new File(Environment
                    .getExternalStorageDirectory().getPath()
                    + "/Android/data/"
                    + context.getPackageName() + "/cache/"));
        }
        if (FileUtils.sd_cache_dir == null) {
            return FileUtils.getPhoneCacheDir(context);
        }
        return FileUtils.sd_cache_dir;
    }

    private static File getCacheDir(final File cacheDir) {
        try {
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            return cacheDir;
        } catch (final Exception e) {
            return null;
        }
    }


    static public String formatSize(long size) {
        String suffix = null;

        if (size >= 1024) {
            suffix = "KiB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MiB";
                size /= 1024;
            }
        }

        final StringBuilder resultBuffer = new StringBuilder(
                Long.toString(size));
        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null) {
            resultBuffer.append(suffix);
        }
        return resultBuffer.toString();
    }

    /**
     * 根据现在的sd卡状态,自动获得cache的目录
     *
     * @param context
     * @return
     */
    public static File getCacheDir(final Context context) {
        FileUtils.currentDir = FileUtils.hasSdCard() ? FileUtils
                .getSDCacheDir(context) : FileUtils.getPhoneCacheDir(context);
        return FileUtils.currentDir;
    }

    /**
     * 获得手机的cache目录
     *
     * @param context
     * @return
     */
    public static File getPhoneCacheDir(final Context context) {
        if (FileUtils.phone_cache_dir == null) {
            FileUtils.phone_cache_dir = FileUtils.getCacheDir(new File(context
                    .getCacheDir(), "coco"));
        }
        return FileUtils.phone_cache_dir;
    }

    public static void initSDFolder() {
        File mydir = null;
        mydir = new File(FileUtils.SD);
        if (!mydir.exists()) {
            mydir.mkdir();
            FileUtils.createText(".nomedia");
        }
    }

    public static void createText(final String path) {
        try {
            FileUtils.filename = new File(FileUtils.SD + path);
            if (!FileUtils.filename.exists()) {
                FileUtils.filename.createNewFile();
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteText(final String path) {
        try {
            final RandomAccessFile file = new RandomAccessFile(FileUtils.SD
                    + path, "rw");
            file.setLength(0);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean exsit(final String path) {
        FileUtils.filename = new File(FileUtils.SD + path);
        return FileUtils.filename.exists();
    }

    public static String readText(final String path) {
        FileUtils.filename = new File(FileUtils.SD + path);
        try {
            final FileInputStream st = new FileInputStream(FileUtils.filename);
            st.skip(2);
            final InputStreamReader isr = new InputStreamReader(st, "UTF-8");
            final BufferedReader read = new BufferedReader(isr);
            try {
                FileUtils.line = read.readLine();
            } catch (final IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }

        return FileUtils.line;
    }// end method readText()

    public static void writeText(final String body, final String path) {
        // 先读取原有文件内容，然后进行写入操作
        RandomAccessFile mm = null;
        FileUtils.filename = new File(FileUtils.SD + path);
        try {
            mm = new RandomAccessFile(FileUtils.filename, "rw");
            mm.writeUTF(body);
        } catch (final IOException e1) {
            // TODO 自动生成 catch 块
            e1.printStackTrace();
        } finally {
            if (mm != null) {
                try {
                    mm.close();
                } catch (final IOException e2) {
                    // TODO 自动生成 catch 块
                    e2.printStackTrace();
                }
            }
        }
    }// end method writeText()

    /**
     * 持久化对象
     *
     * @param obj
     */
    public static void writeObject(final Object obj) {
        ObjectOutputStream oos = null;
        try {
            final FileOutputStream fos = new FileOutputStream(FileUtils.SD
                    + obj.getClass().getName());
            oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);
            oos.flush();
        } catch (final IOException e1) {
            // TODO 自动生成 catch 块
            e1.printStackTrace();
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (final IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 恢复对象
     *
     * @param obj
     * @return
     */
    public static Object restoreObject(Object obj) {
        try {
            final FileInputStream fis = new FileInputStream(FileUtils.SD
                    + obj.getClass().getName());
            final ObjectInputStream ois = new ObjectInputStream(fis);
            obj = ois.readObject();
            ois.close();
            return obj;
        } catch (final Exception e) {
            obj = null;
            return null;
        }
    }

    public static void backupdb(final Context ctx) {
        try {
            final File file = ctx.getDatabasePath("coco.db");

            final File exportDir = new File(
                    Environment.getExternalStorageDirectory(), "coco");
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }

            final File backup = new File(exportDir, "coco.db");
            if (backup.exists()) {
                backup.delete();
            }
            backup.createNewFile();
            FileUtils.fileCopy(file, backup);

        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public static void restoredb(final Context context) {
        try {
            File file = context.getDatabasePath("coco.db");
            if (file.exists()) {
                file.delete();
            }
            file = context.getDatabasePath("coco.db");

            final File exportDir = new File(
                    Environment.getExternalStorageDirectory(), "coco");
            final File backup = new File(exportDir, "coco.db");

            FileUtils.fileCopy(backup, file);

        } catch (final Exception e) {
            e.printStackTrace();
        }

    }

    private static void fileCopy(final File dbFile, final File backup)
            throws IOException {
        // TODO Auto-generated method stub
        final FileChannel inChannel = new FileInputStream(dbFile).getChannel();
        final FileChannel outChannel = new FileOutputStream(backup)
                .getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (inChannel != null) {
                inChannel.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
        }
    }

    // 保存文件
    public static void saveFile(final String filename, final byte[] content,
                                final Context context) throws Exception { // 异常交给调用处处理
        final FileOutputStream out = context.openFileOutput(filename,
                Context.MODE_PRIVATE);
        out.write(content);
        out.close();
    }

    public static byte[] readFile(final String filename, final Context context)
            throws Exception { // 异常交给调用处处理
        final FileInputStream in = context.openFileInput(filename);
        final byte b[] = new byte[1024];
        int len = 0;
        final ByteArrayOutputStream array = new ByteArrayOutputStream();
        while ((len = in.read(b)) != -1) { // 开始读取文件
            array.write(b, 0, len);
        }
        final byte data[] = array.toByteArray(); // 把内存里的数据读取出来

        in.close(); // 每个流都必须关闭
        array.close();
        return data; // 把byte数组转换为字符串并返回
    }

    public static final HashMap<String, String> mFileTypes = new HashMap<String, String>();

    public static final String GIF = "gif";

    static {
        // images
        FileUtils.mFileTypes.put("FFD8FF", "jpg");
        FileUtils.mFileTypes.put("89504E47", "png");
        FileUtils.mFileTypes.put("47494638", FileUtils.GIF);
        FileUtils.mFileTypes.put("49492A00", "tif");
        FileUtils.mFileTypes.put("424D", "bmp");
        //
        FileUtils.mFileTypes.put("41433130", "dwg"); // CAD
        FileUtils.mFileTypes.put("38425053", "psd");
        FileUtils.mFileTypes.put("7B5C727466", "rtf"); // 日记本
        FileUtils.mFileTypes.put("3C3F786D6C", "xml");
        FileUtils.mFileTypes.put("68746D6C3E", "html");
        FileUtils.mFileTypes.put("44656C69766572792D646174653A", "eml"); // 邮件
        FileUtils.mFileTypes.put("D0CF11E0", "doc");
        FileUtils.mFileTypes.put("5374616E64617264204A", "mdb");
        FileUtils.mFileTypes.put("252150532D41646F6265", "ps");
        FileUtils.mFileTypes.put("255044462D312E", "pdf");
        FileUtils.mFileTypes.put("504B0304", "zip");
        FileUtils.mFileTypes.put("52617221", "rar");
        FileUtils.mFileTypes.put("57415645", "wav");
        FileUtils.mFileTypes.put("41564920", "avi");
        FileUtils.mFileTypes.put("2E524D46", "rm");
        FileUtils.mFileTypes.put("000001BA", "mpg");
        FileUtils.mFileTypes.put("000001B3", "mpg");
        FileUtils.mFileTypes.put("6D6F6F76", "mov");
        FileUtils.mFileTypes.put("3026B2758E66CF11", "asf");
        FileUtils.mFileTypes.put("4D546864", "mid");
        FileUtils.mFileTypes.put("1F8B08", "gz");
    }

    public static String getFileType(final String filePath) {
        return FileUtils.mFileTypes.get(FileUtils.getFileHeader(filePath));
    }

    // 获取文件头信息
    public static String getFileHeader(final String filePath) {
        FileInputStream is = null;
        String value = null;
        try {
            is = new FileInputStream(filePath);
            final byte[] b = new byte[3];
            is.read(b, 0, b.length);
            value = FileUtils.bytesToHexString(b);
        } catch (final Exception e) {
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (final IOException e) {
                }
            }
        }
        return value;
    }

    private static String bytesToHexString(final byte[] src) {
        final StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        String hv;
        for (final byte element : src) {
            hv = Integer.toHexString(element & 0xFF).toUpperCase();
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        return builder.toString();
    }

    /**
     * 判读是否有加载sdcard
     *
     * @return
     */
    public static Boolean hasSdCard() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return false;
        }
        return true;
    }

    /**
     * 判读是否有加载sdcard
     *
     * @return
     */

    public static boolean hasEnoughSpace(final long fileLengh) {
        return FileUtils.getSdCardSpace() > fileLengh;
    }

    /**
     * 获取sdcard剩余空间 单位为byte 1024*1024 byte = 1024 KB = 1 MB
     *
     * @return
     */

    public static long getSdCardSpace() {
        final File path = Environment.getExternalStorageDirectory();
        final StatFs stat = new StatFs(path.getPath());
        final long availableBlocks = stat.getAvailableBlocks();

        return availableBlocks * stat.getBlockSize();
    }

    /**
     * 获取手机内部可用空间大小
     *
     * @return
     */
    static public long getAvailableInternalMemorySize() {
        final File path = Environment.getDataDirectory();
        final StatFs stat = new StatFs(path.getPath());
        final long blockSize = stat.getBlockSize();
        final long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * 获取手机内部空间大小
     *
     * @return
     */
    static public long getTotalInternalMemorySize() {
        final File path = Environment.getDataDirectory();
        final StatFs stat = new StatFs(path.getPath());
        final long blockSize = stat.getBlockSize();
        final long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    /**
     * 获取手机外部可用空间大小
     *
     * @return
     */
    static public long getAvailableExternalMemorySize() {
        if (FileUtils.hasSdCard()) {
            final File path = Environment.getExternalStorageDirectory();
            final StatFs stat = new StatFs(path.getPath());
            final long blockSize = stat.getBlockSize();
            final long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        } else {
            return -1;
        }
    }

    /**
     * 获取手机外部空间大小
     *
     * @return
     */
    static public long getTotalExternalMemorySize() {
        if (FileUtils.hasSdCard()) {
            final File path = Environment.getExternalStorageDirectory();
            final StatFs stat = new StatFs(path.getPath());
            final long blockSize = stat.getBlockSize();
            final long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize;
        } else {
            return -1;
        }
    }

    public static String size(long size) {
        if (size / (1024 * 1024) > 0) {
            float tmpSize = (float) (size) / (float) (1024 * 1024);
            DecimalFormat df = new DecimalFormat("#.##");
            return "" + df.format(tmpSize) + "MB";
        } else if (size / 1024 > 0) {
            return "" + (size / (1024)) + "KB";
        } else
            return "" + size + "B";
    }

    /**
     * Get a string suitable to be used as a file name.<br/>
     * This will replace characters that cannot be used in a file name (for instance '/' or '='), with the given replacement character, or with nothing if
     * {@code null} is given.
     *
     * @param originalName The original name.
     * @param replacementChar The replacement character to use or {@code null} to just strip the bad characters.
     * @return A new string equal to {@code originalName} with the bad characters stripped or replaced.
     */
    public static String getValidFileName(String originalName, Character replacementChar) {
        int len = originalName.length();
        StringBuilder res = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = originalName.charAt(i);
            if (c == ' ' || c == '-' || c == '_' || c == '.' || c == ',' || c == '(' || c == ')') {
                res.append(c);
            } else if (c == '\u00E9' || c == '\u00E0' || c == '\u00E9' || c == '\u00E7' || c == '\u00F4' || c == '\u00EE') {
                res.append(c);
            } else if ('0' <= c && c <= '9') {
                res.append(c);
            } else if ('a' <= c && c <= 'z') {
                res.append(c);
            } else if ('A' <= c && c <= 'Z') {
                res.append(c);
            } else if (replacementChar != null) {
                res.append(replacementChar.charValue());
            }
        }
        return res.toString();
    }

    /**
     * Equivalent of calling {@code getValidFilename(originalName, null)}.
     */
    public static String getValidFileName(String originalName) {
        return getValidFileName(originalName, null);
    }

    /**
     * A criteria to delete files if they are older than a given max age.
     */
    public class ExpiredFileFilter implements FileFilter {
        private long mMaxAgeMs;

        public ExpiredFileFilter(long maxAgeMs) {
            mMaxAgeMs = maxAgeMs;
        }

        @Override
        public boolean accept(File file) {
            return file.lastModified() < System.currentTimeMillis() - mMaxAgeMs;
        }
    }

    /**
     * Recursively delete a file or directory.<br/>
     * An optional {@link FileFilter} can be given to choose to delete only certain files ({@link FileFilter#accept(File)} returning {@code true} means the file
     * should be deleted).<br/>
     * If a filter is given, it will only be used on files, not directories and because of that, directories will not be deleted. If {@code null} is given, then
     * files <strong>and</strong> directories are deleted.
     *
     * @param fileOrDirectory The file or directory to delete.
     * @param criteria The criteria to use to choose to delete only certain files, or {@code null} to delete all of them.
     */
    public static void deleteRecursively(File fileOrDirectory, FileFilter criteria) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursively(child, criteria);
            }
            if (criteria == null) {
                boolean ok = fileOrDirectory.delete();
                Log.d(TAG, "deleted directory " + fileOrDirectory + (ok ? " ok" : " NOT ok"));
            }
        } else {
            if (criteria == null || criteria.accept(fileOrDirectory)) {
                boolean ok = fileOrDirectory.delete();
                Log.d(TAG, "deleted file " + fileOrDirectory + (ok ? " ok" : " NOT ok"));
            }
        }
    }

    /**
     * Recursively delete a file or directory.
     *
     * @param fileOrDirectory The file or directory to delete.
     */
    public static void deleteRecursively(File fileOrDirectory) {
        deleteRecursively(fileOrDirectory, null);
    }

}