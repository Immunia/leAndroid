package com.landeseternelles.jeu.tchatle;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.res.ResourcesCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Levleyth on 23/03/2015.
 */
public class Obj extends FrameLayout {

    private int itemID, quant, flags, other;

    TextView nbr = new TextView(getContext());
    ImageView img = new ImageView(getContext());

    Obj copy = null;

    public Obj(Context context, int itemID, int quant, int flags, int other) {
        super(context);

        this.itemID = itemID;
        this.quant = quant;
        this.flags = flags;
        this.other = other;

        FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        int hauteur = (int)(Inventory.objSize * 0.8);
        this.setY(hauteur);

        int re = getContext().getResources().getIdentifier("item" + Utility.getNumberLetters(itemID), "drawable", getContext().getPackageName());
        if(re > 0) {
            img.setImageResource(re);
        }
        else
        {
            System.out.println("Image not found");
            img.setImageResource(R.drawable.other);
        }
        this.addView(img);

        nbr.setTextSize(TypedValue.COMPLEX_UNIT_PX, Inventory.objSize - hauteur);
        //nbr.setGravity(Gravity.RIGHT);
        String qua = Integer.toString(quant);
        nbr.setText(qua);
        nbr.setTextColor(Color.WHITE);
        this.addView(nbr);
    }

    public int getQuant() {
        return quant;
    }

    public void setQuant(int quant) {
        this.quant = quant;
        String qua = Integer.toString(quant);
        nbr.setText(qua);
    }

    public int getFlags() {
        return flags;
    }

    public int getOther() {
        return other;
    }
}
