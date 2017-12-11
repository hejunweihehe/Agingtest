package com.android.agingtest;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.android.agingtest.test.TPTestActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AgingTestMainActivity extends Activity
        implements OnClickListener, OnCheckedChangeListener {
    private static final String TAG = "AgingTestMainActivity";
    private final int ALL_TEST_ITEMS = 10;
    private CheckBox[] allChecBoxes = new CheckBox[8];
    private CheckBox mBackTakingPictureCb;
    private CheckBox mCameraMotorCb;
    private CheckBox mFlashLightCB;
    private CheckBox mFrontTakingPictureCb;
    private CheckBox mLoudSpeakerCB;
    private CheckBox mPlayVideoCb;
    private CheckBox mSelectCb;
    private CheckBox mReceiverCb;
    private SharedPreferences mSharedPreferences;
    private Button mStartBt;
    private Button mTPTestBt;
    private CheckBox mVibrateCb;
    private ArrayList<String> testkeys = null;
    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    List<String> mPermissionList = new ArrayList<>();

    protected void onCreate(Bundle paramBundle) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        super.onCreate(paramBundle);
        Log.d(TAG, "onCreate()...");
        setContentView(R.layout.activity_aging_test);

        mPermissionList.clear();
        for (int i = 0; i < permissions.length; i++) {
            if (checkSelfPermission(permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);
            }
        }

        /**
         * 判断是否为空
         */
        if (mPermissionList.isEmpty()) {
            //未授予的权限为空，表示都授予了
        } else {
            //请求权限
            String[] permissions = mPermissionList.toArray(new String[mPermissionList.size()]);//将List转为数组
            requestPermissions(permissions, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
        initValues();
        initViews();
        updateViewsVisible();
    }

    private int[] getSortedIndex() {
        int i = testkeys.size();
        int[] arrayOfInt = new int[i];
        for (int j = 0; j < i; j++)
            arrayOfInt[j] = ((Integer) TestUtils.indexMap.get(testkeys.get(j))).intValue();
        for (int k = 0; k < i; k++)
            Log.e("lsz", "indexs[" + k + "]=" + arrayOfInt[k]);
        return arrayOfInt;
    }

    private int getStateColor(int paramInt) {
        Resources localResources = getResources();
        switch (paramInt) {
            default:
                return localResources.getColor(R.color.not_test_text_color);
            case 0:
                return localResources.getColor(R.color.fail_text_color);
            case 1:
        }
        return localResources.getColor(R.color.pass_text_color);
    }

    private void initValues() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        testkeys = new ArrayList();
    }

    private void initViews() {
        mSelectCb = ((CheckBox) findViewById(R.id.select));
        mVibrateCb = ((CheckBox) findViewById(R.id.vibrate_test));
        allChecBoxes[0] = mVibrateCb;
        mReceiverCb = ((CheckBox) findViewById(R.id.receiver_test));
        allChecBoxes[1] = mReceiverCb;
        mFrontTakingPictureCb = ((CheckBox) findViewById(R.id.front_taking_picture_test));
        allChecBoxes[2] = mFrontTakingPictureCb;
        mBackTakingPictureCb = ((CheckBox) findViewById(R.id.back_taking_picture_test));
        allChecBoxes[3] = mBackTakingPictureCb;
        mPlayVideoCb = ((CheckBox) findViewById(R.id.play_video_test));
        allChecBoxes[4] = mPlayVideoCb;
        mCameraMotorCb = ((CheckBox) findViewById(R.id.camera_motor_test));
        allChecBoxes[5] = mCameraMotorCb;
        mFlashLightCB = ((CheckBox) findViewById(R.id.flashlight_test));
        allChecBoxes[6] = mFlashLightCB;
        mLoudSpeakerCB = ((CheckBox) findViewById(R.id.loudspeaker_test));
        allChecBoxes[7] = mLoudSpeakerCB;
        mStartBt = ((Button) findViewById(R.id.start));
        mTPTestBt = ((Button) findViewById(R.id.tp_test));
        mVibrateCb.setOnCheckedChangeListener(this);
        mReceiverCb.setOnCheckedChangeListener(this);
        mFrontTakingPictureCb.setOnCheckedChangeListener(this);
        mBackTakingPictureCb.setOnCheckedChangeListener(this);
        mPlayVideoCb.setOnCheckedChangeListener(this);
        mCameraMotorCb.setOnCheckedChangeListener(this);
        mFlashLightCB.setOnCheckedChangeListener(this);
        mLoudSpeakerCB.setOnCheckedChangeListener(this);
        mSelectCb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((CheckBox) v).isChecked();
                for (CheckBox b : allChecBoxes) {
                    b.setChecked(checked);
                }
                if (checked) {
                    mSelectCb.setText(getString(R.string.deselect_all));
                } else {
                    mSelectCb.setText(getString(R.string.select_all));
                }
            }
        });
        mStartBt.setOnClickListener(this);
        mTPTestBt.setOnClickListener(this);
    }

    private void updateAllTestKeys() {
        int i = TestUtils.ALLKEYS.length;
        testkeys.clear();
        for (int j = 0; j < i; j++)
            if (mSharedPreferences.getBoolean(TestUtils.ALLKEYS[j], false))
                testkeys.add(TestUtils.ALLKEYS[j]);
    }

    private void updateUI() {
        Resources localResources = getResources();
		Log.d(TAG,"localResources="+localResources);
        boolean isChecked = true;
        int i = TestUtils.ALLKEYS.length;
        for (int j = 0; j < i; j++) {
            boolean bool = mSharedPreferences.getBoolean(TestUtils.ALLKEYS[j], localResources.getBoolean(R.bool.default_reboot_value));
            allChecBoxes[j].setChecked(bool);
            if (bool) {
                testkeys.add(TestUtils.ALLKEYS[j]);
            } else {
                isChecked = false;
            }
            allChecBoxes[j].setTextColor(getStateColor(mSharedPreferences.getInt(TestUtils.ALLKEYS[j] + "result", -1)));
        }
        if (isChecked == true) {
            mSelectCb.setText(getString(R.string.deselect_all));
        }
        mSelectCb.setChecked(isChecked);
    }

    private void updateViewsVisible() {
        Resources localResources = getResources();
        if (!localResources.getBoolean(R.bool.vibrate_visible))
            mVibrateCb.setVisibility(View.GONE);
        if (!localResources.getBoolean(R.bool.receiver_visible))
            mReceiverCb.setVisibility(View.GONE);
        if (!localResources.getBoolean(R.bool.front_taking_picture_visible))
            mFrontTakingPictureCb.setVisibility(View.GONE);
        if (!localResources.getBoolean(R.bool.back_taking_picture_visible))
            mBackTakingPictureCb.setVisibility(View.GONE);
        if (!localResources.getBoolean(R.bool.play_video_visible))
            mPlayVideoCb.setVisibility(View.GONE);
        if (!localResources.getBoolean(R.bool.camera_motor_visible))
            mCameraMotorCb.setVisibility(View.GONE);
    }

    public void onCheckedChanged(CompoundButton paramCompoundButton, boolean paramBoolean) {
        Editor localEditor = mSharedPreferences.edit();
        String str = "";
        switch (paramCompoundButton.getId()) {
            case R.id.reboot_test:
                str = "reboot";
                break;
            case R.id.sleep_test:
                str = "sleep";
                break;
            case R.id.vibrate_test:
                str = "vibrate";
                break;
            case R.id.receiver_test:
                str = "receiver";
                break;
            case R.id.front_taking_picture_test:
                str = "front_camera";
                break;
            case R.id.back_taking_picture_test:
                str = "back_camera";
                break;
            case R.id.play_video_test:
                str = "video";
                break;
            case R.id.camera_motor_test:
                str = "motor";
                break;
            case R.id.flashlight_test:
                str = "flashlight";
                break;
            case R.id.loudspeaker_test:
                str = "loudspeaker";
                break;
        }
        if (TextUtils.isEmpty(str)) {
            return;
        }
        localEditor.putBoolean(str, paramBoolean);
        localEditor.commit();
        updateAllTestKeys();

        Button localButton = mStartBt;
        int i = testkeys.size();
        boolean bool = false;
        if (i > 0)
            bool = true;
        localButton.setEnabled(bool);

        //更新全选/非全选状态
        if (paramBoolean == false) {
            if (mSelectCb.isChecked() == true) {
                mSelectCb.setChecked(false);
                mSelectCb.setText(getString(R.string.select_all));
            }
        } else {
            boolean checked = true;
            for (CheckBox c : allChecBoxes) {
                if (c.isChecked() == false) {
                    checked = false;
                    break;
                }
            }
            mSelectCb.setChecked(checked);
            if (checked) {
                mSelectCb.setText(getString(R.string.deselect_all));
            } else {
                mSelectCb.setText(getString(R.string.select_all));
            }
        }
    }

    public void onClick(View paramView) {
        switch (paramView.getId()) {
            case R.id.start: {
                updateAllTestKeys();
                int[] arrayOfInt = getSortedIndex();
                Intent localIntent = new Intent(this, TestUtils.ALLCLASSES[arrayOfInt[0]]);
                localIntent.putExtra("all_index", arrayOfInt);
                localIntent.putExtra("current_test_index", 0);
                startActivity(localIntent);
            }
            break;
            case R.id.tp_test:{
                Intent intent = new Intent(this, TPTestActivity.class);
                startActivity(intent);
            }
            break;
        }
    }

    public boolean onCreateOptionsMenu(Menu paramMenu) {
        getMenuInflater().inflate(R.menu.aging_test, paramMenu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem paramMenuItem) {
        switch (paramMenuItem.getItemId()) {
            default:
                return super.onOptionsItemSelected(paramMenuItem);
            case R.id.action_test_time:
                Log.d(TAG, "onOptionsItemSelected=>set time.");
                startActivity(new Intent(this, TimeSettingsActivity.class));
                return true;
            case R.id.action_test_report:
                Log.d(TAG, "onOptionsItemSelected=>open report.");
                startActivity(new Intent(this, ReportActivity.class));
        }
        return true;
    }

    protected void onResume() {
        updateUI();
        updateAllTestKeys();
        Button localButton = mStartBt;
        int i = testkeys.size();
        boolean bool = false;
        if (i > 0)
            bool = true;
        localButton.setEnabled(bool);
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_CONTACTS) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    //权限申请失败
                    finish();
                    return;
                }
            }
            //权限申请成功
        }
    }
}