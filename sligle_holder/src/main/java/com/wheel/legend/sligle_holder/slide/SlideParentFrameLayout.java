package com.wheel.legend.sligle_holder.slide;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * 容器，用来装载Activity的上层view，并提供滑动
 */
public class SlideParentFrameLayout extends FrameLayout {

    private float defaultSpan;

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

    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        float dx;

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            dx = ev.getRawX();

            if (dx <= defaultSpan) {
                return true;
            }
        }

        return super.onInterceptTouchEvent(ev);
    }
}
