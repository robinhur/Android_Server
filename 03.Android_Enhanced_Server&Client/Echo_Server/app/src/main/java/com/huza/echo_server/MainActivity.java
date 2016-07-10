package com.huza.echo_server;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Handler mHandler;
    private ArrayList<EchoThread> threadList = null;
    private ServerThread thread = null;
    protected static final int MSG_ID = 1;
    protected static final int QUIT_ID = 2;
    private int port = 6000;
    private Button start;
    private Button finish;
    private TextView text;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (thread!=null) {
            new Thread() {
                @Override
                public void run() {
                    thread.quit();
                    thread = null;
                }
            }.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView ip = (TextView) findViewById(R.id.ip);
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

        final EditText eport = (EditText) findViewById(R.id.editText2);
        start = (Button) findViewById(R.id.button1);
        finish = (Button) findViewById(R.id.button2);
        text = (TextView) findViewById(R.id.textView1);
        start.setEnabled(true);
        finish.setEnabled(false);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    port = Integer.parseInt(eport.getText().toString());
                } catch (NumberFormatException e) {
                    port = 6000;
                }

                if (thread == null) {
                    try {
                        thread = new ServerThread(port);
                        thread.start();
                        text.setText("Service started");
                        start.setEnabled(false);
                        finish.setEnabled(true);
                    } catch (IOException e) {
                        text.setText("Server Thread cannot started " + e.toString());
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Service is now available", Toast.LENGTH_LONG).show();
                }
            }
        });

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (thread != null) {
                    new Thread() {
                        @Override
                        public void run() {
                            thread.quit();
                            try{
                                sleep(500);
                                if (thread.isAlive()) {
                                    sleep(2000);
                                }
                            } catch (InterruptedException e) {

                            }

                            thread = null;
                            Message m = new Message();
                            m.what = QUIT_ID;
                            m.obj = ("End service");
                            mHandler.sendMessage(m);
                        }
                    }.start();
                }
            }
        });

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_ID:
                        text.append((String) msg.obj + "\n");
                        break;
                    case QUIT_ID:
                        text.setText((String) msg.obj);
                        start.setEnabled(true);
                        finish.setEnabled(false);
                        Toast.makeText(MainActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    public class ServerThread extends Thread{
        private Boolean loop;
        private ServerSocket server;

        public ServerThread(int port) throws IOException {
            super();
            server = new ServerSocket(port);
            server.setSoTimeout(3000);

            threadList = new ArrayList<EchoThread>();
            loop = true;
        }

        @Override
        public void run() {
            while(loop) {
                try {
                    Socket sock = server.accept();
                    EchoThread thread = new EchoThread(sock);
                    thread.start();
                    threadList.add(thread);
                } catch (InterruptedIOException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    Message m = new Message();
                    m.what = QUIT_ID;
                    m.obj = ("Exception occurred in Server Thread " + e.toString());
                    mHandler.sendMessage(m);
                    break;
                }
            }
            try {
                if (server != null) {
                    server.close();
                    server = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void quit() {
            loop = false;
            if (server != null) {
                try {
                    server.close();
                    server = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            for (int i = 0; i < threadList.size(); i++) {
                EchoThread t = threadList.remove(i);
                t.quit();
                t.interrupt();
                if (t.isAlive())
                    SystemClock.sleep(1000);
            }
        }
    }

    private class EchoThread extends Thread{
        private Socket sock;
        private InetAddress inetaddr;
        private OutputStream out;
        private InputStream in;
        private PrintWriter pw;
        private BufferedReader br;

        public EchoThread(Socket sock) {
            this.sock = sock;
        }

        @Override
        public void run() {
            try {
                inetaddr = sock.getInetAddress();
                Message m = new Message();
                m.what = MSG_ID;
                m.obj = (inetaddr.getHostAddress() + " had connected");
                mHandler.sendMessage(m);

                out = sock.getOutputStream();
                in = sock.getInputStream();
                pw = new PrintWriter(new OutputStreamWriter(out));
                br = new BufferedReader(new InputStreamReader(in));
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                String line = null;
                while((line = br.readLine()) != null) {
                    pw.println(line);
                    pw.flush();

                    Message m2 = new Message();
                    m2.what = MSG_ID;
                    m2.obj = line;
                    mHandler.sendMessage(m2);
                }
            } catch (InterruptedIOException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                Message m3 = new Message();
                m3.what = MSG_ID;
                m3.obj = (inetaddr.getHostAddress() + "has disconnected");
                mHandler.sendMessage(m3);
                try {
                    threadList.remove(this);

                    if (sock != null) {
                        sock.close();
                        sock = null;
                    }
                    if (pw != null) {
                        pw.close();
                        pw = null;
                    }
                    if (br != null) {
                        br.close();
                        br = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void quit() {
            if (sock != null) {
                try {
                    sock.close();
                    sock = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
