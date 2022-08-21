package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Story extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    // 빈 데이터 리스트 생성.
    ArrayList<StoryData> storyDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);




//        Intent intent1 = getIntent();
//        String url = intent1.getExtras().getString("url");
//        String en_url = encodeForFirebaseKey(url);
//
//        String keyword = intent1.getExtras().getString("keyword");
//
//        Log.i("url: ", "-"+url);
//        Log.i("en_url: ","-"+en_url);
//        Log.i("keyword","-"+keyword);
//
//        db.collection("crawlingUrl").document(en_url).collection(keyword).document("data").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                Log.i("qwe",""+task.getResult().getData().get("data"));
//                ArrayList<Object> ar = (ArrayList<Object>) task.getResult().getData().get("data");
//                Log.i("dict : ",ar.toString());
//                for(int i=1;i<ar.size();i++) {
//                    Map<String, Object> map = (Map<String, Object>) ar.get(i);
//                    Log.i("Map : -", map.toString());
//                    Log.i("entrySEt",map.entrySet().toString());
//                    String a = map.keySet().toString();
//                    a = a.replaceAll("\\[","");
//                    a = a.replaceAll("\\]","");
//                    String b = map.get(a).toString();
//
//                    Log.i("a+b : : :", a+","+b);
//                    title.add(a);
//                    mainStory.add(b);
//                    Log.i("aaaaa"+title.size(), "bbbbb"+mainStory.size());
//
//
//                }
//            }
//        });

        this.InitializeStoryData();

        ListView listView = (ListView)findViewById(R.id.listView);
        final StoryAdapter myAdapter = new StoryAdapter(this,storyDataList);

        listView.setAdapter(myAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id){
                Toast.makeText(getApplicationContext(),
                        myAdapter.getItem(position).getTitle(),
                        Toast.LENGTH_LONG).show();
                Intent intent1 = getIntent();
                ArrayList<String> mainStory = intent1.getStringArrayListExtra("mainStory");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://m.naver.com"));
                startActivity(intent);
            }
        });

    }

    public void InitializeStoryData()
    {
        Intent intent1 = getIntent();
        ArrayList<String> title = intent1.getStringArrayListExtra("title");
        ArrayList<String> mainStory = intent1.getStringArrayListExtra("mainStory");

        storyDataList = new ArrayList<StoryData>();
        Log.i("a"+title.size(), "b"+mainStory.size());
        for(int i=0;i<title.size();i++){
            storyDataList.add(new StoryData(title.get(i), mainStory.get(i)));
        }
    }


    public static String encodeForFirebaseKey(String s) {
        return s
                .replace("_", "__")
                .replace(".", "_P")
                .replace("$", "_D")
                .replace("#", "_H")
                .replace("[", "_O")
                .replace("]", "_C")
                .replace("/", "_S")
                ;
    }
    public static String decodeFromFirebaseKey(String s) {
        int i = 0;
        int ni;
        String res = "";
        while ((ni = s.indexOf("_", i)) != -1) {
            res += s.substring(i, ni);
            if (ni + 1 < s.length()) {
                char nc = s.charAt(ni + 1);
                if (nc == '_') {
                    res += '_';
                } else if (nc == 'P') {
                    res += '.';
                } else if (nc == 'D') {
                    res += '$';
                } else if (nc == 'H') {
                    res += '#';
                } else if (nc == 'O') {
                    res += '[';
                } else if (nc == 'C') {
                    res += ']';
                } else if (nc == 'S') {
                    res += '/';
                } else {
                    // this case is due to bad encoding
                }
                i = ni + 2;
            } else {
                // this case is due to bad encoding
                break;
            }
        }
        res += s.substring(i);
        return res;
    }
}