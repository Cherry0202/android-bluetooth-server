package jp.ac.hal.smaplebluetoothserver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
	private static final String TAG = "debug";
	private static OutputStream mOutput; //出力ストリーム
	private static InputStream mInput;
	private final int REPEAT_INTERVAL = 1000;
	private boolean isRepeat = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button button = this.findViewById(R.id.button);

		final AcceptThread AT = new AcceptThread();
		AT.start();
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "ここまできたよ！");
				AT.send2(mOutput);
			}
		});

		Runnable looper = new Runnable() {
			@Override
			public void run() {
//isRepeatがtrueなら処理を繰り返す
				while (isRepeat) {
					try {
						Thread.sleep(REPEAT_INTERVAL);
					} catch (InterruptedException e) {
						Log.e("looper", "InterruptedException");
					}
//繰り返し処理
					while (true) {
						Log.d(TAG, "受信待機中...");
						if (mInput != null) {
							// InputStreamのバッファを格納
							byte[] buffer = new byte[1024];
							// 取得したバッファのサイズを格納
							int bytes;
							// InputStreamの読み込み
							try {
								Log.d(TAG, "sample input-stream読み込み1");
								bytes = mInput.read(buffer);
								Log.d(TAG, "sample input-stream読み込み2");
								String msg = new String(buffer, 0, bytes);
								Log.d(TAG, "sample manageMyConnectedSocket: " + msg);
							} catch (IOException e) {
								e.printStackTrace();
								Log.d(TAG, "読み込み失敗" + e);
								break;
							}
						} else {
							Log.d(TAG, "送られてないよ！");
							break;
						}
					}
				}
			}
		};

		//スレッド起動
		Thread thread = new Thread(looper);
		thread.start();
	}

	private class AcceptThread extends Thread {
		private BluetoothServerSocket mmServerSocket;

		AcceptThread() {
			// Use a temporary object that is later assigned to mmServerSocket
			// because mmServerSocket is final.
			BluetoothServerSocket tmp = null;
			try {
				Log.d(TAG, "AT呼び出し");
				// uuid
				UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
				Log.d(TAG, String.valueOf(MY_UUID));
				BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
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
						mInput = socket.getInputStream();
						Log.d(TAG, "getInputStream呼び出し");
					} catch (IOException e) {
						e.printStackTrace();
						Log.d(TAG, "manageMyConnectedSocket:" + e);
					}
					try {
						mOutput = socket.getOutputStream();
						Log.d(TAG, "getOutStream呼び出し");
						send(mOutput);
					} catch (IOException e) {
						e.printStackTrace();
						Log.d(TAG, "manageMyConnectedSocket:" + e);
					}
					// A connection was accepted. Perform work associated with
					// the connection in a separate thread.
					manageMyConnectedSocket(mInput);
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

		private void send2(OutputStream mOutput) {
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
}


