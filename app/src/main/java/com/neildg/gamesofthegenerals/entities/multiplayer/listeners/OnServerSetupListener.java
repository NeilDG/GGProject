package com.neildg.gamesofthegenerals.entities.multiplayer.listeners;
import org.andengine.engine.Engine;
import org.andengine.extension.multiplayer.protocol.server.BluetoothSocketServer;
import org.andengine.extension.multiplayer.protocol.server.BluetoothSocketServer.IBluetoothSocketServerListener;
import org.andengine.extension.multiplayer.protocol.server.connector.BluetoothSocketConnectionClientConnector;
import org.andengine.util.debug.Debug;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.neildg.gamesofthegenerals.core.EngineCore;

public class OnServerSetupListener implements IBluetoothSocketServerListener<BluetoothSocketConnectionClientConnector> {
	
	private final static String TAG = "OnServerSetupListener";
	
    @Override
    public void onStarted(final BluetoothSocketServer<BluetoothSocketConnectionClientConnector> pBluetoothSocketServer) {
    	
    	final Activity runningActivity = (Activity) EngineCore.getInstance().getContext();
		
		final String deviceName = pBluetoothSocketServer.getName();
    	Engine engine = EngineCore.getInstance().getEngine();
		engine.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
            	Toast.makeText(runningActivity, "Server started with name  "+ deviceName, Toast.LENGTH_LONG).show();
            }
        });
		
		Log.v(TAG, "Server started with name  "+ deviceName);
		
    }

    @Override
    public void onTerminated(final BluetoothSocketServer<BluetoothSocketConnectionClientConnector> pBluetoothSocketServer) {
    	final Activity runningActivity = (Activity) EngineCore.getInstance().getContext();
		
		final String deviceName = pBluetoothSocketServer.getName();
    	Engine engine = EngineCore.getInstance().getEngine();
		engine.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
            	Toast.makeText(runningActivity, "Server terminated  "+ deviceName, Toast.LENGTH_LONG).show();
            }
        });
		
		Log.v(TAG, "Server terminated with name  "+ deviceName);
		
    }

    @Override
    public void onException(final BluetoothSocketServer<BluetoothSocketConnectionClientConnector> pBluetoothSocketServer, final Throwable pThrowable) {
            Debug.e(pThrowable);
            final Activity runningActivity = (Activity) EngineCore.getInstance().getContext();
    		
    		final String deviceName = pBluetoothSocketServer.getName();
        	Engine engine = EngineCore.getInstance().getEngine();
    		engine.runOnUpdateThread(new Runnable() {
                @Override
                public void run() {
                	Toast.makeText(runningActivity, "SERVER: Exception: " + pThrowable, Toast.LENGTH_LONG).show();
                }
            });
    		
    		Log.e(TAG, "SERVER: Exception: " + pThrowable);
    }
}