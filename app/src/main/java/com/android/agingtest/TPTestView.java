package com.android.agingtest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by hjw on 2017/12/6.
 */

public class TPTestView extends View {
    private static final String TAG = "TPTestActivity";
    Paint mPaint;
    private final ArrayList<Float> mXs = new ArrayList<Float>();
    private final ArrayList<Float> mYs = new ArrayList<Float>();
    Handler mHandler;
    private boolean disable=false;

    public TPTestView(Context context) {
        super(context);
        init();
    }

    public void setDisable(boolean disable){
        this.disable = disable;
    }

    public void setHandler(android.os.Handler handler) {
        mHandler = handler;
    }

    public TPTestView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth(10);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int size = mXs.size();
        for (int i = 0; i < size; i++) {
            canvas.drawPoint(mXs.get(i), mYs.get(i), mPaint);
            if (i < size - 1) {
                canvas.drawLine(mXs.get(i), mYs.get(i), mXs.get(i + 1), mYs.get(i + 1), mPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent action =" + event.getAction());
        if(disable){
            return super.onTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (mHandler != null) {
                    mHandler.sendEmptyMessage(1);
                }
                break;
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                mXs.add(event.getX());
                mYs.add(event.getY());
                invalidate();
        }
        return true;
    }
}
