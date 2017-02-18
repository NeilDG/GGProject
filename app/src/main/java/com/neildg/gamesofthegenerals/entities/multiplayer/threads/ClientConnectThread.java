/**
 * 
 */
package com.neildg.gamesofthegenerals.entities.multiplayer.threads;

import java.io.IOException;
import java.util.UUID;

import com.neildg.gamesofthegenerals.entities.multiplayer.BluetoothConnector;
import com.neildg.gamesofthegenerals.entities.multiplayer.SocketManager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

/**
 * Thread that runs to connect the client to a server. 
 * @author NeilDG
 *
 */
public class ClientConnectThread extends Thread {
	
	private final static String TAG = "ClientConnectThread";
	
	private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
 
    public ClientConnectThread(BluetoothAdapter bluetoothAdapter, BluetoothDevice device) {
    	
    	this.mBluetoothAdapter = bluetoothAdapter;
    	
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        mmDevice = device;
 
        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(BluetoothConnector.UUIDString));
        } catch (IOException e) { }
        mmSocket = tmp;
    }
 
    public void run() {
        // Cancel discovery because it will slow down the connection
        mBluetoothAdapter.cancelDiscovery();
 
        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mmSocket.close();
                mmSocket = null;
            } catch (IOException closeException) { 
            	Log.e(TAG, "IOEXCEPTION in connecting!");
            }
            return;
        }
 
        // Do work to manage the connection (in a separate thread)
        //TODO: pass socket to a method
        SocketManager.getInstance().assignHostSocket(mmSocket);
    }
 
    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            mmSocket.close();
            mmSocket = null;
        } catch (IOException e) { }
    }
}
