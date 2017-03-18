package com.android.ardic.mykhackathonapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

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
import com.ardic.android.iotignite.things.ThingData;
import com.ardic.android.iotignite.things.ThingType;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {


    private static final String TAG = MainActivity.class.getSimpleName() ;
    private IotIgniteManager manager;
    private ThingType mThingType = new ThingType("My Awesome Thing Type"," Awesome Vendor", ThingDataType.INTEGER);

    private static final String LED_THING_ID = "Led Controller";
    private  Thing mLedController;

    private Switch mLedSwitch;


    private ThingListener ledControllerListener = new ThingListener() {
        @Override
        public void onConfigurationReceived(Thing thing) {

        }

        @Override
        public void onActionReceived(String s, String s1, ThingActionData thingActionData) {

        }

        @Override
        public void onThingUnregistered(String s, String s1) {

        }
    };
    private  ThingType mLedControllerThingType = new ThingType("LED Switch Controller Type", "Vendor", ThingDataType.INTEGER);
    private ThingListener mThingListener = new ThingListener() {
        @Override
        public void onConfigurationReceived(Thing thing) {

            Log.i(TAG,"Config Received For : " + thing.getThingID());

        }

        @Override
        public void onActionReceived(String s, String s1, ThingActionData thingActionData) {
            Log.i(TAG,"Action Received For : " + s1 );
            thingActionData.getMessage();

        }

        @Override
        public void onThingUnregistered(String s, String s1) {
            Log.i(TAG,"Thing Unregistered " + s1 );

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mLedSwitch = (Switch) findViewById(R.id.switch1);

        mLedSwitch.setOnCheckedChangeListener(this);


       /// mLedSwitch.setOnCheckedChangeListener();



        try {
            manager = new IotIgniteManager.Builder().setConnectionListener(new ConnectionCallback() {
                @Override
                public void onConnected() {

                    Log.i(TAG,"Ignite connected");

                    Node mSampleNode = IotIgniteManager.NodeFactory.
                            createNode("K-Hack Node",
                                    "K-Hack Node",
                                    NodeType.GENERIC,
                                    null,
                                    new NodeListener() {
                                        @Override
                                        public void onNodeUnregistered(String s) {

                                        }
                                    });



                    if(mSampleNode !=null){



                       if( mSampleNode.isRegistered() || mSampleNode.register()){

                           mSampleNode.setConnected(true,"");


                           Thing mRandomThing = mSampleNode.createThing(
                                   "My Random Thing", mThingType , ThingCategory.EXTERNAL,false,mThingListener,null);


                           if( mRandomThing != null){

                               if(mRandomThing.isRegistered() || mRandomThing.register()){
                                   mRandomThing.setConnected(true,"");


                                   ThingData mData = new ThingData();

                                   mData.addData(new Random(50).nextInt());


                                  if( mRandomThing.sendData(mData)){
                                      Log.i(TAG,"DAta sent successfully");
                                  }else {
                                      Log.i(TAG,"DAta failure");
                                  }




                               }
                           }


                           mLedController = mSampleNode.createThing(LED_THING_ID,
                                   mLedControllerThingType,
                                   ThingCategory.BUILTIN,
                                   true,
                                   ledControllerListener,
                                   null
                                   );

                          if( mLedController.isRegistered() || mLedController.register() ){

                              mLedController.setConnected(true,"");

                              Log.i(TAG," Thing registration success");

                          }


                       }
                    }

                }

                @Override
                public void onDisconnected() {

                    Log.i(TAG,"Ignite disconnected");
                }
            }).setContext(getApplicationContext()).build();
        } catch (UnsupportedVersionException e) {
            e.printStackTrace();
        }




    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


        Log.i(TAG,"Switch STATE : " + isChecked);


        if(mLedController !=null && mLedController.isRegistered()){
            ThingData ledData = new ThingData();

            ledData.addData(isChecked ? 1 : 0);
            mLedController.sendData(ledData);
        }
    }
}
