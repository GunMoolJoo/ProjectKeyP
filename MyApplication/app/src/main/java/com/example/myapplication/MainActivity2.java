package com.example.myapplication;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.net.InternetDomainName;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class MainActivity2 extends AppCompatActivity {
    int i =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

                    Map<String, Object> city = new HashMap<>();
                    city.put("keyWordList",FieldValue.arrayUnion(editText_keyword.getText().toString()) );

                    Map<String, Object> city3 = new HashMap<>();
                    city3.put("data", FieldValue.arrayUnion(""));

                    Map<String, Object> city4 = new HashMap<>();
                    city4.put("user", FieldValue.arrayUnion(""));

                    String et= new String(editText_keyword.getText().toString());

                    Log.d(TAG, "이거 확인 : "+editText_url.getText().toString());
                    String exam=encodeForFirebaseKey(editText_url.getText().toString());
                    DocumentReference docUrl = db.collection("crawlingUrl").document("urlList");
                    docUrl.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot documentSnapshot = task.getResult();



                            Log.d("aaaaaa","dddd"+et);
                            Log.d("AaaaaAA", "이거 머임"+documentSnapshot.get("urlList").toString());


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
                                                                docRef1.update("keyWordList", FieldValue.arrayUnion(et));
                                                                // user id 추가하기
                                                                //docRef1.collection(et).document("user").update();
                                                            }
                                                            else{

                                                                Log.d("xx","이거는 폴스");
                                                                docRef1.update("keyWordList", FieldValue.arrayUnion(et));
                                                                docRef1.collection(et).document("data").set(city3);
                                                                docRef1.collection(et).document("user").set(city4);
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
                                        docUrl.update("urlList", FieldValue.arrayUnion(exam));
                                        // document에 지금 입력받은 url 추가
                                        Log.d("ttt","ddd"+et);
                                        db.collection("crawlingUrl").document(exam).update("keyWordList", FieldValue.arrayUnion(et));
                                        db.collection("crawlingUrl").document(exam).set(city);
                                        db.collection("crawlingUrl").document(exam).collection(et).document("data").set(city3);
                                        db.collection("crawlingUrl").document(exam).collection(et).document("user").set(city4);


                                    }
                                }
                            });
                            // urlList 안에 입력받은 url이 있으면



                        }
                    });


                        //로그인 시 아이디 추가 예정
                    db.collection("crawlingUrl").document(exam).collection(editText_keyword.getText().toString()).document("user")
                                .update("user", FieldValue.arrayUnion("qwer"));


                    editText_url.setText(null);
                    editText_keyword.setText(null);
                }

                else{
                    Toast.makeText(MainActivity2.this, "제대로 입력해 주세요.", Toast.LENGTH_SHORT).show();
                }
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }

        });
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

