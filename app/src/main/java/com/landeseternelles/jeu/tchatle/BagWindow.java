package com.landeseternelles.jeu.tchatle;

/**
 * Created by Levleyth on 27/03/2015.
 */

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BagWindow extends FrameLayout {

    private FrameLayout frameLay;

    public static int objSize = 50;
    public static int startX = 20;

    //[ItemID] UInt16
    //[Quantity] UInt32
    //[PosInInventory] UInt8
    //[Flags] Uint8
    //[autre] UInt16
    private Obj[] moi = new Obj[40];

    private TextView quitMe = new TextView(getContext());

    public BagWindow(Context context, FrameLayout frame) {
        super(context);

        this.frameLay = frame;
        frameLay.setOnTouchListener(new MyTouchListener());

        LayoutParams lparams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        lparams.gravity = Gravity.CENTER_HORIZONTAL;

        quitMe.setText("Quitter");
        quitMe.setLayoutParams(lparams);

        quitMe.setTextColor(Color.WHITE);

        quitMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Game.leInterface.closeBag();
            }
        });
        frameLay.addView(quitMe);

        frameLay.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int height = frameLay.getMeasuredHeight();

                //objSize = height/6;
                startX = (int)(Game.imageSize/2 - objSize * 6);

                quitMe.setGravity(Gravity.CENTER_HORIZONTAL);
                quitMe.setY(objSize * 5);
            }
        });
    }

    public void addItemsFromServ (byte[] data)
    {
        int nbr = data[0];
        byte[] prov = new byte[7];

        for (int i = 0 ; i < nbr ; i++)
        {
            prov[0] = data[7*i+1];
            prov[1] = data[7*i+2];
            prov[2] = data[7*i+3];
            prov[3] = data[7*i+4];
            prov[4] = data[7*i+5];
            prov[5] = data[7*i+6];
            prov[6] = data[7*i+7];

            addItemFromServ(prov);
        }
    }

    public void addItemFromServ (byte[] data)
    {
        int itemID, quant;

        //[ItemID] UInt16
        itemID = (data[0]&0xFF)+(data[1]&0xFF)*256;
        //[Quantity] UInt32
        quant = (data[2]&0xFF)+(data[3]&0xFF)*256+(data[4]&0xFF)*65536+(data[5]&0xFF)*16777216;
        //Perso
        //perso = data[6]&0xFF;
        //[PosInInventory] UInt8
        final int posInInv = data[6]&0xFF;
        //[Flags] Uint8
        //flags = data[8]&0xFF;
        //[autre] UInt16
        //other = (data[9]&0xFF)+(data[10]&0xFF)*256;

        System.out.println("Bag : Add Item " + itemID + " " + quant);

        if(moi[posInInv]!=null) {
            System.out.println("Bag : Add existing object from server");
            moi[posInInv].setQuant(quant);
            return;
        }

        LayoutParams lparams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);


            moi[posInInv] = new Obj(getContext(), itemID, quant, 0, 0);
            moi[posInInv].setX((posInInv % 10) * objSize + startX);
            moi[posInInv].setY((posInInv / 10) * objSize + objSize);
            moi[posInInv].setLayoutParams(lparams);
            frameLay.addView(moi[posInInv]);
            moi[posInInv].getLayoutParams().height = objSize;
            moi[posInInv].getLayoutParams().width = objSize;

        moi[posInInv].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Game.leInterface.pickUpItem(posInInv, Game.quantObj);
            }
        });

        /*if (perso == 0) {
            prov.setX((posInInv % 4) * objSize + startX);
            autre[posInInv] = prov;
        }
        else {
            prov.setX((posInInv % 4) * objSize + Game.imageSize + objSize * 2);
            moi[posInInv] = prov;
        }*/
    }

    public void delItemFromServ(byte[] data)
    {
        int quant, posInInv, perso;
        //[Quantity] UInt32
        //quant = (data[0]&0xFF)+(data[1]&0xFF)*256+(data[2]&0xFF)*65536+(data[3]&0xFF)*16777216;
        //[PosInInventory] UInt8
        posInInv = data[0]&0xFF;
        //Perso
        //perso = data[5]&0xFF;

        System.out.println("Remove : " + (data[0]&0xFF));

                frameLay.removeView(moi[posInInv]);
                moi[posInInv] = null;
    }

    public void showBagWindow()
    {
        frameLay.setVisibility(LinearLayout.VISIBLE);
    }

    public void hideBagWindow()
    {
        frameLay.setVisibility(LinearLayout.GONE);
        for (int i=1;i<40;i++)
        {
            frameLay.removeView(moi[i]);
            moi[i] = null;
        }
    }

    // This defines your touch listener
    private final class MyTouchListener implements OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            final float x = motionEvent.getX();
            final float y = motionEvent.getY();
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if(Game.inventoryItemSelected != -1) {
                    Game.leInterface.dropItem(Game.inventoryItemSelected, Game.quantObj);
                }
                else
                    Game.inventoryItemSelected = -1;
            } else {
                return false;
            }
            return true;
        }
    }

}
