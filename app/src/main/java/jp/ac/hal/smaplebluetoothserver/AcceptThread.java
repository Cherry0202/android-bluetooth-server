package jp.ac.hal.smaplebluetoothserver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;

public class AcceptThread extends Thread {
	private static final String TAG = "debug";
	private BluetoothServerSocket mmServerSocket;
	private MainActivity mainActivity = new MainActivity();

	AcceptThread(BluetoothManager bluetoothManager) {
		// Use a temporary object that is later assigned to mmServerSocket
		// because mmServerSocket is final.
		BluetoothServerSocket tmp = null;
		try {
			Log.d(TAG, "AT呼び出し");
			// uuid
			UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
			Log.d(TAG, String.valueOf(MY_UUID));
//			BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			Log.d(TAG, String.valueOf(bluetoothManager));
			//BTアダプタ
			BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
			Log.d(TAG, "BA呼び出し");
			// MY_UUID is the app's UUID string, also used by the client code.
			tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("", MY_UUID);
			Log.d(TAG, "TMP呼び出し");

		} catch (IOException e) {
			Log.e(TAG, "Socket's listen() method failed", e);
		}
		mmServerSocket = tmp;
	}

	public void run() {
		BluetoothSocket socket;
		// Keep listening until exception occurs or a socket is returned.
		Log.d(TAG, "runだよ");
		while (true) {
			Log.d(TAG, "検索中...");
			try {
				socket = mmServerSocket.accept();
				Log.d(TAG, "acceptできたよ");
			} catch (IOException e) {
				Log.e(TAG, "Socket's accept() method failed", e);
				break;
			}
			if (socket != null) {
				try {
					MainActivity.mInput = socket.getInputStream();
					Log.d(TAG, "getInputStream呼び出し");
				} catch (IOException e) {
					e.printStackTrace();
					Log.d(TAG, "manageMyConnectedSocket:" + e);
				}
				try {
					MainActivity.mOutput = socket.getOutputStream();
					Log.d(TAG, "getOutStream呼び出し");
					send(MainActivity.mOutput);
				} catch (IOException e) {
					e.printStackTrace();
					Log.d(TAG, "manageMyConnectedSocket:" + e);
				}
				manageMyConnectedSocket(MainActivity.mInput);
				break;
			}
		}
	}

	private void manageMyConnectedSocket(InputStream mInput) {
		// InputStreamのバッファを格納
		byte[] buffer = new byte[1024];
		// 取得したバッファのサイズを格納
		int bytes;
		// InputStreamの読み込み
		try {
			bytes = mInput.read(buffer);
			Log.d(TAG, "input-stream読み込み");
			String msg = new String(buffer, 0, bytes);
			Log.d(TAG, "manageMyConnectedSocket: " + msg);
		} catch (IOException e) {
			e.printStackTrace();
			Log.d(TAG, "読み込み失敗" + e);
		}
	}

	private void send(OutputStream mOutput) {
		//文字列を送信する
		byte[] bytes;
		String str = "sampleから送られたやつだよ！";
		bytes = str.getBytes();
		try {
			mOutput.write(bytes);
			Log.d(TAG, "送信！" + Arrays.toString(bytes));
		} catch (IOException e) {
			Log.d(TAG, "送信エラー:" + e);
			e.printStackTrace();
		}
	}

	void send2(OutputStream mOutput) {
		//文字列を送信する
		byte[] bytes;
		String str = "sampleから送られたやつだよ2！";
		bytes = str.getBytes();
		try {
			mOutput.write(bytes);
			Log.d(TAG, "送信！");
		} catch (IOException e) {
			Log.d(TAG, "送信エラー:" + e);
			e.printStackTrace();
		}

	}
}
