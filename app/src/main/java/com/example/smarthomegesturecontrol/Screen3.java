package com.example.smarthomegesturecontrol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Screen3 extends AppCompatActivity {
    private Button recordButton;
    private Button videoPreviewButton;
    private VideoView videoView2;
    private Button uploadButton;
    private Button nextGestureButton;

    private static final int REQUEST_VIDEO_CAPTURE = 1;


    static int videoNumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen3);
        askPermissions();
        Bundle bundle = getIntent().getExtras();
        String gestureVideoName= bundle.getString("Gesture");

        recordButton = (Button) findViewById(R.id.recordButton);
        videoPreviewButton = (Button) findViewById(R.id.videoPreviewButton);
        videoView2 = (VideoView) findViewById(R.id.videoView2);
        uploadButton = (Button) findViewById(R.id.uploadButton);
        nextGestureButton = (Button) findViewById(R.id.nextGestureButton);
        nextGestureButton.setEnabled(false);
        int practice_counter = 0;

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordVideo();
            }
        });

        videoPreviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
                String fileName = "GestureDetectionVideos/test_video.mp4";
                String videoFile = (baseDir+"/"+fileName);
                Log.d("tag",videoFile);
                videoView2.setVideoPath(videoFile);
                videoView2.start();
            }
        });

        uploadButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
                String fileName = "GestureDetectionVideos/test_video.mp4";
                String videoFile = (baseDir+"/"+fileName);
                uploadVideo(videoFile,gestureVideoName);
            }
        }));

        nextGestureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Screen3.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }


    void recordVideo()
    {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 5);
        Uri fileUri = getOutputMediaFileUri();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        startActivityForResult(intent, REQUEST_VIDEO_CAPTURE);
    }

    public Uri getOutputMediaFileUri()
    {
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "GestureDetectionVideos");
        imagesFolder.mkdir();
        File image = new File(imagesFolder, "test_video.mp4");
//        videoNumber++;
        Uri uriSavedVideo = FileProvider.getUriForFile(Screen3.this,getApplicationContext().getPackageName()+ ".provider", image);
        return uriSavedVideo;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            // Video capture was successful
            Log.d("Screen3", "Video saved to: " + getOutputMediaFileUri().getPath());
            // Here you can update UI or perform other actions as needed
        }
    }

    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }



    public void uploadVideo(String videoFile, String gestureVideoName)
    {
        OkHttpClient okHttpClient = new OkHttpClient();
        String url = "http://10.0.2.2:5000/upload-video";

        // Ensure this directory exists on the device
        File videoDirectory = new File(Environment.getExternalStorageDirectory(), "GestureDetectionVideos");
        if (!videoDirectory.exists()) {
            videoDirectory.mkdirs();
        }

        // File path for the video to upload
        File videoFileToUpload = new File(videoDirectory, "test_video.mp4");

        if (!videoFileToUpload.exists()) {
            // Handle the case where the video file doesn't exist
            Log.e("uploadVideo", "Video file not found: " + videoFileToUpload.getAbsolutePath());
            return;
        }

        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        String videoName = gestureVideoName + "_PRACTICE_" + videoNumber + "_aditya_meka.mp4";
        multipartBodyBuilder.addFormDataPart("video", videoName, RequestBody.create(MediaType.parse("video/*mp4"), videoFileToUpload));
        RequestBody postBodyImage = multipartBodyBuilder.build();
        Request request = new Request.Builder().url(url).post(postBodyImage).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("onFailure", "onFailure: ");
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
//                    String myResponse = response.body().string();

                    Screen3.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(videoNumber >=3)
                            {
                                videoNumber = 1;
                                uploadButton.setEnabled(false);
                                nextGestureButton.setEnabled(true);
                            } else {
                                videoNumber+=1;
                            }
                        }
                    });

                    Log.d("response", "onResponse: 11");
                }
                Screen3.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("response", "onResponse: ");
                        Toast.makeText(Screen3.this, "Gesture Uploaded", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }
}