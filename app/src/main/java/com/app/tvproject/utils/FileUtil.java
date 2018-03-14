package com.app.tvproject.utils;

import com.app.tvproject.constants.Constants;
import com.app.tvproject.mvp.model.data.ContentBean;

import java.io.File;

/**
 * Created by www on 2018/3/13.
 */

public class FileUtil {
    public static Boolean isFileExists(String path) {
        if (path != null && !path.isEmpty())
            return new File(path).exists();
        else return false;
    }

    public static String getFileName(ContentBean contentBean) {
        String[] resourceUrl = contentBean.getResourcesUrl().replaceAll(" ", "").split(",");
        StringBuffer sb = new StringBuffer();
        for (int j = 0; j < resourceUrl.length; j++) {
            sb.append(FileUtil.getSingleFileName(resourceUrl[j], contentBean, j));
            if (j < resourceUrl.length - 1)
                sb.append(",");
        }
        return sb.toString();
    }

    public static String getSingleFileName(String path, ContentBean bean, int position) {
        File downloadFile = new File(path);
        String downFileName = downloadFile.getName();
        String fileSuffix = downFileName.substring(downFileName.lastIndexOf("."), downFileName.length());
        if (bean.getImgormo() == Constants.IS_IMAGE) {
            return DownLoadFileManager.getInstance().getDownloadDir() + File.separator +
                    "IMG" + bean.getId() + (position + 1) + fileSuffix;
        }
        if (bean.getImgormo() == Constants.IS_VIDEO) {
            return DownLoadFileManager.getInstance().getDownloadDir() + File.separator +
                    "VIDEO" + bean.getId() + fileSuffix;
        }
        return "";
    }

    public static String getBgmFileName(ContentBean bean) {
        File downloadFile = new File(bean.getBgm());
        String downFileName = downloadFile.getName();
        String fileSuffix = downFileName.substring(downFileName.lastIndexOf("."), downFileName.length());
        return DownLoadFileManager.getInstance().getDownloadDir() + File.separator +
                "BGM" + bean.getId() + fileSuffix;
    }

    public static String getFileSuffix(String path) {
        String downFileName = new File(path).getName();
        return downFileName.substring(downFileName.lastIndexOf("."), downFileName.length());
    }
}
