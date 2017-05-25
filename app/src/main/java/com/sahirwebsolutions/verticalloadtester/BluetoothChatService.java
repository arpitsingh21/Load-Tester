package com.sahirwebsolutions.verticalloadtester;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;

/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for
 * incoming connections, a thread for connecting with a device, and a
 * thread for performing data transmissions when connected.
 */
public class BluetoothChatService {

    // Debugging
    private static final String TAG = "BluetoothChatService";

    // Name for the SDP record when creating server socket
    private static final String NAME_SECURE = "BluetoothChatSecure";


    // Unique UUID for this application
    private static final UUID UUID_CODE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    private boolean battery=false;
    private int batteryValue=10;

    // Member fields

    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;
    private Context context;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    /**
     * Constructor. Prepares a new BluetoothChat session.
     *
     * @param context The UI Activity Context
     * @param handler A Handler to send messages back to the UI Activity
     */
    public BluetoothChatService(Context context, Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        this.context=context;
        mState = STATE_NONE;
        mHandler = handler;


    }

    /**
     * Set the current state of the chat connection
     *
     * @param state An integer defining the current connection state
     */
    private synchronized void setState(int state) {
       // Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        // Give the new state to the Handler so the UI Activity can update
        mHandler.obtainMessage(Constants.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    /**
     * Return the current connection state.
     */
    public synchronized int getState() {
        return mState;
    }


    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     *
     * @param device The BluetoothDevice to connect
     */
    public synchronized void connect(BluetoothDevice device) {
    //    Log.d(TAG, "connect to: " + device);
        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to connect with the given device
        if(device!=null) {
            mConnectThread = new ConnectThread(device);
            mConnectThread.start();
            setState(STATE_CONNECTING);
        }else{
            connectionFailed();
        }
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     *
     * @param socket The BluetoothSocket on which the connection was made
     * @param device The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice
            device) {
      //  Log.d(TAG, "connected, Socket Type:" + socketType);

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        // Send the name of the connected device back to the UI Activity
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        setState(STATE_CONNECTED);
    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
        Log.d(TAG, "stop");

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        setState(STATE_NONE);
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     *
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Unable to connect device.Try Again!");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Device connection was lost.Try connecting again!");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

    }



    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;


            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
        /*    try {

                    tmp = device.createRfcommSocketToServiceRecord(
                            UUID_CODE);

            } catch (IOException e) {
                Log.e(TAG, "Socket "  + "create() failed", e);
                //Toast.makeText(context, "ConnectThread IOException 1 "+e.toString(), Toast.LENGTH_SHORT).show();
            }

            */


            Method m;
            try {
                m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class         });
                tmp = (BluetoothSocket) m.invoke(device, 1);
            } catch (SecurityException e) {
                Log.e(TAG, "create() failed", e);
            } catch (NoSuchMethodException e) {
                Log.e(TAG, "create() failed", e);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "create() failed", e);
            } catch (IllegalAccessException e) {
                Log.e(TAG, "create() failed", e);
            } catch (InvocationTargetException e) {
                Log.e(TAG, "create() failed", e);
            }

        //    mmSocket = tmp;

            mmSocket = tmp;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread ");
            setName("ConnectThread Secure");

            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();


//                object.showGraph();
            } catch (IOException e) {
                // Close the socket
               // Toast.makeText(context, "ConnectThread IOException 2 "+ e.toString(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "ConnectThread IOException 2    ---------", e);
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close()socket during connection failure ----------", e2);
                    //Toast.makeText(context, "unable to close()socket during connection failure 3 "+ e2.toString(), Toast.LENGTH_SHORT).show();
                }
                connectionFailed();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothChatService.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect failed", e);
            }
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            //Log.d(TAG, "create ConnectedThread: " + socketType);
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");

/*
            byte[] buffer = new byte[1024];
            int bytes=0;
            while (mState == STATE_CONNECTED) {
                try {
                    // Read from the InputStream
                    buffer[bytes] = (byte) mmInStream.read();
                    // Send the obtained bytes to the UI Activity
                    if (buffer[bytes] == '#')
                    {
                        mHandler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                        bytes=0;
                    }
                    else
                        bytes++;
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }


            byte[] buffer = new byte[1024];
            int begin = 0;
            int bytes = 0;
            while (true) {
                try {
                    bytes += mmInStream.read(buffer, bytes, buffer.length - bytes);
                    for(int i = begin; i < bytes; i++) {
                        if(buffer[i] == "#".getBytes()[0]) {
                            mHandler.obtainMessage(Constants.MESSAGE_READ, begin, i, buffer).sendToTarget();
                            begin = i + 1;
                            if(i == bytes - 1) {
                                bytes = 0;
                                begin = 0;
                            }
                        }
                    }
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }

            */
            /*
            byte[] buffer = new byte[1024];
            byte[] copy;
            int bytes;

            // Keep listening to the InputStream while connected
            while (mState == STATE_CONNECTED) {
                try {

                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    if (buffer.length > 0) {
                        copy = buffer.clone();
                        // Send the obtained bytes to the UI Activity

                        mHandler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, copy)
                                .sendToTarget();
                        buffer = null;
                        bytes = 0;
                    }

                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();

                    break;
                }
            }

*/
            //TRY THIS!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

            byte[] buffer,copy;
            ArrayList<Integer> arr_byte = new ArrayList<Integer>();



            int hash3='#';
            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    int data = mmInStream.read();
                    if (data == 0x73 || data==0x45 || data==0x66) {
                    } else if (data == 0x0a) {
                        buffer = new byte[arr_byte.size()];
                        for (int i = 0; i < arr_byte.size(); i++) {


                            buffer[i] = arr_byte.get(i).byteValue();
                        }
                        // Send the obtained bytes to the UI Activity
                        copy=buffer.clone();
                       // float f = ByteBuffer.wrap(copy).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                        // chartActivity.feedMultiple(f);

                        mHandler.obtainMessage(Constants.MESSAGE_READ
                                , copy.length, batteryValue, copy).sendToTarget();
                        arr_byte = new ArrayList<Integer>();
                   //     arr_byte.clear();                                   DO THIS
                    } else {

                        if(data == 0x62)//0x62==b
                        {
                            battery=true;
                        }else if(battery){
                            battery=false;

                            if(data == 0x30)//0
                                batteryValue=0;
                            else if(data == 0x31) //1
                                batteryValue=1;
                            else if(data == 0x32) //2
                                batteryValue=2;
                            else if(data == 0x33) //3
                                batteryValue=3;
                            else if(data == 0x34) //4
                                batteryValue=4;

                        }else {
                            arr_byte.add(data);
                        }



                    }
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();

                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         *
         * @param buffer The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
                mHandler.obtainMessage(Constants.MESSAGE_WRITE, -1, -1, buffer)
                        .sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}
