package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;




// 아래 내용을 처음 실행되는 java 파일에 넣어준다.
// 앱이 실행되면 FCM 서비스를 실행하게 된다.
//Intent fcm = new Intent(getApplicationContext(), MyFirebaseFCMService.class);
//startService(fcm);

public class MyFirebaseFCMService extends FirebaseMessagingService {

    //field=====================================

    private static final String TAG = "FirebaseService";



    //method=====================================

    public MyFirebaseFCMService() {
        super();
        Task<String> token = FirebaseMessaging.getInstance().getToken();
        token.addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isSuccessful()){
                    Log.d("FCM Token", task.getResult());
                }
            }
        });

    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        if (message.getData().size() > 0) {
            showNotification(message.getData().get("url"),
                    message.getData().get("keyword"),
                    message.getData().get("summary"));
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        SharedPreferences pref = this.getSharedPreferences("token", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("token", token).apply();
        editor.commit();

        Log.i("log","성공적 토큰 저장");
    }

    private RemoteViews getCustomDesign(String url, String keyword, String summary) {
        RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.pop_up_alarm);
        remoteViews.setTextViewText(R.id.noti_title, "\""+url+"\" 에서  \""+keyword+"\" 를 찾았습니다.");
        remoteViews.setTextViewText(R.id.noti_message, summary);
        remoteViews.setImageViewResource(R.id.logo, R.drawable.ic_launcher_background);
        return remoteViews;
    }

    public void showNotification(String url, String keyword, String summary) {
        //팝업 터치시 이동할 액티비티를 지정합니다.
        Intent intent = new Intent(this, MainActivity2.class);
        //알림 채널 아이디 : 본인 하고싶으신대로...
        String channel_id = "GunMoolJoo_ID";
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);

        //기본 사운드로 알림음 설정. 커스텀하려면 소리 파일의 uri 입력
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channel_id)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setSound(uri)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000}) //알림시 진동 설정 : 1초 진동, 1초 쉬고, 1초 진동
                .setOnlyAlertOnce(true) //동일한 알림은 한번만.. : 확인 하면 다시 울림
                .setContentIntent(pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) { //안드로이드 버전이 커스텀 알림을 불러올 수 있는 버전이면
            //커스텀 레이아웃 호출
            builder = builder.setContent(getCustomDesign(url,keyword,summary));
        } else { //아니면 기본 레이아웃 호출
            builder = builder.setContentTitle("\""+url+"\" 에서  \""+keyword+"\" 를 찾았습니다.")
                    .setContentText(summary)
                    .setSmallIcon(R.drawable.ic_launcher_background); //커스텀 레이아웃에 사용된 로고 파일과 동일하게..
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //알림 채널이 필요한 안드로이드 버전을 위한 코드
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channel_id, "CHN_NAME", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setSound(uri, null);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        //알림 표시 !
        notificationManager.notify(0, builder.build());
    }
}