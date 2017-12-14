package com.app.tvproject.utils;

import android.media.AudioManager;

import com.app.tvproject.constants.Constants;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by www on 2017/11/17.
 */

public class BaiduVoiceUtil {

    //百度语音的属性
    private static String mSampleDirPath;
    private static String appKey = "DN6X6AaqHZnjAbGagZeXqD7Z";

    private static String secretKey = "d9bedb1fd9ae4a19b9283e32958b9f4c";

    private static String appId = "10349271";

    private static TtsMode ttsMode = TtsMode.MIX;


    protected static SpeechSynthesizer mSpeechSynthesizer;

    private static final String SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male.dat";
    private static final String TEXT_MODEL_NAME = "bd_etts_text.dat";
    private static final String SAMPLE_DIR_NAME = "baiduTTS";


    public static void initialEnv() {
        if (mSampleDirPath == null) {
            mSampleDirPath = Constants.DOWNLOAD_DIR + SAMPLE_DIR_NAME;
        }
        File file = new File(mSampleDirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        copyFromAssetsToSdcard(false, SPEECH_MALE_MODEL_NAME, mSampleDirPath + "/" + "bd_etts_speech_male.dat");
        copyFromAssetsToSdcard(false, TEXT_MODEL_NAME, mSampleDirPath + "/" + "bd_etts_text.dat");
    }

    /**
     * 将工程需要的资源文件拷贝到SD卡中使用（授权文件为临时授权文件，请注册正式授权）
     *
     * @param isCover 是否覆盖已存在的目标文件
     * @param source
     * @param dest
     */
    private static void copyFromAssetsToSdcard(boolean isCover, String source, String dest) {
        File file = new File(dest);
        if (isCover || (!isCover && !file.exists())) {
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                is = ContextUtil.getContext().getAssets().open(source);
                String path = dest;
                fos = new FileOutputStream(path);
                byte[] buffer = new byte[1024];
                int size = 0;
                while ((size = is.read(buffer, 0, 1024)) >= 0) {
                    fos.write(buffer, 0, size);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static SpeechSynthesizer initTTs() {
        boolean isSuccess;
        // 检查2个离线资源是否可读
        isSuccess = checkOfflineResources();
        if (isSuccess) {
            LogUtil.w("离线资源存在并且可读");
        }
        SpeechSynthesizerListener listener = new SpeechSynthesizerListener() {
            @Override
            public void onSynthesizeStart(String s) {

            }

            @Override
            public void onSynthesizeDataArrived(String s, byte[] bytes, int i) {

            }

            @Override
            public void onSynthesizeFinish(String s) {

            }

            @Override
            public void onSpeechStart(String s) {

            }

            @Override
            public void onSpeechProgressChanged(String s, int i) {

            }

            @Override
            public void onSpeechFinish(String s) {

            }

            @Override
            public void onError(String s, SpeechError speechError) {

            }
        };
        // 日志更新在UI中，可以换成MessageListener，在logcat中查看日志
        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        mSpeechSynthesizer.setContext(ContextUtil.getContext());
        mSpeechSynthesizer.setSpeechSynthesizerListener(listener);
        mSpeechSynthesizer.setAppId(appId);
        mSpeechSynthesizer.setApiKey(appKey, secretKey);
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, mSampleDirPath + "/" + "bd_etts_text.dat"); // 文本模型文件路径 (离线引擎使用)
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, mSampleDirPath + "/" + "bd_etts_speech_male.dat"); // 声学模型文件路径 (离线引擎使用)

        // 以下setParam 参数选填。不填写则默认值生效
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "3"); // 设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "9"); // 设置合成的音量，0-9 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "4");// 设置合成的语速，0-9 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5");// 设置合成的语调，0-9 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
        // 该参数设置为TtsMode.MIX生效。即纯在线模式不生效。
        // MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
        mSpeechSynthesizer.setAudioStreamType(AudioManager.MODE_IN_CALL);
        mSpeechSynthesizer.initTts(ttsMode);
        return mSpeechSynthesizer;
    }


    private static boolean checkOfflineResources() {
        String[] filenames = {mSampleDirPath + "/" + "bd_etts_text.dat", mSampleDirPath + "/" + "bd_etts_speech_male.dat"};
        for (String path : filenames) {
            File f = new File(path);
            if (!f.canRead()) {
                LogUtil.i("[ERROR] 文件不存在或者不可读取，请从assets目录复制改文件到：" + path);
                return false;
            }
        }
        return true;
    }

}
