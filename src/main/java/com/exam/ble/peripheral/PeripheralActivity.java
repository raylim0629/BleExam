package com.exam.ble.peripheral;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import android.bluetooth.BluetoothAdapter;

import java.util.Calendar;

import com.exam.ble.R;

import static com.exam.ble.Constants.REQUEST_ENABLE_BT;

public class PeripheralActivity extends AppCompatActivity {

    private final String TAG = PeripheralActivity.class.getSimpleName();

    private TextView tvStatus;
    private Button btnSend, btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peripheral);

        initView();
        initServer();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT)
            initServer();
    }

    @Override
    public void onBackPressed() {
        showStatusMsg("Close Server");
        /**
         * 액티비티가 종료될때 서버를 종료시켜주기 위한 처리.
         */
        PeripheralManager.getInstance(PeripheralActivity.this).close();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 500);
    }

    /**
     * 텍스트뷰와 버튼 바인딩.
     * 버튼 이벤트 셋팅.
     */
    private void initView() {
        tvStatus = (TextView) findViewById(R.id.tv_status);
        btnSend = (Button) findViewById(R.id.btnSend);
        btnClose = (Button) findViewById(R.id.btnClose);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar calendar = Calendar.getInstance();
                String todayTime = (calendar.get(Calendar.MONTH) + 1)
                        + "/" + calendar.get(Calendar.DAY_OF_MONTH)
                        + " " + calendar.get(Calendar.HOUR_OF_DAY)
                        + ":" + calendar.get(Calendar.MINUTE)
                        + ":" + calendar.get(Calendar.SECOND);


                PeripheralManager.getInstance(PeripheralActivity.this).sendData(todayTime);
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PeripheralManager.getInstance(PeripheralActivity.this).close();
            }
        });
    }

    /**
     * Gatt Server 시작.
     * Peripheral Callback 을 셋팅해준다.
     */
    private void initServer() {
        PeripheralManager.getInstance(PeripheralActivity.this).setCallBack(peripheralCallback);
        PeripheralManager.getInstance(PeripheralActivity.this).initServer();
    }

    /**
     * 불루투스 기능을 켠다.
     */
    private void requestEnableBLE() {
        Intent ble_enable_intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(ble_enable_intent, REQUEST_ENABLE_BT);
    }

    /**
     * 텍스트뷰에 메세지를 표시한다.
     * @param message
     */
    private void showStatusMsg(final String message) {
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                String oldMsg = tvStatus.getText().toString();
                tvStatus.setText(oldMsg + "\n" + message);

                scrollToBottom();
            }
        };
        handler.sendEmptyMessage(1);
    }

    /**
     * 토스트를 표시한다.
     * @param message
     */
    private void showToast(final String message) {
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Toast.makeText(PeripheralActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        };
        handler.sendEmptyMessage(1);
    }

    /**
     * 텍스트뷰에 메세지를 표시하면 스크롤을 아래로 이동시킨다.
     */
    private void scrollToBottom() {
        final ScrollView scrollview = ((ScrollView) findViewById(R.id.scrollview));
        scrollview.post(new Runnable() {
            @Override public void run() {
                scrollview.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    /**
     * Peripheral Callback
     */
    PeripheralCallback peripheralCallback = new PeripheralCallback() {
        @Override
        public void requestEnableBLE() {
            PeripheralActivity.this.requestEnableBLE();
        }

        @Override
        public void onStatusMsg(String message) {
            showStatusMsg(message);
        }

        @Override
        public void onToast(String message) {
            showToast(message);
        }
    };

}