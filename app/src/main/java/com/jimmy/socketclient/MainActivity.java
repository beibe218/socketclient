package com.jimmy.socketclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private EditText hostAddressEt;
    private EditText hostPortEt;
    private Socket socket;
    private DataOutputStream dos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hostAddressEt = findViewById(R.id.host_address_et);
        hostPortEt = findViewById(R.id.host_port_et);
    }

    public void onJoin(View view) {
        String hostAddress = hostAddressEt.getText().toString();
        String hostPort = hostPortEt.getText().toString();
        if (TextUtils.isDigitsOnly(hostAddress) || TextUtils.isEmpty(hostPort)) {
            return;
        }

        JoinThread thread = new JoinThread(hostAddress, Integer.parseInt(hostPort));
        thread.start();
    }

    public void onSend(View view) {
        if (dos == null) {
            return;
        }
        SendThread sendThread = new SendThread(dos);
        sendThread.start();
    }

    private class JoinThread extends Thread {

        private String hostAddress;
        private int hostPort;

        public JoinThread(String hostAddress, int hostPort) {
            this.hostAddress = hostAddress;
            this.hostPort = hostPort;
        }

        @Override
        public void run() {
            try {
                socket = new Socket(hostAddress, hostPort);
                dos = new DataOutputStream(socket.getOutputStream());
                String msg = "join";
                dos.writeUTF(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class SendThread extends Thread {

        private DataOutputStream dos;

        public SendThread(DataOutputStream dos) {
            this.dos = dos;
        }

        @Override
        public void run() {
            if (dos != null) {
                try {
                    String msg = "test message";
                    dos.writeUTF(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
