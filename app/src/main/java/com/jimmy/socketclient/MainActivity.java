package com.jimmy.socketclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private EditText hostAddressEt;
    private EditText hostPortEt;
    private Socket socket;
    private DataOutputStream dos;
    private EditText userNameEt;
    private EditText messageEt;
    private boolean isReading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hostAddressEt = findViewById(R.id.host_address_et);
        hostPortEt = findViewById(R.id.host_port_et);
        userNameEt = findViewById(R.id.user_name_et);
        messageEt = findViewById(R.id.message_et);
    }

    public void onJoin(View view) {
        String hostAddress = hostAddressEt.getText().toString();
        String hostPort = hostPortEt.getText().toString();
        String userName = userNameEt.getText().toString().trim();

        if (TextUtils.isDigitsOnly(hostAddress) || TextUtils.isEmpty(hostPort) || TextUtils.isEmpty(userName)) {
            return;
        }
        hostAddress = "10.0.3.2";
        hostPort = "6100";
        JoinThread thread = new JoinThread(hostAddress, Integer.parseInt(hostPort), userName);
        thread.start();
    }

    public void onSend(View view) {
        if (dos == null) {
            return;
        }

        String userName = userNameEt.getText().toString().trim();
        String message = messageEt.getText().toString().trim();

        if (TextUtils.isEmpty(message) || TextUtils.isEmpty(userName)) {
            return;
        }

        SendThread sendThread = new SendThread(dos, userName, message);
        sendThread.start();
    }

    private class JoinThread extends Thread {

        private String hostAddress;
        private int hostPort;
        private String userName;

        public JoinThread(String hostAddress, int hostPort, String userName) {
            this.hostAddress = hostAddress;
            this.hostPort = hostPort;
            this.userName = userName;
        }

        @Override
        public void run() {
            try {
                socket = new Socket(hostAddress, hostPort);
                dos = new DataOutputStream(socket.getOutputStream());
                String msg = "join&" + userName;
                dos.writeUTF(msg);
                isReading = true;
                new ReceiveThread(new DataInputStream(socket.getInputStream())).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class SendThread extends Thread {

        private DataOutputStream dos;
        private String userName;
        private String message;

        public SendThread(DataOutputStream dos, String userName, String message) {
            this.dos = dos;
            this.userName = userName;
            this.message = message;
        }

        @Override
        public void run() {
            if (dos != null) {
                try {
                    String msg = "message&" + userName + "&" + message;
                    dos.writeUTF(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class ReceiveThread extends Thread {
        private DataInputStream dis;

        public ReceiveThread(DataInputStream dis) {
            this.dis = dis;
        }

        @Override
        public void run() {
            if (dis != null) {
                while (isReading) {
                    try {
                        final String s = dis.readUTF();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
