package com.landeseternelles.jeu.tchatle;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Created by Levleyth on 23/03/2015.
 */
public class Inventory extends FrameLayout{

    private FrameLayout frameLay;
    private FrameLayout selec = new FrameLayout(getContext());

    public static int objSize = 20;

    //[ItemID] UInt16
    //[Quantity] UInt32
    //[PosInInventory] UInt8
    //[Flags] Uint8
    //[autre] UInt16
    private Obj[] inv = new Obj[44];

    public Inventory(Context context, FrameLayout frame) {
        super(context);

        this.frameLay = frame;
        frameLay.setOnTouchListener(new MyTouchListener());
        objSize = Game.imageSize / 11;

        final LayoutParams lparams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        selec.setLayoutParams(lparams);
        selec.setBackgroundColor(0x40FF0000);
        frameLay.addView(selec);
        selec.getLayoutParams().height = objSize;
        selec.getLayoutParams().width = objSize;
        selec.setVisibility(View.GONE);
        selec.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Game.inventoryItemSelected = -1;
                    selec.setVisibility(View.GONE);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    public void setInv(byte[] data)
    {
        int nbItem = data[0]&0xFF;
        int itemID, quant, posInInv, flags, other;

        for(int j = 0; j<44; j++)
        {
            if (inv[j] != null)
                delItem(j);
        }

        for (int i = 0; i < nbItem ; i++)
        {
            //[ItemID] UInt16
            itemID = (data[i*10+1]&0xFF)+(data[i*10+2]&0xFF)*256;
            //[Quantity] UInt32
            quant = (data[i*10+3]&0xFF)+(data[i*10+4]&0xFF)*256+(data[i*10+5]&0xFF)*65536+(data[i*10+6]&0xFF)*16777216;
            //[PosInInventory] UInt8
            posInInv = data[i*10+7]&0xFF;
            //[Flags] Uint8
            flags = data[i*10+8]&0xFF;
            //[autre] UInt16
            other = (data[i*10+9]&0xFF)+(data[i*10+10]&0xFF)*256;
            addItem(itemID, quant, posInInv, flags, other);
        }

    }

    public void addItemFromServ(byte[] data)
    {
        int itemID, quant, posInInv, flags, other;
        //[ItemID] UInt16
        itemID = (data[0]&0xFF)+(data[1]&0xFF)*256;
        //[Quantity] UInt32
        quant = (data[2]&0xFF)+(data[3]&0xFF)*256+(data[4]&0xFF)*65536+(data[5]&0xFF)*16777216;
        //[PosInInventory] UInt8
        posInInv = data[6]&0xFF;
        //[Flags] Uint8
        flags = data[7]&0xFF;
        //[autre] UInt16
        other = (data[8]&0xFF)+(data[9]&0xFF)*256;
        addItem(itemID, quant, posInInv, flags, other);
    }

    public void removeItemFromServ(byte[] data)
    {
        for(int i = 0 ; i < data.length ; i++)
            delItem(data[i]);
    }

    public void addItem(final int itemID, int quant, final int posInInv, final int flags, final int other)
    {
        if(inv[posInInv]!=null) {
            System.out.println("Bag : Add existing object from server");
            inv[posInInv].setQuant(quant);
            return;
        }

        final LayoutParams lparams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);

        final Obj obj = new Obj(getContext() ,itemID, quant, flags, other);
        inv[posInInv] = obj;

        if(posInInv<36)
        {
            inv[posInInv].setX((posInInv % 6) * objSize + objSize);
            inv[posInInv].setY((posInInv / 6) * objSize + objSize);
            inv[posInInv].setLayoutParams(lparams);
            frameLay.addView(inv[posInInv]);
            inv[posInInv].getLayoutParams().height = objSize;
            inv[posInInv].getLayoutParams().width = objSize;
        }
        else
        {
            inv[posInInv].setX(((posInInv-36) % 2) * objSize + objSize*8);
            inv[posInInv].setY(((posInInv-36) / 2) * objSize + objSize*2);
            inv[posInInv].setLayoutParams(lparams);
            frameLay.addView(inv[posInInv]);
            inv[posInInv].getLayoutParams().height = objSize;
            inv[posInInv].getLayoutParams().width = objSize;
        }

        inv[posInInv].setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    System.out.println("Item Selected");
                    if(Game.clicMod == Game.USE_MOD)
                        Game.leInterface.useInventoryItem(posInInv);
                    else if(Game.inventoryItemSelected == posInInv) {
                        Game.inventoryItemSelected = -1;
                        selec.setVisibility(View.GONE);
                    }
                    else {
                        Game.inventoryItemSelected = posInInv;
                        selec.setVisibility(View.VISIBLE);
                        selec.setX(inv[posInInv].getX());
                        selec.setY(inv[posInInv].getY());
                        frameLay.bringChildToFront(selec);
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });

    }

    public void delItem(int posInInv)
    {
        frameLay.removeView(inv[posInInv]);
        inv[posInInv] = null;
    }

    public void showInv()
    {
        frameLay.setVisibility(LinearLayout.VISIBLE);
    }

    public void hideInv()
    {
        frameLay.setVisibility(LinearLayout.GONE);
    }

    // This defines your touch listener
    private final class MyTouchListener implements OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            final float x = motionEvent.getX();
            final float y = motionEvent.getY();
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                int emplac = -1;
                if(x > objSize && x < objSize*7 && y > objSize && y < objSize*7)
                {
                    emplac = (int)((x - objSize)/objSize) + (int)((y - objSize)/objSize)*6;
                } else if(x > objSize*8 && x < objSize*10 && y > objSize*2 && y < objSize*8)
                {
                    emplac = 36 + (int)((x - objSize*8)/objSize) + (int)((y - objSize*2)/objSize)*2;
                }

                System.out.println("Inventory Move : " + x + " " + y + " " + emplac);

                if(emplac != -1 && Game.inventoryItemSelected != -1) {
                    Game.leInterface.moveInventoryItem(Game.inventoryItemSelected, emplac);
                    //addItem(inv[Game.inventoryItemSelected].getId(), inv[Game.inventoryItemSelected].getQuant(), emplac, inv[Game.inventoryItemSelected].getFlags(), inv[Game.inventoryItemSelected].getOther());
                    //delItem(Game.inventoryItemSelected);
                    Game.inventoryItemSelected = -1;
                    selec.setVisibility(View.GONE);
                }
                else {
                    Game.inventoryItemSelected = -1;
                    selec.setVisibility(View.GONE);
                }
            } else {
                return false;
            }
            return true;
        }
    }
}
