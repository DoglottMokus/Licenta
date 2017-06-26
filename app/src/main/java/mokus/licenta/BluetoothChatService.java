package mokus.licenta;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Mokus on 6/26/2017.
 */

public class BluetoothChatService {
    private static final String TAG = "BluetoothChatService";

    private static final String ApplicationName = "Licenta";

    private static final UUID MY_UUID_SECURE =
            UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

    private ConnectThread mConnectThread;
    private BluetoothDevice mmDevice;
    private UUID deviceUUID;
    ProgressDialog mProgressDialog;
    private final BluetoothAdapter myBluetoothAdapter;

    private AcceptThread mSecureAcceptThread;

    public BluetoothChatService(Context context)
    {
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private class AcceptThread extends Thread
    {
        private final BluetoothServerSocket myBluetoothServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = myBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(ApplicationName, MY_UUID_SECURE);

                Log.d(TAG, "AcceptThread: Setting up Server using: " + MY_UUID_SECURE);
            } catch (IOException e) {
                Log.e(TAG, "AcceptThread: Exception: " + e.getMessage());
            }
            myBluetoothServerSocket = tmp;
        }
        public void run()
        {
            Log.d(TAG, "Run: AcceptThread running.");
            BluetoothSocket socket = null;
            try {
                socket = myBluetoothServerSocket.accept();
                Log.d(TAG, "Run: ServerSocket started...");
            } catch (Exception e) {
                Log.e(TAG, "AcceptThread: Exception: " + e.getMessage());
            }
            if (socket != null) {
                connected(socket, mmDevice);
            }
            Log.i(TAG, "END mAcceptThread ");
        }
        public void cancel() {
            Log.d(TAG, "cancel: Canceling AcceptThread.");
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: Close of AcceptThread ServerSocket failed. " + e.getMessage());
            }
        }
        private class ConnectThread extends Thread
        {
            private BluetoothSocket mmSocket;
            public ConnectThread(BluetoothDevice device, UUID uuid)
            {
                Log.d(TAG, "ConnectThread: started.");
                mmDevice = device;
                deviceUUID = uuid;
            }
            public void run()
            {
                BluetoothSocket tmp = null;
                Log.i(TAG, "RUN mConnectThread ");
                try {
                    Log.d(TAG, "ConnectThread: Trying to create InsecureRfcommSocket using UUID: "
                            +MY_UUID_SECURE );
                    tmp = mmDevice.createRfcommSocketToServiceRecord(deviceUUID);
                } catch (IOException e) {
                    Log.e(TAG, "ConnectThread: Could not create InsecureRfcommSocket " + e.getMessage());
                }
                mmSocket = tmp;
                
            }

        }
    }
}
