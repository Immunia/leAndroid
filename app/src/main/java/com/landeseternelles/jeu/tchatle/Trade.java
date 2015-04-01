package com.landeseternelles.jeu.tchatle;

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

/**
 * Created by Levleyth on 23/03/2015.
 */
public class Trade extends FrameLayout {

    private FrameLayout frameLay;

    public static int objSize = 20;
    public static int startX = 20;

    //[ItemID] UInt16
    //[Quantity] UInt32
    //[PosInInventory] UInt8
    //[Flags] Uint8
    //[autre] UInt16
    private Obj[] moi = new Obj[16];
    private Obj[] autre = new Obj[16];

    private TextView title = new TextView(getContext());
    private TextView quitMe = new TextView(getContext());
    private TextView acceptMe = new TextView(getContext());

    Byte other, me;

    public Trade(Context context, FrameLayout frame) {
        super(context);

        this.frameLay = frame;
        frameLay.setOnTouchListener(new MyTouchListener());

        LayoutParams lparams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        lparams.gravity = Gravity.CENTER_HORIZONTAL;

        title.setLayoutParams(lparams);
        frameLay.addView(title);
        quitMe.setText("Quitter");
        quitMe.setLayoutParams(lparams);

        title.setTextColor(Color.RED);
        acceptMe.setTextColor(Color.RED);
        quitMe.setTextColor(Color.WHITE);

        quitMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Game.leInterface.tradeExit();
            }
        });
        frameLay.addView(quitMe);

        acceptMe.setText("Accepter");
        acceptMe.setLayoutParams(lparams);
        acceptMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (me == 0) {
                    Game.leInterface.tradeAccept();
                } else if (me == 2 || (me == 1 && other == 0)) {
                    Game.leInterface.tradeReject();
                } else if (me == 1 && other == 1) {
                    Game.leInterface.tradeAccept();
                }
            }
        });
        frameLay.addView(acceptMe);

        frameLay.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int height = frameLay.getMeasuredHeight();

                objSize = height/6;
                title.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (objSize * 0.8));
                startX = (int)(Game.imageSize/4 - objSize * 2);

                acceptMe.setX(startX);
                acceptMe.setY(objSize * 5);

                quitMe.setX(startX + Game.imageSize / 2);
                quitMe.setY(objSize * 5);
            }
        });
    }

    public void addItemFromServ (byte[] data)
    {
        int itemID, quant, posInInv, flags, other, perso;

        //[ItemID] UInt16
        itemID = (data[0]&0xFF)+(data[1]&0xFF)*256;
        //[Quantity] UInt32
        quant = (data[2]&0xFF)+(data[3]&0xFF)*256+(data[4]&0xFF)*65536+(data[5]&0xFF)*16777216;
        //Perso
        perso = data[6]&0xFF;
        //[PosInInventory] UInt8
        posInInv = data[7]&0xFF;
        //[Flags] Uint8
        flags = data[8]&0xFF;
        //[autre] UInt16
        other = (data[9]&0xFF)+(data[10]&0xFF)*256;

        if(autre[posInInv]!=null && flags == 1)
        {
            System.out.println("Trade : Add existing object from server");
            autre[posInInv].setQuant(autre[posInInv].getQuant() + quant);
            return;
        } else if(moi[posInInv]!=null && perso == 0) {
            System.out.println("Trade : Add existing object from server");
            moi[posInInv].setQuant(quant);
            return;
        }

        LayoutParams lparams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);

        if (flags == 1) {
            autre[posInInv] = new Obj(getContext(), itemID, quant, flags, other);
            autre[posInInv].setX((posInInv % 4) * objSize + Game.imageSize/2 + objSize);
            autre[posInInv].setY((posInInv / 4) * objSize + objSize);
            autre[posInInv].setLayoutParams(lparams);
            frameLay.addView(autre[posInInv]);
            autre[posInInv].getLayoutParams().height = objSize;
            autre[posInInv].getLayoutParams().width = objSize;
        }else {
            moi[posInInv] = new Obj(getContext(), itemID, quant, flags, other);
            moi[posInInv].setX((posInInv % 4) * objSize + startX);
            moi[posInInv].setY((posInInv / 4) * objSize + objSize);
            moi[posInInv].setLayoutParams(lparams);
            frameLay.addView(moi[posInInv]);
            moi[posInInv].getLayoutParams().height = objSize;
            moi[posInInv].getLayoutParams().width = objSize;
        }

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
        quant = (data[0]&0xFF)+(data[1]&0xFF)*256+(data[2]&0xFF)*65536+(data[3]&0xFF)*16777216;
        //[PosInInventory] UInt8
        posInInv = data[4]&0xFF;
        //Perso
        perso = data[5]&0xFF;

        System.out.println("Remove : " + quant + " " + (data[4]&0xFF) + " " + (data[5]&0xFF));

        if(perso==0) {
            autre[posInInv].setQuant(autre[posInInv].getQuant() - quant);
            if (autre[posInInv].getQuant() == 0) {
                frameLay.removeView(autre[posInInv]);
                autre[posInInv] = null;
            }
        } else {
            moi[posInInv].setQuant(moi[posInInv].getQuant() - quant);
            if (moi[posInInv].getQuant() == 0) {
                frameLay.removeView(moi[posInInv]);
                moi[posInInv] = null;
            }
        }
    }

    public void acceptTrade(byte[] data)
    {
        if(data[0] == 1) {
            if (other == 0) {
                other = 1;
                title.setTextColor(Color.YELLOW);
            } else {
                other = 2;
                title.setTextColor(Color.GREEN);
            }
        } else {
            if (me == 0) {
                acceptMe.setTextColor(Color.YELLOW);
                me = 1;
            } else {
                acceptMe.setTextColor(Color.GREEN);
                me = 2;
            }
        }
    }

    public void rejectTrade(byte[] data)
    {
        if(data[0] == 1) {
            other = 0;
            title.setTextColor(Color.RED);
        } else {
            acceptMe.setTextColor(Color.RED);
            me = 0;
        }
    }

    public void showTrade(String name)
    {
        frameLay.setVisibility(LinearLayout.VISIBLE);
        other = 0;
        title.setTextColor(Color.RED);
        me = 0;
        acceptMe.setTextColor(Color.RED);
        title.setText(name);
    }

    public void hideTrade()
    {
        frameLay.setVisibility(LinearLayout.GONE);
        for (int i=1;i<16;i++)
        {
            frameLay.removeView(autre[i]);
            autre[i] = null;
        }
        for (int i=1;i<16;i++)
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
                    Game.leInterface.putObjectOnTrade(1, Game.inventoryItemSelected, Game.quantObj);
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
