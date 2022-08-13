package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;     //파이어 베이스 인증
    private DatabaseReference mDatabaseRef; //실시간 데이터베이스
    private EditText email_R, password_1;       //로그인 입력 필드

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("front ");

        email_R = findViewById(R.id.login_email_Id);
        password_1  = findViewById(R.id.login_passWord);


        Button loginButton = (Button)findViewById(R.id.login);
        Button imageButton = (Button) findViewById(R.id.Test);
        Button signUp = (Button)findViewById(R.id.sign);

        //회원가입 버튼 접ㅈ속!
        signUp.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity3.class);
                startActivity(intent);
            }
        }));

//      로그인 버튼
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strEmail = email_R.getText().toString();
                String strPass = password_1.getText().toString();

                mFirebaseAuth.signInWithEmailAndPassword(strEmail,strPass).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //로그인 성공
                            Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                            startActivity(intent);
                            finish();//
                        }
                        else{
                            Toast.makeText(LoginActivity.this, "로그인 실패...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });



        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                startActivity(intent);
            }

        });

    }

}
