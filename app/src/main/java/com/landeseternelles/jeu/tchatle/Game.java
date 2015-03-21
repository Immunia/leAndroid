package com.landeseternelles.jeu.tchatle;

import android.app.Activity;
import android.media.Image;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Levleyth on 20/03/2015.
 */
public class Game extends Activity
{
    //Server => Client
    final static private byte LOG_IN_TYPE = (byte) 140;
    final static private byte HEART_BEAT = 14;
    final static private byte PING_REQUEST = 60;
    final static private byte LOG_IN_OK = (byte) 250;
    final static private byte LOG_IN_NOT_OK = (byte) 251;
    final static private byte NEW_MINUTE = 5;
    final static private byte GET_ACTIVE_SPELL_LIST = 45;
    final static private byte SYNC_CLOCK = 4;
    final static private byte YOU_ARE = 3;
    final static private byte CHANGE_MAP = 7;
    final static private byte HERE_YOUR_INVENTORY = 19;
    final static private byte RAW_TEXT = 0;
    final static private byte ADD_NEW_ACTOR = 1;
    final static private byte ADD_ACTOR_COMMAND = 2;
    final static private byte REMOVE_ACTOR = 6;
    final static private byte ADD_NEW_ENHANCED_ACTOR = 51;
    final static private byte HERE_YOUR_STATS = 18;

    //Client => Server
    final static private byte MOVE_TO = 1;
    final static private byte ATTACK_SOMEONE = 40;

    static LEInterfaceNetv2 leInterface;

    static View imageZone;
    static int imageSize;
    static int heightScreen;

    static int id;

    private EditText tchatG;
    private Button sendGameB;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CreatecallbackRecept();

        //setContentView(new GameWindow(this));
        setContentView(R.layout.activity_game);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        imageSize = metrics.widthPixels;
        heightScreen = metrics.heightPixels;

        TextView pse = (TextView)findViewById(R.id.textView5);
        pse.getLayoutParams().height = imageSize;

        RelativeLayout rl = (RelativeLayout)findViewById(R.id.main_frame);
        imageZone = new GameWindow(this);
        //imageZone.getLayoutParams().height = imageSize;
        rl.addView(imageZone);

        tchatG = (EditText)findViewById(R.id.tchatG);

        sendGameB = (Button)findViewById(R.id.sendGameB);
        sendGameB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText senten = (EditText)findViewById(R.id.sentenceGame);
                System.out.print(senten.getText().toString());
                leInterface.sendRawText(senten.getText().toString());
                senten.setText("");
            }
        });

    }

    public void CreatecallbackRecept()
    {
        leInterface.reception = new CallbackRecept() {
            @Override
            public void callbackFunc(byte type, byte[] data) {
                System.out.println("RECEIVED");
                System.out.println("Type: " + type);

                if (type==PING_REQUEST)
                {
                    System.out.println("PING_REQUEST");
                    leInterface.send(PING_REQUEST, data);
                } else if (type == LOG_IN_OK)
                    System.out.println("LOGIN OK");
                else if (type == LOG_IN_NOT_OK)
                    System.out.println("LOGIN fail");
                else if (type == NEW_MINUTE)
                    System.out.println("NEW_MINUTE");
                else if (type == GET_ACTIVE_SPELL_LIST)
                    System.out.println("GET_ACTIVE_SPELL_LIST");
                else if (type == SYNC_CLOCK)
                    System.out.println("SYNC_CLOCK");
                else if (type == YOU_ARE)
                {
                    System.out.println("YOU_ARE");
                    id = (data[0]&0xFF)+(data[1]&0xFF)*255;
                }
                else if (type == CHANGE_MAP)
                {
                    System.out.println("CHANGE_MAP");
                    //imageZone.changeMap(new String(data));
                }
                else if (type == HERE_YOUR_INVENTORY)
                    System.out.println("HERE_YOUR_INVENTORY");
                else if (type == RAW_TEXT)
                {
                    System.out.println("RAW_TEXT");
                    tchatG.append("\n" + new String(data));
                }else if (type == ADD_NEW_ACTOR)
                {
                    System.out.println("ADD_NEW_ACTOR");
                    //imageZone.newActor(data);
                }else if (type == ADD_ACTOR_COMMAND)
                {
                    System.out.println("ADD_ACTOR_COMMAND");
                    //imageZone.actorCommand(data);
                }
                else if (type == REMOVE_ACTOR)
                {
                    System.out.println("REMOVE_ACTOR");
                    //imageZone.removeActor(data);
                }
                else if (type == ADD_NEW_ENHANCED_ACTOR)
                {
                    System.out.println("ADD_NEW_ENHANCED_ACTOR");
                    //imageZone.newEnhancedActor(data);
                }
                else if (type == HERE_YOUR_STATS)
                    System.out.println("HERE_YOUR_STATS");

                System.out.print("ByteData: ");
                for(int i = 0; i<data.length;i++)
                {
                    System.out.print(";" + (int)data[i]);
                }
                System.out.println();
                System.out.print("Textdata: ");
                for(int i = 0; i<data.length;i++)
                {
                    System.out.print((char)data[i]);
                }
                //System.out.println("reception : callbackFunc raw : " + new String(data));

            }
        };
    }

}
