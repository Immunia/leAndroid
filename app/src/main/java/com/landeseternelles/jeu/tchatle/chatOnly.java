package com.landeseternelles.jeu.tchatle;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Levleyth on 19/03/2015.
 */
public class chatOnly extends Activity implements View.OnClickListener
{
    static LEInterfaceNetv2 leInterface;

    final static private byte RAW_TEXT = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CreatecallbackRecept();

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

                System.out.print(data.toString());
            }
        };
    }

    @Override
    public void onClick(View v) {
        EditText senten = (EditText)findViewById(R.id.sentenceFull);
        leInterface.sendRawText(senten.getText().toString());
    }
}
