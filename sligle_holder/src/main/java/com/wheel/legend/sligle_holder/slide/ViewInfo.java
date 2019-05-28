package com.wheel.legend.sligle_holder.slide;

import android.view.View;

class ViewInfo {

    private View view;
    private int current ;
    private int sp ;
    private int remain ;

    ViewInfo(View view, int current, int sp, int remain) {
        this.view = view;
        this.current = current;
        this.sp = sp;
        this.remain = remain;
    }

    View getView() {
        return view;
    }

    int getCurrent() {
        return current;
    }

    int getSp() {
        return sp;
    }

    int getRemain() {
        return remain;
    }

}
