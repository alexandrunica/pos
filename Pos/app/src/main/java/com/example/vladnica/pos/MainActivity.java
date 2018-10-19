package com.example.vladnica.pos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.vladnica.pos.socket.NSDDiscover;
import com.example.vladnica.pos.socket.NSDListen;

public class MainActivity extends AppCompatActivity  {

    private final static String TAG = MainActivity.class.getSimpleName();

    private NSDListen mNSDListener;
    private NSDDiscover mNSDDiscover;

    private Button mRegisterBtn;
    private Button mDiscoverBtn;
    private Button mSayHelloBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startActivity(new Intent(MainActivity.this, ServerSocketTestActivity.class));




        mRegisterBtn = (Button)findViewById(R.id.register);
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNSDListener.registerDevice();
            }
        });

        mDiscoverBtn = (Button)findViewById(R.id.discover);
        mDiscoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNSDDiscover.discoverServices();
            }
        });

        mSayHelloBtn = (Button)findViewById(R.id.sayHello);
        mSayHelloBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNSDDiscover.sayHello();

            }
        });

        //Show selection alert dialog...
        new AlertDialog.Builder(this)
                .setMessage("Select if you want to discover or register a service.")
                .setPositiveButton("Register", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDiscoverBtn.setVisibility(View.GONE);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("discover_dlg_btn", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mRegisterBtn.setVisibility(View.GONE);
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private NSDDiscover.DiscoveryListener mDiscoveryListener = new NSDDiscover.DiscoveryListener() {
        @Override
        public void serviceDiscovered(String host, int port) {
            //This callback is on a worker thread...
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    findViewById(R.id.sayHello).setVisibility(View.VISIBLE);
                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mNSDListener.shutdown();
        mNSDDiscover.shutdown();
    }
}