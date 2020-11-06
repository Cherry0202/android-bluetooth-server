package jp.ac.hal.smaplebluetoothserver;

import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
	private static final String TAG = "debug";
	static InputStream mInput;
	static OutputStream mOutput; //出力ストリーム
	private final int REPEAT_INTERVAL = 1000;
	private boolean isRepeat = true;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button button = this.findViewById(R.id.button);
		BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		final AcceptThread AT = new AcceptThread(bluetoothManager);
		AT.start();
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "ここまできたよ！");
				AT.send2(mOutput);
			}
		});
		Receiving();
	}

	private void Receiving() {
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
}


