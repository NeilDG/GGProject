/**
 * 
 */
package com.neildg.gamesofthegenerals.entities.multiplayer;

import java.io.IOException;

import com.neildg.gamesofthegenerals.core.notification.NotificationCenter;
import com.neildg.gamesofthegenerals.core.notification.Notifications;
import com.neildg.gamesofthegenerals.entities.game.TurnManager;
import com.neildg.gamesofthegenerals.entities.multiplayer.threads.DataTransferThread;
import com.neildg.gamesofthegenerals.entities.openings.OpeningMovesLibrary;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

/**
 * Holds the respective bluetooth sockets
 * @author NeilDG
 *
 */
public class SocketManager {

	private final static String TAG = "SocketManager";
	
	private static SocketManager sharedInstance = null;
	
	private BluetoothSocket hostSocket;
	private BluetoothSocket clientSocket;
	
	private DataTransferThread dataTransferThread;
	
	
	
	private SocketManager() {
		
	}
	
	public void assignHostSocket(BluetoothSocket hostSocket) {
		this.hostSocket = hostSocket;
		this.dataTransferThread = new DataTransferThread(this.hostSocket);
		this.dataTransferThread.start();
		Log.v(TAG, "Assigned host: " +this.hostSocket.getRemoteDevice().getName());
		
		TurnManager.getInstance().MarkAsClient();
	}
	
	public void assignClientSocket(BluetoothSocket clientSocket) {
		this.clientSocket = clientSocket;
		this.dataTransferThread = new DataTransferThread(this.clientSocket);
		this.dataTransferThread.start();
		
		Log.v(TAG, "Assigned client: " +this.clientSocket.getRemoteDevice().getName());
		NotificationCenter.getInstance().postNotification(Notifications.ON_CLIENT_SUCCESSFULLY_CONNECTED, this);
		
		TurnManager.getInstance().MarkAsHost();
	}
	
	public BluetoothSocket getHostSocket() {
		return this.hostSocket;
	}
	
	public BluetoothSocket getClientSocket() {
		return this.clientSocket;
	}
	
	public void sendMessage(String message) {
		this.dataTransferThread.write(message);
	}
	
	@SuppressLint("NewApi")
	public boolean isConnectedToHost() {
		if(this.hostSocket != null && this.hostSocket.isConnected()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	//condition to determine if the device is a host server
	public boolean isHost() {
		if(this.hostSocket == null && this.clientSocket != null) {
			return true;
		}
		else {
			return false;
		}
	}
	
	//condition to determine if the device is a client
	public boolean isClient() {
		if(this.hostSocket != null && this.clientSocket == null) {
			return true;
		}
		else {
			return false;
		}
	}
	
	@SuppressLint("NewApi")
	public boolean isClientConnected() {
		if(this.clientSocket != null && this.clientSocket.isConnected()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	//close host and client sockets, preventing further communication.
	public void closeConnections() {
		
		
		try {
			if(this.dataTransferThread != null) {
				//send disconnect message first to remote device
				SocketManager.getInstance().sendMessage(Notifications.ON_BLUETOOTH_DISCONNECT);
				
				//add a pause so the message won't be cut on the receiving client
				/*try {
					Thread.sleep(1000);
				} catch (final Throwable t) {
					Log.e(TAG,t.toString());
				}*/
				
				this.dataTransferThread.cancel();
			}
			if(this.clientSocket != null)
				this.clientSocket.close();
			if(this.hostSocket != null)
				this.hostSocket.close();

			Log.v(TAG, "Closing bluetooth connections!");
		}
		catch(IOException e) {
			Log.e(TAG, "There has been an error closing connections! " +e.getMessage());
		}
		
		this.dataTransferThread = null;
		this.clientSocket = null;
		this.hostSocket = null;
		DataInterpreter.getInstance().reset();
		OpeningMovesLibrary.getInstance().clearAdHocBoardState();
		NotificationCenter.getInstance().postNotification(Notifications.ON_BLUETOOTH_DISCONNECT, this);
	}
	
	public static SocketManager getInstance() {
		if(sharedInstance == null) {
			sharedInstance = new SocketManager();
		}
		
		return sharedInstance;
	}
}
