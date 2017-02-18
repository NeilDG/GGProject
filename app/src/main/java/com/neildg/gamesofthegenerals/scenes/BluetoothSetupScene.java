/**
 * 
 */
package com.neildg.gamesofthegenerals.scenes;

import org.andengine.engine.Engine;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.neildg.gamesofthegenerals.core.EngineCore;
import com.neildg.gamesofthegenerals.core.ResolutionManager;
import com.neildg.gamesofthegenerals.core.SceneList;
import com.neildg.gamesofthegenerals.core.SceneManager;
import com.neildg.gamesofthegenerals.core.notification.NotificationCenter;
import com.neildg.gamesofthegenerals.core.notification.Notifications;
import com.neildg.gamesofthegenerals.entities.multiplayer.BluetoothConnector;
import com.neildg.gamesofthegenerals.layout.bluetooth.BluetoothDeviceListUI;
import com.neildg.gamesofthegenerals.layout.bluetooth.BluetoothErrorPrompt;
import com.neildg.gamesofthegenerals.layout.bluetooth.BluetoothHostEstablishPrompt;
import com.neildg.gamesofthegenerals.layout.bluetooth.BluetoothSetupPrompt;
import com.neildg.gamesofthegenerals.layout.bluetooth.BluetoothWarningPrompt;
import com.neildg.gamesofthegenerals.layout.prompts.ConfirmListener;
import com.neildg.gamesofthegenerals.layout.prompts.GenericPrompt;

/**
 * Scene where all bluetooth setup takes place
 * @author NeilDG
 *
 */
public class BluetoothSetupScene extends AbstractScene {

	private final static String TAG = "BluetoothSetupScene";
	
	private BluetoothWarningPrompt warningPrompt;
	private BluetoothSetupPrompt setupPrompt;
	private BluetoothHostEstablishPrompt hostSetupPrompt;
	private BluetoothErrorPrompt errorPrompt;
	private BluetoothDeviceListUI deviceListPrompt;
	
	private ConfirmListener warningConfirmListener;
	private ConfirmListener hostEstablishConfirmListener;
	private ConfirmListener errorConfirmListener;
	private ConfirmListener hostGameListener;
	private ConfirmListener joinGameListener;
	private ConfirmListener connectGameListener;
	
	private BluetoothConnector bluetoothConnector;
	
	private final Activity runningActivity = (Activity) EngineCore.getInstance().getContext();

	private GenericPrompt disconnectPrompt;
	
	public BluetoothSetupScene() {
		super();
		
	}
	
	@Override
	protected void onManagedUpdate(float pSecondsElapsed)
	{
		super.onManagedUpdate(pSecondsElapsed);
	}
	
	public void initializeConfirmListeners() {
		
		//bluetooth warning on startup confirm
		this.warningConfirmListener = new ConfirmListener() {
			@Override
			public void onConfirm() {
				BluetoothSetupScene.this.setupPrompt.show();
			}
		};
		
		//host game confirm
		this.hostGameListener = new ConfirmListener() {
			@Override
			public void onConfirm() {
				BluetoothSetupScene.this.setupHostConnection();
			}
		};
		
		//join game confirm
		this.joinGameListener = new ConfirmListener() {
			@Override
			public void onConfirm() {
				BluetoothSetupScene.this.setupClientConnection();
			}
		};
		
		//error bluetooth setup confirm
		this.errorConfirmListener = new ConfirmListener() {
			@Override
			public void onConfirm() {
				BluetoothSetupScene.this.warningPrompt.show();
			}
		};
		
		//host established successful confirm
		this.hostEstablishConfirmListener = new ConfirmListener() {
			@Override
			public void onConfirm() {
				SceneManager.getInstance().loadScene(SceneList.PIECE_PLACEMENT_SCENE);
			}
		};
		
		//device list connect 
		this.connectGameListener = new ConfirmListener() {
			@Override
			public void onConfirm() {
				BluetoothSetupScene.this.initClientConnection();
			}
		};
	}
	
	private void initClientConnection() {
		BluetoothDevice selectedDevice = this.deviceListPrompt.getSelectedDevice();
		this.bluetoothConnector.initClientConnection(selectedDevice);
		SceneManager.getInstance().loadScene(SceneList.PIECE_PLACEMENT_SCENE);
		
	}
	
	private void setupHostConnection() {
		this.bluetoothConnector = new BluetoothConnector();
		
		if(this.bluetoothConnector.verifyBluetooth()) {
			this.bluetoothConnector.initServer();
			this.hostSetupPrompt.assignServerAddress(this.bluetoothConnector.getServerMacAddress());
			this.hostSetupPrompt.show();
		}
		else {
			this.errorPrompt.show();
		}
	}
	
	private void setupClientConnection() {
		this.bluetoothConnector = new BluetoothConnector();
		
		if(this.bluetoothConnector.verifyBluetooth()) {
			this.bluetoothConnector.discoverDevices();
			this.deviceListPrompt.show();
		}
		else {
			this.errorPrompt.show();
		}
	}
	
	
	
	@Override
	public void loadSceneAssets() {
		
		this.createFPSDisplay();
		this.initializeConfirmListeners();
		
		this.warningPrompt = new BluetoothWarningPrompt(this);
		this.warningPrompt.setPosition(ResolutionManager.SCENE_WIDTH * 0.5f, ResolutionManager.SCENE_HEIGHT * 0.5f);
		this.warningPrompt.setConfirmListener(this.warningConfirmListener);
		this.warningPrompt.show();
		
		this.setupPrompt = new BluetoothSetupPrompt(this);
		this.setupPrompt.setPosition(ResolutionManager.SCENE_WIDTH * 0.5f, ResolutionManager.SCENE_HEIGHT * 0.5f);
		this.setupPrompt.setConfirmListener(this.hostGameListener, this.joinGameListener);
		//this.setupPrompt.show();
		
		this.errorPrompt = new BluetoothErrorPrompt(this);
		this.errorPrompt.setPosition(ResolutionManager.SCENE_WIDTH * 0.5f, ResolutionManager.SCENE_HEIGHT * 0.5f);
		this.errorPrompt.setConfirmListener(this.errorConfirmListener);
		
		this.hostSetupPrompt = new BluetoothHostEstablishPrompt(this);
		this.hostSetupPrompt.setPosition(ResolutionManager.SCENE_WIDTH * 0.5f, ResolutionManager.SCENE_HEIGHT * 0.5f);
		this.hostSetupPrompt.setConfirmListener(this.hostEstablishConfirmListener);
		
		this.deviceListPrompt = new BluetoothDeviceListUI(this);
		this.deviceListPrompt.setPosition(ResolutionManager.SCENE_WIDTH * 0.5f, ResolutionManager.SCENE_HEIGHT * 0.5f);
		this.deviceListPrompt.setConfirmListener(this.connectGameListener);
		
		this.attachChild(this.warningPrompt);
		this.attachChild(this.setupPrompt);
		this.attachChild(this.errorPrompt);
		this.attachChild(this.hostSetupPrompt);
		this.attachChild(this.deviceListPrompt);
		
		//disconnect prompt that will show if the remote device has disconnected
		/*this.disconnectPrompt = new GenericPrompt(this);
		this.disconnectPrompt.setPosition(ResolutionManager.SCENE_WIDTH * 0.5f, ResolutionManager.SCENE_HEIGHT * 0.5f);
		this.disconnectPrompt.setDisplayText("Your opponent has disconnected from the game. Please return to the main menu to play again.");
		this.disconnectPrompt.setPositiveButtonText("Main Menu");
		this.disconnectPrompt.hideNegativeButton();
		
		this.attachChild(this.disconnectPrompt);
		
		//disconnect confirm listener
		ConfirmListener confirmListener = new ConfirmListener() {
			@Override
			public void onConfirm() {
				Log.v(TAG, "GOING BACK TO MAIN!!");
				SceneManager.getInstance().loadScene(SceneList.MAIN_MENU_SCENE);
			}
		};
		
		this.disconnectPrompt.setConfirmListener(confirmListener, confirmListener);
		NotificationCenter.getInstance().addObserver(Notifications.ON_BLUETOOTH_DISCONNECT, this.disconnectPrompt);*/
	}

	@Override
	public void destroyScene() {
		Engine engine = EngineCore.getInstance().getEngine();
		
		engine.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
            	BluetoothSetupScene.this.detachChildren();
            	BluetoothSetupScene.this.detachSelf();
            }
        });
	}

}
