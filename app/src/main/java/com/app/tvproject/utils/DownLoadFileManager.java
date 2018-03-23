package com.app.tvproject.utils;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;

import com.app.tvproject.constants.Constants;
import com.app.tvproject.mvp.model.data.ContentBean;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.app.tvproject.myDao.DaoUtil.queryContentById;

/**
 * Created by www on 2017/11/27.
 * 单任务队列下载，尽量保证在前面要显示的界面优先下载好
 */

public class DownLoadFileManager {
    private static Boolean stopDownLoad = false;
    private long stopId = 0;
    private String downloadDir; // 文件保存路径
    private static volatile DownLoadFileManager instance; // 单例

    // 单线程任务队列
//    private static final Executor executor = new SerialExecutor();
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "DownLoadFileManager #" + mCount.getAndIncrement());
        }
    };
    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<>(256);
    private static final Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(1, 1, 1,
            TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);


    private DownLoadFileManager() {
        // 初始化下载路径
        downloadDir = Constants.DOWNLOAD_DIR + "TvDownload";
    }


    public void stopDownLoad(Boolean stop) {
        stopDownLoad = stop;
    }

    public void setStopId(long id) {
        this.stopId = id;
    }

//
//    /**
//     * 顺序执行下载任务
//     */
//    private static class SerialExecutor implements Executor {
//        private ArrayDeque<Runnable> mTasks = new ArrayDeque<>();
//        Runnable mActive;
//
//        public synchronized void execute(@NonNull final Runnable r) {
//            mTasks.offer(new Runnable() {
//                public void run() {
//                    try {
//                        r.run();
//                    } catch (Exception e) {
//                        LogUtil.d("www", e.toString());
//                    } finally {
//                        scheduleNext();
//                    }
//                }
//            });
//            if (mActive == null) {
//                scheduleNext();
//            }
//        }
//
//        protected synchronized void scheduleNext() {
//            if ((mActive = mTasks.poll()) != null && !stopDownLoad) {
//                THREAD_POOL_EXECUTOR.execute(mActive);
//            }
//        }
//
//    }

    /**
     * 获取单例对象
     * 双重校验锁
     *
     * @return
     */
    public static DownLoadFileManager getInstance() {
        if (instance == null) {
            synchronized (DownLoadFileManager.class) {
                if (instance == null) {
                    instance = new DownLoadFileManager();
                }
            }
        }
        return instance;
    }

    /**
     * 添加普通下载任务
     */
    public void addDownloadTask(int httpIndex, ContentBean contentBean) {
        if (NetUtil.isConnectNoToast()) {
            THREAD_POOL_EXECUTOR.execute(() -> {
                if (!stopDownLoad)
                    download(httpIndex, contentBean);
            });
        }
    }

    public void addDownLoadBgm(ContentBean contentBean) {
        if (NetUtil.isConnectNoToast()) {
            THREAD_POOL_EXECUTOR.execute(() -> {
                if (!stopDownLoad) {
                    downLoadBgm(contentBean, contentBean.getBgm());
                }
            });
        }
    }


    //全部删掉之前下载的文件
    public void deleteFilesByDirectory(String path) {
        File directory = new File(path);
        if (directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }

    //删除未下载完成的脏数据
    public void deleteTempData() {
        File directory = new File(downloadDir);
        if (directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                if (FileUtil.getFileSuffix(item.getPath()).equals(".temp"))
                    item.delete();
            }
        }
    }

    /**
     * 下载APK
     */
    private String apkPath;

    public String getApkPath() {
        return apkPath;
    }

    public Boolean downLoadApk(ProgressDialog pd, String appUtl) {
        if (NetUtil.isConnectNoToast()) {
            String apkDirPath = Constants.DOWNLOAD_DIR + "apkDownload";
            deleteFilesByDirectory(apkDirPath);
            File apkDir = new File(apkDirPath);
            if (!apkDir.exists()) {
                LogUtil.d("xiazai", apkDir.mkdirs() + "");
            }
            try {
                URL url = new URL(appUtl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                apkPath = apkDirPath + File.separator + System.currentTimeMillis() + ".apk";
                File apkFile = new File(apkPath);
                if (!apkFile.exists()) {
                    apkFile.createNewFile();
                }
                InputStream is = url.openStream();
                OutputStream os = new FileOutputStream(apkPath);
                pd.setMax(conn.getContentLength());


                byte[] fileByte = new byte[4096];
                int len;
                int per = 0;
                while ((len = is.read(fileByte)) != -1) {
                    os.write(fileByte, 0, len);
                    per += len;
                    pd.setProgress(per);
//                    LogUtil.w("download", "apk下载" + per);
                }
                is.close();
                os.close();
                return true;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

        } else return false;
    }

    /**
     * 下载背景音乐
     *
     * @param contentBean
     * @param bgmUrl
     */
    private void downLoadBgm(ContentBean contentBean, String bgmUrl) {
        if (!stopDownLoad) {
            if (bgmUrl.substring(0, 4).equals("http") && queryContentById(contentBean.getId()) != null) {
                String fileSuffix = FileUtil.getFileSuffix(bgmUrl);
                File fileDir = new File(downloadDir);
                if (!fileDir.exists()) {
                    fileDir.mkdirs();
                }
                //加进下载数组
                String fileName = downloadDir + File.separator + System.currentTimeMillis() + ".temp";
                try {
                    URL url = new URL(bgmUrl);
                    LogUtil.w("download", contentBean.getHeadline() + "正在下载背景音乐");
                    // todo change the file location/names according to your needs
                    File tempFile = new File(fileName);
                    if (!tempFile.exists()) {
                        tempFile.createNewFile();
                    }
                    InputStream is = url.openStream();
                    OutputStream os = new FileOutputStream(tempFile);
                    long fileSizeDownloaded = 0;
                    try {
                        byte[] fileReader = new byte[4096];
                        int len;
                        while ((len = is.read(fileReader)) != -1) {
                            //如果收到全局暂停 或者 该消息停播了，要关流
                            if (stopDownLoad || contentBean.getId() == stopId) {
                                is.close();
                                os.close();
                                stopId = -1;
                            } else {
                                os.write(fileReader, 0, len);
                                fileSizeDownloaded += len;
//                            LogUtil.w("www", "下载进度" + fileSizeDownloaded + "of" + url.getFile().length());
                            }
                        }
                        is.close();
                        os.close();
                        //下载完要重命名
                        File lastFile = new File(contentBean.getBgmDir());
                        tempFile.renameTo(lastFile);

                        //查询保存过数据的contentBean
//                    contentBean.setBgm(fileName);
                        //要检查一下下载过程中有没被停播,停播了要删掉文件
                        if (queryContentById(contentBean.getId()) == null) {
//                        insertOrReplaceContent(contentBean);
//                        LogUtil.w("download", "插入BGM" + queryContentById(contentBean.getId()).getHeadline());
//                    } else {
                            File file = new File(contentBean.getBgmDir());
                            if (file.exists())
                                file.delete();
                        }
                        LogUtil.w("download", "下载完成BGM");
                    } catch (IOException e) {
                    } finally {
                        if (os != null) {
                            os.close();
                        }
                        if (is != null) {
                            is.close();

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 下载文件
     */
    private void download(int downloadPosition, ContentBean contentBean) {
        if (!stopDownLoad) {
            LogUtil.w("download", "download" + contentBean.getHeadline() + "的第" + downloadPosition + "条连接");
            String[] downLoadUrl = contentBean.getResourcesUrl().replaceAll(" ", "").split(",");
            for (int i = 0; i < downLoadUrl.length; i++) {
                //是对应位置且数据库的数据还在，才去提交下载
                if (i == downloadPosition && !downLoadUrl[downloadPosition].isEmpty()
                        && queryContentById(contentBean.getId()) != null) {
                    LogUtil.w("download", "提交下载" + contentBean.getHeadline() + "的第" + downloadPosition + "条连接");
                    LogUtil.w("download", "看看数据" + contentBean.getResourcesUrl());
                    String path = downLoadUrl[i];
                    if (path != null && !path.isEmpty()) {
                        //判断文件类型
//                    String fileSuffix = FileUtil.getFileSuffix(path);
                        File fileDir = new File(downloadDir);
                        if (!fileDir.exists()) {
                            fileDir.mkdirs();
                        }
                        //加进下载数组
                        String fileName = downloadDir + File.separator + System.currentTimeMillis() + ".temp";

                        try {
                            URL url = new URL(path);
                            LogUtil.w("download", "正在下载" + contentBean.getHeadline() + "的第" + downloadPosition + "条连接");
                            // todo change the file location/names according to your needs
                            File tempFile = new File(fileName);
                            if (!tempFile.exists()) {
                                tempFile.createNewFile();
                            }
                            InputStream is = url.openStream();
                            OutputStream os = new FileOutputStream(tempFile);
                            long fileSizeDownloaded = 0;
                            try {
                                byte[] fileReader = new byte[4096];
                                int len;
                                while ((len = is.read(fileReader)) != -1) {
                                    //如果收到全局暂停 或者 该消息停播了，要关流
                                    if (stopDownLoad || contentBean.getId() == stopId) {
                                        is.close();
                                        os.close();
                                        stopId = -1;
                                    } else {
                                        os.write(fileReader, 0, len);
                                        fileSizeDownloaded += len;
                                        LogUtil.w("www", "下载进度" + fileSizeDownloaded + "of" + url.getFile().length());
                                    }
                                }
                                is.close();
                                os.close();
                                //下载完要替换url
//                            //查询保存过数据的contentBean
//                            StringBuffer buffer = new StringBuffer();
                                ContentBean mContentBean = queryContentById(contentBean.getId());
                                //要检查一下下载过程中有没被停播,停播了要删掉文件
                                if (mContentBean == null) {
//                                insertOrReplaceContent(mContentBean);
//                                LogUtil.w("download", "插入第" + downloadPosition + "条连接 " + queryContentById(contentBean.getId()).getResourcesUrl());
//                            } else {
                                    File file = new File(contentBean.getResourcesDir());
                                    if (file.exists())
                                        file.delete();
                                } else {
                                    LogUtil.w("download", "下载完成" + contentBean.getHeadline() + "的第" + downloadPosition + "条连接");
                                    //下载完成重命名
                                    String resultName[] = mContentBean.getResourcesDir().split(",");
                                    File lastName = new File(resultName[i]);
                                    tempFile.renameTo(lastName);
                                }
                                //只替换对应位置的imgUrl，要不会出现重复下载的问题
//                            String[] mPath = mContentBean.getResourcesUrl().replaceAll(" ", "").split(",");
//                            for (int j = 0; j < mPath.length; j++) {
//                                if (j == downloadPosition) {
//                                    mPath[j] = fileName;
//                                }
//                                buffer.append(mPath[j]);
//                                if (j < mPath.length - 1) {
//                                    buffer.append(",");
//                                }
//                            }
//                            LogUtil.w("download", "替换完成第" + downloadPosition + "条连接 " + buffer.toString());
//                            mContentBean.setResourcesDir(buffer.toString());

                            } catch (IOException e) {
                            } finally {
                                if (os != null) {
                                    os.close();

                                }
                                if (is != null) {
                                    is.close();

                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }


    /**
     * 添加删除任务
     *
     * @param path
     */
    public void addDeleteTask(final String path) {
        String[] imgUrl = path.replaceAll(" ", "").split(",");
        for (int i = 0; i < imgUrl.length; i++) {
            File file = new File(imgUrl[i]);
            if (file.exists())
                file.delete();
            LogUtil.w("download", "正在删除" + imgUrl[i]);
        }
    }

    /**
     * 返回下载路径
     *
     * @return
     */
    public String getDownloadDir() {
        return downloadDir;
    }
}
