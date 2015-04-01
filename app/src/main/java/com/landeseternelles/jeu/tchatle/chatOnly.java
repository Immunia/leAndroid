package com.landeseternelles.jeu.tchatle;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.Charset;

/**
 * Created by Levleyth on 19/03/2015.
 */
public class chatOnly extends Activity implements View.OnClickListener
{
    static LEInterfaceNetv2 leInterface;

    static Button sendFullB;
    static EditText tchatW;

    final static private byte RAW_TEXT = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LEInterfaceNetv2.ui = this;

        CreatecallbackRecept();

        leInterface.chatOnlyMod = true;
        setContentView(R.layout.activity_tchat);

        sendFullB = (Button)findViewById(R.id.sendFull);
        sendFullB.setOnClickListener(this);
        tchatW = (EditText)findViewById(R.id.tchatFull);
    }

    public void CreatecallbackRecept()
    {
        leInterface.reception = new CallbackRecept() {
            @Override
            public void callbackFunc(byte type, byte[] data) {
                if(type != RAW_TEXT)
                    return;

                System.out.println("reception : callbackFunc raw : " + new String(data));

                /*int leng = tchatW.getText().length() + 1 + data.length;
                if (leng>100)
                    tchatW.setText(tchatW.getText().toString().substring(leng-100) + "\n" + data.toString());
                else*/
                    //tchatW.setText(tchatW.getText() + "\nNew: " + data.toString());
                Charset iso = Charset.forName("ISO-8859-1");
                CharsetDecoder isodecoder = iso.newDecoder();
                ByteBuffer bbuf = ByteBuffer.wrap( data );
                CharBuffer cbuf = null;
                try {
                    cbuf = isodecoder.decode(bbuf);
                } catch (CharacterCodingException e) {
                    e.printStackTrace();
                }

                String sData = new String( cbuf.array() );

                System.out.println("reception : callbackFunc raw ////// : " + sData.substring( 2 ) );


                tchatW.append("\n" + sData.substring( 2 ) );

            }
        };
    }

    @Override
    public void onClick(View v) {
        EditText senten = (EditText)findViewById(R.id.sentenceFull);
        System.out.print(senten.getText().toString());
        leInterface.sendRawText(senten.getText().toString());
        senten.setText("");
    }
}
