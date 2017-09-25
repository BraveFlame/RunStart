package com.runstart.history;

import android.app.Application;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.runstart.R;
import com.runstart.history.MyChart.DayLineChart;
import com.runstart.history.MyChart.MonthLineChart;
import com.runstart.history.MyChart.WeekLineChart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class HistoryChartActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView all,walk,run,ride,day,week,month,amount,speed,time,cal;
    Animation translateAnimation;
    private View type_cursor1,type_cursor2,type_cursor3,type_cursor4,nowView;
    private Button start;
    MyApplication myApplication;
    int mode=0,type=3;
    public LineChart lineChart1,lineChart2,lineChart3;

    Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ArrayList<ExerciseData> myal=myApplication.nowDB.query5(msg.what);
            boolean flag=true;
            ExerciseData edata;
            switch (msg.what){
                case 0:
                    for(int i=0;i<myal.size();i++){
                        if(myal.get(i).dayOfYear==msg.arg1){
                            edata=myal.get(i);
                            Log.e("database",edata.day_distance[type]+","+edata.day_time+","+edata.day_cal);
                            mySetText((int)edata.day_distance[type],(int)edata.day_time,(int)edata.day_cal);
                            flag=false;
                        }
                    }
                    break;
                case 1:
                    for(int i=0;i<myal.size();i++){
                        if(myal.get(i).week==msg.arg1){
                            edata=myal.get(i);
                            Log.e("database",edata.week_distance[type]+","+edata.week_time+","+edata.week_cal);
                            mySetText((int)edata.week_distance[type],(int)edata.week_time,(int)edata.week_cal);
                            flag=false;

                        }
                    }
                    break;
                case 2:
                    for(int i=0;i<myal.size();i++){
                        if(myal.get(i).month0==msg.arg1){
                            edata=myal.get(i);
                            Log.e("database",edata.month_distance[type]+","+edata.month_time+","+edata.month_cal);
                            mySetText((int)edata.month_distance[type],(int)edata.month_time,(int)edata.month_cal);
                            flag=false;
                        }
                    }
                    break;
                default:break;
            }
            Log.e("database","handler flag:"+flag+",arg1:"+msg.arg1);
            if(flag)mySetText(0,0,0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_main);
        myApplication=(MyApplication)getApplication();
        init_view();
    }

    private void init_view(){
        all=(TextView)findViewById(R.id.history_tv_all);
        walk=(TextView)findViewById(R.id.history_tv_walk);
        run=(TextView)findViewById(R.id.history_tv_run);
        ride=(TextView)findViewById(R.id.history_tv_ride);
        day=(TextView)findViewById(R.id.history_tv_day);
        week=(TextView)findViewById(R.id.history_tv_week);
        month=(TextView)findViewById(R.id.history_tv_month);
        amount=(TextView)findViewById(R.id.history_tv_amount);
        speed=(TextView)findViewById(R.id.history_tv_speed);
        time=(TextView)findViewById(R.id.history_tv_time);
        cal=(TextView)findViewById(R.id.history_tv_Kcal);
        start=(Button)findViewById(R.id.history_bt_start);

        type_cursor1=findViewById(R.id.type_cursor1);
        type_cursor2=findViewById(R.id.type_cursor2);
        type_cursor3=findViewById(R.id.type_cursor3);
        type_cursor4=findViewById(R.id.type_cursor4);


        all.setOnClickListener(this);
        walk.setOnClickListener(this);
        run.setOnClickListener(this);
        ride.setOnClickListener(this);
        day.setOnClickListener(this);
        week.setOnClickListener(this);
        month.setOnClickListener(this);
        start.setOnClickListener(this);

        lineChart1=(LineChart) findViewById(R.id.mLineChar1);
        lineChart1.setNoDataText("");
        lineChart1.setNoDataTextDescription("");
        lineChart2=(LineChart) findViewById(R.id.mLineChar2);
        lineChart2.setNoDataText("");
        lineChart2.setNoDataTextDescription("");
        lineChart3=(LineChart) findViewById(R.id.mLineChar3);
        lineChart3.setNoDataText("");
        lineChart3.setNoDataTextDescription("");
        nowView=type_cursor1;
        Log.e("database","begin to show");
        mode=0;
        showByModeType();
        Log.e("database","end0");
        mode=1;
        showByModeType();
        Log.e("database","end1");
        mode=2;
        showByModeType();
        Log.e("database","end2");
        mode=0;
        showByModeType();
        Log.e("database","init_view ending");
    }

    private void upper_tv_transfer(TextView tv){
        all.setTextColor(0xff858585);
        run.setTextColor(0xff858585);
        walk.setTextColor(0xff858585);
        ride.setTextColor(0xff858585);

        tv.setTextColor(0xff21187f);
    }
    private void lower_tv_transfer(TextView tv){
        day.setTextColor(0xff858585);
        week.setTextColor(0xff858585);
        month.setTextColor(0xff858585);

        tv.setTextColor(0xff21187f);
    }

    private void send(){
        exercise_data p2 = new exercise_data();
        p2.setDay(13);
        p2.setMonth(6);
        p2.setWeek(1);
        p2.setDistance(7);
        p2.setTime(2400);
        p2.setID(3);
        p2.setCal(70);
        p2.save(new SaveListener<String>() {
            @Override
            public void done(String objectId,BmobException e) {
                if(e==null){
                    Log.e("tryBmob","添加数据成功，返回objectId为："+objectId);
                }else{
                    Log.e("tryBmob","创建数据失败：" + e.getMessage());
                }
            }
        });
    }

    private void myAnimation(View aim){
        int qi_location[]=new int[2];
        int zhong_location[]=new int[2];
        nowView.getLocationOnScreen(qi_location);
        aim.getLocationOnScreen(zhong_location);
        translateAnimation=new TranslateAnimation(qi_location[0]-100,zhong_location[0]-100,0,0);
       // translateAnimation=new TranslateAnimation(30,120,0,0);
        translateAnimation.setDuration(300);
        //设置动画持续时间
        nowView=aim;
        translateAnimation.setFillAfter(true);
        type_cursor1.startAnimation(translateAnimation);
    }

    private void mySetText(int iamount,int itime,int ical){
        if(itime==0)speed.setText("0km/h");
        else {
            int ispeed=(iamount/itime);
            speed.setText(ispeed + "km/h");
        }
        amount.setText(iamount+"m");
        int hour=itime/3600;
        int minute=(itime-hour*3600)/60;
        int second=itime%60;
        String str;
        if(hour<10)str="0"+hour+":";
        else str=""+hour+":";
        if(minute<10)str+=("0"+minute+":");
        else str+=minute+":";
        if(second<10)str+=("0"+second);
        else str+=""+second;
        time.setText(str);
        cal.setText(""+ical);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.history_tv_all:
                upper_tv_transfer(all);
                myAnimation(type_cursor1);
                type=3;
                showByModeType();
                break;
            case R.id.history_tv_walk:
                upper_tv_transfer(walk);
                myAnimation(type_cursor2);
                type=0;
                showByModeType();
                break;
            case R.id.history_tv_run:
                upper_tv_transfer(run);
                myAnimation(type_cursor3);
                type=1;
                showByModeType();
                break;
            case R.id.history_tv_ride:
                upper_tv_transfer(ride);
                myAnimation(type_cursor4);
                type=2;
                showByModeType();
                break;
            case R.id.history_tv_day:
                lower_tv_transfer(day);
                mode=0;
                showByModeType();
                break;
            case R.id.history_tv_week:
                lower_tv_transfer(week);
                mode=1;
                showByModeType();
                break;
            case R.id.history_tv_month:
                lower_tv_transfer(month);
                mode=2;
                showByModeType();
                break;
            case R.id.history_bt_start:
                new NowDialog(this).single_dialog();
                break;
            default:break;
        }
    }

    public boolean isDataExist(){
        boolean exist_flag;
        Cursor cursor=myApplication.nowDB.select("id=1");
        if(cursor.moveToFirst())exist_flag=true;
        else exist_flag=false;
        return exist_flag;
    }

    private void changeVisibility(){
        lineChart1.setVisibility(View.INVISIBLE);
        lineChart2.setVisibility(View.INVISIBLE);
        lineChart3.setVisibility(View.INVISIBLE);
        switch (mode){
            case 0:lineChart1.clear();lineChart1.setVisibility(View.VISIBLE);break;
            case 1:lineChart2.clear();lineChart2.setVisibility(View.VISIBLE);break;
            case 2:lineChart3.clear();lineChart3.setVisibility(View.VISIBLE);break;
            default:break;
        }
    }

    private void showByModeType(){
        changeVisibility();
        if(isDataExist()) {
            ArrayList<ExerciseData> myal;
            ExerciseData edata;
            myal = myApplication.nowDB.query5(mode);
            Collections.sort(myal, new SortByDay());
            edata = myal.get(myal.size() - 1);
            switch (mode) {
                case 0:
                    mySetText((int) edata.day_distance[type], (int) edata.day_time, (int) edata.day_cal);
                    new DayLineChart(this, handler).start_chart(getAlToShow2(mode, type));
                    break;
                case 1:
                    mySetText((int) edata.week_distance[type], (int) edata.week_time, (int) edata.week_cal);
                    new WeekLineChart(this, handler).start_chart(getAlToShow2(mode, type));
                    break;
                case 2:
                    mySetText((int) edata.month_distance[type], (int) edata.month_time, (int) edata.month_cal);
                    new MonthLineChart(this, handler).start_chart(getAlToShow2(mode, type));
                    break;
                default:
                    break;
            }
        }
    }

    ArrayList<Entry> getAlToShow2(int Mode,int type){
        Log.e("database","has got in getAlToShow");
        ArrayList<ExerciseData> al=myApplication.nowDB.query5(Mode);
        Collections.sort(al, new SortByDay());
        ArrayList<Entry> values = new ArrayList<Entry>();
        for(int i=0;i<al.size();i++){
            Log.e("database","al.dayofyear"+i+":"+al.get(i).dayOfYear+",week:"+al.get(i).week);
        }
        switch (Mode){
            case 0:
                values.add(new Entry(al.get(0).dayOfYear,(float)al.get(0).day_distance[type]));
                for(int i=1;i<al.size();){
//                    Log.e("database","valuesize:"+values.size()+",values.get:"+values.get(values.size()-1).getX()+",al.get -1:"+(al.get(i).dayOfYear-1));
                    if((values.get(values.size()-1).getX())==(al.get(i).dayOfYear-1)){
                        Log.e("database","i:"+i+",getAlToShow2,al.get(i).dayofYear:"+al.get(i).dayOfYear+",al.get(i).day_distance:"+al.get(i).day_distance[type]);
                        values.add(new Entry(al.get(i).dayOfYear,(float) al.get(i).day_distance[type]));
                        i++;
                    } else {
                        values.add(new Entry(values.get(values.size()-1).getX()+1,0));
                    }
                }
                break;
            case 1:
                values.add(new Entry(al.get(0).week,(float)al.get(0).week_distance[type]));
                for(int i=1;i<al.size();){
                    if(values.get(values.size()-1).getX()==(al.get(i).week-1)){
                        values.add(new Entry(al.get(i).week,(float) al.get(i).week_distance[type]));
                        i++;
                    } else {
                        values.add(new Entry(values.get(values.size()-1).getX()+1,0));
                    }
                }
                break;
            case 2:
                values.add(new Entry(al.get(0).month0,(float)al.get(0).month_distance[type]));
                for(int i=1;i<al.size();){
//                    Log.e("database","valuesize:"+values.size()+",values.get:"+values.get(values.size()-1).getX()+",al.get -1:"+(al.get(i).month0-1));
                    if(values.get(values.size()-1).getX()==(al.get(i).month0-1)){
                        Log.e("database","i:"+i+",getAlToShow2,al.get(i).month:"+al.get(i).month0+",al.get(i).month_distance:"+al.get(i).month_distance[type]);
                        values.add(new Entry(al.get(i).month0,(float) al.get(i).month_distance[type]));
                        i++;
                    } else {
                        values.add(new Entry(values.get(values.size()-1).getX()+1,0));
                    }
                }
                for(int j=0;j<values.size();j++){
                    Log.e("database","valuesal:"+values.get(j));
                }
                break;
            default:break;
        }
        return values;
    }

    class SortByDay implements Comparator {
        public int compare(Object o1, Object o2) {
            ExerciseData s1 = (ExerciseData) o1;
            ExerciseData s2 = (ExerciseData) o2;
            if (s1.dayOfYear > s2.dayOfYear)
                return 1;
            return -1;
        }
    }
}
