package com.landeseternelles.jeu.tchatle;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Levleyth on 23/03/2015.
 */
public class ObjInv extends FrameLayout {

    int itemID, quant, flags, other;

    TextView nbr = new TextView(getContext());
    ImageView img = new ImageView(getContext());

    public ObjInv(Context context, int itemID, int quant, int flags, int other) {
        super(context);

        this.itemID = itemID;
        this.quant = quant;
        this.flags = flags;
        this.other = other;

        FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        int hauteur = (int)(Inventory.objSize * 0.8);
        this.setY(hauteur);

        img.setImageResource(R.drawable.rose);
        this.addView(img);

        nbr.setTextSize(TypedValue.COMPLEX_UNIT_PX, Inventory.objSize - hauteur);
        //nbr.setGravity(Gravity.RIGHT);
        String qua = Integer.toString(quant);
        nbr.setText(qua);
        nbr.setTextColor(Color.WHITE);
        this.addView(nbr);
    }
}
