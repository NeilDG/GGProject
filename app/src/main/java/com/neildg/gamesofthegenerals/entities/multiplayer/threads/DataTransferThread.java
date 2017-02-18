/**
 * 
 */
package com.neildg.gamesofthegenerals.entities.multiplayer.threads;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringBufferInputStream;
import java.nio.charset.Charset;

import com.neildg.gamesofthegenerals.core.notification.NotificationCenter;
import com.neildg.gamesofthegenerals.core.notification.Notifications;
import com.neildg.gamesofthegenerals.entities.multiplayer.DataInterpreter;
import com.neildg.gamesofthegenerals.entities.multiplayer.SocketManager;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

/**
 * Thread that handles data transfer between two connected devices.
 * @author NeilDG
 *
 */
public class DataTransferThread extends Thread{
	
	public final static String END_MSG = "<!MSG>";
	
	private final static String TAG = "DataTransferThread";
	
	private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    
    private DataInputStream dataInputStream;
 
    public DataTransferThread(BluetoothSocket socket) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
 
        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }
 
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }
    
    public void run() {
    	
    	final int BYTE_SIZE = 1024;
        byte[] buffer = new byte[BYTE_SIZE];  // buffer store for the stream
        int bytes; // bytes returned from read()
 
        int bytesRead = -1;
        String message = "";
        while (true) {
        	try {
        		
        		/*BufferedReader reader = new BufferedReader(new InputStreamReader(mmInStream));
                StringBuilder out = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    out.append(line);
                }
                
                message = out.toString();
                //reader.close();
                DataInterpreter.getInstance().interpretMessage(message);*/
                
                
        		/*message = "";
            	bytesRead = mmInStream.read(buffer);
            	if (bytesRead != -1) {
            		while ((bytesRead==BYTE_SIZE) && (buffer[BYTE_SIZE-1] != 0)) {
            			message = message + new String(buffer, 0, bytesRead);
            			bytesRead = mmInStream.read(buffer);
            		}
            	}
            	
            	message = message + new String(buffer, 0, bytesRead - 1);
            	DataInterpreter.getInstance().interpretMessage(message);*/
        		
        		StringBuilder messageBuilder = new StringBuilder();
        		while (-1 != (bytes = mmInStream.read(buffer))) {
        			messageBuilder.append(new String(buffer, 0, bytes, Charset.forName("UTF-8")));
        		    int endIdx = messageBuilder.indexOf(END_MSG);
        		    if (endIdx != -1) {
        		        message = messageBuilder.substring(0, endIdx + END_MSG.length());
        		        messageBuilder.delete(0, endIdx + END_MSG.length());
        		        
        		        Log.v(TAG, "Message received: " +message);
                    	DataInterpreter.getInstance().interpretMessage(message);
                    	
                    	//thread sleep
                    	this.sleep(500);
        		    }
        		}
 
            	
        	}
        	
        	catch(IOException e) {
        		Log.e(TAG, "EXCEPTION! " +e.getMessage());
        		NotificationCenter.getInstance().postNotification(Notifications.ON_BLUETOOTH_DISCONNECT, this);
        		break;
        	} catch (InterruptedException e) {
        		Log.e(TAG, "EXCEPTION! " +e.getMessage());
				e.printStackTrace();
			}
        	
        	/*catch(StringIndexOutOfBoundsException e) {
        		Log.e(TAG, "EXCEPTION: " +e.getMessage());
        	}*/
        	
        }
        	
        // Keep listening to the InputStream until an exception occurs
        /*while (true) {
            try {
                // Read from the InputStream
                bytes = mmInStream.read(buffer);

                Log.v(TAG, "Message received in bytes: " +bytes+ " From: " +mmSocket.getRemoteDevice().getName());
                
                this.dataInputStream = new DataInputStream(mmInStream);
                String message = this.dataInputStream.readUTF();
                
                Log.v(TAG, "Message received: " +message+ "  From: " +mmSocket.getRemoteDevice().getName());
                // Send the obtained bytes to the UI activity
            } catch (IOException e) {
                break;
            }
        }*/
    }
    
    public void write(String string) {
    	final PrintStream printStream = new PrintStream(mmOutStream);
		printStream.print(string + END_MSG); //end message means end of string. therefore, we can now read the string properly.
		Log.v(TAG, "Writing message: " +string + END_MSG);
    	/*try {
    		Log.v(TAG, "Writing message: " +string);
            mmOutStream.write(string.getBytes());
        } catch (IOException e) { }*/
    }
 
    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) { }
    }
 
    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}
