// 
// Decompiled by Procyon v0.5.30
// 

package com.gprinter.io;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import com.gprinter.command.GpCom;
import java.util.Vector;
import android.os.Message;
import android.os.Bundle;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.os.Handler;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAdapter;
import java.util.UUID;

public class BluetoothPort extends GpPort
{
    private static final String DEBUG_TAG = "BluetoothService";
    private static final UUID SERIAL_PORT_SERVICE_CLASS_UUID;
    private BluetoothAdapter mAdapter;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    BluetoothDevice mDevice;
    
    public BluetoothPort(final int id, final BluetoothDevice device, final Handler handler) {
        this.mAdapter = null;
        this.mConnectThread = null;
        this.mConnectedThread = null;
        this.mDevice = null;
        this.mAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mState = 0;
        this.mHandler = handler;
        this.mDevice = device;
        this.mPrinterId = id;
    }
    
    public synchronized void connect() {
        Log.d("BluetoothService", "connect to: " + this.mDevice);
        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }
        if (this.mConnectedThread != null) {
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }
        (this.mConnectThread = new ConnectThread(this.mDevice)).start();
        this.setState(2);
    }
    
    public synchronized void connected(final BluetoothSocket socket, final BluetoothDevice device) {
        Log.d("BluetoothService", "connected");
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
        bundle.putInt("printer.id", this.mPrinterId);
        bundle.putString("device_name", device.getName());
        msg.setData(bundle);
        this.mHandler.sendMessage(msg);
        this.setState(3);
    }
    
    public synchronized void stop() {
        Log.d("BluetoothService", "stop");
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
    
    static {
        SERIAL_PORT_SERVICE_CLASS_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    }
    
    private class ConnectThread extends Thread
    {
        private BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        
        public ConnectThread(final BluetoothDevice device) {
            this.mmDevice = device;
            BluetoothSocket tmp = null;
            try {
                tmp = device.createRfcommSocketToServiceRecord(BluetoothPort.SERIAL_PORT_SERVICE_CLASS_UUID);
            }
            catch (IOException e) {
                Log.e("BluetoothService", "create() failed", (Throwable)e);
            }
            this.mmSocket = tmp;
        }
        
        @Override
        public void run() {
            Log.i("BluetoothService", "BEGIN mConnectThread");
            this.setName("ConnectThread");
            BluetoothPort.this.mAdapter.cancelDiscovery();
            try {
                this.mmSocket.connect();
            }
            catch (IOException e3) {
                BluetoothPort.this.connectionFailed();
                try {
                    if (this.mmSocket != null) {
                        this.mmSocket.close();
                    }
                }
                catch (IOException e2) {
                    Log.e("BluetoothService", "unable to close() socket during connection failure", (Throwable)e2);
                }
                BluetoothPort.this.stop();
                return;
            }
            synchronized (BluetoothPort.this) {
                BluetoothPort.this.mConnectThread = null;
            }
            BluetoothPort.this.connected(this.mmSocket, this.mmDevice);
        }
        
        public void cancel() {
            try {
                if (this.mmSocket != null) {
                    this.mmSocket.close();
                }
                this.mmSocket = null;
            }
            catch (IOException e) {
                Log.e("BluetoothService", "close() of connect socket failed", (Throwable)e);
                BluetoothPort.this.closePortFailed();
            }
        }
    }
    
    private class ConnectedThread extends Thread
    {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        
        public ConnectedThread(final BluetoothSocket socket) {
            Log.d("BluetoothService", "create ConnectedThread");
            this.mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            }
            catch (IOException e) {
                Log.e("BluetoothService", "temp sockets not created", (Throwable)e);
            }
            this.mmInStream = tmpIn;
            this.mmOutStream = tmpOut;
        }
        
        @Override
        public void run() {
            Log.i("BluetoothService", "BEGIN mConnectedThread");
            while (!BluetoothPort.this.mClosePort) {
                try {
                    final byte[] buffer = new byte[64];
                    final int bytes = this.mmInStream.read(buffer);
                    final Message msg = new Message();
                    msg.what = 2;
                    final Bundle bundle = new Bundle();
                    bundle.putInt("printer.id", BluetoothPort.this.mPrinterId);
                    bundle.putInt("device.readcnt", bytes);
                    bundle.putByteArray("device.read", buffer);
                    msg.setData(bundle);
                    BluetoothPort.this.mHandler.sendMessage(msg);
                    Log.d("BluetoothService", bytes + " \u7684\u957f\u5ea6");
                }
                catch (IOException e) {
                    this.cancel();
                    BluetoothPort.this.connectionLost();
                    e.printStackTrace();
                    Log.e("BluetoothService", "disconnected", (Throwable)e);
                }
            }
            Log.d("BluetoothService", "Closing Bluetooth work");
        }
        
        public void cancel() {
            try {
                BluetoothPort.this.mClosePort = true;
                this.mmOutStream.flush();
                if (this.mmSocket != null) {
                    this.mmSocket.close();
                }
            }
            catch (IOException e) {
                BluetoothPort.this.closePortFailed();
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
                            Log.d("BluetoothService", "Exception occured while sending data immediately: " + e.getMessage());
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
