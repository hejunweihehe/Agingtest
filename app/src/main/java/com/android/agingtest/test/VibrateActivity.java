package com.android.agingtest.test;


import com.android.agingtest.BaseActivity;
import com.android.agingtest.R;
import com.android.agingtest.TestUtils;

public class VibrateActivity extends BaseActivity {
    @Override
    public void setText() {
        super.setText();
        mTextTitle.setText(R.string.vibrate_title);
    }

    public void doTest() {
        super.doTest();
        TestUtils.vibrate(this);
    }

    public void stopTest(boolean paramBoolean) {
        super.stopTest(paramBoolean);
        TestUtils.cancelVibrate(this);
    }
}