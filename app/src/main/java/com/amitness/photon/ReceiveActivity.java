package com.amitness.photon;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;

import com.amitness.photon.utils.Code;

import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

public class ReceiveActivity extends AppCompatActivity {

    public boolean commandReceived = false;
    private TextView mTextViewLightLabel;
    private SensorManager mSensorManager;
    private SensorEventListener mEventListenerLight;
    private double currentLightIntensity;
    private double bgIntensity = -1; //changed from float
    private ArrayList<Double> intensityValues = new ArrayList<>();
    private TreeMap<Long, Double> records;
    private Code bc = new Code();
    private long startTime;
    private long lastTime;
    private String bit;
    private String rawReading = "";
    private boolean started = false;
    private String lastFiveBits;
    private String payload = "";
    private boolean startBitDetected = false;
    private boolean isTransferring = true;

    public TextView bgIntensityTextVar, rawReadingTextVar, curLightIntenTextVar;
    public Button recalcBackgroundVar;

    private void updateUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                mTextViewLightLabel.append(bit);
                String message = bc.decode(payload);
                if (message != null && !commandReceived) {
                    mTextViewLightLabel.setText("Received command.");
                    Log.d("Received:", message);
                    commandReceived = true;
                    performAction(message);
                } else {
                    mTextViewLightLabel.setText("Command was not found.");
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);

        bgIntensityTextVar = findViewById(R.id.bgIntensityText);
        rawReadingTextVar = findViewById(R.id.rawReadingText);
        curLightIntenTextVar = findViewById(R.id.curLightIntenText);

        recalcBackgroundVar = findViewById(R.id.recalcBackground);

        records = new TreeMap<Long, Double>();

        mTextViewLightLabel = findViewById(R.id.sensorValue);
        mTextViewLightLabel.setText("Waiting for transfer...");
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        mEventListenerLight = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {

                if (bgIntensity == -1) {
                    startTime = System.currentTimeMillis();
                    bgIntensity = event.values[0];
                    records.put(0L, bgIntensity);
                    Date currentDateTime = Calendar.getInstance().getTime();
                    bgIntensityTextVar.setText("Time: " + currentDateTime + " bgIntensity: " + bgIntensity);
                }

                recalcBackgroundVar.setOnClickListener(V-> {
                    Date currentDateTime = Calendar.getInstance().getTime();
                    bgIntensity = event.values[0];
                    bgIntensityTextVar.setText("Time: " + currentDateTime + " bgIntensity: " + bgIntensity);
                });

                rawReadingTextVar.setText("rawReading: " + rawReading);

                currentLightIntensity = event.values[0];
                curLightIntenTextVar.setText("currentLightIntensity: " + currentLightIntensity);

                if (currentLightIntensity > 1000 && !started) {
                    lastTime = System.currentTimeMillis();
                    started = true;
                }
                if (currentLightIntensity > bgIntensity) {
                    bit = "1";
                } else {
                    bit = "0";
                }
                long currentTime = System.currentTimeMillis();

                //todo this was previously at 499, what if i change it?
                if ((currentTime - lastTime) > 499 && started) {
                    Log.d("1 second.", "passed.");
                    lastTime = currentTime;
                    records.put(currentTime - startTime, currentLightIntensity);
                    Log.d("Bit:", bit);
                    rawReading += bit;
                }

                intensityValues.add(currentLightIntensity);

                String startBits = bc.getStartBits();
                String stopBits = bc.getStopBits();

                //todo changing rawReading.length() >= 3 from 3 to 5?
                if (rawReading.length() >= 4) {
                    lastFiveBits = rawReading.substring(rawReading.length() - 3);
                    if (!startBitDetected) {
                        if (lastFiveBits.equals(startBits)) {
                            System.out.println("Start bit detected.");
                            mTextViewLightLabel.setText("Start bit detected.");
                            startBitDetected = true;
                        }
                    } else {
                        if (!lastFiveBits.equals(stopBits)) {
                            payload += lastFiveBits;
                            System.out.println("Stop bit detected.");
                            isTransferring = false;
                            mSensorManager.unregisterListener(mEventListenerLight);
                            updateUI();
                        } else {

                        }
                    }
                    rawReading = "";
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(mEventListenerLight, mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onStop() {
        Log.d("Read all intensities:", String.valueOf(intensityValues));
        Log.d("Read all values:", String.valueOf(records));
        Log.d("Read all data:", rawReading);
        mSensorManager.unregisterListener(mEventListenerLight);
        super.onStop();
    }

    private void performAction(String received) {
        switch (received) {
            case "A":
                Toast.makeText(this, "case A", Toast.LENGTH_LONG).show();
                break;
            case "B":
                Toast.makeText(this, "case B", Toast.LENGTH_LONG).show();
                break;
            case "C":
                Toast.makeText(this, "case C", Toast.LENGTH_LONG).show();
                break;
            case "D":
                Toast.makeText(this, "case D", Toast.LENGTH_LONG).show();
                break;
            case "E":
                Toast.makeText(this, "case E", Toast.LENGTH_LONG).show();
                break;
            case "F":
                Toast.makeText(this, "case F", Toast.LENGTH_LONG).show();
                break;
            case "G":
                Toast.makeText(this, "case G", Toast.LENGTH_LONG).show();
                break;
        }
    }
}