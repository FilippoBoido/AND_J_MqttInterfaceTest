package com.example.mqttandroid;


import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttWorker extends Thread implements MqttCallback {

    boolean bQuit = false;

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
/*
    @Override
    public void run() {
        super.run();

        while(!bQuit)
        {
            ;
        }
        Log.d(MainActivity.DEBUG,"MqttWorker quitting.");
    }

    public void quit()
    {
        bQuit = true;
    }

 */
}
