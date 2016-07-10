package com.huza.enhanced_socket_server;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private Button connect;
    private Button finish;
    private Button start;
    private TextView text;

    private String ip;
    private int port;

    private TCPClient client;
    private Handler mHandler;
    private Socket socket;
    private BufferedWriter networkWriter;
    private BufferedReader networkReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler();

        final EditText eip = (EditText) findViewById(R.id.editText1);
        final EditText eport = (EditText) findViewById(R.id.editText2);
        final EditText et = (EditText) findViewById(R.id.editText3);

        connect = (Button) findViewById(R.id.btn_connect);
        finish = (Button) findViewById(R.id.btn_connect);
        start = (Button) findViewById(R.id.btn_connect);
        text = (TextView) findViewById(R.id.textView1);

        connect.setEnabled(true);
        finish.setEnabled(false);
        start.setEnabled(false);

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ip = eip.getText().toString();
                try {
                    port = Integer.parseInt(eport.getText().toString());
                } catch (NumberFormatException e) {
                    port = 5000;
                }

                if (client == null) {
                    try {
                        client = new TCPClient(ip, port);
                        client.start();
                    } catch (RuntimeException e) {
                        text.setText("Wrong IP Address or Port Number");
                    }
                }
            }
        });

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (client!=null) {
                    new Thread(){
                        @Override
                        public void run() {
                            client.quit();
                            client = null;
                        }
                    }.start();

                    text.setText("TCP/IP disconnected");
                    connect.setEnabled(true);
                    finish.setEnabled(false);
                    start.setEnabled(false);
                    Toast.makeText(getApplicationContext(), "TCP/IP disconnected", Toast.LENGTH_LONG).show();
                }
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et.getText().toString() != null) {
                    String return_msg = et.getText().toString();
                    if(networkWriter != null) {
                        try {
                            networkWriter.write(return_msg);
                            networkWriter.newLine();
                            networkWriter.flush();
                        } catch (IOException e) {
                            text.setText("String cannot be send");
                        }
                    }
                }
            }
        });
    }

    private class TCPClient extends Thread{

        private final InetSocketAddress socketAddress;
        private int connection_timeout = 3000;
        private boolean loop;
        private String line;

        public TCPClient(String ip, int port) {
            socketAddress = new InetSocketAddress(ip, port);
        }

        @Override
        public void run() {
            try {
                socket = new Socket();
                socket.setSoTimeout(connection_timeout);
                socket.setSoLinger(true, connection_timeout);

                socket.connect(socketAddress, connection_timeout);
                networkWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                InputStreamReader i = new InputStreamReader(socket.getInputStream());
                networkReader = new BufferedReader(i);

                Runnable showUpdate = new Runnable() {
                    @Override
                    public void run() {
                        text.setText("Server connected");
                        connect.setEnabled(false);
                        finish.setEnabled(true);
                        start.setEnabled(true);
                    }
                };

                mHandler.post(showUpdate);
                loop = true;
            } catch (Exception e) {
                loop = false;
                e.printStackTrace();
            }

            while(loop) {
                try {
                    line = networkReader.readLine();

                    if (line == null)
                        break;

                    Runnable showUpdate = new Runnable() {
                        @Override
                        public void run() {
                            text.setText(line);
                        }
                    };
                    mHandler.post(showUpdate);
                } catch (InterruptedIOException e) {
                } catch (IOException e) {
                    if (loop) {
                        loop = false;
                        Runnable showUpdate = new Runnable() {
                            @Override
                            public void run() {
                                text.setText("Connection disconnected from unknown problem");
                                connect.setEnabled(true);
                                finish.setEnabled(false);
                                start.setEnabled(false);
                            }
                        };
                        mHandler.post(showUpdate);
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    break;
                }
            }

            try {
                if (networkWriter != null) {
                    networkWriter.close();
                    networkWriter = null;
                }
                if (networkReader != null) {
                    networkReader.close();
                    networkReader = null;
                }
                if (socket != null) {
                    socket.close();
                    socket = null;
                }

                client = null;

                if (loop) {
                    Runnable showUpdate = new Runnable() {
                        @Override
                        public void run() {
                            text.setText("Server closed");
                            connect.setEnabled(true);
                            finish.setEnabled(false);
                            start.setEnabled(false);
                        }
                    };
                    mHandler.post(showUpdate);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void quit() {
            loop = false;

            try {
                if (socket != null) {
                    socket.close();
                    socket = null;
                }

                Thread.sleep(connection_timeout);
            } catch (InterruptedException e) {
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
