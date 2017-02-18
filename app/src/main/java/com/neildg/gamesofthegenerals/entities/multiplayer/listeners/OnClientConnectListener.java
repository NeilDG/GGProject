package com.neildg.gamesofthegenerals.entities.multiplayer.listeners;

import org.andengine.engine.Engine;
import org.andengine.extension.multiplayer.protocol.server.connector.BluetoothSocketConnectionClientConnector.IBluetoothSocketConnectionClientConnectorListener;
import org.andengine.extension.multiplayer.protocol.server.connector.ClientConnector;
import org.andengine.extension.multiplayer.protocol.shared.BluetoothSocketConnection;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.neildg.gamesofthegenerals.core.EngineCore;

public class OnClientConnectListener implements IBluetoothSocketConnectionClientConnectorListener {
   
	private final static String TAG = "OnClientConnectListener";

	@Override
	public void onStarted(
			ClientConnector<BluetoothSocketConnection> pClientConnector) {
		final Activity runningActivity = (Activity) EngineCore.getInstance().getContext();
		
		final String deviceName = pClientConnector.getConnection().getBluetoothSocket().getRemoteDevice().getName();
		
		runningActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
            	Toast.makeText(runningActivity, "Client connected:  "+ deviceName, Toast.LENGTH_LONG).show();
            }
        });
		
		Log.v(TAG, "Client connected:  "+ deviceName);
	}

	@Override
	public void onTerminated(
			ClientConnector<BluetoothSocketConnection> pClientConnector) {
		final Activity runningActivity = (Activity) EngineCore.getInstance().getContext();
		
		final String deviceName = pClientConnector.getConnection().getBluetoothSocket().getRemoteDevice().getName();
		
		runningActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
            	Toast.makeText(runningActivity, "Client disconnected  "+ deviceName, Toast.LENGTH_LONG).show();
            }
        });
		
		Log.v(TAG, "Client disconnected:  "+ deviceName);
		
	}
}