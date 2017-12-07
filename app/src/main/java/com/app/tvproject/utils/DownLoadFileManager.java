package com.app.tvproject.utils;

import android.os.Environment;
import android.support.annotation.NonNull;

import com.app.tvproject.mvp.model.data.ContentBean;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.app.tvproject.myDao.DaoUtil.insertOrReplaceContent;
import static com.app.tvproject.myDao.DaoUtil.queryContentById;

/**
 * Created by www on 2017/11/27.
 */

public class DownLoadFileManager {

    private static String PNG_CONTENT_TYPE = "image/png";
    private static String MP4_CONTENT_TYPE = "video/mp4";
    private static String JPG_CONTENT_TYPE = "image/jpg";
    private static String JPEG_CONTENT_TYPE = "image/jpeg";

    private Boolean stopDownLoad = false;
    private static String fileSuffix = "";

    private long stopId = 0;
    private static final String TAG = "DownLoadFileManager";
    private String downloadDir; // 文件保存路径
    private static DownLoadFileManager instance; // 单例

    // 单线程任务队列
    private static Executor executor;
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
        downloadDir = Environment.getExternalStorageDirectory().toString() + File.separator + "TvDownload";
        File file = new File(downloadDir);
        if (!file.exists()){
            file.mkdirs();
        }
        executor = new SerialExecutor();
    }

    public void stopDownLoad(Boolean stop) {
        stopDownLoad = stop;
    }

    public void setStopId(long id) {
        this.stopId = id;
    }

//    @Override
//    public void stopPlayThis(ContentBean contentBean) {
//        LogUtil.w("download", "停止下载：" + contentBean.getHeadline() + "的内容");
////        if(is != null ) {
////            try {
////                is.close();
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
////        }
////        if(os != null){
////            try {
////                os.close();
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
////        }
//    }


    /**
     * 顺序执行下载任务
     */
    private static class SerialExecutor implements Executor {
        private ArrayDeque<Runnable> mTasks = new ArrayDeque<>();
        Runnable mActive;

        public synchronized void execute(@NonNull final Runnable r) {
            mTasks.offer(new Runnable() {
                public void run() {
                    try {
                        r.run();
                    } finally {
                        scheduleNext();
                    }
                }
            });
            if (mActive == null) {
                scheduleNext();
            }
        }

        protected synchronized void scheduleNext() {
            if ((mActive = mTasks.poll()) != null) {
                THREAD_POOL_EXECUTOR.execute(mActive);
            }
        }

        public void stopDownLoad() {
            mTasks.remove();
        }
    }

    /**
     * 获取单例对象
     *
     * @return
     */
    public static DownLoadFileManager getInstance() {
        if (instance == null) {
            instance = new DownLoadFileManager();
        }
        return instance;
    }

    /**
     * 添加下载任务
     */
    public void addDownloadTask(int httpIndex, ContentBean contentBean) {
        if (NetUtil.isConnectNoToast()) {
            executor.execute(() -> download(httpIndex, contentBean));
        }
    }

    /**
     * 下载文件
     */
    private void download(int downloadPosition, ContentBean contentBean) {
        String[] downLoadUrl = contentBean.getImageurl().replaceAll(" ", "").split(",");
        for (int i = 0; i < downLoadUrl.length; i++) {
            //是对应位置，且以http开头，且数据库的数据还在，才去提交下载
            if (i == downloadPosition && downLoadUrl[downloadPosition].substring(0, 4).equals("http")
                    && queryContentById(contentBean.getId()) != null) {
                LogUtil.w("download", "提交下载" + contentBean.getHeadline() + "的第" + downloadPosition + "条连接");
                LogUtil.w("download", "看看数据" + contentBean.getImageurl());
                String path = downLoadUrl[i];
                if (path != null && !path.isEmpty()) {
                    //加进下载数组
                    if (path.substring(path.length() - 3, path.length()).equals("mp4"))
                        fileSuffix = ".mp4";
                    else fileSuffix = ".png";
//                    if (type.equals(PNG_CONTENT_TYPE)) {
//                        fileSuffix = ".png";
//                    } else if (type.equals(MP4_CONTENT_TYPE)) {
//                        fileSuffix = ".mp4";
//                    } else if (type.equals(JPG_CONTENT_TYPE)) {
//                        fileSuffix = ".jpeg";
//                    } else if (type.equals(JPEG_CONTENT_TYPE)) {
//                        fileSuffix = ".jpg";
//                    }
                    // 其他同上 自己判断加入
                    String fileName = downloadDir + File.separator + System.currentTimeMillis() + fileSuffix;

                    try {
                        URL url = new URL(path);
                        LogUtil.w("download", "正在下载" + contentBean.getHeadline() + "的第" + downloadPosition + "条连接");
                        // todo change the file location/name according to your needs
                        File futureStudioIconFile = new File(fileName);
                        if (!futureStudioIconFile.exists()) {
                            futureStudioIconFile.createNewFile();
                        }
                        InputStream is = url.openStream();
                        OutputStream os = new FileOutputStream(futureStudioIconFile);
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
//                                    LogUtil.w("www", "下载进度" + fileSizeDownloaded + "of" + url.getFile().length());
                                }
                            }
                            is.close();
                            os.close();
                            //下载完要替换url
                            //查询保存过数据的contentBean
                            StringBuffer buffer = new StringBuffer();
                            ContentBean mContentBean = queryContentById(contentBean.getId());
                            //只替换对应位置的imgUrl，要不会出现重复下载的问题
                            String[] mPath = mContentBean.getImageurl().replaceAll(" ", "").split(",");
                            for (int j = 0; j < mPath.length; j++) {
                                if (j == downloadPosition) {
                                    mPath[j] = fileName;
                                }
                                buffer.append(mPath[j]);
                                if (j < mPath.length - 1) {
                                    buffer.append(",");
                                }
                            }
                            LogUtil.w("download", "替换完成第" + downloadPosition + "条连接 " + buffer.toString());
                            mContentBean.setImageurl(buffer.toString());
                            //要检查一下下载过程中有没被停播,停播了要删掉文件
                            if (queryContentById(mContentBean.getId()) != null) {
                                insertOrReplaceContent(mContentBean);
                                LogUtil.w("download", "插入第" + downloadPosition + "条连接 " + queryContentById(contentBean.getId()).getImageurl());
                            } else {
                                File file = new File(fileName);
                                if (file.exists())
                                    file.delete();
                            }
                            LogUtil.w("download", "下载完成" + contentBean.getHeadline() + "的第" + downloadPosition + "条连接");
                        } catch (IOException e) {
                        } finally {
                            if (os != null) {
                                os.close();

                            }
                            if (is != null) {
                                is.close();

                            }
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
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
