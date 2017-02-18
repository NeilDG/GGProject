/**
 * 
 */
package com.neildg.gamesofthegenerals.layout.bluetooth;

import java.util.ArrayList;

import org.andengine.engine.Engine;
import org.andengine.entity.Entity;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.AutoWrap;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.util.HorizontalAlign;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.util.Log;

import com.neildg.gamesofthegenerals.core.AssetLoader;
import com.neildg.gamesofthegenerals.core.EngineCore;
import com.neildg.gamesofthegenerals.core.extension.AnchoredRectangle;
import com.neildg.gamesofthegenerals.core.extension.IOnTouchListener;
import com.neildg.gamesofthegenerals.core.extension.SimpleButton;
import com.neildg.gamesofthegenerals.core.extension.AnchoredRectangle.AnchorType;
import com.neildg.gamesofthegenerals.layout.prompts.ConfirmListener;
import com.neildg.gamesofthegenerals.scenes.GameScene;
import com.neildg.gamesofthegenerals.utils.RGBNormalizer;

/**
 * @author NeilDG
 *
 */
public class BluetoothDeviceListUI extends AnchoredRectangle implements IOnTouchListener{

	public final static int UI_WIDTH = 950;
	public final static int UI_HEIGHT = 690;
	
	private final static String TAG = "BluetoothDeviceListUI";
	
	private Engine engine;
	private AssetLoader assetLoader;
	private Scene assignedScene;
	
	private ConfirmListener confirmListener;
	
	private ArrayList<BluetoothDevice> deviceList = new ArrayList<BluetoothDevice>();
	private ArrayList<SimpleButton> deviceButtonList = new ArrayList<SimpleButton>();
	
	private BluetoothDevice selectedDeviceToConnect;
	private BroadcastReceiver receiver;
	
	private float startY = 100.0f;
	
	public BluetoothDeviceListUI(Scene assignedScene) {
		super(0, 0, UI_WIDTH, UI_HEIGHT, EngineCore.getInstance().getEngine().getVertexBufferObjectManager());
		
		this.assignedScene = assignedScene;
		this.engine = EngineCore.getInstance().getEngine();
		this.assetLoader = AssetLoader.getInstance();
		
		this.setAnchorPoint(AnchorType.CENTER);
		this.createUI(this.engine);
		//this.setZIndex(Z_INDEX);
		this.setupDiscovery();
		
		// Register the BroadcastReceiver
		Activity runningActivity = (Activity) EngineCore.getInstance().getContext();
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		runningActivity.registerReceiver(receiver, filter); // Don't forget to unregister during onDestroy
	}
	
	private void setupDiscovery() {
		this.receiver= new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				 String action = intent.getAction();
			        // When discovery finds a device
			        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
			            // Get the BluetoothDevice object from the Intent
			            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			            BluetoothDeviceListUI.this.updateDeviceListDisplay(device);
			        }
			}
		};
	}
	
	public void setConfirmListener(ConfirmListener confirmListener) {
		this.confirmListener = confirmListener;
	}
	
	private void createUI(Engine engine) {
		float[] normalizedRGB = RGBNormalizer.normalizeRGB(139, 119, 101);
		
		this.setColor(normalizedRGB[RGBNormalizer.RED], normalizedRGB[RGBNormalizer.GREEN], normalizedRGB[RGBNormalizer.BLUE]); //peach puff
		
		this.createTexts();
		//this.createButtons();
		this.setVisible(false);
		
	}
	
	private void createTexts() {
		Font font = FontFactory.create(engine.getFontManager(), engine.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 30, false);
		font.load();
		 
		Text deviceListTitle = new Text(0, 0, font, "Scanning for Visible Devices", engine.getVertexBufferObjectManager());
		deviceListTitle.setHorizontalAlign(HorizontalAlign.CENTER);
		deviceListTitle.setAutoWrap(AutoWrap.WORDS);
		deviceListTitle.setAutoWrapWidth(UI_WIDTH - 75.0f);
		deviceListTitle.setPosition((UI_WIDTH * 0.5f) - (deviceListTitle.getWidth() * 0.5f), 25);
		
	    this.attachChild(deviceListTitle);
	}
	
	
	private void updateDeviceListDisplay(final BluetoothDevice discoveredDevice) {
		
		Log.v(TAG, "Found device: " +discoveredDevice.getName());
		
		if(this.deviceList.contains(discoveredDevice)) {
			return;
		}
		
		this.deviceList.add(discoveredDevice);
		
		final Engine engine = EngineCore.getInstance().getEngine();
		
		engine.runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
            	Font font = FontFactory.create(engine.getFontManager(), engine.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 25, false);
        		font.load();
        		
        		
        		float buttonWidth = 800; float buttonHeight = 60;
        		
        		Log.v(TAG, "Device Name: " +discoveredDevice.getName());
        		SimpleButton deviceButton = new SimpleButton(UI_WIDTH * 0.5f, startY, buttonWidth, buttonHeight, BluetoothDeviceListUI.this, engine.getVertexBufferObjectManager());
        		deviceButton.setButtonId(BluetoothDeviceListUI.this.deviceList.size() - 1);
        		deviceButton.setAnchorPoint(AnchorType.CENTER);
        		deviceButton.addText(discoveredDevice.getName(), font);
        		BluetoothDeviceListUI.this.assignedScene.registerTouchArea(deviceButton);
        		BluetoothDeviceListUI.this.attachChild(deviceButton);
        		BluetoothDeviceListUI.this.deviceButtonList.add(deviceButton);
        		startY += buttonHeight + 15.0f;
            }
        });
		
		
	}
	
	
	public void show() {
		this.setVisible(true);
	}
	
	public void hide() {
		this.setVisible(false);
		
		for(int i = 0; i < this.deviceButtonList.size(); i++) {
			this.assignedScene.unregisterTouchArea(this.deviceButtonList.get(i));
		}
		
		//unregister broadcast receiver
		Activity runningActivity = (Activity) EngineCore.getInstance().getContext();
		runningActivity.unregisterReceiver(this.receiver);
		
	}


	@Override
	public boolean onTouch(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX,
			float pTouchAreaLocalY, Entity touchedEntity) {
		if(pSceneTouchEvent.isActionUp()) {
			if(this.isVisible()) {
				SimpleButton deviceBtn = (SimpleButton) touchedEntity;
				this.selectedDeviceToConnect = this.deviceList.get(deviceBtn.getButtonId());
				this.confirmListener.onConfirm();
				this.hide();
			}
		}
		
		return true;
	}
	
	public BluetoothDevice getSelectedDevice() {
		return this.selectedDeviceToConnect;
	}

	
}
