package com.example.smarthomegesturecontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.VideoView;

public class Screen2 extends AppCompatActivity {
    private VideoView videoView;
    private Button practiceButton;
    private int numberOfTimesVideoPlayed;
    private ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen2);

        Bundle bundle = getIntent().getExtras();
        String gestureVideoName= bundle.getString("Gesture");
        Toast.makeText(getApplicationContext(), String.valueOf(gestureVideoName)+"gesture", Toast.LENGTH_SHORT).show();

        String videoName = getVideoName(gestureVideoName);
        videoView = (VideoView) findViewById(R.id.videoView);
        practiceButton = (Button) findViewById(R.id.practiceButton);
        imageButton = (ImageButton) findViewById(R.id.imageButton);
        imageButton.setVisibility(View.INVISIBLE);
        String path = "android.resource://" + this.getPackageName() + "/" + getResources().getIdentifier(videoName,"raw",getPackageName());
        Log.d("resource name", String.valueOf(getResources().getIdentifier("h0","raw",getPackageName())));
        videoView.setVideoURI(Uri.parse(path));
        videoView.start();
        numberOfTimesVideoPlayed = 0;


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageButton.setVisibility(View.INVISIBLE);
                videoView.start();
            }
        });

        practiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Screen2.this, Screen3.class);
                Bundle b = new Bundle();
                b.putString("Gesture", gestureVideoName);
                intent.putExtras(b);
                startActivity(intent);
            }
        });
        //to-do add the logic on click not to setOncompletipn listener
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                numberOfTimesVideoPlayed++;
                imageButton.setVisibility(View.VISIBLE);
                if(numberOfTimesVideoPlayed >= 3)
                {
                    imageButton.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    String getVideoName(String gestureName) {
        String videoName;
        switch (gestureName) {
            case "LightOn" : videoName = "hlighton"; break;
            case "LightOff" : videoName = "hlightoff"; break;
            case "FanOn" : videoName = "hfanon"; break;
            case "FanOff" : videoName = "hfanoff"; break;
            case "FanUp" : videoName = "hincreasefanspeed"; break;
            case "FanDown": videoName = "hdecreasefanspeed"; break;
            case "SetThermo" : videoName = "hsetthermo"; break;
            case "Num0" : videoName = "h0"; break;
            case "Num1" : videoName = "h1"; break;
            case "Num2" : videoName = "h2"; break;
            case "Num3" : videoName = "h3"; break;
            case "Num4" : videoName = "h4"; break;
            case "Num5" : videoName = "h5"; break;
            case "Num6" : videoName = "h6"; break;
            case "Num7" : videoName = "h7"; break;
            case "Num8" : videoName = "h8"; break;
            case "Num9" : videoName = "h9"; break;
            default: videoName = "h0";
        }
        return videoName;
    }
}