package com.example.vladnica.pos;

import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerSocketTestActivity extends AppCompatActivity {
    /**
     * Called when the activity is first created.
     */
    private static String TAG = "ServerSocketTest";

    private ServerSocket server;
    DataInputStream dataInputStream = null;
    DataOutputStream dataOutputStream = null;
    List<CommandModel> commandModelList;
    SendMessageListen sendMessageListen;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_socket_test);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sendMessageListen == null) {
                    sendMessageListen = new SendMessageListen(ServerSocketTestActivity.this);
                } else {
                    Toast.makeText(ServerSocketTestActivity.this, "Connected", Toast.LENGTH_LONG).show();
                }
            }
        });

        ListView listView = findViewById(R.id.listview);
        commandModelList = generateListCommand();
        if (commandModelList != null) {
            CommandAdapter commandAdapter = new CommandAdapter(this, commandModelList);
            listView.setAdapter(commandAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    sendMessageListen.sendMessage(commandModelList.get(i));
                }
            });
        } else {

        }
    }

    private List<CommandModel> generateListCommand() {
        List<CommandModel> list = new ArrayList<>();
        list.add(new CommandModel(1, "CMD_PURCHASE", 23, new byte[] {0x05, 0x00, 0x13, (byte) 0xDF, (byte) 0xFF, 0x7A, 0x01, 0x07,
                (byte) 0xDF, (byte) 0xFF, 0x69, 0x01, (byte) 0xFF, (byte) 0x9F, 0x02, 0x06,
                0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00}, "purchase request"));
        list.add(new CommandModel(2, "CMD_FIRST_DLL ", 9, new byte[] {0x05, 0x00, 0x05, (byte) 0xDF, (byte) 0xFF, 0x7A, 0x01, 0x01,
                0x00}, "first dll request"));
        list.add(new CommandModel(3, "CMD_MANUAL_DLL ", 9, new byte[] {0x05, 0x00, 0x05, (byte) 0xDF, (byte) 0xFF, 0x7A, 0x01, 0x02,
                0x00}, "manual dll request"));
        list.add( new CommandModel(4, "CMD_CLOSURE ", 9, new byte[] {0x05, 0x00, 0x05, (byte) 0xDF, (byte) 0xFF, 0x7A, 0x01, 0x06,
                0x00}, "closing accounts request"));
        list.add(new CommandModel(5, "CMD_REMOTE_TOTAL ", 9, new byte[] {0x05, 0x00, 0x05, (byte) 0xDF, (byte) 0xFF, 0x7A, 0x01, 0x04,
                0x00}, "closing accounts request"));
        list.add( new CommandModel(6, "CMD_LOCAL_TOTAL ", 9, new byte[] {0x05, 0x00, 0x05, (byte) 0xDF, (byte) 0xFF, 0x7A, 0x01, 0x05,
                0x00}, "local totals request"));
        list.add( new CommandModel(7, "CMD_REFUND ", 9, new byte[] {0x05, 0x00, 0x05, (byte) 0xDF, (byte) 0xFF, 0x7A, 0x01, 0x08,
                0x00}, "reversal request"));
        list.add(new CommandModel(8, "CMD_PREAUTH ", 23, new byte[] {0x05, 0x00, 0x13, (byte) 0xDF, (byte) 0xFF, 0x7A, 0x01, 0x10,
                (byte) 0xDF, (byte) 0xFA, 0x41, 0x01, 0x00, (byte) 0xDF, 0x6E, 0x06,
                0x00, 0x00, 0x00, 0x00, 0x55, 0x00, 0x00}, "preauthorization request"));
        list.add( new CommandModel(9, "CMD_PREAUTH_CLOSE ", 46, new byte[] {0x05, 0x00, 0x2A, (byte) 0xDF, (byte) 0xFF, 0x7A, 0x01, 0x11,
                (byte) 0xDF, (byte) 0xFA, 0x41, 0x01, 0x00, (byte) 0xDF, 0x6E, 0x06,
                0x00, 0x00, 0x00, 0x00, 0x55, 0x00, (byte) 0xDF, (byte) 0xFF,
                0x67, 0x01, 0x00, (byte) 0x9F, 0x02, 0x06, 0x00, 0x00,
                0x00, 0x00, 0x01, 0x00, (byte) 0xDF, 0x4F, 0x06, 0x00,
                0x04, 0x00, 0x00, 0x19, 0x60, 0x00}, "losing preauthorization request"));
        list.add(new CommandModel(10, "CMD_PREAUTH_SLOT_LIST ", 9, new byte[] {0x05, 0x00, 0x05, (byte) 0xDF, (byte) 0xFF, 0x7A, 0x01, 0x12,
                0x00}, "slot preauthorization state request"));
        list.add( new CommandModel(11, "CMD_LAST_RECEIPT  ", 14, new byte[] {0x05, 0x00, 0x0A, (byte) 0xDF, (byte) 0xFF, 0x7A, 0x01, 0x21,
                (byte) 0xDF, (byte) 0xFA, 0x43, 0x01, 0x00, 0x00}, "sending last ticket request"));

        list.add( new CommandModel(12, "CMD_STATE_REQUEST  ", 4, new byte[] {0x01, 0x00, 0x00, (byte) 0xFF}, "state request"));
        list.add( new CommandModel(13, "CMD_STATE_REQUEST_SET_HEARTBEAT_FREQ  ", 16, new byte[] {0x01, 0x00, 0x0C, (byte) 0xDF, (byte) 0xFF, 0x6F, 0x08, 0x30,
                0x30, 0x30, 0x30, 0x30, 0x33, 0x30, 0x30, 0x00}, "state request with set up heartbeat fequency"));

       return list;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (server != null) {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}