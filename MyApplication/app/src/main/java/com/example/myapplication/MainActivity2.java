package com.example.myapplication;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;

import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;

import java.util.Arrays;
import java.util.HashMap;

import java.util.Map;


public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String takeEmail = intent.getExtras().getString("throw_email");


        // 빈 데이터 리스트 생성.
        final ArrayList<String> items = new ArrayList<String>() ;
        // ArrayAdapter 생성. 아이템 View를 선택(single choice)가능하도록 만듦.
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, items) ;

        // listview 생성 및 adapter 지정.
        final ListView listview = (ListView) findViewById(R.id.listview1) ;
        listview.setAdapter(adapter) ;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        InputMethodManager manager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE) ;
        ArrayList<String> arrayList = new ArrayList<>();
        Map<String, Object> url = new HashMap<>();
        EditText editText_url = findViewById(R.id.url);
        EditText editText_keyword = findViewById(R.id.keyword);
        String exam=editText_url.getText().toString();
        Button plus = (Button) findViewById(R.id.plus);
        DocumentReference docRef = db.collection("crawlingUrl").document("User id");

        Query query = db.collection("crawlingUrl");

       //로그인 사용자 기존 리스트 가져오기
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            String email = user.getEmail();




        } else {
            // No user is signed in
        }

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());

                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });


        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());

                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
        docRef.addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                // ...
            }
        });

        ListenerRegistration registration = query.addSnapshotListener(
                new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                    }
                    // ...
                });



// ...
        db.collection("crawlingUrl")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                Log.d(TAG, "New city: " + dc.getDocument().getData());
                            }
                        }

                    }
                });


// Stop listening to changes
        registration.remove();



        plus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(!editText_url.getText().toString().equals("")&&!editText_keyword.getText().toString().equals("")) {
                    Toast.makeText(MainActivity2.this, "추가 되었습니다~!", Toast.LENGTH_SHORT).show();

                    String keyword;
                    keyword = editText_keyword.getText().toString();// 아이템 추가.
                    items.add("링크:"+ editText_url.getText().toString() + "keyword:" +keyword);
                    // listview 갱신
                    adapter.notifyDataSetChanged();

                    Map<String, Object> city = new HashMap<>();
                    city.put("keyWordList",FieldValue.arrayUnion(editText_keyword.getText().toString()) );

                    Map<String, Object> city2 = new HashMap<>();
                    city2.put("user", FieldValue.arrayUnion(takeEmail));

                    Map<String, Object> city3 = new HashMap<>();
                    city3.put("data", FieldValue.arrayUnion(""));

                    Map<String, Object> city4 = new HashMap<>();
                    city4.put("user", FieldValue.arrayUnion("Anonymous"));

                    Map<String, Object> city5= new HashMap<>();
                    city5.put(encodeForFirebaseKey(editText_url.getText().toString()), FieldValue.arrayUnion(editText_keyword.getText().toString()));

                    String et= new String(editText_keyword.getText().toString());

                    Log.d(TAG, "이거 확인 : "+editText_url.getText().toString());
                    String exam=encodeForFirebaseKey(editText_url.getText().toString());
                    DocumentReference docUrl = db.collection("crawlingUrl").document("urlList");
                    docUrl.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot documentSnapshot = task.getResult();

                            db.collection("crawlingUrl").whereArrayContains("urlList", exam).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    Log.d("이거 url","흐허   "+queryDocumentSnapshots.size());//막
                                    if(queryDocumentSnapshots.size() == 1){
                                        Log.d("이거는 큰트루","ㅇㅇㅇ");
                                        // keyword 체크 해야함
                                        // 입력받은 keyword가 있을 때
                                        DocumentReference docRef1 = db.collection("crawlingUrl").document(exam);
                                        docRef1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if(task.isSuccessful()){
                                                    DocumentSnapshot document = task.getResult();
                                                    // keyword가 field에 있을 때

                                                    db.collection("crawlingUrl").document(exam).collection(et).document("data").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                            if(documentSnapshot.exists()){

                                                                Log.d("dd", "이거는 트루");
                                                                // user id 추가하기
                                                                //docRef1.collection(et).document("user").update();
                                                            }
                                                            else{

                                                                Log.d("xx","이거는 폴스");
                                                                docRef1.update("keyWordList", FieldValue.arrayUnion(et));
                                                                if(!user.getEmail().equals("anonymous")) {//로그인 성공시
                                                                    docRef1.collection(et).document("user").set(city2);


                                                                    db.collection("user").document("User id").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                                                                        @Override
                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                                            Log.d("d","take"+user.getEmail());
                                                                            db.collection("user").whereArrayContains("user", user.getEmail()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                                @Override
                                                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                                    Log.d("dd","dd"+queryDocumentSnapshots.size());
                                                                                    if (queryDocumentSnapshots.size() == 1) {
                                                                                        db.collection("user").document(user.getEmail()).update(city5);
                                                                                        db.collection("user").document("User id").update(city2);
                                                                                        Log.d("위","이메일 있음");
                                                                                    }
                                                                                    else {//이메일이 있을떄
                                                                                        db.collection("user").document(user.getEmail()).set(city5);
                                                                                        db.collection("user").document("User id").update(city2);
                                                                                        Log.d("위","이메일 없음");
                                                                                    }
                                                                                }
                                                                            });
                                                                        }
                                                                    });
                                                                }
                                                                    docRef1.collection(et).document("data").set(city3);

                                                                }
                                                            }

                                                    });

                                                }
                                            }
                                        });
                                    }
                                    // 없으면
                                    else{
                                        Log.d("이거는 큰폴스","ㅇㅇㅇ");
                                        // urlList에 지금 입력받은 url 추가
                                        //et=keyword
                                        docUrl.update("urlList", FieldValue.arrayUnion(exam));
                                        // document에 지금 입력받은 url 추가
                                        Log.d("ttt","ddd"+et);
                                        db.collection("crawlingUrl").document(exam).update("keyWordList", FieldValue.arrayUnion(et));
                                        db.collection("crawlingUrl").document(exam).set(city);
                                        if(!user.getEmail().equals("Anonymous")){//로그인 성공시
                                            db.collection("crawlingUrl").document(exam).collection(et).document("user").set(city2);

                                            db.collection("user").document("User id").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    DocumentSnapshot documentSnapshot2 = task.getResult();
                                                    db.collection("user").whereArrayContains("user", user.getEmail()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                            if (queryDocumentSnapshots.size() == 1) {//이메일이 있을때
                                                                db.collection("user").document(user.getEmail()).update(city5);
                                                                db.collection("user").document("User id").update(city2);
                                                                Log.d("밑","이메일 있");
                                                            }
                                                            else {//이메일이 없을때
                                                                db.collection("user").document(user.getEmail()).set(city5);
                                                                db.collection("user").document("User id").update(city2);
                                                                Log.d("밑","이메일 없");
                                                            }
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                        db.collection("crawlingUrl").document(exam).collection(et).document("data").set(city3);
                                    }
                                }
                            });
                        }
                    });
                    editText_url.setText(null);
                    editText_keyword.setText(null);
                }

                else{
                    Toast.makeText(MainActivity2.this, "제대로 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }

        });

        // delete button에 대한 이벤트 처리.
        Button deleteButton = (Button)findViewById(R.id.delete) ;
        deleteButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                int count, checked ;
                count = adapter.getCount() ;


                if (count > 0) {
                    // 현재 선택된 아이템의 position 획득.

                    checked = listview.getCheckedItemPosition();

                    String message = "해당 데이터를 삭제하시겠습니까?<br />"+
                            "URL"+checked+"<br />" ;
                    if (checked > -1 && checked < count) {
                        // 아이템 삭제
                        items.remove(checked) ;

                        // listview 선택 초기화.
                        listview.clearChoices();

                        // listview 갱신.
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }) ;
        Button story = (Button)findViewById(R.id.story) ;
        story.setOnClickListener(new View.OnClickListener() {
            int checked;
            @Override
            public void onClick(View v) {
                checked = listview.getCheckedItemPosition();
                String sentence = items.get(checked);//[링크:타이틀 , keyword:수도권]

                String[] arr= sentence.split("링크:|keyword:");
                String a1= Arrays.toString(arr[1].split(" "));
                String a2= Arrays.toString(arr[2].split(" "));
                a1 = a1.replaceAll("\\[","");
                a1 = a1.replaceAll("\\]","");
                a1 = a1.replaceAll(" ","");
                a2 = a2.replaceAll("\\[","");
                a2 = a2.replaceAll("\\]","");
                a2 = a2.replaceAll(" ","");
                Intent intent1 = new Intent(getApplicationContext(), Story.class);
                String finalA = a1; // a1, a2 넓게 사용하기 위해
                String finalA1 = a2;
                db.collection("crawlingUrl").document(encodeForFirebaseKey(a1)).collection(a2).document("data").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        db.collection("crawlingUrl").whereArrayContains(encodeForFirebaseKey(finalA), user.getEmail()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (queryDocumentSnapshots.size() == 1) {

                                    Log.d("data","   data 있");
                                }
                                else{
                                    //null값으로 넘겨주기
                                }
                            }
                        });

                    }
                });

                startActivity(intent1);
            }
        }) ;


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

