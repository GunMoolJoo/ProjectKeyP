package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity3 extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;     //파이어 베이스 인증
    private DatabaseReference mDatabaseRef; //실시간 데이터베이스
    private EditText email_R, password_1, password_2;       //회원가입 입력 필드
    private Button mBtnRegi;                //회원가입 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("front ");

        email_R = findViewById(R.id.signUp_email);
        password_1  = findViewById(R.id.signUp_pass_1);
        password_2 = findViewById(R.id.signUp_pass_2);
        mBtnRegi = findViewById(R.id.signUp_signUp);

        mBtnRegi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //회원가입 처리 시작
                String strEmail = email_R.getText().toString();
                String strPass = password_1.getText().toString();

                //Firebase Auth 진행
                mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPass).addOnCompleteListener(SignUpActivity3.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                            UserAccount account = new UserAccount();
                            account.setIdToken(firebaseUser.getUid());
                            account.setEmailTd(firebaseUser.getEmail());
                            account.setPassword(strPass);

                            //setValue : database에 insert하는 행위
                            mDatabaseRef.child("UserAccount").child(firebaseUser.getUid()).setValue(account);

                            Toast.makeText(SignUpActivity3.this, "회원가입에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignUpActivity3.this, MainActivity2.class);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            Toast.makeText(SignUpActivity3.this, "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });




    }
}