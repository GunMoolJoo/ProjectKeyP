package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class Story extends AppCompatActivity {
    Intent intent1 = getIntent();
    String story = intent1.getExtras().getString("story");
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    // 빈 데이터 리스트 생성.
    final ArrayList<String> items2 = new ArrayList<String>() ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.story);
        items2.add (story);


    }

}
