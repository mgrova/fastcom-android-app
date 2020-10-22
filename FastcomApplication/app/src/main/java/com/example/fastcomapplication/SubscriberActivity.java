package com.example.fastcomapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.InputType;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.fastcomapplication.fastcom.Callable;
import com.example.fastcomapplication.fastcom.ImageSubscriber;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

public class SubscriberActivity extends Activity  {

    private EditText ipInput_;
    private ImageView mScreen;
    private ImageSubscriber imageSubscriber_;
    private long lastTime = System.nanoTime();
    private String mHostIP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_subscriber);

        ipInput_ = new EditText(this);
        ipInput_.setInputType(InputType.TYPE_CLASS_PHONE);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mScreen = findViewById(R.id.screen);

        Intent intent = this.getIntent();
        mHostIP = intent.getStringExtra("ip");

    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback() {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OPENCV", "OpenCV loaded successfully");
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        if (OpenCVLoader.initDebug()) {
            Log.d("OPENCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        } else {
            Log.d("OPENCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // IP SELECTOR
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose PC IP")
                .setView(ipInput_)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        imageSubscriber_ = new ImageSubscriber(ipInput_.getText().toString(), 9777);
                        imageSubscriber_.registerCallback(new Callable<Mat>(){

                            @Override
                            public void run(Mat _data){
                                //Log.d("EAGLE_STREAMER", "received new image");
                                //final Mat image = new Mat();
                                //List<Mat> src = Arrays.asList(_data.clone(), _data.clone());
                                //Core.hconcat(src, image);
                                final Mat image = _data.clone();
                                double incT = (System.nanoTime()-lastTime)*10e-9;
                                //Log.d("EAGLE_STREAM", "IncT: " + String.valueOf(incT)+". FPS: " + String.valueOf(1/incT));
                                lastTime = System.nanoTime();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Bitmap bmp = Bitmap.createBitmap(image.width(), image.height(), Bitmap.Config.RGB_565);
                                        Utils.matToBitmap(image, bmp);
                                        mScreen.setImageBitmap(bmp);
                                    }
                                });
                            }
                        });
                    }
                })
                .setNegativeButton("Do nothing", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

}