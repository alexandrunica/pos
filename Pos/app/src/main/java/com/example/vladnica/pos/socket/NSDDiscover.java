package com.example.vladnica.pos.socket;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by Vlad on 10/3/2018.
 */
public class NSDDiscover {

    public static final String TAG = "TrackingFlow";
    public String mDiscoveryServiceName = "NSDDoEpicCodingDiscover";
    private Context mContext;
    private NsdManager mNsdManager;
    private DiscoveryListener mListener;
    private String mHostFound;
    private int mPortFound;
    private DISCOVERY_STATUS mCurrentDiscoveryStatus = DISCOVERY_STATUS.OFF;
    private String SERVICE_TYPE = "_http._tcp.";

    private enum DISCOVERY_STATUS{
        ON,
        OFF
    }

    public NSDDiscover(Context context, DiscoveryListener listener) {
        this.mContext = context;
        this.mListener = listener;
        this.mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
    }

    public void discoverServices() {
        if(mCurrentDiscoveryStatus == DISCOVERY_STATUS.ON)return;
        Toast.makeText(mContext, "Discover SERVICES!", Toast.LENGTH_LONG).show();
        mCurrentDiscoveryStatus = DISCOVERY_STATUS.ON;
        mNsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
    }

    public void sayHello(){
//        mHostFound = "192.168.43.209";
//        mPortFound = 5005;
        if(mHostFound == null || mPortFound <= 0){
            Toast.makeText(mContext, "Device not found", Toast.LENGTH_LONG).show();
            return;
        }

        new SocketConnection().sayHello(mHostFound, mPortFound);
    }

    NsdManager.ResolveListener mResolveListener = new NsdManager.ResolveListener() {
        @Override
        public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
            Log.e(TAG, "Resolve failed" + errorCode);
        }

        @Override
        public void onServiceResolved(NsdServiceInfo serviceInfo) {
            Log.e(TAG, "Resolve Succeeded. " + serviceInfo);

            if (serviceInfo.getServiceName().equals(mDiscoveryServiceName)) {
                Log.d(TAG, "Same IP.");
                return;
            }
            Toast.makeText(mContext, "FOUND A CONNECTION!", Toast.LENGTH_LONG).show();
            mNsdManager.stopServiceDiscovery(mDiscoveryListener);//TODO: You can remove this line if necessary, that way the discovery process continues...
            setHostAndPortValues(serviceInfo);
            if(mListener != null){
                mListener.serviceDiscovered(mHostFound, mPortFound);
            }
        }
    };

    private void setHostAndPortValues(NsdServiceInfo serviceInfo){
        mHostFound = serviceInfo.getHost().getHostAddress();
        mPortFound = serviceInfo.getPort();
    }

    private class SocketConnection {
        private Socket mSocket;
        public void sayHello(final String host, final int port){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mSocket = new Socket();
                    SocketAddress address = new InetSocketAddress(host, port);
                    try {
                        android.util.Log.e("TrackingFlow", "Trying to connect to: " + host);
                        mSocket.connect(address);
                        DataOutputStream os = new DataOutputStream(mSocket.getOutputStream());
                        DataInputStream is = new DataInputStream(mSocket.getInputStream());
                        //Send a message...
                        os.write("state request".getBytes());
                        //os.write(new byte[]{0x01, 0x00, 0x00, (byte) 0xFF});
                        os.flush();
                        android.util.Log.e("TrackingFlow", "Message SENT!!!");

                        //Read the message
                        int bufferSize = 1024;
                        byte[] buffer = new byte[bufferSize];
                        StringBuilder sb = new StringBuilder();
                        int length = Integer.MAX_VALUE;
                        try {
                            while (length >= bufferSize) {
                                length = is.read(buffer);
                                sb.append(new String(buffer, 0, length));
                            }
                            final String receivedMessage = sb.toString();
                            new Handler(mContext.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, "Message received: " + receivedMessage, Toast.LENGTH_LONG).show();
                                }
                            });
                        } catch (Exception e) {e.printStackTrace();}
                        os.close();
                        is.close();

                    } catch (IOException e) {
                        e.printStackTrace();}
                }
            }).start();
        }
    }

    private NsdManager.DiscoveryListener mDiscoveryListener = new NsdManager.DiscoveryListener() {

        @Override
        public void onDiscoveryStarted(String regType) {
            Log.d(TAG, "Service discovery started");
        }

        @Override
        public void onServiceFound(NsdServiceInfo service) {
            Log.d(TAG, "Service discovery success" + service);
            if (!service.getServiceType().equals(SERVICE_TYPE)) {
                Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
            } else if (service.getServiceName().equals(mDiscoveryServiceName)) {
                Log.d(TAG, "Same machine: " + mDiscoveryServiceName);
            } else {
                mNsdManager.resolveService(service, mResolveListener);
            }
        }

        @Override
        public void onServiceLost(NsdServiceInfo service) {
            Log.e(TAG, "service lost" + service);
        }

        @Override
        public void onDiscoveryStopped(String serviceType) {
            Log.i(TAG, "Discovery stopped: " + serviceType);
        }

        @Override
        public void onStartDiscoveryFailed(String serviceType, int errorCode) {
            Log.e(TAG, "Discovery failed: Error code:" + errorCode);
            mNsdManager.stopServiceDiscovery(this);
        }

        @Override
        public void onStopDiscoveryFailed(String serviceType, int errorCode) {
            Log.e(TAG, "Discovery failed: Error code:" + errorCode);
            mCurrentDiscoveryStatus = DISCOVERY_STATUS.OFF;
            mNsdManager.stopServiceDiscovery(this);
        }
    };


    public void shutdown(){
        try {
            mNsdManager.stopServiceDiscovery(mDiscoveryListener);
        }catch(Exception e){e.printStackTrace();}
    }

    public interface DiscoveryListener {
        public void serviceDiscovered(String host, int port);
    }
}