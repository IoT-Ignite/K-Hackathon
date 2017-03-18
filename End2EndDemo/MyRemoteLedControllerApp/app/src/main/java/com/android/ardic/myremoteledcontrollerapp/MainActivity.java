package com.android.ardic.myremoteledcontrollerapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.ardic.android.iotignite.callbacks.ConnectionCallback;
import com.ardic.android.iotignite.enumerations.NodeType;
import com.ardic.android.iotignite.enumerations.ThingCategory;
import com.ardic.android.iotignite.enumerations.ThingDataType;
import com.ardic.android.iotignite.exceptions.UnsupportedVersionException;
import com.ardic.android.iotignite.listeners.NodeListener;
import com.ardic.android.iotignite.listeners.ThingListener;
import com.ardic.android.iotignite.nodes.IotIgniteManager;
import com.ardic.android.iotignite.nodes.Node;
import com.ardic.android.iotignite.things.Thing;
import com.ardic.android.iotignite.things.ThingActionData;
import com.ardic.android.iotignite.things.ThingType;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements ConnectionCallback,ThingListener,NodeListener {

    private static final String TAG =MainActivity.class.getSimpleName();
    private PeripheralManagerService mPeripheralManagerService = new PeripheralManagerService();

    private static final String LED_PIN = "BCM21";

    private Gpio mGpio;

    private IotIgniteManager manager;

    private static final String NODE_ID = "LED ACTION NODE";
    private static final String THING_ID = "LED ACTION THING";

    private Node mLedActionNode;
    private Thing mLedActionThing;

    private ThingType mLedActionThingType = new ThingType("Led Action","Led Action", ThingDataType.INTEGER);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Log.i(TAG," GPIO INTERFACE :" + mPeripheralManagerService.getGpioList());

        try {
            mGpio = mPeripheralManagerService.openGpio(LED_PIN);
            mGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mGpio.setValue(true);


            Log.i(TAG,"GPIO VAL : " + mGpio.getValue());

        } catch (IOException e) {

        }


        try {
            manager = new IotIgniteManager.Builder()
                    .setContext(getApplicationContext())
                    .setConnectionListener(this)
                    .build();
        } catch (UnsupportedVersionException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConnected() {

        Log.i(TAG,"Ignite Connected");

        mLedActionNode = IotIgniteManager.NodeFactory.createNode(
                NODE_ID,
                NODE_ID,
                NodeType.GENERIC,
                null,
                this
        );



       if( mLedActionNode.isRegistered() || mLedActionNode.register() ){

           mLedActionNode.setConnected(true,"");


           mLedActionThing = mLedActionNode.createThing(THING_ID,
                   mLedActionThingType,
                   ThingCategory.BUILTIN,
                   true,
                   this,
                   null
           );


           if(mLedActionThing.isRegistered() || mLedActionThing.register()){
               mLedActionThing.setConnected(true,"");
           }
       }


    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConfigurationReceived(Thing thing) {

    }

    @Override
    public void onActionReceived(String s, String s1, ThingActionData thingActionData) {


        if(thingActionData != null) {

            String msg =  thingActionData.getMessage();
            Log.i(TAG, "Action Received : " + msg);


            /*
                  {
        "angle0": -90,
        "angle1": 90
      }
             */
      /**      JSONObject mObject=null;

            try {
                 mObject = new JSONObject(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            if(mObject != null && mObject.has("angle0")){

                try {
                    int angle0 = mObject.getInt("angle0");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if(mObject != null && mObject.has("angle1")){

                try {
                    int angle1 = mObject.getInt("angle1");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }*/

            if("LED_OFF".equals(msg)){
                try {
                    mGpio.setValue(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if("LED_ON".equals(msg)){

                try {
                    mGpio.setValue(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    @Override
    public void onThingUnregistered(String s, String s1) {

    }

    @Override
    public void onNodeUnregistered(String s) {

    }
}
