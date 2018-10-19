package com.example.vladnica.pos.socket;

import android.content.Context;

import com.example.vladnica.pos.R;
import com.example.vladnica.pos.socket.CustomSSLSocketFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by Vlad on 10/18/2018.
 */
public class HoshList {

    private Context mContext;
    private int mSelectedPort = -1;
    SSLSocket sslSocket;
//    public HoshList(Context context){
//        this.mContext = context;
//
//        SocketFactory sf = CustomSSLSocketFactory.create(context, R.raw.ctecheu_ca);
//        SSLSocket socket = null;
//        try {
//            socket = (SSLSocket) sf.createSocket("gmail.com", 5005);
//
//        HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
//        SSLSession s = socket.getSession();
//
//        if (!hv.verify("mail.google.com", s)) {
//            throw new SSLHandshakeException("Expected mail.google.com, " +
//                    "found " + s.getPeerPrincipal());
//        }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        //socket.close();
//
//    }

    public HoshList(final Context context) {
        this.mContext = context;
        try {
            ServerSocketFactory ssf = ServerSocketFactory.getDefault();
            final ServerSocket serverSocket = ssf.createServerSocket(5005);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Socket socket = serverSocket.accept();

                        SSLSocketFactory sslSf = CustomSSLSocketFactory.create(context, R.raw.ctecheu_ca);
                        sslSocket = (SSLSocket) sslSf.createSocket(socket, null,
                                socket.getPort(), false);

                        sslSocket.setUseClientMode(false);

                        //mSocketOutput = new DataOutputStream(socket.getOutputStream());
                        //mSocketInput = new DataInputStream(socket.getInputStream());

                        // listenForMessages();

                    } catch (IOException e) {
                        //Log.e(TAG, "Error creating ServerSocket: ", e);
                        e.printStackTrace();
                    } finally {
//                        if(mSocketInput != null) {
//                            try{mSocketInput.close();}catch (Exception e){e.printStackTrace();}
//                        }
//                        if(mSocketOutput != null){
//                            try {mSocketOutput.close();}catch (Exception e){e.printStackTrace();}
//                        }
                    }
                }
            }).start();


        } catch (IOException e) {
            e.printStackTrace();
        }

        //socket.close();

    }

}
