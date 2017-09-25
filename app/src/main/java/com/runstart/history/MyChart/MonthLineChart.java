package com.runstart.history.MyChart;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.runstart.history.HistoryChartActivity;

import java.util.ArrayList;

/**
 * Created by 10605 on 2017/9/19.
 */

public class MonthLineChart implements OnChartValueSelectedListener{

    private LineChart mLineChar;
    private LineDataSet set1;
    private HistoryChartActivity activity;
    Handler handler;
    String months0[]=new String[]{"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

    public MonthLineChart(HistoryChartActivity activity, Handler handler){
        this.activity=activity;
        this.handler=handler;
        init_chart();
    }

    AxisValueFormatter axisValueFormatter=new AxisValueFormatter() {
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            Log.e("database","value:"+value);
            if((value<12)&(value>=0))
                return months0[(int)value];
            return "";
        }

        @Override
        public int getDecimalDigits() {
            return 0;
        }
    };

    public void start_chart(ArrayList values){
        //设置数据
//        Matrix m=new Matrix();
//        m.postScale(0.5f, 1f);//两个参数分别是x,y轴的缩放比例。例如：将x轴的数据放大为之前的1.5倍
//        mLineChar.getViewPortHandler().refresh(m, mLineChar, false);//将图表动画显示之前进行缩放
        Entry entry=(Entry) values.get(values.size()-1);
        mLineChar.moveViewToX(entry.getX());
        setData(values);
        mLineChar.animateY(700);
    }

    private void init_chart(){
        mLineChar=activity.lineChart3;
        mLineChar.setVisibleXRangeMinimum(3);
        mLineChar.setVisibleXRangeMaximum(4);
        init_XYaxis();

        //默认动画
        mLineChar.setScaleEnabled(false);
        mLineChar.setDescription("");
        mLineChar.setDragOffsetX(135);
        mLineChar.setOnChartValueSelectedListener(this);
        //刷新
        //mChart.invalidate();
    }

    private void setData(ArrayList<Entry> values) {
        // 创建一个数据集,并给它一个类型
        set1 = new LineDataSet(values,"月数据");

        // 在这里设置线
        set1.setColor(Color.rgb(183,80,198));
        set1.setLineWidth(3f);
        set1.setDrawValues(false);
        set1.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        set1.setDrawCircles(true);
        set1.setDrawCircleHole(true);
        set1.setCircleRadius(5f);
        set1.setCircleHoleRadius(4f);
        set1.setCircleColor(Color.WHITE);
        set1.setCircleColorHole(Color.rgb(139,83,230));
        set1.setHighlightLineWidth(2.5f);
        set1.setHighLightColor(Color.rgb(139,83,230));
        set1.setDrawVerticalHighlightIndicator(true);
        set1.setDrawHorizontalHighlightIndicator(false);
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        //添加数据集
        dataSets.add(set1);
        //创建一个数据集的数据对象
        LineData data = new LineData(dataSets);
        //谁知数据
        mLineChar.setData(data);
    }

    private void init_XYaxis(){
        //设置垂直x轴的线为虚线
        XAxis xAxis = mLineChar.getXAxis();
//        xAxis.setAxisMinValue(0f);
        xAxis.setTextSize(16);
        xAxis.setTextColor(Color.rgb(33,24,127));
        xAxis.setDrawAxisLine(false);
        xAxis.setGridColor(Color.WHITE);
        xAxis.setGridLineWidth(1);
        xAxis.setGranularityEnabled(true);
        xAxis.setValueFormatter(axisValueFormatter);

        YAxis leftAxis = mLineChar.getAxisLeft();
        leftAxis.setEnabled(false);
        YAxis rightAxis = mLineChar.getAxisRight();
        rightAxis.setEnabled(false);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Message msg=new Message();
        msg.what=2;
        int x=(int)e.getX();
        msg.arg1=x;
        handler.sendMessage(msg);
    }

    @Override
    public void onNothingSelected() {

    }
}
