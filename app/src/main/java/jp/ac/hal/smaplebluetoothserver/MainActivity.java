package jp.ac.hal.smaplebluetoothserver;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
	String TAG = "debug";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		AcceptThread AT = new AcceptThread();
		AT.run();
	}

	private class AcceptThread extends Thread {

		private BluetoothServerSocket mmServerSocket;
		private InputStream mInput;

		AcceptThread() {
			// Use a temporary object that is later assigned to mmServerSocket
			// because mmServerSocket is final.
			BluetoothServerSocket tmp = null;
			try {
				Log.d(TAG,"AT呼び出し");
				// uuid
				UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
				Log.d(TAG, String.valueOf(MY_UUID));
				BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
				//BTアダプタ
				BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
				// MY_UUID is the app's UUID string, also used by the client code.
				tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("", MY_UUID);

			} catch (IOException e) {
				Log.e(TAG, "Socket's listen() method failed", e);
			}
			mmServerSocket = tmp;
		}

		public void run() {
			BluetoothSocket socket = null;
			// Keep listening until exception occurs or a socket is returned.
			while (true) {
				try {
					socket = mmServerSocket.accept();
				} catch (IOException e) {
					Log.e(TAG, "Socket's accept() method failed", e);
					break;
				}

				if (socket != null) {
					Log.d(TAG,"認証できたよ");
					// A connection was accepted. Perform work associated with
					// the connection in a separate thread.
					manageMyConnectedSocket(socket);
					try {
						mmServerSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
				}
			}
		}

		private void manageMyConnectedSocket(BluetoothSocket socket) {
			try {
				mInput = socket.getInputStream();
				Log.d(TAG,"getInputStream呼び出し");
			} catch (IOException e) {
				e.printStackTrace();
				Log.d(TAG,"manageMyConnectedSocket:"+e);
			}

		}

		// Closes the connect socket and causes the thread to finish.
		public void cancel() {
			try {
				mmServerSocket.close();
			} catch (IOException e) {
				Log.e(TAG, "Could not close the connect socket", e);
			}
		}
	}
}


