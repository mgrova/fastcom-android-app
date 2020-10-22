package com.example.fastcomapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {

    private String mIP;
    private TextView mIPMobile;
    private Button mBtnSend, mBtnReceive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }

    private void initUI() {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo info = wm.getConnectionInfo();
        mIP = "Error reading IP";

        if (info != null && info.getIpAddress() != 0)
            mIP = android.text.format.Formatter.formatIpAddress(info.getIpAddress());

        mIPMobile = (TextView) findViewById(R.id.current_ip);
        mIPMobile.setText("Current IP: " + mIP + ":8888"); // 666

        mBtnSend = (Button) findViewById(R.id.send_btn);
        mBtnSend.setOnClickListener(this);
        mBtnSend.setEnabled(true);

        mBtnReceive = (Button) findViewById(R.id.receive_btn);
        mBtnReceive.setOnClickListener(this);
        mBtnReceive.setEnabled(true);
    }

    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.send_btn: {
                Intent intent = new Intent(this, PublisherActivity.class);
                intent.putExtra("ip","129.129.129.129");
                startActivity(intent);
                break;
            }
            case R.id.receive_btn: {
                Intent intent = new Intent(this, SubscriberActivity.class);
                startActivity(intent);
                break;
            }
            default:
                break;
        }
    }
}