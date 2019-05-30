package com.wheel.legend.sligle_holder.slide;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.wheel.legend.sligle_holder.slide.slide_interface.SlideCallBack;

/**
 * 容器，用来装载Activity的上层view，并提供滑动
 */
public class SlideParentFrameLayout extends FrameLayout {

    private float defaultSpan;

    private ViewDragHelper viewDragHelper;

    private View dragView;

    private SlideCallBack callBack;

    private View shadow;//阴影

    private View bottomView;//底部view

    private SlideHelper slideHelper;

    private boolean up=false;



    public SlideParentFrameLayout(Context context) {
        super(context);
        init();
    }

    public SlideParentFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlideParentFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init(){

        this.defaultSpan = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());

        viewDragHelper=ViewDragHelper.create(this,0.1f,callback);

        viewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);

        this.shadow = new View(getContext());

        FrameLayout.LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        this.shadow.setBackgroundColor(Color.parseColor("#c8000000"));

        this.shadow.setLayoutParams(layoutParams);

        addView(shadow,0);

        this.shadow.setVisibility(GONE);




    }

    public void setDragView(View dragView) {
        this.dragView = dragView;

        if (this.dragView!=null) {
            removeView(this.dragView);

//            this.dragView.setBackgroundColor(Color.WHITE);

            addView(this.dragView);
        }

    }

    public void setBottomView(View bottomView) {
        this.bottomView = bottomView;
        if (this.bottomView!=null) {
            removeView(this.bottomView);
            addView(this.bottomView, 0);
        }
    }

    public View getBottomView() {
        return bottomView;
    }

    public void setSlideHelper(SlideHelper slideHelper) {
        this.slideHelper = slideHelper;
    }

    public View getDragView() {
        return dragView;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        float dx;

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            dx = ev.getRawX();

            if (dx <= defaultSpan) {//在边距范围内才允许滑动

                setActivity();

                return viewDragHelper.shouldInterceptTouchEvent(ev);
            }
        }

        return super.onInterceptTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {


        float dx;

        viewDragHelper.processTouchEvent(event);

        performClick();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

        }

        switch (event.getAction()){

            case MotionEvent.ACTION_DOWN:

                dx = event.getRawX();

                if (dx<=defaultSpan) {

                    up = false;

                    return true;

                }

                break;

                //在边距范围内才允许滑动

            case MotionEvent.ACTION_UP:

                if (!up) {

                    up = true;

                }

                break;

        }



        return false;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private ViewDragHelper.Callback callback=new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(@NonNull View view, int i) {
            return view==dragView;//只滑动需要滑动的view
        }

        @Override
        public int getViewHorizontalDragRange(@NonNull View child) {
            return getMeasuredWidth() - child.getMeasuredWidth();
        }

        @Override
        public int getViewVerticalDragRange(@NonNull View child) {
            return 0;
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {


            //避免向左滑过界
            if (left>0){
                return left;
            }

            return 0;
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {


             //xvel往右是正，往左是负


            int speed=500;


            if (xvel<-speed){//往左恢复

                viewDragHelper.settleCapturedViewAt(0,0);
                //恢复下方


            }else if (xvel>speed){//往右退出


                viewDragHelper.settleCapturedViewAt(getMeasuredWidth(),0);



            }else if (Math.abs(xvel)<=speed){//速度不足，判断当前位置，过半退出，没过返回


                int half=releasedChild.getLeft();

                if (isHalf(half)){//过半退出

                    viewDragHelper.settleCapturedViewAt(getMeasuredWidth(),0);



                }else {//没过半恢复

                    viewDragHelper.settleCapturedViewAt(0,0);


                }


            }

            invalidate();

//            super.onViewReleased(releasedChild, xvel, yvel);
        }


        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);

//            Log.d("dis--->>",left+"");

            slideDistance(left);

            if (left>=getMeasuredWidth()){

                slideOpen();

            }

            if (left<=0){

                slideClose();

            }


        }
    };


    /**
     * 判断是否已经划过屏幕的一半
     * @param h 传入当前位置
     * @return 返回是否超过一半
     */
    private boolean isHalf(int h){

        int width=getMeasuredWidth()/2;//虽然取自屏幕，但有些放小操作会导致变小，所以取容器的一半

        return h>=width;

    }


    /**
     * 搭配viewDragHelper.settleCapturedViewAt，不然将不会滑动
     */
    @Override
    public void computeScroll() {
        super.computeScroll();

        if (viewDragHelper.continueSettling(true)){

            invalidate();

        }

    }

    public void setCallBack(SlideCallBack callBack) {
        this.callBack = callBack;
    }

    /**
     * 滑动关闭，不退出
     */
    private void slideClose(){



        if (this.shadow!=null) {


            this.shadow.setVisibility(GONE);

        }

        if (!up){
            return;
        }

        if (this.callBack!=null){

            callBack.slideClose();

        }



    }

    int count=0;

    /**
     * 滑动完成，退出
     */
    private void slideOpen(){

        if (count>0){
            return;
        }


        Log.d("sa--->>",""+count);

        if (this.callBack!=null){

            callBack.slideComplete();

            count++;

        }


    }

    /**
     * 按下的那一刻
     * 设置Activity
     * 同时判断是否应该添加阴影或是偏移
     */
    private void setActivity(){

        if (this.callBack!=null){

            callBack.setActivity();

        }

        if (slideHelper.isScroll) {

            if (this.shadow!=null){
                this.shadow.setVisibility(GONE);
            }


            if (this.bottomView==null){
                return;
            }

            int distance= (int) (getMeasuredWidth()*0.4);//取宽度的0.4

            this.bottomView.scrollTo(-distance,0);



        }else {

            if (this.shadow!=null){
                this.shadow.setVisibility(VISIBLE);
            }

//            setShadow(view);//设置上阴影

        }

    }

    /**
     * 划过距离
     * @param dx 距离
     */
    private void slideDistance(int dx){


        if (slideHelper.isScroll){//滑动


            if (this.bottomView==null){
                return;
            }
            int distance= (int) (getMeasuredWidth()*0.4);//取宽度的0.4

            int s= (int) (0.4*dx)-distance;

            this.bottomView.scrollTo(-s,0);


        }else {//阴影

            if (this.shadow==null){
                return;
            }

            if (this.shadow.getVisibility()==GONE){
                this.shadow.setVisibility(VISIBLE);
            }

            int width = getMeasuredWidth();

            int dis = width / 200;

            int space = (width - dx);//平均分为200份

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



            String dex = Integer.toHexString(speed);

            //更改算法，实现真正透明
            if (dex.length()==1){

                dex="0"+dex;
            }

            String alpha = "#" + dex + "000000";

            this.shadow.setBackgroundColor(Color.parseColor(alpha));


        }



    }


}
