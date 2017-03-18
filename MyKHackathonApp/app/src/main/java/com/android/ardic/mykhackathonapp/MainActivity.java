package com.android.ardic.mykhackathonapp;

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
import com.ardic.android.iotignite.things.ThingData;
import com.ardic.android.iotignite.things.ThingType;

import java.util.Random;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = MainActivity.class.getSimpleName() ;
    private IotIgniteManager manager;
    private ThingType mThingType = new ThingType("My Awesome Thing Type"," Awesome Vendor", ThingDataType.INTEGER);

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

                                   mData.addData();


                                  if( mRandomThing.sendData(mData)){
                                      Log.i(TAG,"DAta sent successfully");
                                  }else {
                                      Log.i(TAG,"DAta failure");
                                  }




                               }
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
}
