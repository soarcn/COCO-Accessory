package com.cocosw.accessory.downloadmgr;

public interface DownloadTaskListener {
    public void updateProcess(DownloadTask mgr);			// 更新进度
    public void finishDownload(DownloadTask mgr);			// 完成下载
    public void preDownload();					// 准备下载
    public void errorDownload(int error);				// 下载错误
}
