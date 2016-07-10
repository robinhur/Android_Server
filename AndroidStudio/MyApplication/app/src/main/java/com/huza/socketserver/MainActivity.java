package com.huza.socketserver;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    protected static final int MSG_ID = 0x1337;
    Thread myCommsThread;
    ServerSocket ss;
    String mClientMsg = "";
    public static final int SERVERPORT = 6000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv = (TextView) findViewById(R.id.TextView01);
        tv.setText("Nothing from client yet");
    }

    @Override
    protected void onStart() {
        super.onStart();

        myCommsThread = new Thread(new CommsThread());
        myCommsThread.start();
    }

    @Override
    protected void onStop() {
        super.onStop();

        try {
            ss.close();
            myCommsThread.interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Handler myUpdateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ID:
                    TextView tv = (TextView) findViewById(R.id.TextView01);
                    tv.setText(mClientMsg);
                    break;
                default:
                    break;
            }

            super.handleMessage(msg);
        }
    };

    private class CommsThread implements Runnable {
        @Override
        public void run() {
            Socket s = null;
            try {
                ss = new ServerSocket(SERVERPORT);

                while (!Thread.currentThread().isInterrupted()) {
                    Message m = new Message();
                    m.what = MSG_ID;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
