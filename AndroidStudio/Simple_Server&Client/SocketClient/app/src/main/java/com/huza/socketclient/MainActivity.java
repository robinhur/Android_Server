package com.huza.socketclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REDIRECTED_SERVERPORT = 5000;
    Button bt;
    TextView tv;
    Socket socket;
    String serverIPAddress = "127.0.0.1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt = (Button) findViewById(R.id.button);

        TextView ip = (TextView) findViewById(R.id.textView1);
        ip.setText("");
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface ni = en.nextElement();
                List<InterfaceAddress> list = ni.getInterfaceAddresses();
                Iterator<InterfaceAddress> it = list.iterator();
                while (it.hasNext()) {
                    InterfaceAddress ia = it.next();
                    ip.setText(ip.getText().toString()+ia.getAddress()+"\n");
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        tv = (TextView) findViewById(R.id.TextView);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InetAddress serverAddr = null;
                try {
                    serverAddr = InetAddress.getByName(serverIPAddress);
                    socket = new Socket(serverAddr, REDIRECTED_SERVERPORT);

                    EditText et = (EditText) findViewById(R.id.editText);
                    String str = et.getText().toString();
                    PrintWriter out = new PrintWriter(new BufferedWriter(
                            new OutputStreamWriter(socket.getOutputStream())),
                            true);
                    out.println(str);
                    Log.d("Client","Client sent message");
                    out.close();
                    socket.close();
                } catch (UnknownHostException e) {
                    tv.setText("UnknownHostException");
                    e.printStackTrace();
                } catch (IOException e) {
                    tv.setText("IOException");
                    e.printStackTrace();
                } catch (Exception e) {
                    tv.setText("Exception");
                    e.printStackTrace();
                }
            }
        });
    }
}
