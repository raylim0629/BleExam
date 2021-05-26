package com.exam.ble.central;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import com.exam.ble.R;

import static com.exam.ble.Constants.REQUEST_COARSE_LOCATION;
import static com.exam.ble.Constants.REQUEST_ENABLE_BT;
import static com.exam.ble.Constants.REQUEST_FINE_LOCATION;


public class CentralActivity extends AppCompatActivity {

    //// GUI variables
    // text view for status
    private TextView tvStatus;
    // button for start scan
    private Button btnScan;
    // button for stop connection
    private Button btnStop;
    // button for send data
    private Button btnOn;
    // button for Off
    private Button btnOff;
    // Radio for time
    private RadioButton radioTime;
    // Timer view
    private TextView timeStatus;
    // Radio for touch
    private RadioButton radioTouch;
    // Touch view
    private TextView touchStatus;
    // button for Timer Reset
    private Button btnTimerReset;
    // button for Timer Start
    private Button btnTimerStart;
    // button for Timer Stop
    private Button btnTimerStop;

    // Counter
    static int counter;
    static int touch_counter;

    Timer timer = null;
    TimerTask tt = new TimerTask() {
        @Override
        public void run() {
            counter++;
            timeStatus.setText(String.valueOf(counter/10)+"."+String.valueOf(counter%10));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central);

        initView();
        initBle();
        CentralManager.getInstance(this).initBle();
        timer = new Timer();
        radioTime.setChecked(true);
        radioTouch.setChecked(false);
        //timer.schedule(tt, 0, 100);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // finish app if the BLE is not supported
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        CentralManager.getInstance(this).disconnectGattServer();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 500);
    }

    /**
     * 안드로이드 권한 설정 결과
     * 블루투스는 위치 권한을 필요로 함.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(CentralActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    /**
     * 화면의 텍스트뷰와 버튼을 바인딩하고 버튼 이벤트를 설정.
     */
    private void initView() {
        //// get instances of gui objects
        // status textview
        tvStatus = findViewById(R.id.tv_status);
        // scan button
        btnScan = findViewById(R.id.btnScan);
        // stop button
        btnStop = findViewById(R.id.btnStop);
        // On button
        btnOn = findViewById(R.id.btnOn);
        // Off button
        btnOff = findViewById(R.id.btnOff);
        // radio for time
        radioTime = findViewById(R.id.radio_timer);
        // status timer view
        timeStatus = findViewById(R.id.time);
        // radio for touch
        radioTouch = findViewById(R.id.radio_touch);
        // status touch view
        touchStatus = findViewById(R.id.touch);
        // Timer Reset button
        btnTimerReset = findViewById(R.id.TimerReset);
        // Timer Start button
        btnTimerStart = findViewById(R.id.TimerStart);
        // Timer Stop button
        btnTimerStop = findViewById(R.id.TimerStop);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CentralManager.getInstance(CentralActivity.this).startScan();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CentralManager.getInstance(CentralActivity.this).disconnectGattServer();
            }
        });

        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String temp = "A";
                CentralManager.getInstance(CentralActivity.this).sendData(temp);
            }
        });

        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String temp = "B";
                CentralManager.getInstance(CentralActivity.this).sendData(temp);
            }
        });

        radioTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                CentralManager.getInstance(CentralActivity.this).app_mode = 0;
                radioTouch.setChecked(false);
                radioTime.setChecked(true);
                Toast.makeText(CentralActivity.this, "TimeMode", Toast.LENGTH_SHORT);
            }
        });

        radioTouch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                CentralManager.getInstance(CentralActivity.this).app_mode = 1;
                radioTouch.setChecked(true);
                radioTime.setChecked(false);
                Toast.makeText(CentralActivity.this, "TouchMode", Toast.LENGTH_SHORT);
            }
        });

        btnTimerReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter = 0;
                timeStatus.setText("0");
                touch_counter = 0;
                touchStatus.setText("0");
            }
        });

        btnTimerStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimerTask tt = new TimerTask() {
                    @Override
                    public void run() {
                        counter++;
                        timeStatus.setText(String.valueOf(counter/10)+"."+String.valueOf(counter%10));
                    }
                };
                timer = new Timer();
                timer.schedule(tt, 0, 100);
            }
        });

        btnTimerStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(timer != null) timer.cancel();
            }
        });
    }

    /**
     * CentralManager 에 콜백을 설정한다.
     */
    private void initBle() {
        CentralManager.getInstance(this).setCallBack(centralCallback);
    }

    /**
     * Request BLE enable
     * 블루투스 기능을 켠다.
     */
    private void requestEnableBLE() {
        Intent ble_enable_intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(ble_enable_intent, REQUEST_ENABLE_BT);
    }

    /**
     * Request Fine Location permission
     * 위치 권한을 안내한다.
     */
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(CentralActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION);
    }

    /**
     * 화면에 텍스트 정보를 표시한다.
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
     * 화면에 텍스트 정보를 표시한다.
     * @param message
     */
    private void showTimerMsg(final String message) {
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                timeStatus.setText(message);
            }
        };
        handler.sendEmptyMessage(1);
    }

    /**
     * 화면에 텍스트 정보를 표시한다.
     * @param message
     */
    private void showTouchMsg(final String message) {
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                touchStatus.setText(message);
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
                Toast.makeText(CentralActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        };
        handler.sendEmptyMessage(1);
    }

    /**
     * 텍스트뷰에 새로운 내용이 입력된 후 스크롤을 아래로 이동시킨다.
     */
    private void scrollToBottom() {
        final ScrollView scrollview = ((ScrollView) findViewById(R.id.scrollview));
        scrollview.post(new Runnable() {
            @Override public void run() {
                scrollview.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    CentralCallback centralCallback = new CentralCallback() {
        @Override
        public void requestEnableBLE() {
            CentralActivity.this.requestEnableBLE();
        }

        @Override
        public void requestLocationPermission() {
            CentralActivity.this.requestLocationPermission();
        }

        @Override
        public void onStatusMsg(final String message) {
            showStatusMsg(message);
        }

        @Override
        public void onTimerMsg(final String message) {
            showTimerMsg(message);
        }

        @Override
        public void onIncreaseTouchCounter() {
            touch_counter++;
        }

        @Override
        public int onGetTouchCounter() {
            return touch_counter;
        }

        @Override
        public void onTouchMsg(String message) {
            showTouchMsg(message);
        }

        @Override
        public void onResetCounter() {
            timer.cancel();
            counter = 0;
            touch_counter = 0;
        }

        @Override
        public void onStartCounter() {
            TimerTask tt = new TimerTask() {
                @Override
                public void run() {
                    counter++;
                    timeStatus.setText(String.valueOf(counter/10)+"."+String.valueOf(counter%10));
                }
            };
            timer = new Timer();
            timer.schedule(tt, 0, 100);
        }

        @Override
        public void onStopCounter(){
            timer.cancel();
        }

        @Override
        public void onToast(String message) {
            showToast(message);
        }
    };
}