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
import android.widget.TextView;
import android.widget.Toast;

import com.amitness.photon.utils.Code;

import java.util.ArrayList;
import java.util.TreeMap;

public class ReceiveActivity extends AppCompatActivity {

    public boolean commandReceived = false;
    private TextView mTextViewLightLabel;
    private SensorManager mSensorManager;
    private SensorEventListener mEventListenerLight;
    private float currentLightIntensity;
    private float bgIntensity = -1;
    private ArrayList<Float> intensityValues = new ArrayList<>();
    private TreeMap<Long, Float> records;
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

    //from alex
    public TextView bgIntensityTextVar, rawReadingTextVar, curLightIntenTextVar;

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

        //BG intensity and rawReading
        bgIntensityTextVar = findViewById(R.id.bgIntensityText);
        rawReadingTextVar = findViewById(R.id.rawReadingText);
        curLightIntenTextVar = findViewById(R.id.curLightIntenText);

        records = new TreeMap<Long, Float>();

        mTextViewLightLabel = findViewById(R.id.sensorValue);
        mTextViewLightLabel.setText("Waiting for transfer...");
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//        mTextViewLightLabel.setText("Receiving...");

        mEventListenerLight = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {

                if (bgIntensity == -1) {
                    //startTime = System.currentTimeMillis();
                    //startTime = event.timestamp;
                    startTime = System.currentTimeMillis();
//                    referenceTime = System.currentTimeMillis();
                    Log.d("Start timestamp: ", String.valueOf(startTime));
//                    Log.d("Start timestamp: ", String.valueOf(referenceTime));
                    bgIntensity = event.values[0];
                    records.put(0L, bgIntensity);

                    //todo playing with bgIntensity
                    Log.d("Background Intensity: ", String.valueOf(bgIntensity));
                    bgIntensityTextVar.setText("bgIntensity: " + bgIntensity);

//                    rawReading += "0";
                }

                //todo playing with rawReading
                Log.d("RawReading:", String.valueOf(rawReading));
                rawReadingTextVar.setText("rawReading: " + rawReading);

                currentLightIntensity = event.values[0];

                //todo playing with currentLightIntensity
                curLightIntenTextVar.setText("currentLightIntensity: " + currentLightIntensity);

                if (currentLightIntensity > 1000 && !started) {
                    lastTime = System.currentTimeMillis();
                    started = true;
//                    mTextViewLightLabel.setText("1");
//                    rawReading += "1";
                }
                //long timestamp = event.timestamp;
                if (currentLightIntensity > bgIntensity) {
                    bit = "1";
                } else {
                    bit = "0";
                }
                long currentTime = System.currentTimeMillis();

                if ((currentTime - lastTime) > 499 && started) {
                    Log.d("1 second.", "passed.");
                    lastTime = currentTime;
                    records.put(currentTime - startTime, currentLightIntensity);
                    Log.d("Bit:", bit);
//                    mTextViewLightLabel.setText(bit);
                    rawReading += bit;
                }


                //records.put( referenceTime + Math.round((timestamp - startTime) / 1000000.0), lastLightValue);
                //records.put(timestamp - startTime, lastLightValue);
//                Log.d("Time Stamp:", String.valueOf(timestamp));
                intensityValues.add(currentLightIntensity);
//                Log.d("Sensor Value", String.valueOf(lastLightIntensity));


                String startBits = bc.getStartBits();
                String stopBits = bc.getStopBits();


                if (rawReading.length() >= 3) {
                    lastFiveBits = rawReading.substring(rawReading.length() - 3);
                    if (!startBitDetected) {
                        if (lastFiveBits.equals(startBits)) {
                            System.out.println("Start bit detected.");
                            mTextViewLightLabel.setText("Start bit detected.");
                            startBitDetected = true;
                            // received =  "";
                            // System.exit(0);
                        }
                    } else {

                        if (!lastFiveBits.equals(stopBits)) {
                            payload += lastFiveBits;
                            System.out.println("Stop bit detected.");
                            isTransferring = false;
//                            mSensorManager.unregisterListener(mEventListenerLight);
//                            updateUI();
                            mSensorManager.unregisterListener(mEventListenerLight);
                            updateUI();
                        } else {
//                            System.out.println("Stop bit detected.");
//                            isTransferring = false;
////                            mSensorManager.unregisterListener(mEventListenerLight);
////                            updateUI();
//                            mSensorManager.unregisterListener(mEventListenerLight);
//                            updateUI();
                        }
                    }
                    rawReading = "";
                }
                // System.out.println("==>" + received);

//                updateUI();
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
//        updateUI();
        super.onStop();
    }

    private void performAction(String received) {
//        Intent intent = null;
        switch (received) {
            case "A":
                Log.d("Got A.", received);
//                intent = getAppIntent("com.kabouzeid.gramophone");
//                String fileLocation = "file:///" + Environment.getExternalStorageDirectory().getAbsolutePath() + "/Music/Music/joel.mp3";
//                intent = new Intent(Intent.ACTION_VIEW);
//                intent.setDataAndType(Uri.parse(fileLocation), "audio/*");
//                startActivity(intent);
                Toast.makeText(this, "case A", Toast.LENGTH_LONG).show();
                break;

            case "B":
//                String url = "http://www.google.com";
//                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                Toast.makeText(this, "case B", Toast.LENGTH_LONG).show();
                break;

            case "C":
//                intent = getAppIntent("com.google.android.GoogleCamera");
                Toast.makeText(this, "case C", Toast.LENGTH_LONG).show();
                break;

            case "D":
//                intent = getAppIntent("com.android.dialer");
                Toast.makeText(this, "case D", Toast.LENGTH_LONG).show();
                break;
            case "E":
//                intent = getAppIntent("com.google.android.apps.inbox");
                Toast.makeText(this, "case E", Toast.LENGTH_LONG).show();
                break;
            case "F":
//                intent = getAppIntent("com.android.messaging");
                Toast.makeText(this, "case F", Toast.LENGTH_LONG).show();
                break;
            case "G":
//                intent = getAppIntent("com.android.settings");
                Toast.makeText(this, "case G", Toast.LENGTH_LONG).show();
                break;
        }
//        if (intent != null) {
//            startActivity(intent);
//        }
    }


    private Intent getAppIntent(String packageName) {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
        return launchIntent;
    }
}