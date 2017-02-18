/**
 * 
 */
package com.neildg.gamesofthegenerals.entities.multiplayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import org.andengine.engine.Engine;
import org.andengine.extension.multiplayer.protocol.adt.message.IMessage;
import org.andengine.extension.multiplayer.protocol.adt.message.server.IServerMessage;
import org.andengine.extension.multiplayer.protocol.client.IServerMessageHandler;
import org.andengine.extension.multiplayer.protocol.client.connector.BluetoothSocketConnectionServerConnector;
import org.andengine.extension.multiplayer.protocol.client.connector.ServerConnector;
import org.andengine.extension.multiplayer.protocol.exception.BluetoothException;
import org.andengine.extension.multiplayer.protocol.server.BluetoothSocketServer;
import org.andengine.extension.multiplayer.protocol.server.connector.BluetoothSocketConnectionClientConnector;
import org.andengine.extension.multiplayer.protocol.shared.BluetoothSocketConnection;
import org.andengine.extension.multiplayer.protocol.util.MessagePool;
import org.andengine.util.debug.Debug;

import com.neildg.gamesofthegenerals.core.EngineCore;
import com.neildg.gamesofthegenerals.core.notification.NotificationCenter;
import com.neildg.gamesofthegenerals.core.notification.NotificationListener;
import com.neildg.gamesofthegenerals.core.notification.Notifications;
import com.neildg.gamesofthegenerals.entities.multiplayer.threads.ClientConnectThread;
import com.neildg.gamesofthegenerals.entities.multiplayer.threads.ServerAcceptThread;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Responsible for establishing bluetooth connection
 * @author NeilDG
 *
 */
public class BluetoothConnector implements NotificationListener {

	public final static String UUIDString = "6b2087a3-4295-41a1-8be5-ea156a968596"; //from http://uuidgenerator.net/
	
	private final static String TAG = "BluetoothConnector";
	
	private String serverName;
	private String serverMacAddress;
    
    private BluetoothAdapter bluetoothAdapter;
    
    public BluetoothConnector() {
    	this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    	
    	this.serverMacAddress = this.bluetoothAdapter.getAddress();
    	this.serverName = this.bluetoothAdapter.getName();
    	
    	NotificationCenter.getInstance().addObserver(Notifications.ON_BLUETOOTH_DISCONNECT, this);
    }
    
    public boolean verifyBluetooth() {
    	if(this.bluetoothAdapter == null) {
    		Log.e(TAG, "Bluetooth in this device is not supported!");
    		return false;
    	}
    	
    	else if(!this.bluetoothAdapter.isEnabled()) {
    		Log.e(TAG, "Bluetooth is not turned on!");
    		return false;
    	}
    	else {
    		return true;
    	}
    }
    
    //returns the list of bonded devices that are turned ON
    public BluetoothDevice[] getActiveBondedDevices() {
    	BluetoothDevice[] deviceList = new BluetoothDevice[this.bluetoothAdapter.getBondedDevices().size()];
    	this.bluetoothAdapter.getBondedDevices().toArray(deviceList);
    	
    	return deviceList;
    }
    
    public void discoverDevices() {
    	this.bluetoothAdapter.startDiscovery();
    }
    
    public String getServerMacAddress() {
    	return this.serverMacAddress;
    }
    
    public String getServerName() {
    	return this.serverName;
    }
    
    public void initServer() {
    	//we request the bluetooth device to be discoverable
    	Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
    	discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,150);
    	Activity runningActivity = (Activity) EngineCore.getInstance().getContext();
        runningActivity.startActivity(discoverableIntent);
    	
    	ServerAcceptThread serverAcceptThread = new ServerAcceptThread(this.bluetoothAdapter);
    	serverAcceptThread.start();
    }
    
    //connects to the host device
    public void initClientConnection(BluetoothDevice hostDevice) {
    
    	ClientConnectThread clientConnectThread = new ClientConnectThread(bluetoothAdapter, hostDevice);
    	clientConnectThread.start();
    	
    }

	@Override
	public void onNotify(String notificationString, Object sender) {
		//turn off bluetooth adapter and enable it again. to clear all links
		/*if(this.bluetoothAdapter.isEnabled()) {
			this.bluetoothAdapter.disable();
			this.bluetoothAdapter.enable();
		}*/
	}
}
