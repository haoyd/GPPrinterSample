// 
// Decompiled by Procyon v0.5.30
// 

package com.gprinter.io;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.InetAddress;
import com.gprinter.command.GpCom;
import java.util.Vector;
import android.os.Message;
import android.os.Bundle;
import java.net.Socket;
import android.util.Log;
import android.os.Handler;

public class EthernetPort extends GpPort
{
    private static final String DEBUG_TAG = "EthernetService";
    private String mIp;
    private int mPortNumber;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    
    public EthernetPort(final int id, final String Ip, final int Port, final Handler handler) {
        this.mConnectThread = null;
        this.mConnectedThread = null;
        Log.e("EthernetService", "recreate Socket");
        this.mState = 0;
        this.mHandler = handler;
        this.mPortNumber = Port;
        this.mIp = Ip;
        this.mPrinterId = id;
    }
    
    public synchronized void connect() {
        Log.d("EthernetService", "connect to Ip :" + this.mIp + " Port: " + this.mPortNumber);
        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }
        if (this.mConnectedThread != null) {
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }
        (this.mConnectThread = new ConnectThread(this.mIp, this.mPortNumber)).start();
        this.setState(2);
    }
    
    public synchronized void connected(final Socket socket, final String ip) {
        Log.d("EthernetService", "connected");
        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }
        if (this.mConnectedThread != null) {
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }
        (this.mConnectedThread = new ConnectedThread(socket)).start();
        final Message msg = this.mHandler.obtainMessage(4);
        final Bundle bundle = new Bundle();
        bundle.putString("device_name", ip);
        msg.setData(bundle);
        this.mHandler.sendMessage(msg);
        this.setState(3);
    }
    
    public synchronized void stop() {
        Log.d("EthernetService", "stop");
        this.setState(0);
        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }
        if (this.mConnectedThread != null) {
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }
    }
    
    public GpCom.ERROR_CODE writeDataImmediately(final Vector<Byte> data) {
        GpCom.ERROR_CODE retval = GpCom.ERROR_CODE.SUCCESS;
        final ConnectedThread r;
        synchronized (this) {
            if (this.mState != 3) {
                return GpCom.ERROR_CODE.PORT_IS_NOT_OPEN;
            }
            r = this.mConnectedThread;
        }
        retval = r.writeDataImmediately(data);
        return retval;
    }
    
    private class ConnectThread extends Thread
    {
        private Socket mmSocket;
        private String mmIp;
        InetAddress mmIpAddress;
        SocketAddress mmRemoteAddr;
        
        public ConnectThread(final String Ip, final int Port) {
            this.mmSocket = new Socket();
            try {
                this.mmIpAddress = InetAddress.getByName(Ip);
                this.mmRemoteAddr = new InetSocketAddress(this.mmIpAddress, Port);
                this.mmIp = Ip;
            }
            catch (UnknownHostException e) {
                Log.e("EthernetService", "IpAddress is invalid", (Throwable)e);
                EthernetPort.this.connectionFailed();
            }
        }
        
        @Override
        public void run() {
            Log.i("EthernetService", "BEGIN mConnectThread");
            this.setName("ConnectThread");
            Log.e("EthernetService", this.mmRemoteAddr.toString());
            try {
                this.mmSocket.connect(this.mmRemoteAddr, 4000);
            }
            catch (IOException e2) {
                EthernetPort.this.connectionFailed();
                Log.e("EthernetService", "connectThread failed");
                try {
                    if (this.mmSocket != null) {
                        this.mmSocket.close();
                    }
                }
                catch (IOException e1) {
                    Log.e("EthernetService", "unable to close() socket during connection failure", (Throwable)e1);
                }
                EthernetPort.this.stop();
                return;
            }
            synchronized (EthernetPort.this) {
                EthernetPort.this.mConnectThread = null;
            }
            EthernetPort.this.connected(this.mmSocket, this.mmIp);
        }
        
        public void cancel() {
            try {
                this.mmSocket.close();
            }
            catch (IOException e) {
                Log.e("EthernetService", "close() of connect socket failed", (Throwable)e);
                EthernetPort.this.closePortFailed();
            }
        }
    }
    
    private class ConnectedThread extends Thread
    {
        private final Socket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        
        public ConnectedThread(final Socket socket) {
            Log.d("EthernetService", "create ConnectedThread");
            this.mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            }
            catch (IOException e) {
                Log.e("EthernetService", "temp sockets not created", (Throwable)e);
            }
            this.mmInStream = tmpIn;
            this.mmOutStream = tmpOut;
        }
        
        @Override
        public void run() {
            Log.i("EthernetService", "BEGIN mConnectedThread");
            EthernetPort.this.mClosePort = false;
            while (!EthernetPort.this.mClosePort) {
                try {
                    final byte[] ReceiveData = new byte[100];
                    final int bytes = this.mmInStream.read(ReceiveData);
                    Log.d("EthernetService", "bytes " + bytes);
                    if (bytes > 0) {
                        final Message msg = EthernetPort.this.mHandler.obtainMessage(2);
                        final Bundle bundle = new Bundle();
                        bundle.putInt("printer.id", EthernetPort.this.mPrinterId);
                        bundle.putInt("device.readcnt", bytes);
                        bundle.putByteArray("device.read", ReceiveData);
                        msg.setData(bundle);
                        EthernetPort.this.mHandler.sendMessage(msg);
                        continue;
                    }
                    Log.e("EthernetService", "disconnected");
                    EthernetPort.this.connectionLost();
                    EthernetPort.this.stop();
                }
                catch (IOException e) {
                    EthernetPort.this.connectionLost();
                    Log.e("EthernetService", "disconnected", (Throwable)e);
                }
                break;
            }
            Log.d("EthernetService", "Closing ethernet work");
            EthernetPort.this.setState(0);
        }
        
        public void cancel() {
            try {
                EthernetPort.this.mClosePort = true;
                this.mmOutStream.flush();
                if (this.mmSocket != null) {
                    this.mmSocket.close();
                }
            }
            catch (IOException e) {
                EthernetPort.this.closePortFailed();
            }
        }
        
        public GpCom.ERROR_CODE writeDataImmediately(final Vector<Byte> data) {
            GpCom.ERROR_CODE retval = GpCom.ERROR_CODE.SUCCESS;
            if (this.mmSocket != null && this.mmOutStream != null) {
                if (data != null && data.size() > 0) {
                    final byte[] sendData = new byte[data.size()];
                    if (data.size() > 0) {
                        for (int i = 0; i < data.size(); ++i) {
                            sendData[i] = data.get(i);
                        }
                        try {
                            this.mmOutStream.write(sendData);
                            this.mmOutStream.flush();
                        }
                        catch (Exception e) {
                            Log.d("EthernetService", "Exception occured while sending data immediately: " + e.getMessage());
                            retval = GpCom.ERROR_CODE.FAILED;
                        }
                    }
                }
            }
            else {
                retval = GpCom.ERROR_CODE.PORT_IS_NOT_OPEN;
            }
            return retval;
        }
    }
}
