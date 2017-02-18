/**
 * 
 */
package com.neildg.gamesofthegenerals.entities.multiplayer.threads;

import java.io.IOException;
import java.util.UUID;

import com.neildg.gamesofthegenerals.core.EngineCore;
import com.neildg.gamesofthegenerals.core.SceneList;
import com.neildg.gamesofthegenerals.core.SceneManager;
import com.neildg.gamesofthegenerals.entities.multiplayer.BluetoothConnector;
import com.neildg.gamesofthegenerals.entities.multiplayer.SocketManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;


/**
 * Thread that runs to accept incoming client connections for the server.
 * @author NeilDG
 *
 */
public class ServerAcceptThread extends Thread {
	
	private final static String TAG = "ServerAcceptThread";
	
	private BluetoothServerSocket mmServerSocket;
	private BluetoothAdapter bluetoothAdapter;
	 
    public ServerAcceptThread(BluetoothAdapter bluetoothAdapter) {
    	this.bluetoothAdapter = bluetoothAdapter;
        // Use a temporary object that is later assigned to mmServerSocket,
        // because mmServerSocket is final
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code
            tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(bluetoothAdapter.getName(), UUID.fromString(BluetoothConnector.UUIDString));
            
        } catch (IOException e) {
        	Log.e(TAG, "Error creating server!"); 
        	this.bluetoothAdapter.disable();
        	this.interrupt();
        	this.requestForBluetoothRestart();
        }
        mmServerSocket = tmp;
    }
    
    //function that requests the user to restart the bluetooth if it fails.
    public void requestForBluetoothRestart() {
    	
    	final Activity runningActivity = (Activity) EngineCore.getInstance().getContext();
    	
    	runningActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				new AlertDialog.Builder(runningActivity)
			    .setTitle("Server Creation Failed")
			    .setMessage("There has been an error setting up the server. Bluetooth will restart. Please try hosting a game again.")
			    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) { 
			            bluetoothAdapter.enable();
			            dialog.cancel();
			            SceneManager.getInstance().loadScene(SceneList.MAIN_MENU_SCENE);
			        }
			     }).setCancelable(false).show();
			}
		});
    	
    }
 
    public void run() {
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned
        while (true) {
            try {
                socket = mmServerSocket.accept();
            
		        // If a connection was accepted
		        if (socket != null) {
		            // Do work to manage the connection (in a separate thread)
		            //TODO: create a method to pass the socket here
		        	SocketManager.getInstance().assignClientSocket(socket);
		            mmServerSocket.close();
		            mmServerSocket = null;
		            break;
		        }
            
            }
            catch (IOException e) {
                break;
            }
        }
    }
 
    /** Will cancel the listening socket, and cause the thread to finish */
    public void cancel() {
        try {
            mmServerSocket.close();
            mmServerSocket = null;
        } catch (IOException e) { }
    }
}
