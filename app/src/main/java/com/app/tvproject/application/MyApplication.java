package com.app.tvproject.application;

import android.app.Application;
import android.content.Context;

import com.app.tvproject.myDao.DaoUtil;
import com.app.tvproject.utils.SharedPreferencesUtil;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2017/10/8 0008.
 */

public class MyApplication extends Application {
    private static Context context;
    private static MyApplication application;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        application = this;
        //初始化数据库
        DaoUtil.initDao(this);
        //极光推送
        JPushInterface.init(this);
        JPushInterface.setDebugMode(true);

        //静默推送
        JPushInterface.setSilenceTime(this, 0, 0, 23, 59);
    }

    public static Context getContext() {
        return context;
    }

    public static MyApplication getAppContext(){
        return application;
    }
    /**
     * 设置极光的别名或标签
     */
    public void setAlisa(String alias) {
        if(alias != null){
            JPushInterface.setAlias(getContext(), 1001, alias);
            SharedPreferencesUtil.saveJpushAlias(alias);
        }
    }


//    /**
//     * DevOpenHelper：创建SQLite数据库的SQLiteOpenHelper的具体实现
//     * DaoMaster：GreenDao的顶级对象，作为数据库对象、用于创建表和删除表
//     * DaoSession：管理所有的Dao对象，Dao对象中存在着增删改查等API
//     * 配置数据库
//     */
//    private void setupDatabase() {
//        //创建数据库shop.db"
//        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "content.db", null);
//        //获取可写数据库
//        SQLiteDatabase db = helper.getWritableDatabase();
//        //获取数据库对象
//        DaoMaster daoMaster = new DaoMaster(db);
//        //获取Dao对象管理者
//        daoSession = daoMaster.newSession();
//    }
//
//    public static DaoSession getDaoInstant() {
//        return daoSession;
//    }
}
