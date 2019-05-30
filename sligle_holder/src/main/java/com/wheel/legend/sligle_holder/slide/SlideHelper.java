package com.wheel.legend.sligle_holder.slide;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.wheel.legend.sligle_holder.R;
import com.wheel.legend.sligle_holder.slide.slide_interface.SlideCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * 滑动返回操作
 * Created by legend on 2017/11/23.
 */

public class SlideHelper implements Application.ActivityLifecycleCallbacks {

    private Activity activity;

    private ViewManager viewManager;

    @SuppressLint("StaticFieldLeak")
    private static SlideHelper slideHelper;

    private static final int CLOSE = 2000;

    private static final int OPEN = 1000;

    boolean isScroll = true;


    private List<Activity> activityList;//用于管理可以滑动的Activity

    public static SlideHelper getInstance() {
        return slideHelper;
    }


    public void setScroll(boolean scroll) {
        isScroll = scroll;
    }

    /**
     * 设置
     *
     * @param activity 需要滑动的Activity
     */
    public void setSlideActivity(Activity activity) {

        this.activityList.add(activity);

        this.activity = activity;

        ViewGroup viewGroup = (ViewGroup) this.activity.getWindow().getDecorView();

        SlideParentFrameLayout parent=new SlideParentFrameLayout(activity);

        parent.setSlideHelper(this);

        View view= viewGroup.getChildAt(0);//获取viewgroup里的第一个view

        viewGroup.removeView(view);//移除它

        parent.setDragView(view);

        TypedValue value = new TypedValue();
        activity.getTheme().resolveAttribute(android.R.attr.background,value,true);

        if (value.resourceId==0) {

            view.setBackgroundColor(Color.WHITE);//设置上背景
        }

        viewGroup.addView(parent,0);//将parent放入到这个viewgroup里

        parent.setCallBack(new SlideCallBack() {
            @Override
            public void slideComplete() {

                close();//关闭Activity
            }

            @Override
            public void slideClose() {
                resetView(CLOSE);
            }

            @Override
            public void setActivity() {


                addToManager();

            }


        });


//        slideView(parent.getChildAt(0),parent);//设置可滑动view

    }

    //交给manager处理显示view
    private void addToManager() {
        this.viewManager.addViewAtContent(this.activity);

    }

    private SlideHelper(Application application) {
        application.registerActivityLifecycleCallbacks(this);
//        if (this.viewManager == null) {
            this.viewManager = new ViewManager(this);
            this.activityList = new ArrayList<>();
//        }
    }


    /**
     * 结束Activity
     */
    private void close() {

        if (this.activity != null) {

            resetView(OPEN);

            this.activity.finish();

            this.activity.overridePendingTransition(0, R.anim.fade);

            resetActivity(this.activity);

        }

    }

    /**
     * 重置底部view
     */
    private void resetView(int type) {
        switch (type) {
            case OPEN:
                this.viewManager.resetView(this.activity);

                break;
            case CLOSE:
                this.viewManager.closeResetView(this.activity);
                break;
        }

    }


    /**
     * 在application里进行注册
     *
     * @param application application对象
     */
    public static void setApplication(Application application) {

        synchronized (SlideHelper.class){

            if (slideHelper == null) {
                slideHelper = new SlideHelper(application);
            }

        }

    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        this.viewManager.add(activity);//产生Activity就丢进去

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

        this.viewManager.remove(activity);

        Log.d("destroy--->>", "" + activity.toString());
    }

    /**
     * 移除Activity后，需要将上一个Activity作为持有Activity进行操作
     *
     * @param activity 需要重置的Activity
     */
    void resetActivity(Activity activity) {

        if (this.activityList.contains(activity)) {//只有本地已添加滑动返回的Activity被移除时才切换

            this.activityList.remove(activity);//移除
            if (this.activityList.size() == 0) {
                return;
            }

            this.activity = activityList.get(activityList.size() - 1);
        }

    }

}

