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

        this.slideHelper = slideHelper;

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

        slideHelper.resetActivity(activity);


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

        //获取上一个Activity
//        for (int i = activities.size(); i > 0; i--) {
//
//            Log.d("ii--->>>",i+"");
//
//            if (activity == activities.get(i - 1)) {
//                previousActivity = activities.get(i - 2);
//                break;
//            }
//
//        }


        int index = activities.indexOf(activity);//获取当前Activity的位置

        if (index > 0) {

            previousActivity = activities.get(index - 1);


        }


        if (previousActivity == null) {

            Log.w("waning!", "the activity is not in the list!");

            return;
        }

        //获取上一个Activity的界面,不要特殊view，要特殊view内的真实view
        ViewGroup viewGroup1 = (ViewGroup) previousActivity.getWindow().getDecorView();


        if (isT(viewGroup1.getChildAt(0))) {

            ViewGroup tView = (ViewGroup) viewGroup1.getChildAt(0);//获取到特殊view

            if (isT(tView)) {

                View sView = ((SlideParentFrameLayout) tView).getDragView();//直接获取拖拽view

                this.preView = sView;//获取里面真实的view

                tView.removeView(sView);//移除

            }

        } else {//不是特殊view

            this.preView = viewGroup1.getChildAt(0);

            viewGroup1.removeView(this.preView);//移除


        }

        //获取当前Activity最底部ViewGroup,放置底部view
        ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView();

        if (isT(viewGroup.getChildAt(0))) {//如果是特定的view，则放入其中，如果不是，则不动

            ViewGroup frame = (ViewGroup) viewGroup.getChildAt(0);

            ((SlideParentFrameLayout) frame).setBottomView(this.preView);

        }


    }

    /**
     * 重置Activity界面，避免关闭Activity后使得界面消失
     * 带动画效果，为向右滑到尽头后使Activity退出
     *
     * @param activity 传入当前Activity
     */
    void resetView(Activity activity) {
        if (activity == null || go()) {
            Log.e("waning!", "the activities is null or size is 0");
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

        ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView();//获取当前Activity的容器


        ViewGroup frame = (ViewGroup) viewGroup.getChildAt(0);//从当前Activity挖出特殊viewgroup

        if (isT(frame)) {//获取到的是特殊view

            View view = ((SlideParentFrameLayout) frame).getBottomView();//从特殊view获取上一个Activity的view

            ViewGroup previousViewGroup = (ViewGroup) previousActivity.getWindow().getDecorView();

            if (isT(previousViewGroup.getChildAt(0))) {//是容器
                ViewGroup tView = (ViewGroup) previousViewGroup.getChildAt(0);

                if (isT(tView)) {//上一个也是特殊view，

                    //动画，代替下面三行代码
                    setTransition(view, frame, tView);


                }

            } else {


                setTransition(view, frame, previousViewGroup);


            }


        }


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

        int index = activities.indexOf(activity);

        if (index > 0) {

            previousActivity = activities.get(index - 1);

        }


        if (previousActivity == null) {
            return;
        }

        ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView();//获取底部最外层

        ViewGroup frame = (ViewGroup) viewGroup.getChildAt(0);//获取自定义的外层

        if (isT(frame)) {

            View view = ((SlideParentFrameLayout) frame).getBottomView();//获取上一个界面的view

            ViewGroup previousViewGroup = (ViewGroup) previousActivity.getWindow().getDecorView();//获取上一个界面的外层

//            setTransition(view,viewGroup,previousViewGroup);

            frame.removeView(view);//移除上一个界面的view

            if (isT(previousViewGroup.getChildAt(0))) {

                ViewGroup tView = (ViewGroup) previousViewGroup.getChildAt(0);


                if (isT(tView)) {

                    ((SlideParentFrameLayout) tView).setDragView(view);

                }
            } else {

                previousViewGroup.addView(view, 0);//添加回上一个界面

            }

            if (slideHelper.isScroll) {
                view.scrollTo(0, 0);//摆正位置
            }

        }


    }

    //判断队列是否为空以及其长度
    private boolean go() {

        return activities == null || activities.size() == 0 | activities.size() == 1;
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

                if (isT(viewGroup2)) {


                    ((SlideParentFrameLayout) viewGroup2).setDragView(view);
                } else {

//                    viewGroup2.removeView(view);
                    viewGroup2.addView(view, 0);
                }

                //摆正view的位置
                if (slideHelper.isScroll) {
                    view.scrollTo(0, 0);
                }


            }
        });

        animator.start();
    }


    private boolean isT(View view) {


        return view instanceof SlideParentFrameLayout;

    }

}
