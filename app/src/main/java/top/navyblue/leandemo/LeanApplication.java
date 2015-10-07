package top.navyblue.leandemo;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;

/**
 * Created by CIR on 10/4/15.
 */
public class LeanApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        AVOSCloud.initialize(this, Constants.APP_ID, Constants.APP_KEY);
    }
}
