package com.runstart.slidingpage;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.runstart.R;



public class SelectLogin extends AppCompatActivity {

    private Button jump, login;

    private TextView nullCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.select_login_layout);

        jump = (Button)findViewById(R.id.jump);
        login = (Button)findViewById(R.id.login);
        nullCount = (TextView)findViewById(R.id.nullCount);

        jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SelectLogin.this, LoginPageActivity.class));
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SelectLogin.this, LoginPageActivity.class));
                finish();
            }
        });
        nullCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SelectLogin.this, RegisterActivity.class));
            }
        });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
