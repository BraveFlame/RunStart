package com.runstart.sport_map;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import com.runstart.help.LockScreenActivity;
import com.runstart.sport_map.acceleromete.StepCount;
import com.runstart.sport_map.acceleromete.StepValuePassListener;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by user on 17-9-22.
 */

public class WalkService extends ServiceLocation implements SensorEventListener {
    private String TAG = "StepService";
    /**
     * 当前的日期
     */
    private static String CURRENT_DATE = "";
    /**
     * 传感器管理对象
     */
    private SensorManager sensorManager;

    /**
     * 当前所走的步数
     */
    int CURRENT_STEP;
    /**
     * 计步传感器类型  Sensor.TYPE_STEP_COUNTER或者Sensor.TYPE_STEP_DETECTOR
     */
    private static int stepSensorType = -1;
    /**
     * 每次第一次启动记步服务时是否从系统中获取了已有的步数记录
     */
    private boolean hasRecord = false;
    /**
     * 系统中获取到的已有的步数
     */
    private int hasStepCount = 0;
    /**
     * 上一次的步数
     */
    private int previousStepCount = 0;
    /**
     * 加速度传感器中获取的步数
     */
    private StepCount mStepCount;

    /**
     * IBinder对象，向Activity传递数据的桥梁
     */


    @Override
    public void onCreate() {
        super.onCreate();
   //     initTodayData();
        new Thread(new Runnable() {
            public void run() {
                startStepDetector();
            }
        }).start();


    }

    /**
     * 获取当天日期
     *
     * @return
     */
    private String getTodayDate() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }
//
//

//    /**
//     * 初始化当天的步数
//     */
//    private void initTodayData() {
//        CURRENT_DATE = getTodayDate();
//        DbUtils.createDb(this, "DylanStepCount");
//        DbUtils.getLiteOrm().setDebugged(false);
//        //获取当天的数据，用于展示
//        List<StepData> list = DbUtils.getQueryByWhere(StepData.class, "today", new String[]{CURRENT_DATE});
//        if (list.size() == 0 || list.isEmpty()) {
//            CURRENT_STEP = 0;
//        } else if (list.size() == 1) {
//            Log.v(TAG, "StepData=" + list.get(0).toString());
//            CURRENT_STEP = Integer.parseInt(list.get(0).getStep());
//        } else {
//            Log.v(TAG, "出错了！");
//        }
//        if (mStepCount != null) {
//            mStepCount.setSteps(CURRENT_STEP);
//        }
//        //updateNotification();
//    }
//

    /**
     * 监听晚上0点变化初始化数据
     */
//    private void isNewDay() {
//        String time = "00:00";
//        if (time.equals(new SimpleDateFormat("HH:mm").format(new Date())) || !CURRENT_DATE.equals(getTodayDate())) {
//            //initTodayData();
//        }
//    }


    //
//    /**
//     * 获取当前步数
//     *
//     * @return
//     */
    public int getStepCount() {
        return CURRENT_STEP;
    }

//

    /**
     * 获取传感器实例
     */
    private void startStepDetector() {
        if (sensorManager != null) {
            sensorManager = null;
        }
        // 获取传感器管理器的实例
        sensorManager = (SensorManager) this
                .getSystemService(SENSOR_SERVICE);
        //android4.4以后可以使用计步传感器
        int VERSION_CODES = Build.VERSION.SDK_INT;
        if (VERSION_CODES >= 19) {
            addCountStepListener();
        } else {
            addBasePedometerListener();
        }
    }
//

    /**
     * 添加传感器监听
     * 1. TYPE_STEP_COUNTER API的解释说返回从开机被激活后统计的步数，当重启手机后该数据归零，
     * 该传感器是一个硬件传感器所以它是低功耗的。
     * 为了能持续的计步，请不要反注册事件，就算手机处于休眠状态它依然会计步。
     * 当激活的时候依然会上报步数。该sensor适合在长时间的计步需求。
     * <p>
     * 2.TYPE_STEP_DETECTOR翻译过来就是走路检测，
     * API文档也确实是这样说的，该sensor只用来监监测走步，每次返回数字1.0。
     * 如果需要长事件的计步请使用TYPE_STEP_COUNTER。
     */
    private void addCountStepListener() {
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        Sensor detectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (countSensor != null) {
            stepSensorType = Sensor.TYPE_STEP_COUNTER;
            Log.v(TAG, "Sensor.TYPE_STEP_COUNTER");
            sensorManager.registerListener(WalkService.this, countSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else if (detectorSensor != null) {
            stepSensorType = Sensor.TYPE_STEP_DETECTOR;
            Log.v(TAG, "Sensor.TYPE_STEP_DETECTOR");
            sensorManager.registerListener(WalkService.this, detectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Log.v(TAG, "Count sensor not available!");
            addBasePedometerListener();
        }
    }
//

    /**
     * 传感器监听回调
     * 记步的关键代码
     * 1. TYPE_STEP_COUNTER API的解释说返回从开机被激活后统计的步数，当重启手机后该数据归零，
     * 该传感器是一个硬件传感器所以它是低功耗的。
     * 为了能持续的计步，请不要反注册事件，就算手机处于休眠状态它依然会计步。
     * 当激活的时候依然会上报步数。该sensor适合在长时间的计步需求。
     * <p>
     * 2.TYPE_STEP_DETECTOR翻译过来就是走路检测，
     * API文档也确实是这样说的，该sensor只用来监监测走步，每次返回数字1.0。
     * 如果需要长事件的计步请使用TYPE_STEP_COUNTER。
     *
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (stepSensorType == Sensor.TYPE_STEP_COUNTER) {
            //获取当前传感器返回的临时步数
            int tempStep = (int) event.values[0];
            //首次如果没有获取手机系统中已有的步数则获取一次系统中APP还未开始记步的步数
            if (!hasRecord) {
                hasRecord = true;
                hasStepCount = tempStep;
            } else {
                //获取APP打开到现在的总步数=本次系统回调的总步数-APP打开之前已有的步数
//                int thisStepCount = tempStep - hasStepCount;
//                //本次有效步数=（APP打开后所记录的总步数-上一次APP打开后所记录的总步数）
//                int thisStep = thisStepCount - previousStepCount;
//                //总步数=现有的步数+本次有效步数
//                CURRENT_STEP += (thisStep);
//                //记录最后一次APP打开到现在的总步数
//                previousStepCount = thisStepCount;

                CURRENT_STEP=tempStep-hasStepCount;
            }
//            Logger.d("tempStep" + tempStep);
        } else if (stepSensorType == Sensor.TYPE_STEP_DETECTOR) {
            if (event.values[0] == 1.0) {
                CURRENT_STEP++;
            }
        }
        //updateNotification();
    }
//

    /**
     * 通过加速度传感器来记步
     */
    private void addBasePedometerListener() {
        mStepCount = new StepCount();
        mStepCount.setSteps(CURRENT_STEP);
        // 获得传感器的类型，这里获得的类型是加速度传感器
        // 此方法用来注册，只有注册过才会生效，参数：SensorEventListener的实例，Sensor的实例，更新速率
        Sensor sensor = sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        boolean isAvailable = sensorManager.registerListener(mStepCount.getStepDetector(), sensor,
                SensorManager.SENSOR_DELAY_UI);
        mStepCount.initListener(new StepValuePassListener() {
            @Override
            public void stepChanged(int steps) {
                CURRENT_STEP = steps;
                //updateNotification();
            }
        });
        if (isAvailable) {
            Log.v(TAG, "加速度传感器可以使用");
        } else {
            Log.v(TAG, "加速度传感器无法使用");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    public void updateNotify() {
        Intent hangIntent = new Intent(this, PacingActivity.class);
        PendingIntent hangPendingIntent = PendingIntent.getActivity(this, 0, hangIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        mBuilder.setContentTitle("已步行" + CURRENT_STEP + "步")
                .setContentText("Walking  " + distance + " m" + "  " + format(miss))
                .setContentIntent(hangPendingIntent)
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示
                .build();
        super.updateNotify();
    }
    @Override
    void initBroadcastReceiver() {
        lockscreenIntent=new Intent(this,PacingActivity.class);
        lockscreenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        super.initBroadcastReceiver();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("last_pace_count", CURRENT_STEP + "");
        editor.commit();
        stopForeground(true);

    }
}
