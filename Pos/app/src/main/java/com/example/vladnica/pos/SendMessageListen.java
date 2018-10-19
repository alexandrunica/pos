package com.example.vladnica.pos;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

/**
 * Created by Vlad on 10/18/2018.
 */
public class SendMessageListen {

    private Context mContext;
    private int mSelectedPort = -1;
    private ServerSocket server;
    private SocketServerConnection mSocketServerConnection;
    private ServerSocket mDiscoverableServerSocket;
    private ProgressDialog progressDialog;

    public SendMessageListen(Context context) {
        this.mContext = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Connect to device...");
        progressDialog.show();
        mSocketServerConnection = new SocketServerConnection();
        mSocketServerConnection.openConnection();

    }

    public void sendMessage(CommandModel commandModel) {
        //mSocketServerConnection.sendMessage(new byte[] {0x01, 0x00, 0x00, (byte) 0xFF}); //status
//        mSocketServerConnection.sendMessage(new byte[]{0x05, 0x00, 0x13, (byte) 0xDF, (byte) 0xFF, 0x7A, 0x01, 0x07,
//                (byte) 0xDF, (byte) 0xFF, 0x69, 0x01, (byte) 0xFF, (byte) 0x9F, 0x02, 0x06,
//                0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00}); //purchase
//        mSocketServerConnection.sendMessage(new byte[]{0x05, 0x00, 0x05, (byte) 0xDF, (byte) 0xFF, 0x7A, 0x01, 0x01,
//                0x00});
        mSocketServerConnection.sendMessage(commandModel.getCommandBytes());
    }

    private class SocketServerConnection {
        private boolean mIsReady;
        private DataOutputStream mSocketOutput;
        private DataInputStream mSocketInput;

        public SocketServerConnection() {
            try {
                mDiscoverableServerSocket = new ServerSocket(5005);
                mSelectedPort = mDiscoverableServerSocket.getLocalPort();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void openConnection() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //Assign the socket that will be used for communication and let the thread die...
                        Log.e("TrackingFlow", "Waiting for connection...");
                        final Socket socket = mDiscoverableServerSocket.accept();
                        Log.e("TrackingFlow", "Connection found...");
                        mIsReady = true;
                        mSocketOutput = new DataOutputStream(socket.getOutputStream());
                        mSocketInput = new DataInputStream(socket.getInputStream());
                        final String socketAddress = socket.getInetAddress().getHostAddress() + " : " + socket.getPort();
                        new Handler(mContext.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(mContext, "Connected with " + socketAddress , Toast.LENGTH_LONG).show();
                            }
                        });


                        listenForMessages();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (mSocketInput != null) {
                            try {
                                mSocketInput.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (mSocketOutput != null) {
                            try {
                                mSocketOutput.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    //Reopen the connection to wait for another message...
                    openConnection();
                }
            }).start();
        }

        public void listenForMessages() {
            if (!mIsReady || mSocketInput == null) return;
            int bufferSize = 4096;
            byte[] buffer = new byte[bufferSize];
            final StringBuilder sb = new StringBuilder();
            int length = Integer.MAX_VALUE;
            int bytesRead;
            try {
                while (length >= bufferSize) {
                    length = mSocketInput.read(buffer);

                    sb.append(Arrays.toString(buffer), 0,length);
                }
                new Handler(mContext.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "Message received: " + sb.toString(), Toast.LENGTH_LONG).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void release() {
            if (mSocketOutput != null) {
                try {
                    mSocketOutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (mSocketInput != null) {
                try {
                    mSocketInput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void sendMessage(byte[] message) {
            if (mSocketOutput != null) {
                try {
                    mSocketOutput.write(message);
                    mSocketOutput.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
