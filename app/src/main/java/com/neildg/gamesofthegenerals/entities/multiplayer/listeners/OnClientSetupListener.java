package com.neildg.gamesofthegenerals.entities.multiplayer.listeners;

import org.andengine.engine.Engine;
import org.andengine.extension.multiplayer.protocol.client.connector.BluetoothSocketConnectionServerConnector.IBluetoothSocketConnectionServerConnectorListener;
import org.andengine.extension.multiplayer.protocol.client.connector.ServerConnector;
import org.andengine.extension.multiplayer.protocol.shared.BluetoothSocketConnection;

import android.app.Activity;
import android.widget.Toast;

import com.neildg.gamesofthegenerals.core.EngineCore;
import com.neildg.gamesofthegenerals.scenes.GameScene;

public class OnClientSetupListener implements IBluetoothSocketConnectionServerConnectorListener {

	@Override
	public void onStarted(
			ServerConnector<BluetoothSocketConnection> pServerConnector) {
		final Activity runningActivity = (Activity) EngineCore.getInstance().getContext();
		
		final String deviceName = pServerConnector.getConnection().getBluetoothSocket().getRemoteDevice().getName();
		
		Engine engine = EngineCore.getInstance().getEngine();
		engine.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
            	Toast.makeText(runningActivity, "Initializing connection to  "+ deviceName, Toast.LENGTH_LONG).show();
            }
        });
		
	}

	@Override
	public void onTerminated(
			ServerConnector<BluetoothSocketConnection> pServerConnector) {
		final Activity runningActivity = (Activity) EngineCore.getInstance().getContext();
		
		final String deviceName = pServerConnector.getConnection().getBluetoothSocket().getRemoteDevice().getName();
		
		Engine engine = EngineCore.getInstance().getEngine();
		engine.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
            	Toast.makeText(runningActivity, "Disconnected to  "+ deviceName, Toast.LENGTH_LONG).show();
            }
        });
		
	}
}