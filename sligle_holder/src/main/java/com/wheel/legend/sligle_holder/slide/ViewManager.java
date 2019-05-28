package com.wheel.legend.sligle_holder.slide;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

class ViewManager {

    private List<Activity> activities;

    private View preView;

    private SlideHelper slideHelper;

    ViewManager(SlideHelper slideHelper) {

        if (activities == null) {

            activities = new ArrayList<>();
        }

        this.slideHelper=slideHelper;

    }



    //添加
    void add(Activity activity) {
        if (activity == null) {
            return;
        }

        activities.add(activity);

//            Log.d("add-size-->>>",activities.size()+"");

    }

    //移除与恢复持有
    void remove(Activity activity) {
        if (activity == null) {
            return;
        }
        activities.remove(activity);
//            Log.d("remove-size-->>>",activities.size()+"");

        //恢复持有
//            slideHelper.resetActivity(activities.get(activities.size()-1));


    }

    /**
     * 传入当前Activity，绘制下一层的Activity
     *
     * @param activity 当前Activity
     */
    void addViewAtContent(Activity activity) {
        Activity previousActivity = null;

        //判断null以及是否唯一，如是则返回不做处理
        if (go() || activity == null) {
            return;
        }

        for (int i = activities.size(); i >= 0; i--) {

            if (activity == activities.get(i - 1)) {
                previousActivity = activities.get(i - 2);
                break;
            }

        }

        if (previousActivity == null) {

            Log.w("waning!", "the activity is not in the list!");

            return;
        }

        //获取上一个Activity的界面
        ViewGroup viewGroup1 = (ViewGroup) previousActivity.getWindow().getDecorView();

        this.preView = viewGroup1.getChildAt(0);

        viewGroup1.removeView(this.preView);//移除


        //获取当前Activity最底部ViewGroup
        ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView();

        ViewGroup frame= (ViewGroup) viewGroup.getChildAt(0);

        frame.addView(this.preView, 0);//

    }

    /**
     * 重置Activity界面，避免关闭Activity后使得界面消失
     * 带动画效果，为向右滑到尽头后使Activity退出
     * @param activity 传入当前Activity
     */
    void resetView(Activity activity) {
        if (activity == null || go()) {
            Log.d("waning!", "the activities is null or size is 0");
            return;
        }

        Activity previousActivity = null;

        for (int i = activities.size(); i >= 0; i--) {

            if (activity == activities.get(i - 1)) {
                previousActivity = activities.get(i - 2);
                break;
            }

        }

        if (previousActivity == null) {
            return;
        }

        ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView();

        ViewGroup frame= (ViewGroup) viewGroup.getChildAt(0);

        View view = frame.getChildAt(0);

        ViewGroup previousViewGroup = (ViewGroup) previousActivity.getWindow().getDecorView();

        //动画，代替下面三行代码
        setTransition(view, frame, previousViewGroup);

//            frame.removeView(view);
//
//            previousViewGroup.addView(view);
//
//            if (slideHelper.isScroll) {
//                view.scrollTo(0, 0);//摆正位置
//            } else {
//                frame.removeViewAt(0);//移除阴影
//            }


    }

    /**
     * 手势未成功关闭Activity时，恢复上一个Activity的界面，避免用户按返回键后，上一个界面是空白的
     * 原理与重置view差不多，只是没有了动画效果，但是一定要摆正上一个view的位置，或是清除阴影
     *
     * @param activity 当前所持Activity
     */
    void closeResetView(Activity activity) {
        if (activity == null || go()) {
            Log.d("waning!", "the activities is null or size is 0");
            return;
        }

        Activity previousActivity = null;

        for (int i = activities.size(); i >= 0; i--) {

            if (activity == activities.get(i - 1)) {
                previousActivity = activities.get(i - 2);
                break;
            }

        }

        if (previousActivity == null) {
            return;
        }

        ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView();//获取底部最外层

        ViewGroup frame= (ViewGroup) viewGroup.getChildAt(0);//获取自定义的外层

        View view = frame.getChildAt(0);//获取上一个界面的view

        ViewGroup previousViewGroup = (ViewGroup) previousActivity.getWindow().getDecorView();//获取上一个界面的外层

//            setTransition(view,viewGroup,previousViewGroup);

        frame.removeView(view);//移除上一个界面的view

        previousViewGroup.addView(view, 0);//添加回上一个界面

        if (slideHelper.isScroll) {
            view.scrollTo(0, 0);//摆正位置
        } else {
            frame.removeViewAt(0);//移除阴影
        }

    }

    //判断队列是否为空以及其长度
    private boolean go() {

        return activities == null || activities.size() == 0 | activities.size() == 1;
    }


    /**
     * 改变底下view的位置，随着滑动而滑动
     *
     * @param space 距离
     */
    void changeViewLocation(int space) {

        if (this.preView == null) {
            return;
        }
        if (space > slideHelper.getWidth()) {
            space = slideHelper.getWidth();
        }

        int width = this.preView.getResources().getDisplayMetrics().widthPixels - space;

        this.preView.scrollTo((int) (width * 0.5), 0);

    }

    /**
     * 移除view动画
     *
     * @param view       需要移除的view
     * @param viewGroup1 从viewGroup1移除
     * @param viewGroup2 添加到viewGroup2
     */
    private void setTransition(final View view, final ViewGroup viewGroup1, final ViewGroup viewGroup2) {

        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 1.0f, 1.0f).setDuration(100);



        //重点！！动画结束后立刻将view移除并添加到上一个Activity里，保证无缝跳转
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {

                viewGroup1.removeView(view);
                if (slideHelper.isScroll) {
                    viewGroup1.removeViewAt(0);
                }

                addView(view, viewGroup2);

            }
        });

        animator.start();
    }

    /**
     * 添加view
     *
     * @param view 上面的内容
     * @param viewGroup 容器
     */
    private void addView(View view, ViewGroup viewGroup) {

//            ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 1.0f, 1.0f).setDuration(100);
//
//
//            animator.addListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
////                    super.onAnimationStart(animation);

        viewGroup.addView(view, 0);

        //摆正view的位置
        if (slideHelper.isScroll) {
            view.scrollTo(0, 0);
        }

    }
}
