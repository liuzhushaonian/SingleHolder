package com.wheel.legend.sligle_holder.slide;

import android.app.Activity;
import android.app.Application;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.wheel.legend.sligle_holder.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 滑动返回操作
 * Created by legend on 2017/11/23.
 */

public class SlideHelper implements Application.ActivityLifecycleCallbacks {

    private Activity activity;

    private float defaultSpan;

    private ViewManager viewManager;

    private static SlideHelper slideHelper;

    private View shadowView;

    private VelocityTracker velocityTracker;

    private static final int CLOSE = 2000;

    private static final int OPEN = 1000;

    boolean isScroll = false;

    private List<Activity> activityList;//用于管理可以滑动的Activity

    /**
     * 获取屏幕宽度
     *
     * @return 返回宽度
     */
    int getWidth() {

        return this.activity.getResources().getDisplayMetrics().widthPixels;

    }


    public static SlideHelper getInstance() {
        return slideHelper;
    }


    /**
     * 设置
     *
     * @param activity 需要滑动的Activity
     */
    public void setSlideActivity(Activity activity) {

        this.activityList.add(activity);

        this.activity = activity;

        this.defaultSpan = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 16,
                activity.getResources().getDisplayMetrics());

        ViewGroup viewGroup = (ViewGroup) this.activity.getWindow().getDecorView();

        SlideParentFrameLayout parent=new SlideParentFrameLayout(activity);

        View view= viewGroup.getChildAt(0);//获取viewgroup里的第一个view

        viewGroup.removeView(view);//移除它

        view.setBackgroundColor(Color.WHITE);

        parent.addView(view,0);//放置在自己写的parent里

        viewGroup.addView(parent,0);//将parent放入到这个viewgroup里

        slideView(parent.getChildAt(0),parent);//设置可滑动view

    }

    //交给manager处理显示view
    private void addToManager() {
        this.viewManager.addViewAtContent(this.activity);
        if (!isScroll) {
            addShadow();
        }
//        addShadow();
    }

    private SlideHelper(Application application) {
        application.registerActivityLifecycleCallbacks(this);
//        if (this.viewManager == null) {
            this.viewManager = new ViewManager(this);
            velocityTracker = VelocityTracker.obtain();
            this.activityList = new ArrayList<>();
//        }
    }

    private void slideView(View view, ViewGroup parent) {

        if (view == null||parent==null) {
            Log.w("waning!slideView-->>>", " the view is null!");
            return;
        }
        slideViewByHelper(view,parent);
    }


    /**
     * 滑动view
     *
     * @param view 传入需要滑动的view
     * @param parent 传入父布局，重写其touch事件
     */
    private void slideViewByHelper(final View view, ViewGroup parent) {

        if (parent==null||view==null){
            return;
        }

        parent.setOnTouchListener(new View.OnTouchListener() {

            float dx, rx;

            boolean con = false;


            @Override
            public boolean onTouch(View v, MotionEvent event) {

                velocityTracker.addMovement(event);

                float speed;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dx = event.getRawX();

                        if (dx <= defaultSpan) {

                            addToManager();

                            con = true;

                            return true;
                        }

                    case MotionEvent.ACTION_MOVE:

                        if (dx <= defaultSpan && con) {

                            rx = event.getRawX() - dx;

                            if (rx < 0) {
                                rx = 0;
                            }

                            view.scrollTo((int) -rx, 0);

                            if (isScroll) {
                                viewManager.changeViewLocation((int) rx);

                            } else {
                                changeAlpha((int) rx);
                            }

                            return true;

                        }

                    case MotionEvent.ACTION_UP:

                        if (con) {

                            velocityTracker.computeCurrentVelocity(1000);

                            speed = velocityTracker.getXVelocity();

                            float endX = event.getRawX();




                            if (isHalfScreen(endX) || speed > 500) {
                                //滑动距离过半或滑动速度超过限定值，向右滑动并退出当前Activity
                                autoScrollToRight(view, endX);

                            } else {
                                //滑动距离没过半且速度达不到要求
                                autoScrollToLeft(view, endX);

                            }

                            con = false;//抬起手后更改变量，避免二次重复触摸

                            return true;
                        }
                }

                recycleVelocity();

                return false;
            }
        });
    }

    private boolean scroll = true;

    /**
     * 抬起手后自动滑向右端（关闭Activity操作）
     */
    private void autoScrollToRight(final View view, final float currentX) {

        scroll = true;

        final int sp = getWidth() / 100;

        new Thread() {
            int remain = (int) (getWidth() - currentX);

            int current = (int) currentX;

            @Override
            public void run() {
//                super.run();
                try {
                    while (scroll) {

                        sleep(1);//睡眠。保证速度不会太快导致内存溢出
                        remain -= sp;
                        current += sp;
                        ViewInfo info = new ViewInfo(view, current, sp, remain);
                        openHandler.obtainMessage(10, info).sendToTarget();

                        if (remain < -sp * 3) {
                            scroll = false;
                        }

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();


    }


    /**
     * 平滑移动与最后退出，设置在主线程
     */
    private Handler openHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {


            ViewInfo info = (ViewInfo) msg.obj;

            int sp = info.getSp();

            int distance = info.getCurrent();

            View view = info.getView();
            view.scrollBy(-sp, 0);

            //阴影也要随之改变
            if (!isScroll) {
                changeAlpha(distance);
            } else {
                viewManager.changeViewLocation(distance);

            }

            //判断退出，一定要在主线程内。
            if (info.getRemain() < -sp * 3) {
                close();
            }


        }
    };


    /**
     * 抬起手后自动滑向左端（还原Activity操作）
     * 1、将底部view还原至上一个Activity
     * 2、好像也没什么了
     */
    private void autoScrollToLeft(final View view, final float currentX) {

        scroll = true;

        final int sp = getWidth() / 100;

        new Thread() {

            int remain = (int) (getWidth() - currentX);

            int current = (int) currentX;

            @Override
            public void run() {
//               super.run();
                try {

                    Log.d("masg-->>", currentX + "");
                    while (scroll) {
                        sleep(1);

                        current -= sp;
                        remain += sp;
                        ViewInfo info = new ViewInfo(view, current, sp, remain);

                        closeHandler.obtainMessage(20, info).sendToTarget();

                        if (current < sp) {
                            scroll = false;
                        }

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    private Handler closeHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {


            ViewInfo info = (ViewInfo) msg.obj;
            int sp = info.getSp();
            View view = info.getView();
            int current = info.getCurrent();

            view.scrollBy(sp, 0);

            if (!isScroll) {
                changeAlpha(current);
            }

            if (current < sp) {
                view.scrollTo(0, 0);

                resetView(CLOSE);

            }

        }
    };


    /**
     * 回收
     */
    private void recycleVelocity() {

        if (velocityTracker != null) {
            velocityTracker.recycle();
        }
    }

    /**
     * 结束Activity
     */
    private void close() {

        if (this.activity != null) {

            resetView(OPEN);

            this.activity.finish();

            this.activity.overridePendingTransition(0, R.anim.fade);

            scroll = false;

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
     * 判断是否超过屏幕二分之一
     *
     * @param ex 已划过距离
     * @return 返回结果
     */
    private boolean isHalfScreen(float ex) {
        boolean isHalf = false;

        float screenWidth = this.activity.getResources().getDisplayMetrics().widthPixels;

        if (ex >= screenWidth / 2) {
            isHalf = true;
        }

        return isHalf;
    }


    /**
     * 添加阴影
     */
    private void addShadow() {

        this.shadowView = new View(this.activity);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        this.shadowView.setLayoutParams(layoutParams);

        this.shadowView.setBackgroundColor(Color.parseColor("#c8000000"));

        ViewGroup viewGroup = (ViewGroup) this.activity.getWindow().getDecorView();

        ViewGroup frame= (ViewGroup) viewGroup.getChildAt(0);

        frame.addView(this.shadowView, 1);
    }

    /**
     * 改变透明度
     *
     * @param distance 已划过距离
     */
    private void changeAlpha(int distance) {

        if (this.shadowView == null) {
            return;
        }

        int width = this.activity.getResources().getDisplayMetrics().widthPixels;

        int dis = width / 200;

        int space = (width - distance);//平均分为200份

        //???? --怎么写来着？

        int speed = space / dis;

        //从200开始的透明度，可调
        if (speed > 200) {
            speed = 200;
        }

        //防止小于0
        if (speed<0){
            speed=0;
        }
//        if (speed < 16) {
//            speed = 16;
//        }


        String dex = Integer.toHexString(speed);

        //更改算法，实现真正透明
        if (dex.length()==1){

            dex="0"+dex;
        }

        String alpha = "#" + dex + "000000";

        this.shadowView.setBackgroundColor(Color.parseColor(alpha));

        ViewGroup.LayoutParams params=this.shadowView.getLayoutParams();

        params.width=distance;

        this.shadowView.setLayoutParams(params);

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
        this.viewManager.add(activity);

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

        Log.d("destroy->>>>", "" + activity.toString());
    }

    /**
     * 移除Activity后，需要将上一个Activity作为持有Activity进行操作
     *
     * @param activity 需要重置的Activity
     */
    private void resetActivity(Activity activity) {

        this.activityList.remove(activity);
        if (this.activityList.size() == 0) {
            return;
        }

        this.activity = activityList.get(activityList.size() - 1);

    }

}

