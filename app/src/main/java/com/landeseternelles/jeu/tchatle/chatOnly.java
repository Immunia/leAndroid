package com.landeseternelles.jeu.tchatle;

import android.app.Activity;

/**
 * Created by Levleyth on 19/03/2015.
 */
public class chatOnly extends Activity
{
    LEInterfaceNetv2 leInterface;

    final static private byte RAW_TEXT = 0;

    public chatOnly(LEInterfaceNetv2 inte)
    {
        leInterface = inte;
        leInterface.chatOnlyMod = true;
        setContentView(R.layout.activity_tchat);

    }

    public void CreatecallbackRecept()
    {
        leInterface.reception = new CallbackRecept() {
            @Override
            public void callbackFunc(byte type, byte[] data) {
                if(type != RAW_TEXT)
                    return;


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
                System.out.println("\n\n");
            }
        };
    }

}
