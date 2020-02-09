package com.example.mqttandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements MqttCallback,View.OnClickListener {

    static String DEBUG = "debug";
    static String APP_STORAGE = "storage";
    static String TOPIC = "PLC_CHAN";
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private MemoryPersistence persistence;

    MqttClient mqttClient;

    int counter = 0;

    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mqttWorker.quit();


        /*
            Possible storage options are:
        *
        *   Shared preferences
        *   Files
        *   Databasess
        *   Content providers
        *
        *   More info here:
        *   http://developer.android.com/guide/topics/data/data-storage.html*/

        Log.d(DEBUG, "onPause(): storing important app data here.");
        /*
        try {
            FileOutputStream fos = openFileOutput(APP_STORAGE,MODE_PRIVATE);
            DataOutputStream dos = new DataOutputStream(fos);
            Log.d(DEBUG, "onPause(): Storing following value: " + counter);
            dos.writeInt(counter);
            dos.close();
        } catch (FileNotFoundException e) {
            Log.d(DEBUG, "onPause(): FileNotFoundException.");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(DEBUG, "onPause(): IOException.");
            e.printStackTrace();
        }
        */

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            mqttClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }

        Log.d(DEBUG, "onDestroy(): Destroyed.");
    }

    private int restoreFromPreviousUse()
    {
        int counter = 0;

        Log.d(DEBUG, "restoreFromPreviousUse(): restoring important app data here.");

        try {
            FileInputStream fis = openFileInput(APP_STORAGE);
            DataInputStream dis = new DataInputStream(fis);
            Log.d(DEBUG,"Available bytes: " + dis.available());
            counter = dis.readInt();
            Log.d(DEBUG, "restoreFromPreviousUse(): counter: " + counter);
            dis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d(DEBUG, "restoreFromPreviousUse(): FileNotFoundException.");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(DEBUG, "restoreFromPreviousUse(): IOException.");
        }

        return counter;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.d(DEBUG, "onCreate(): Created.");
        setContentView(R.layout.activity_main);

        Button btnMqttHome = findViewById(R.id.btnMqttHome);
        btnMqttHome.setOnClickListener(this);
        Button btnMqttForw = findViewById(R.id.btnMqttMoveForward);
        btnMqttForw.setOnClickListener(this);
        Button btnMqttBackw = findViewById(R.id.btnMqttMoveBackward);
        btnMqttBackw.setOnClickListener(this);

        String generatedString = randomAlphaNumeric(8);

        try {
            mqttClient =
                new MqttClient("tcp://192.168.2.123:1883", generatedString,persistence);
            mqttClient.setCallback(this);
            mqttClient.connect();
            mqttClient.subscribe(TOPIC);
        } catch (MqttException e) {
            e.printStackTrace();
            Log.d(DEBUG, "onCreate(): MqttException.");
        }


    }

    @Override
    public void onClick(View v) {

        String message;
        MqttMessage mqttMessage;

        switch(v.getId()) {

            case R.id.btnMqttHome:

                message = "cmd_home";

                mqttMessage = new MqttMessage(message.getBytes());
                try {
                    mqttClient.publish(TOPIC,mqttMessage);
                } catch (MqttException e) {
                    Log.d(DEBUG,"onClick(): MqttException.");
                    e.printStackTrace();
                }


                //intent = new Intent(this,MqttActivity.class);
                //startActivity(intent);
                break;

            case R.id.btnMqttMoveBackward:

                message = "cmd_forw";

                mqttMessage = new MqttMessage(message.getBytes());
                try {
                    mqttClient.publish(TOPIC,mqttMessage);
                } catch (MqttException e) {
                    Log.d(DEBUG,"onClick(): MqttException.");
                    e.printStackTrace();
                }


                //intent = new Intent(this,MqttActivity.class);
                //startActivity(intent);
                break;

            case R.id.btnMqttMoveForward:

                message = "cmd_backw";

                mqttMessage = new MqttMessage(message.getBytes());
                try {
                    mqttClient.publish(TOPIC,mqttMessage);
                } catch (MqttException e) {
                    Log.d(DEBUG,"onClick(): MqttException.");
                    e.printStackTrace();
                }

                break;
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.d(MainActivity.DEBUG,"connectionLost(): mqtt connection lost!");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Log.d(MainActivity.DEBUG,
            "messageArrived(): Topic: " + topic +
                " Message: " + message.getPayload().toString()
        );
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d(MainActivity.DEBUG,"deliveryComplete(): mqtt delivery complete!");
    }
}
