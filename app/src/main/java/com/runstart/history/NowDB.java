package com.runstart.history;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 10605 on 2017/9/5.
 */

public class NowDB {
    private final static String db_file_paths="/data/data/com.runstart/stu.db";
    private SQLiteDatabase db=null;
    private String tablename;
    private String[] stringData;
    private String[] intData;

    public NowDB(String mtablename,String db_file_path, String[] ss, String[] ii){
        String s=db_file_path;
        tablename=mtablename;
        db=SQLiteDatabase.openOrCreateDatabase(s,null);
        stringData=ss;
        intData=ii;
        createTable();
    }

    public void createTable(){
        String stu_table="create table if not exists "+tablename+"(id integer primary key autoincrement";
        for(int i=0;i<stringData.length;i++){
            stu_table+=","+stringData[i]+" text";
        }
        for(int i=0;i<intData.length;i++){
            stu_table+=","+intData[i]+" integer";
        }
        stu_table+=")";
        db.execSQL(stu_table);
    }

    public void insert(String[] sdata,double[] idata){
        ContentValues cValue = new ContentValues();
        for(int i=0;i<stringData.length;i++){
            cValue.put(stringData[i],sdata[i]);
        }
        for(int i=0;i<intData.length;i++){
            cValue.put(""+intData[i],idata[i]);
        }
        db.insert(tablename,null,cValue);
    }

    public void delete(String whereClause,String[] whereArgs) {
        db.delete(tablename,whereClause,whereArgs);
    }

    public void updateInt(String whereClause,String[] whereArgs,String sfirst,double ilast) {
        ContentValues values = new ContentValues();
        values.put(sfirst,ilast);
        db.update(tablename,values,whereClause,whereArgs);
    }

    public void updateString(String whereClause,String[] whereArgs,String sfirst,String slast) {
        ContentValues values = new ContentValues();
        values.put(sfirst,slast);
        db.update(tablename,values,whereClause,whereArgs);
    }

    //select时，getint和getstring里，id是getint(0），第一个自己定义的是1,以此类推
    public Cursor select(String condition){
        Cursor cursor = db.rawQuery("select * from "+tablename+" where "+condition, null);
        return cursor;
    }

    public void query() {
        ArrayList<String> sal=new ArrayList<String>();
        ArrayList<Double> ial=new ArrayList<>();
        Cursor cursor = db.query (tablename,null,null,null,null,null,null);
        if(cursor.moveToFirst()) {
            for (int j = 0; j < cursor.getCount(); j++) {
                //这里有+1,因为getString（0）得到了id，这个却是不用的，所以跳过了
                for (int i = 0; i < stringData.length; i++) {
                    sal.add(cursor.getString(i+1));
                }
                for (int i = 1; i < (intData.length+1); i++) {
                    ial.add(cursor.getDouble(i + stringData.length));
                }
                cursor.moveToNext();
            }
        }
        Log.e("database","ssize:"+sal.size());
        for(int i=0;i<sal.size();i++)
            Log.e("database","sal:"+sal.get(i));
        Log.e("database","isize:"+ial.size());
        for(int i=0;i<ial.size();i++)
            Log.e("database","ial"+ial.get(i));
    }

    public ArrayList query5(int Mode) {
        double month0,day,week;
        ExerciseData edata;
        ArrayList<Map<String,Double>> Xal=new ArrayList<>();
        ArrayList<ExerciseData> al=new ArrayList<>();
        Cursor cursor = db.query (tablename,null,null,null,null,null,null);
        if(cursor.moveToFirst()) {
            for (int j = 0; j < cursor.getCount(); j++) {
                month0=cursor.getDouble(2);
                week=cursor.getDouble(3);
                day=cursor.getDouble(4);
                boolean flag=true;
                Map<String,Double> map;
                for(int i=0;i<Xal.size();i++){
                    map=Xal.get(i);
                    if(Mode==0){
                        if((map.get("month")==month0)&(map.get("day")==day)){
                            flag=false;
                            break;
                        }
                    } else if(Mode==1){
                        if(map.get("week")==week){
                            flag=false;
                            break;
                        }
                    } else if(Mode==2){
                        if(map.get("month")==month0){
                            flag=false;
                            break;
                        }
                    }
                }
                if(flag) {
                    map = new HashMap<>();
                    String str=null;
                    switch (Mode){
                        case 0:
                            map.put("month", month0);
                            map.put("day", day);
                            str="month=" + month0 + " and day=" + day;
                            break;
                        case 1:
                            map.put("week",week);
                            str="week="+week;
                            break;
                        case 2:
                            map.put("month",month0);
                            str="month="+month0;
                            break;
                        default:break;
                    }
                    Xal.add(map);
                    Cursor cursor1 = select(str);
                    Calendar c=Calendar.getInstance();
                    c.set(Calendar.MONTH,(int)month0);
                    c.set(Calendar.DAY_OF_MONTH,(int)day);
                    if (cursor1.moveToFirst()) {
                        Log.e("database","cursor1size:"+cursor1.getCount()+",month1:"+month0+",day1:"+day+",week:"+week+",week0:"+c.get(Calendar.WEEK_OF_YEAR));
                        edata = new ExerciseData();
                        edata.day_cal=0d;
                        edata.week_cal=0d;
                        edata.month_cal=0d;
                        edata.day_time=0d;
                        edata.week_cal=0d;
                        edata.month_cal=0d;
                        edata.dayOfYear=c.get(Calendar.DAY_OF_YEAR);
                        edata.week=c.get(Calendar.WEEK_OF_YEAR);
                        edata.month0=c.get(Calendar.MONTH);
                        edata.day0=c.get(Calendar.DAY_OF_MONTH);
                        int type;
                        for (int k = 0; k < cursor1.getCount(); k++) {
                            switch (Mode){
                                case 0:
                                    type=(int) cursor1.getDouble(8);
                                    edata.day_distance[type] += cursor1.getDouble(5);
                                    edata.type=type;
                                    edata.day_cal += cursor1.getDouble(6);
                                    edata.day_time += cursor1.getDouble(7);
                                    break;
                                case 1:
                                    type=(int) cursor1.getDouble(8);
                                    edata.week_distance[type] += cursor1.getDouble(5);
                                    edata.type=type;
                                    edata.week_cal += cursor1.getDouble(6);
                                    edata.week_time += cursor1.getDouble(7);
                                    break;
                                case 2:
                                    type=(int) cursor1.getDouble(8);
                                    edata.month_distance[type] += cursor1.getDouble(5);
                                    edata.type=type;
                                    edata.month_cal += cursor1.getDouble(6);
                                    edata.month_time += cursor1.getDouble(7);
                                    break;
                                default:break;
                            }
                            Log.e("database","CursorMoved");
                            cursor1.moveToNext();
                        }
                        edata.day_distance[3]=edata.day_distance[0]+edata.day_distance[1]+edata.day_distance[2];
                        edata.week_distance[3]=edata.week_distance[0]+edata.week_distance[1]+edata.week_distance[2];
                        edata.month_distance[3]=edata.month_distance[0]+edata.month_distance[1]+edata.month_distance[2];
                        Log.e("database","al.add");
                        al.add(edata);
                    }
                }
                cursor.moveToNext();
            }
        }
        Log.e("database","going to return");
        return al;
    }
}
