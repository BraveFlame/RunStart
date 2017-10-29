package com.runstart.mine.mine_activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;


import com.runstart.R;
import com.runstart.bean.AboutSport;
import com.runstart.help.ActivityCollector;
import com.runstart.mine.mine_adapter.AboutSportAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouj on 2017-09-21.
 */

public class MineAboutSportActivity extends Activity implements View.OnClickListener{
    //定义fragment_mine_aboutsport_title.xml的布局组件
    private ImageView iv_zuojiantou;
    //定义fragment_mine_aboutsport_content.xml的布局组件
    private ListView abSportListView;
    //定义ListView的适配器AboutSportAdapter
    AboutSportAdapter aboutSportAdapter;
    //定义List列表来保存AboutSport的相关数据信息
    List<AboutSport> aboutSportList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_mine_aboutsport);
        ActivityCollector.addActivity(this);
        getAboutSportData();
        initAboutSportView();
        useListViewApdater();
        useListViewMethod();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    /**
     * 初始化组件
     */
    public void initAboutSportView(){
        iv_zuojiantou = (ImageView) findViewById(R.id.mine_aboutsport_iv_zuojiantou);
        iv_zuojiantou.setOnClickListener(this);
        abSportListView = (ListView) findViewById(R.id.mine_aboutsport_content_listview);
    }

    /**
     * 定义AboutSport的相关数据
     */
    public void getAboutSportData(){
        aboutSportList = new ArrayList<AboutSport>();
        AboutSport aboutSport01=new AboutSport();
        aboutSport01.setNewsImage(String.valueOf(R.mipmap.sports_01));
        aboutSport01.setNewsBgImage(String.valueOf(R.mipmap.sports_02));
        aboutSport01.setNewsTitle("The relationship between exercise and diet");
        aboutSport01.setNewsContent("    The relationship between exercise and diet The importance of the movement We all know, but the specific operation or there are many questions, such as how long after dinner to exercise, after exercise, what to eat to add strength and so on. First of all we have to clear a question, we are not professional athletes, nor how to participate in the professional fitness competition, so, such as to shape the muscles to eat eight eggs a day is not desirable. Exercise diet, or to combine the time to do some exercise adjustment.\n" +
                "    When is the best exercise for healthy people, anytime, anywhere can exercise, when to want to exercise or when there is time to exercise on when the movement. Generally speaking, 5 pm -7 is the golden time of exercise. Because this time the body&apos;s function is the most active day, when the movement is not only effective, and not easy to hurt. Morning is a lot of older people like the time, but the best after the sun came out. Hypertensive elderly should avoid morning exercises, or by detecting morning blood pressure, to ensure that this period of normal blood pressure. In the young when the morning exercise habits of the elderly can be appropriate for moderate or low-intensity exercise. For patients with diabetes, it is best to exercise 1-1.5 hours after a meal, help reduce postprandial blood sugar. Morning exercises for young people is a bit difficult, most white-collar workers because of fatigue is difficult to get up early exercise. Therefore, for office workers, you can make full use of commute time, work intermittent time, increase the amount of daily activities, use after get off work and weekend free time for specialized exercise.\n" +
                "    Vigorous exercise before the best fasting dinner after exercise, is now a lot of people choose. But after dinner exercise should not be strenuous exercise-based, immediately after a strenuous exercise will not only increase the impact of digestion and absorption, but also increase the burden on the body is not conducive to the effect of exercise, but also adverse effects on the body. After dinner exercise should be a small amount of exercise, digestion as a purpose, such as walking, washing and sweeping such housework. If you want to carry out intense exercise, it is best after 2 hours after a meal. 1-2 times a week intense exercise is good for cardiovascular health, like a week to your cardiovascular system completely washed a bath. But if it is just beginning exercise, poor physical fitness, or hunger, it is best not to carry out high-intensity exercise, so as not to chest tightness, stomach feel discomfort, or hypoglycemia, such as dizziness, fatigue and so on. Have high blood\n" +
                "    Stress, diabetes and stomach, especially those who pay attention. In addition, the intense exercise should not be carried out within two hours before going to bed to prevent the body over excited, difficult to sleep. After high-intensity exercise, should rest 30-60 minutes after eating. This is because after exercise, the blood is mainly supplied to the muscles, this time, gastrointestinal function is weak, digestion and absorption capacity is low, if a large number of easy to lead to discomfort, or even increase the risk of gastrointestinal disease.\n" +
                "    After exercise a balanced diet Some fitness people think that after exercise to add a lot of protein, but if not a professional bodybuilder, there is no need for this. In fact, excessive intake of protein is a heavy burden on the kidneys, and even harmful. The normal diet of protein supply is enough, but it is not recommended to eat only fruits and vegetables, or only drink porridge, should also be a variety of food, balanced diet as well. Some people are afraid of exercise too much consumption, will lead to increased appetite, in fact, this worry is not necessary. Studies have found that, compared with the situation without exercise, the appropriate movement not only will not let people eat more, but conducive to short-term appetite control. It should be noted that, after exercise in a timely enough to add water is true, taking into account the carbohydrates and micronutrients in order to promote recovery. To regain physical strength after exercise, high sugar, low fat, proper amount of protein and easy to digest food is the best choice. In addition, in the body energy reserve material recovery, to add sugar and electrolyte drinks, food, and add the sooner the better. Because the activity of glycogen synthase after exercise the highest, as soon as possible to add, can effectively restore physical strength. We recommend some suitable food for food, such as sports drinks, all kinds of fruit and vegetable juice, milk, seaweed egg soup, all kinds of vegetables, fruits, tofu, kelp, shrimp, etc. These foods can effectively add body fluids and energy at the same time, Help to eliminate fatigue, reduce muscle soreness.");
        aboutSportList.add(aboutSport01);
        AboutSport aboutSport02=new AboutSport();
        aboutSport02.setNewsImage(String.valueOf(R.mipmap.sports_04));
        aboutSport02.setNewsBgImage(String.valueOf(R.mipmap.sports_03));
        aboutSport02.setNewsTitle("Physical exercise will keep your blood flowing");
        aboutSport02.setNewsContent("    We all know that the benefits of exercise is to exercise, to achieve the effect of health and longevity, but often some people will have some movement errors, resulting in poor fitness effect. Today to introduce some of the benefits of sports and fitness for the spring, interested together to find out about it\n" +
                "    There are many benefits of exercise, can promote blood circulation, enhance physical fitness, speed up the metabolism, specifically what, see the following description.\n" +
                "\n" +
                "    Benefit 1: Sports helps you control weight\n" +
                "\n" +
                "    Exercise can prevent obesity or help lose weight. When exercising, you will burn calories. The greater the intensity of exercise, the more calories burned. You do not need to spend a lot of time to lose weight every day exercise, if you can not carry out strict sense of exercise, it is hard on the daily little things - do not take a lift every day but climb the stairs to go downstairs or do more housework.\n" +
                "\n" +
                "    Benefits 2: exercise to help you fight the disease often health\n" +
                "\n" +
                "    Worry about heart disease? Want to prevent high blood pressure? Whether you are now multiple, participate in exercise can increase the body of high-density lipoprotein (HDL) or \"good\" cholesterol, reduce the body of adverse triglycerides.\n" +
                "    Physical exercise will keep your blood flow and reduce the risk of cardiovascular disease. Regular exercise can prevent and control human health problems such as stroke, metabolic syndrome.\n" +
                "    Physical exercise will keep your blood flowing\n" +
                "\n" +
                "    Benefit 3: The sport gives you a good mood\n" +
                "\n" +
                "    Would you like to vent your mind or work in the gym for 30 minutes to help you. Physical exercise will stimulate the brain to release chemical substances, make you feel happy and relaxed. Long-term exercise to make you more fit, more satisfied with their appearance, thereby enhancing self-confidence and self-esteem.\n" +
                "\n" +
                "    Benefit 4: The movement makes energetic\n" +
                "\n" +
                "    Go to the grocery store or a little chores to make you breathless? Daily exercise will increase your muscle strength, so you have a stronger endurance. Exercise and sport help the body to oxygen and nutrients transported to the organization, so that the cardiovascular system more efficient. Cardiopulmonary efficiency is improved, and the daily household chores are more vigorous.\n" +
                "\n" +
                "    Benefits 5: exercise to improve sleep\n" +
                "\n" +
                "    Can not sleep or sleep too thick do not want to get up, then often participate in exercise, you can sleep faster, sleep deeper. But remember that do not exercise at bedtime exercise, or you will be too excited and can not sleep.\n" +
                "    Then often participate in exercise, you can sleep faster\n" +
                "\n" +
                "    Benefit 6: Sports to help you rekindle the fire of love\n" +
                "\n" +
                "    Whether you are too tired or body deformation can not skin blind date? Physical exercise to make you more beautiful, more dynamic, for your husband and wife to bring a positive impact on life. The positive impact of physical exercise on the couple&apos;s life is far more than that, it can awaken the woman&apos;s desire. Moreover, the regular exercise of men with renal dysfunction is less likely than the exercise of the people.\n" +
                "\n" +
                "    Benefit 7: The movement brings you happiness\n" +
                "\n" +
                "    Sports allows you to spend time, make you relax, enjoy outdoor activities or participate in the project to make yourself happy.\n" +
                "    Exercises and sports can also be linked to family and friends through a happy social event. So, go to a dance class, hike or join a football team, find a favorite sport, exercise it! If you are tired, to change the new movement.......");
        aboutSportList.add(aboutSport02);
        AboutSport aboutSport03=new AboutSport();
        aboutSport03.setNewsImage(String.valueOf(R.mipmap.sports_06));
        aboutSport03.setNewsBgImage(String.valueOf(R.mipmap.sports_05));
        aboutSport03.setNewsTitle("Movement is the key of the song of the life and Life is movement");
        aboutSport03.setNewsContent(
                "    1, physical exercise is conducive to human bones, muscle growth, enhance heart and lung function, improve the blood circulation system, respiratory system, digestive system performance, is conducive to the growth and development of the human body to improve disease resistance and enhance the adaptability of the organism.\n" +
                "    2. Reduce children in adult suffering from heart disease, high blood pressure, diabetes and other diseases.\n" +
                "    3, physical exercise is to enhance the constitution of the most active and effective means.\n" +
                "    4, you can reduce the risk of premature aging.\n" +
                "    5, physical exercise can improve the nervous system regulation function, improve the nervous system on human activities when the complex changes in the ability to judge, and timely coordination, accurate and rapid response; the human body to adapt to changes in internal and external environment, to maintain the body life activities Work properly.\n" +
                "    In the psychological:\n" +
                "    1, physical exercise has the role of regulating human tension, can improve the physiological and psychological state, to restore physical strength and energy;\n" +
                "    2, physical exercise can improve the health, so that the body of fatigue to get a positive rest, people energetic into the study, work;\n" +
                "    3, stretch the body and mind, help sleep and eliminate the pressure brought about by reading\n" +
                "    4, physical exercise can cultivate sentiment, maintain a healthy state of mind, give full play to the individual&apos;s enthusiasm, creativity and initiative, thereby enhancing self-confidence and values, so that personality in a harmonious atmosphere to get healthy and harmonious development;\n" +
                "    5, physical exercise in the collective projects and competitions can cultivate people&apos;s unity, cooperation and collectivism.\n" +
                "    Teenage is an important turning point in the development of physical and mental development in life, when you will be surprised to find that there are many unprecedented changes in physical and psychological, and obviously feel that I grew up. With the improvement of people&apos;s living standards and cultural quality, \"beauty of the heart, everyone has,\" we have to grow in sports, in sports to maintain fitness.\n" +
                "    The importance of movement\n" +
                "    Movement is the key of the song of the. Life is movement, exercise so that we live vigorously, feel comfortable, away from the disease, health and longevity.\n" +
                "    The movement is inseparable from us. The movement can make our body more powerful; exercise can make us maintain the type of body; exercise can make our endurance increase; exercise can make our arms magnanimous; exercise can make us vibrant; exercise can make us high spirits; movement Can make us full of perseverance; more so that we can easily face life.");
        aboutSportList.add(aboutSport03);
        AboutSport aboutSport04=new AboutSport();
        aboutSport04.setNewsImage(String.valueOf(R.mipmap.sports_08));
        aboutSport04.setNewsBgImage(String.valueOf(R.mipmap.sports_07));
        aboutSport04.setNewsTitle("Running makes people happy");
        aboutSport04.setNewsContent("    Running is one of the best ways to exercise, as long as a pair of comfortable shoes, you can start a healthy trip. This we all know, but not everyone who have to keep down, may run so several times, feeling the body much better, in fact, only run a few days, the role of mind more fills, in order to exercise through the body,\n" +
                "    Adhere to the benefits of running on the human body\n" +
                "\n" +
                "    Weight loss shaping\n" +
                "\n" +
                "    Running is a kind of aerobic breathing movement, running 20 minutes after the fat began to burn, through running, you can achieve the purpose of weight loss, it can make the body&apos;s muscles rhythmic contraction and relaxation, muscle fiber increased, increased protein content. Muscular development is one of the signs of fitness.\n" +
                "\n" +
                "    Keep young\n" +
                "\n" +
                "    Adhere to running can strengthen the metabolism, delay the degeneration of bone changes, prevention of bone and joint disease in the elderly, so that you delay aging.\n" +
                "\n" +
                "    Enhance heart and lung function\n" +
                "\n" +
                "    During exercise, the frequency and efficacy of heart beat are greatly improved, heart rate, blood pressure and vascular wall elasticity also increased. The maximum oxygen intake of trained athletes is 33-60% higher than that of ordinary people.\n" +
                "\n" +
                "    Improve sleep quality\n" +
                "\n" +
                "    Through running, the brain&apos;s blood supply, oxygen supply can be increased by 25%, so that the quality of sleep at night will be followed.\n" +
                "    Improve sexual performance Long-term exercise helps improve sexual performance.\n" +
                "\n" +
                "    Enhance gastrointestinal motility\n" +
                "\n" +
                "    Running can increase gastrointestinal motility, increased secretion of digestive juice, increased digestion and absorption capacity, thereby increasing the appetite, added nutrition, strong physique.\n" +
                "\n" +
                "    Reduce gynecological diseases, regulate menstruation\n" +
                "\n" +
                "    Running for women, it helps to regulate menstruation and reduce gynecological diseases. US gynecologist incense gay on the 1979 New York marathon run 394 female athletes menstrual cycle survey, found that 26% of menstrual disorders of women returned to normal cycle, 17% of menopausal women recovered menstruation. This is because running increased metabolism, promote digestion and absorption, regulate the nervous system function, improve the endocrine function.\n" +
                "    Temper the will and perseverance of people\n" +
                "    Running can temper people&apos;s will and perseverance, enhance toughness and patience, improve sensitivity, and promote the ability to adapt to the environment. Long-term adhere to the movement of people, in the completion of quantitative work has three characteristics: First, fast action; Second, the potential is large, can play the greatest potential to complete the task; Third, quick recovery, fatigue, fast and complete, can quickly return to Calm level.");
        aboutSportList.add(aboutSport04);
        AboutSport aboutSport05=new AboutSport();
        aboutSport05.setNewsImage(String.valueOf(R.mipmap.sports_10));
        aboutSport05.setNewsBgImage(String.valueOf(R.mipmap.sports_09));
        aboutSport05.setNewsTitle("We are on the way!Has been on the roa");
        aboutSport05.setNewsContent("    In the lonely ride on the road, and occasionally feel lonely, do not know what will happen on the unknown path, my heart always feel inexplicable emptiness, fear, we are lonely travelers, riding in the unknown On the road. Fled the city&apos;s bustling and hustle and bustle, to pursue their own mind that a quiet, perhaps only this moment I can feel that they are real live, do not have to ignore others do not understand the eyes, or someone else&apos;s ridicule eyes , We are just looking for their own dreams, like \"Forrest Gump\", we are running, where to go does not matter, as long as the proof of their existence, to prove that they can complete the hearts of that little dream.\n" +
                "    At this moment we put all down, take a simple luggage, accompany us only our old man, when we face the scenery along the way, open arms closed eyes quietly enjoy the quiet, at this moment we and nature The heart of the heart like dandelion seeds like the wind was taken away, to feel the whole of the heart of the river, world. The original life can be so pleasant. Although we have no money, no power, but we have pure heart and the initial dream and that unremitting spirit.\n" +
                "    Although with the growth of age, social changes, our future life is also subtle changes, after the opportunity to ride this may be lost. But our dreams are still, our innocent heart is still, it will be our eternal power.\n" +
                "    We are on the way!\n" +
                "    Has been on the roa");
        aboutSportList.add(aboutSport05);
        AboutSport aboutSport06=new AboutSport();
        aboutSport06.setNewsImage(String.valueOf(R.mipmap.sports_12));
        aboutSport06.setNewsBgImage(String.valueOf(R.mipmap.sports_11));
        aboutSport06.setNewsTitle("Sports slimming methods to listen to speech");
        aboutSport06.setNewsContent("    Exercise slimming methods 1 exercise slimming methods 2 exercise slimming methods 3 exercise slimming methods 4 sports slimming methods 5 exercise slimming methods 6 sports weight loss method 7\n" +
                "    Do not have time to exercise every day? Today, Xiao Bian teach you a set of 10 minutes will be able to burn fat weight loss gymnastics, master the five key points, and soon see results. Still worry about the weight loss of the MM who followed it to do it quickly.\n" +
                "    \"Lean muscle exercise\" is the most effective lazy movement\n" +
                "    Only control the diet or only aerobic exercise of a single weight-loss way, the effect is not good, in order to make weight more efficient, indispensable is the \"muscle movement.\" Usually no exercise habits, and living in modern society to facilitate people, it should strengthen the muscles, not only has a slimming effect, but also increase the thin muscle strength, creating a fat body is not easy.\n" +
                "    The so-called \"muscle movement\" is not \"do a few minutes will produce a certain effect\" of the movement, but if I advocate \"10 minutes thin muscle exercises\" about 10-30 minutes a day, and do not need to do every day, so you also Do you need to spend a lot of time? Compared with jogging, thin muscle exercise to let the body in a very short period of time changes, so I hope you can at least out of these time.\n" +
                "    Come again, no need to pick the location can be carried out, it can be said that the benefits of lean muscle surgery. As long as the body can rely on their own strength lying down to sleep, the muscles can be strengthened. In other words, not only at home, in the office or travel accommodation hotel room can also be implemented.");
        aboutSportList.add(aboutSport06);
        AboutSport aboutSport07=new AboutSport();
        aboutSport07.setNewsImage(String.valueOf(R.mipmap.sports_14));
        aboutSport07.setNewsBgImage(String.valueOf(R.mipmap.sports_13));
        aboutSport07.setNewsTitle("Fitness / fitness training how to use super group method to increase muscle");
        aboutSport07.setNewsContent("    Traditionally, the super-group of two action training should be a pair of antagonistic muscle, but if the pre-fatigue law with the super group rule with the use, there will be a very different feelings.\n" +
                "    Fitness / fitness training how to use super group method to increase muscle\n" +
                "    Side of the flat and straight rowing group composed of super group, is the popular deltoid muscle training arrangements, first do isolation to stimulate the side of the trumpet in the side of the flat lift, and then do a composite action straight rowing to ensure that the biceps brachial fatigue before the complete Burn your deltoid muscle.\n" +
                "    Fitness / fitness training how to use super group method to increase muscle\n" +
                "    Flattened birds and flat dumbbells / barbell lying down composed of super group. Triceps than chest first kneeling situation for many people are difficult to deal with, do first dumbbell birds, can be isolated to the chest, and then do the bench can ensure that the chest than the triceps faster exhausted.\n" +
                "    Fitness / fitness training how to use super group method to increase muscle\n" +
                "    On the oblique birds and on the reclining push into a super group, the principle of the same as above. The upper part of the pectoralis major is the need for each person to focus on strengthening the site.\n" +
                "    Fitness / fitness training how to use super group method to increase muscle\n" +
                "    Leg curl and straight leg hard pull composed of super group. Legs are isolated to stimulate the movement of the biceps muscles, and straight legs hard pull in the biceps to stimulate the muscles at the same time, the lower back will be stimulated, if the back of the first exhausted, the biceps muscles by the stimulation is not Full, straight legs and pull the legs to form a super group can ensure that the biceps muscles are fully exercise.\n" +
                "    Fitness / fitness training how to use super group method to increase muscle\n" +
                "    Dumbbell straight arm rowing / supine straight arm pull up and sit pilgrimage composed of super group. The latissimus dorsi muscle is basically used in the latissimus dorsi muscle, but the straight arm rowing and the straight arm pull is the exception. These two actions will isolate the latissimus dorsi and deltoid.\n" +
                "    Fitness / fitness training how to use super group method to increase muscle\n" +
                "    Super group training is often used 6 ~ 8RM weight\n" +
                "    Super group training group between the rest time is longer, usually 90 ~ 120s\n" +
                "    No matter what training, have to warm up! Fully warm up! Fully warm up!\n" +
                "    Super group strength is greater, 1 to 2 hours before training, please add enough complex, simple carbohydrate");
        aboutSportList.add(aboutSport07);
    }
    /**
     * 使用ListView的适配器
     */
    public  void useListViewApdater(){
        aboutSportAdapter = new AboutSportAdapter(MineAboutSportActivity.this);
        aboutSportAdapter.setAboutSportList(aboutSportList);
        abSportListView.setAdapter(aboutSportAdapter);
    }
    /**
     * 使用ListView的事件监听
     */
    public void useListViewMethod(){
        abSportListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //创建一个Buddle对象
                Bundle bundle = new Bundle();
                AboutSport aboutSport = aboutSportList.get(position);
                bundle.putSerializable("aboutSport",aboutSport);
                Intent abSportNewIntent = new Intent(MineAboutSportActivity.this, AboutSportNewsActivity.class);
                abSportNewIntent.putExtras(bundle);
                startActivity(abSportNewIntent);
            }
        });
    }
    /**
     * 定义OnClickListener的事件监听总方法
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mine_aboutsport_iv_zuojiantou:
                MineAboutSportActivity.this.finish();
                break;
            default:
                break;
        }
    }
}
